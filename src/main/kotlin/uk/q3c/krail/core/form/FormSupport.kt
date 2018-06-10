package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.data.Converter
import com.vaadin.ui.AbstractField
import net.jodah.typetools.TypeResolver

/**
 * Created by David Sowerby on 09 Jun 2018
 */
interface FormSupport {
    fun fieldFor(dataClass: Class<*>): Provider<AbstractField<*>>
    fun converterFor(modelClass: Class<*>, field: AbstractField<*>): Converter<Any, Any>
}

class DefaultFormSupport @Inject constructor(
        private val dataClassToFieldMap: MutableMap<Class<*>, Provider<AbstractField<*>>>,
        private val converterFactory: ConverterFactory) :

        FormSupport {


    override fun fieldFor(dataClass: Class<*>): Provider<AbstractField<*>> {
        return dataClassToFieldMap.getOrElse(dataClass, { throw DataTypeException(dataClass) })
    }

    override fun converterFor(modelClass: Class<*>, field: AbstractField<*>): Converter<Any, Any> {
        val presentationClass = TypeResolver.resolveRawArgument(AbstractField::class.java, field.javaClass)
        return converterFactory.get(presentationClass, modelClass)
    }

}

class DataTypeException(clazz: Class<*>) : RuntimeException("No presentation class has been defined for $clazz")