package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.data.Converter
import com.vaadin.data.HasValue
import net.jodah.typetools.TypeResolver
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 09 Jun 2018
 */
interface FormSupport {
    /**
     * The presentation class may not return with the same type as [modelClass] - for example an Int may be presented in a [TextField].
     * The class actually used by the component can be retrieved by [presentationValueClassOf]
     */
    fun componentFor(modelClass: KClass<*>): Provider<HasValue<*>>

    fun <PRESENTATIONVALUE : Any, MODEL : Any> converterFor(modelClass: KClass<out MODEL>, presentationValueClass: KClass<out PRESENTATIONVALUE>): Converter<PRESENTATIONVALUE, MODEL>
    fun <PRESENTATIONVALUE : Any, PRESENTATION : HasValue<PRESENTATIONVALUE>, MODEL : Any> converterForComponent(modelClass: KClass<out MODEL>, componentClass: KClass<PRESENTATION>): Converter<PRESENTATIONVALUE, MODEL>
}

/**
 * [dataClassToPresentationMap] is provided by one or more Guice modules (see [FormModule])
 * [rendererFactory] is bound in [FormModule] and can be extended with additional [RendererSet] implementations
 * [converterFactory] is bound in [ConverterModule] and can be extended with additional [ConverterSet] implementations
 */
class DefaultFormSupport @Inject constructor(
        private val dataClassToPresentationMap: MutableMap<Class<*>, Provider<HasValue<*>>>,
        private val converterFactory: ConverterFactory) :

        FormSupport {


    /**
     * We check for Java primitive types first, wrapper types should not normally be needed
     */
    override fun componentFor(modelClass: KClass<*>): Provider<HasValue<*>> {
        return dataClassToPresentationMap.getOrElse(modelClass.java) { throw DataTypeException(modelClass) }
    }


    @Suppress("UNCHECKED_CAST")
    override fun <PRESENTATIONVALUE : Any, MODEL : Any> converterFor(modelClass: KClass<out MODEL>, presentationValueClass: KClass<out PRESENTATIONVALUE>): Converter<PRESENTATIONVALUE, MODEL> {
        return converterFactory.get(presentationValueClass, modelClass)

    }

    @Suppress("UNCHECKED_CAST")
    override fun <PRESENTATIONVALUE : Any, PRESENTATION : HasValue<PRESENTATIONVALUE>, MODEL : Any> converterForComponent(modelClass: KClass<out MODEL>, componentClass: KClass<PRESENTATION>): Converter<PRESENTATIONVALUE, MODEL> {
        val presentationValueClass: KClass<PRESENTATIONVALUE> = TypeResolver.resolveRawArgument(HasValue::class.java, componentClass.java).kotlin as KClass<PRESENTATIONVALUE>
        return converterFactory.get(presentationValueClass, modelClass)
    }

//    @Suppress("UNCHECKED_CAST")
//    override   fun <PRESENTATIONVALUE:Any, PRESENTATION : HasValue<PRESENTATIONVALUE>> presentationValueClassOf(presentationComponent:  PRESENTATION): KClass<PRESENTATIONVALUE> {
//        val presentationJavaClass = presentationComponent.javaClass
//        val presentationValueClass : KClass<PRESENTATIONVALUE> = TypeResolver.resolveRawArgument(HasValue::class.java, presentationJavaClass).kotlin as KClass<PRESENTATIONVALUE>
//        return presentationValueClass
//    }

}

class DataTypeException(clazz: KClass<*>) : RuntimeException("No presentation class has been defined for $clazz")