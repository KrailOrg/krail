package uk.q3c.krail.core.form

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.CheckBoxGroup
import com.vaadin.ui.Component
import com.vaadin.ui.ListSelect
import com.vaadin.ui.TwinColSelect
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


interface MultiSelectProperty<T : Any> : SelectProperty<T> {

    fun selected(): Set<T>
    fun deselect(value: T)
}

/**
 * Created by David Sowerby on 26 Jul 2018
 */
class DefaultMultiSelectProperty<T : Any>(override val dataProvider: ListDataProvider<T>, override var allowNoSelection: Boolean = true) : MultiSelectProperty<T> {

    constructor(permittedValues: Set<T>, allowNoSelection: Boolean = true) : this(ListDataProvider(permittedValues), allowNoSelection)

    var selectedValues: MutableSet<T> = mutableSetOf()

    override fun select(newValue: T) {
        selectedValues.add(newValue)
    }

    override fun deselect(value: T) {
        if (!allowNoSelection && selectedValues.size == 1 && selectedValues.contains(value)) {
            throw MultiSelectException("An empty selection is not allowed")
        }
        selectedValues.remove(value)
    }

    override fun selected(): Set<T> {
        return HashSet(selectedValues)
    }

    override fun clear() {
        if (allowNoSelection) {
            selectedValues.clear()
        } else {
            throw MultiSelectException("An empty selection is not allowed")
        }

    }

    override fun hasValue(): Boolean {
        return selectedValues.isNotEmpty()
    }
}

/**
 * Delegate used to select a multiple values
 */
class MultiSelectPropertyDelegate<BEAN : Any, T : Any> @JvmOverloads constructor(dataProvider: ListDataProvider<T>, allowNoSelection: Boolean = true) : ReadWriteProperty<BEAN, Set<T>>, Serializable, SelectPropertyDelegate {

    constructor(permittedValues: Set<T>, allowNoSelection: Boolean = true) : this(ListDataProvider(permittedValues), allowNoSelection)


    private val selector = DefaultMultiSelectProperty<T>(dataProvider = dataProvider, allowNoSelection = allowNoSelection)

    override fun configureComponent(component: Component) {

        @Suppress("UNCHECKED_CAST")
        when (component) {
            is ListSelect<*> -> {
                val c = component as ListSelect<T>
                c.dataProvider = selector.dataProvider

            }
            is TwinColSelect<*> -> {
                val c = component as TwinColSelect<T>
                c.dataProvider = selector.dataProvider
            }
            is CheckBoxGroup<*> -> {
                val c = component as CheckBoxGroup<T>
                c.dataProvider = selector.dataProvider
            }

            else -> {
                throw MultiSelectException("component not supported: ${component.javaClass}")
            }
        }
    }


    override fun getValue(thisRef: BEAN, property: KProperty<*>): Set<T> {
        return selector.selected()
    }

    override fun setValue(thisRef: BEAN, property: KProperty<*>, value: Set<T>) {
        selector.selectedValues = HashSet(value)
    }

}

class MultiSelectException(msg: String) : RuntimeException(msg)