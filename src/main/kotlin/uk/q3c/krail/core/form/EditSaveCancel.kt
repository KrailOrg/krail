package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Panel
import com.vaadin.ui.themes.ValoTheme
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
    var displayAtTop = true
    var displayAtBottom = true
}

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
        return config.displayAtTop
    }

    override fun topComponent(): EditSaveCancel {
        val esc = component()
        return esc
    }

    private fun component(): EditSaveCancel {
        val esc = editSaveCancelProvider.get()
        editButton(esc.editButton)
        saveButton(esc.saveButton)
        cancelButton(esc.cancelButton)
        return esc
    }



    override fun bottomComponent(): EditSaveCancel {
        val esc = component()
        return esc
    }

    override fun hasBottomComponent(): Boolean {
        return config.displayAtBottom
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

class DefaultEditSaveCancel : Panel(), EditSaveCancel {
    override lateinit var section: FormDetailSection<*>
    override val editButton = Button()
    override val saveButton = Button()
    override val cancelButton = Button()
    private val layout = HorizontalLayout()

    init {
        addStyleName(ValoTheme.PANEL_BORDERLESS)
        content = layout

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
            layout.removeAllComponents()
            layout.addComponent(cancelButton)
            layout.addComponent(saveButton)
        } else {
            layout.removeAllComponents()
            layout.addComponent(editButton)
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