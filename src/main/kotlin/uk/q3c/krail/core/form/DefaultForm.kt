package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.ui.Component
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport


/**
 * Created by David Sowerby on 10 Jun 2018
 */

interface Form : KrailView

class DefaultForm @Inject constructor(
        translate: Translate,
        serializationSupport: SerializationSupport,
        @field:Transient val formBuilderProvider: Provider<FormBuilder>)

    : ViewBase(translate, serializationSupport), Form {


    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private var componentMap: Map<String, FormProperty> = mutableMapOf()

    override fun doBuild(busMessage: ViewChangeBusMessage) {
        doBuild()
    }

    override fun doBuild() {
        val viewConfigurationClass = navigationStateExt.node?.masterNode?.viewConfiguration
        val formConfiguration =
                if (FormConfiguration::class.java.isAssignableFrom(viewConfigurationClass)) {
                    try {
                        viewConfigurationClass?.newInstance() as FormConfiguration
                    } catch (e: Exception) {
                        log.error("Failed to set form configuration", e)
                        EmptyFormConfiguration()
                    }

                } else {
                    throw FormConfigurationException("Configuration for a Form must be of type FormConfiguration")
                }
        val formTypeBuilder = formBuilderProvider.get().selectFormTypeBuilder(formConfiguration)
        val componentSet = formTypeBuilder.build()
        rootComponent = componentSet.rootComponent
        componentMap = componentSet.componentMap
    }

}

data class FormComponentSet(val componentMap: Map<String, FormProperty>, val rootComponent: Component)

