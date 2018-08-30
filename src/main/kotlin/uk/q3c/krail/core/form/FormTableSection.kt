package uk.q3c.krail.core.form

import com.vaadin.event.selection.SelectionEvent
import com.vaadin.event.selection.SelectionListener
import com.vaadin.ui.Grid
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate

class FormTableSection<BEAN : Any>(val form: Form, override val rootComponent: Grid<BEAN>, val dao: FormDao<BEAN>) : FormSection, SelectionListener<BEAN> {
    override fun translate(translate: Translate, currentLocale: CurrentLocale) {
        rootComponent.locale = currentLocale.locale
    }

    override var mode: EditMode = EditMode.READ_ONLY

    init {
        rootComponent.editor.isEnabled = false
    }

    override fun selectionChange(event: SelectionEvent<BEAN>) {
        val selectedItem = event.firstSelectedItem
        if (selectedItem.isPresent) {
            val bean = selectedItem.get()
            if (bean is Entity) {
                form.changeRoute(bean.id)
            }
        }
    }

}