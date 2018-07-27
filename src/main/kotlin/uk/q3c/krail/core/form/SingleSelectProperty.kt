package uk.q3c.krail.core.form

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Component
import com.vaadin.ui.RadioButtonGroup
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Represents the selection of a single value from a list of permitted values
 *
 * Created by David Sowerby on 22 Jul 2018
 */
interface SingleSelectProperty<T : Any> : SelectProperty<T> {

    /**
     * Returns the currently selected value
     *
     * @throws SingleSelectException is no selection has been made (use [hasValue] to avoid exception
     */
    fun selected(): T

    /**
     * 'Clear' the selection, so that it has no value (same as [clear])
     */
    fun deselect()


}

/**
 * Delegate used to select a single value from potentially multiple options.
 */
class SingleSelectPropertyDelegate<BEAN : Any, T : Any> @JvmOverloads constructor(dataProvider: ListDataProvider<T>, allowNoSelection: Boolean = false) : ReadWriteProperty<BEAN, T>, Serializable, SelectPropertyDelegate {

    constructor (permittedValues: Set<T>, allowNoSelection: Boolean = false) : this(ListDataProvider(permittedValues), allowNoSelection)


    private val selector = DefaultSingleSelectProperty<T>(dataProvider = dataProvider, allowNoSelection = allowNoSelection)

    override fun configureComponent(component: Component) {

        @Suppress("UNCHECKED_CAST")
        when (component) {
            is ComboBox<*> -> {
                val c = component as ComboBox<T>
                c.setDataProvider(selector.dataProvider)
                c.isEmptySelectionAllowed = selector.allowNoSelection
            }
            is RadioButtonGroup<*> -> {
                val c = component as RadioButtonGroup<T>
                c.dataProvider = selector.dataProvider
            }
            else -> {
                throw SingleSelectException("component not supported: ${component.javaClass}")
            }
        }
    }


    override fun getValue(thisRef: BEAN, property: KProperty<*>): T {
        return selector.selected()
    }

    override fun setValue(thisRef: BEAN, property: KProperty<*>, value: T) {
        selector.select(value)
    }

}


class DefaultSingleSelectProperty<T : Any> @JvmOverloads constructor(override val dataProvider: ListDataProvider<T>, override var allowNoSelection: Boolean = false) : SingleSelectProperty<T> {

    constructor (permittedValues: Set<T> = setOf(), allowNoSelection: Boolean = false) : this(ListDataProvider(permittedValues), allowNoSelection)

    private var valueSelected = false
    private lateinit var selectedValue: T

    override fun clear() {
        deselect()
    }

    override fun select(newValue: T) {
        selectedValue = newValue
        valueSelected = true
    }

    override fun selected(): T {
        if (!valueSelected) {
            throw SingleSelectException("No value has been selected")
        }
        return selectedValue
    }

    override fun deselect() {
        if (allowNoSelection) {
            valueSelected = false
        } else {
            throw SingleSelectException("An empty selection is not allowed")
        }

    }

    override fun hasValue(): Boolean {
        return valueSelected
    }
}


class SingleSelectException(msg: String) : RuntimeException(msg)


