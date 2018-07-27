package uk.q3c.krail.core.form

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.CheckBoxGroup
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Component
import com.vaadin.ui.RadioButtonGroup
import java.io.Serializable

/**
 * Common interface for properties which use value selection, either single or multi
 *
 * Created by David Sowerby on 26 Jul 2018
 */
interface SelectProperty<T : Any> : Serializable {

    /**
     * The values which could be selected
     */
    val dataProvider: ListDataProvider<T>

    /**
     * Allow an 'empty' selection
     */
    var allowNoSelection: Boolean

    /**
     * Select a value.  **NOTE** This does NOT check that the value is valid (to do so could be very inefficient) - it is assumed that the value has come from
     * a validated source, usually a ComboBox or similar UI component.
     *
     */
    fun select(newValue: T)

    /**
     * Clears all selections, whether single or multiple
     */
    fun clear()

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