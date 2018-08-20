package uk.q3c.krail.core.form

import com.vaadin.event.selection.SelectionEvent
import com.vaadin.event.selection.SelectionListener
import com.vaadin.ui.Grid

class FormTableSection<BEAN : Any>(val form: Form, override val rootComponent: Grid<BEAN>, val dao: FormDao<BEAN>) : FormSection, SelectionListener<BEAN> {
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