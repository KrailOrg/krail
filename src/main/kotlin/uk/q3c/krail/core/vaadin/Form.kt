package uk.q3c.krail.core.vaadin

import com.vaadin.data.HasValue
import com.vaadin.ui.AbstractField
import org.apache.commons.beanutils.Converter

/**
 * Binds a bean property with a UI field.  When [uiField] is set
 *
 * Created by David Sowerby on 13 Oct 2017
 *
 * @param P value type used by presentation (the UI Field)
 * @param M value type used by the bean property
 */
class PropertyBinding<P, M>(val uiField: AbstractField<P>, val bean: Any, val propertyName: String, val valueType: Class<out M>) : HasValue.ValueChangeListener<P> {

    lateinit var converter: Converter

    init {
        uiField.addValueChangeListener(this)
    }

    override fun valueChange(event: HasValue.ValueChangeEvent<P>) {
    }
}