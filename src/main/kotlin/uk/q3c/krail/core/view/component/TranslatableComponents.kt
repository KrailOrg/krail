package uk.q3c.krail.core.view.component

import com.google.inject.Inject
import com.vaadin.ui.AbstractComponent
import net.engio.mbassy.listener.Handler
import uk.q3c.krail.core.eventbus.SessionBusProvider
import uk.q3c.krail.core.i18n.CommonLabelKey
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.krail.i18n.Translate
import java.io.Serializable

/**
 * Simplifies the management of components which take their caption and / or description from [I18NKey]
 *
 * Maintains a map of components which use Krail's I18NKey to set caption and description.  It responds to changes in Locale
 * by updating those components.
 *
 * The [addEntry] method also sets the component icon (assuming icon is in use) - this is done here because the icon is looked up
 * from the [IconFactory] using an I18NKey
 */

interface TranslatableComponents : Serializable {
    val components: MutableMap<AbstractComponent, ComponentI18NKeys>
    fun translate()
    fun addEntry(component: AbstractComponent, captionKey: I18NKey = CommonLabelKey._NullKey_, descriptionKey: I18NKey = CommonLabelKey._NullKey_, useIcon: Boolean = true)
}


class DefaultTranslatableComponents @Inject constructor(val translate: Translate, val currentLocale: CurrentLocale, val iconFactory: IconFactory, sessionBusProvider: SessionBusProvider) : TranslatableComponents {
    override val components: MutableMap<AbstractComponent, ComponentI18NKeys> = mutableMapOf()

    init {
        sessionBusProvider.get().subscribe(this)
    }

    override fun translate() {
        components.forEach { (component, keys) ->
            if (keys.captionKey != CommonLabelKey._NullKey_) {
                component.caption = translate.from(keys.captionKey)
            }
            if (keys.descriptionKey != CommonLabelKey._NullKey_) {
                component.description = translate.from(keys.descriptionKey)
            }
            component.locale = currentLocale.locale

        }
    }

    @Handler
    fun localeChanged(@Suppress("UNUSED_PARAMETER") msg: LocaleChangeBusMessage) {
        translate()
    }

    /**
     * Stores a reference to the component along with a related [ComponentI18NKeys] instance.  Assigns an icon to [component], if [useIcon] is true.  The icon is looked up
     * using the caption key or description key, from an [IconFactory].  [CommonLabelKey._NullKey_] is ignored.
     */

    override fun addEntry(component: AbstractComponent, captionKey: I18NKey, descriptionKey: I18NKey, useIcon: Boolean) {
        if (useIcon) {
            component.icon = if (captionKey == CommonLabelKey._NullKey_) {
                iconFactory.iconFor(descriptionKey)
            } else {
                iconFactory.iconFor(captionKey)
            }
        }
        components[component] = ComponentI18NKeys(captionKey = captionKey, descriptionKey = descriptionKey)
    }
}

data class ComponentI18NKeys @JvmOverloads constructor(val captionKey: I18NKey = CommonLabelKey._NullKey_, val descriptionKey: I18NKey = CommonLabelKey._NullKey_) : Serializable
