package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.themes.ValoTheme
import uk.q3c.krail.core.ConfigurationException
import uk.q3c.krail.core.form.ButtonPosition.*
import uk.q3c.krail.core.form.EditMode.EDIT
import uk.q3c.krail.core.i18n.CommonLabelKey.*
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.core.view.component.IconFactory
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport
import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable


/**
 * Created by David Sowerby on 15 Aug 2018
 */
class EditSaveCancelConfig : Serializable {
    var editKey: I18NKey = Edit
    var saveKey: I18NKey = Save
    var cancelKey: I18NKey = Cancel
    var topPosition = TOP_RIGHT
    var bottomPosition = BOTTOM_RIGHT
}


enum class ButtonPosition { TOP_RIGHT, TOP_LEFT, TOP_CENTER, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT, NO_POSITION }

interface EditSaveCancelBuilder : Serializable {
    val config: EditSaveCancelConfig
    fun hasTopComponent(): Boolean
    fun topComponent(): EditSaveCancel
    fun bottomComponent(): EditSaveCancel
    fun hasBottomComponent(): Boolean

    /**
     * Configure [button] for use as a cancel button
     */
    fun cancelButton(button: Button)

    /**
     * Configure [button] for use as a save button
     */
    fun saveButton(button: Button)

    /**
     * Configure [button] for use as an edit button
     */
    fun editButton(button: Button)
}

class DefaultEditSaveCancelBuilder @Inject constructor(@field:Transient private val editSaveCancelProvider: Provider<EditSaveCancel>, val iconFactory: IconFactory, val serializationSupport: SerializationSupport, val userNotifier: UserNotifier) : EditSaveCancelBuilder {
    override val config = EditSaveCancelConfig()

    override fun editButton(button: Button) {
        button.icon = iconFactory.iconFor(config.editKey)
        button.data = config.editKey
        button.addStyleName(ValoTheme.BUTTON_FRIENDLY)
    }

    override fun cancelButton(button: Button) {
        button.icon = iconFactory.iconFor(config.cancelKey)
        button.data = config.cancelKey
        button.addStyleName(ValoTheme.BUTTON_DANGER)
    }

    override fun saveButton(button: Button) {
        button.icon = iconFactory.iconFor(config.saveKey)
        button.data = config.saveKey
        button.addStyleName(ValoTheme.BUTTON_FRIENDLY)
        button.addStyleName(ValoTheme.BUTTON_PRIMARY)
    }

    override fun hasTopComponent(): Boolean {
        return config.topPosition != NO_POSITION
    }

    override fun topComponent(): EditSaveCancel {
        val esc = component()
//        alignButtons(esc, true)
        return esc
    }

    private fun component(): EditSaveCancel {
        val esc = editSaveCancelProvider.get()
        editButton(esc.editButton)
        saveButton(esc.saveButton)
        cancelButton(esc.cancelButton)
        return esc
    }

//    private fun alignButtons(esc: EditSaveCancel, top: Boolean) {
//        esc.setComponentAlignment(esc.editButton, correlateAlignment(top))
//        esc.setComponentAlignment(esc.saveButton, correlateAlignment(top))
//        esc.setComponentAlignment(esc.cancelButton, correlateAlignment(top))
//    }

    private fun correlateAlignment(top: Boolean): Alignment {
        val kAlign = if (top) {
            config.topPosition
        } else {
            config.bottomPosition
        }
        return when (kAlign) {
            ButtonPosition.TOP_LEFT -> Alignment.TOP_LEFT
            ButtonPosition.TOP_CENTER -> Alignment.TOP_CENTER
            ButtonPosition.TOP_RIGHT -> Alignment.TOP_RIGHT
            ButtonPosition.BOTTOM_LEFT -> Alignment.BOTTOM_LEFT
            ButtonPosition.BOTTOM_CENTER -> Alignment.BOTTOM_CENTER
            ButtonPosition.BOTTOM_RIGHT -> Alignment.BOTTOM_RIGHT
            ButtonPosition.NO_POSITION -> throw ConfigurationException("This value should never be present at this stage")
        }
    }

    override fun bottomComponent(): EditSaveCancel {
        val esc = component()
//        alignButtons(esc, false)
        return esc
    }

    override fun hasBottomComponent(): Boolean {
        return config.bottomPosition != NO_POSITION
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        serializationSupport.deserialize(this)
    }

}


interface EditSaveCancel : Component {

    fun translate(translate: Translate, currentLocale: CurrentLocale)

    val editButton: Button
    val saveButton: Button
    val cancelButton: Button
    var section: FormDetailSection<*>

    fun updateButtonVisibility()
}

class DefaultEditSaveCancel : HorizontalLayout(), EditSaveCancel {
    override lateinit var section: FormDetailSection<*>
    override val editButton = Button()
    override val saveButton = Button()
    override val cancelButton = Button()

    init {
        addStyleName(ValoTheme.PANEL_BORDERLESS)

        editButton.addClickListener { _ ->
            section.editData()
        }

        saveButton.addClickListener { _ ->
            section.saveData()
        }

        cancelButton.addClickListener { _ ->
            section.cancelData()
        }
    }

    override fun updateButtonVisibility() {
        if (section.mode == EDIT) {
            removeAllComponents()
            addComponent(saveButton)
            addComponent(cancelButton)
        } else {
            removeAllComponents()
            addComponent(editButton)
        }
    }

    fun updateButton(button: Button, translate: Translate, currentLocale: CurrentLocale) {
        button.locale = currentLocale.locale
        val key = button.data as I18NKey
        button.caption = translate.from(key, currentLocale.locale)
    }

    override fun translate(translate: Translate, currentLocale: CurrentLocale) {
        updateButton(editButton, translate, currentLocale)
        updateButton(saveButton, translate, currentLocale)
        updateButton(cancelButton, translate, currentLocale)
    }


}


enum class EditMode {
    READ_ONLY, EDIT
}