package uk.q3c.krail.core.form

import com.vaadin.ui.CheckBoxGroup
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
interface SingleSelectProperty<T> : Serializable {

    /**
     * The values which could be selected
     */
    val permittedValues: Set<T>

    /**
     * Allow an 'empty' selection
     */
    var allowNoSelection: Boolean

    /**
     * select a value
     *
     * @throws SingleSelectionException if the [newValue] is not in [permittedValues]
     */
    fun select(newValue: T)

    /**
     * Returns the currently selected value
     *
     * @throws SingleSelectionException is no selection has been made (use [hasValue] to avoid exception
     */
    fun selected(): T

    /**
     * 'Clear' the selection, so that it has no value
     */
    fun deselect()

    /**
     * Returns true if a valid selection has been made
     */
    fun hasValue(): Boolean
}

/**
 * A delegate used to select/lookup values form a list / range to assign to a property.  Component is usually a [ComboBox], [RadioButtonGroup], [CheckBoxGroup] or similar
 */
interface SelectPropertyDelegate {
    fun configureComponent(component: Component)
}


/**
 * Delegate used to select a single value from potentially multiple options.
 */
class SingleSelectPropertyDelegate<BEAN : Any, T : Any>(permittedValues: Set<T>, allowNoSelection: Boolean = false) : ReadWriteProperty<BEAN, T>, Serializable, SelectPropertyDelegate {

    override fun configureComponent(component: Component) {
        if (component is ComboBox<*>) {
            @Suppress("UNCHECKED_CAST")
            val c = component as ComboBox<T>
            c.setItems(selector.permittedValues)
            c.isEmptySelectionAllowed = selector.allowNoSelection
        } else {
            throw SingleSelectionException("component not supported")
        }
    }

    private val selector = DefaultSingleSelectProperty<T>(permittedValues = permittedValues, allowNoSelection = allowNoSelection)

    override fun getValue(thisRef: BEAN, property: KProperty<*>): T {
        return selector.selected()
    }

    override fun setValue(thisRef: BEAN, property: KProperty<*>, value: T) {
        selector.select(value)
    }

}


class DefaultSingleSelectProperty<T : Any>(override val permittedValues: Set<T> = setOf(), override var allowNoSelection: Boolean = false) : SingleSelectProperty<T> {

    private var valueSelected = false

    private lateinit var selectedValue: T

    override fun select(newValue: T) {
        if (permittedValues.contains(newValue)) {
            selectedValue = newValue
            valueSelected = true
        } else {
            throw SingleSelectionException("$newValue is not a valid selection")
        }
    }


    override fun selected(): T {
        if (!valueSelected) {
            throw SingleSelectionException("No value has been selected")
        }
        return selectedValue
    }

    override fun deselect() {
        if (allowNoSelection) {
            valueSelected = false
        } else {
            throw SingleSelectionException("An empty selection is not allowed")
        }

    }

    override fun hasValue(): Boolean {
        return valueSelected
    }
}


class SingleSelectionException(msg: String) : RuntimeException(msg)

class KrailComboBox<T : Any>(val property: SingleSelectProperty<T>) : ComboBox<T>() {
    init {
        setItems(property.permittedValues)
    }


}