package uk.q3c.krail.core.form

import com.vaadin.data.ValidationException
import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.AbstractField
import com.vaadin.ui.AbstractMultiSelect
import com.vaadin.ui.AbstractSingleSelect
import com.vaadin.ui.Component
import uk.q3c.krail.core.i18n.CommonLabelKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate


class FormDetailSection<BEAN : Any>(val translate: Translate, val currentLocale: CurrentLocale, val propertyMap: Map<String, DetailPropertyInfo>, override val rootComponent: Component, val binder: KrailBeanValidationBinder<BEAN>, val dao: FormDao<BEAN>, val escList: MutableList<EditSaveCancel>, val userNotifier: UserNotifier) : FormSection {


    lateinit var originalEntity: BEAN

    override var mode: EditMode = EditMode.READ_ONLY
        set(value) {
            field = value
            if (field == EditMode.READ_ONLY) {
                makeReadOnly()
            } else {
                makeEditable()
            }
        }

    init {
        escList.forEach { esc -> esc.section = this }
        makeReadOnly()
    }


    private fun makeReadOnly() {
        escList.forEach { esc -> esc.updateButtonVisibility() }
        propertyMap.forEach { _, v ->
            if (v.component is AbstractField<*>) {
                v.component.isReadOnly = true
            }
            if (v.component is AbstractSingleSelect<*>) {
                v.component.isReadOnly = true
            }
            if (v.component is AbstractMultiSelect<*>) {
                v.component.isReadOnly = true
            }
        }

    }


    private fun makeEditable() {
        escList.forEach { esc -> esc.updateButtonVisibility() }
        propertyMap.forEach { _, v ->
            if (v.component is AbstractField<*>) {
                v.component.isReadOnly = false
            }
            if (v.component is AbstractSingleSelect<*>) {
                v.component.isReadOnly = false
            }
            if (v.component is AbstractMultiSelect<*>) {
                v.component.isReadOnly = false
            }
        }
    }



    fun loadData(parameters: Map<String, String>) {
        val id = parameters.get("id")
        if (id == null) {
            throw MissingParameterException("id")
        } else {
            val bean = dao.get(id)
            originalEntity = bean
            binder.readBean(bean)
        }
    }

    fun saveData() {
        try {
            binder.writeBean(originalEntity)
            mode = EditMode.READ_ONLY
            dao.put(originalEntity)
            userNotifier.notifyInformation(CommonLabelKey.Saved)
        } catch (ve: ValidationException) {
            userNotifier.notifyInformation(LabelKey.There_are_validation_errors)
        }

    }

    fun cancelData() {
        mode = EditMode.READ_ONLY
        binder.readBean(originalEntity)
    }

    fun editData() {
        mode = EditMode.EDIT
    }

    override fun translate(translate: Translate, currentLocale: CurrentLocale) {
        propertyMap.forEach { _, v ->
            v.component.caption = translate.from(v.captionKey)
            // setDescription is not part of Component interface!
            if (v.component is AbstractComponent) {
                v.component.locale = currentLocale.locale
                v.component.description = translate.from(v.descriptionKey)
            }
        }
        escList.forEach { esc -> esc.translate(translate, currentLocale) }
    }
}