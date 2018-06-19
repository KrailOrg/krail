package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.data.Converter
import com.vaadin.ui.AbstractField
import net.jodah.typetools.TypeResolver
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 09 Jun 2018
 */
interface FormSupport {
    /**
     * The presentation class may not return with the same type as [dataClass] - for example an Int may be presented in a [TextField].
     * The class actually used by the component can be retrieved by [presentationClassOf]
     */
    fun componentFor(dataClass: KClass<*>): Provider<AbstractField<*>>

    fun <PRESENTATION : Any, MODEL : Any> converterFor(modelClass: KClass<MODEL>, presentationClass: KClass<PRESENTATION>): Converter<PRESENTATION, MODEL>
    fun presentationClassOf(field: AbstractField<*>): KClass<*>
}

/**
 * [dataClassToFieldMap] is provided by one or more Guice modules (see [FormModule])
 * [converterFactory] is provided by the [ConverterModule] and can be extended with additional [ConverterSet] implementations
 */
class DefaultFormSupport @Inject constructor(
        private val dataClassToFieldMap: MutableMap<Class<*>, Provider<AbstractField<*>>>,
        private val converterFactory: ConverterFactory) :

        FormSupport {

    override fun componentFor(dataClass: KClass<*>): Provider<AbstractField<*>> {
        return dataClassToFieldMap.getOrElse(dataClass.java, { throw DataTypeException(dataClass) })
    }


    override fun <PRESENTATION : Any, MODEL : Any> converterFor(modelClass: KClass<MODEL>, presentationClass: KClass<PRESENTATION>): Converter<PRESENTATION, MODEL> {
        return converterFactory.get(presentationClass.java, modelClass.java)
    }


    override fun presentationClassOf(field: AbstractField<*>): KClass<*> {
        return TypeResolver.resolveRawArgument(AbstractField::class.java, field.javaClass).kotlin
    }

}

class DataTypeException(clazz: KClass<*>) : RuntimeException("No presentation class has been defined for $clazz")