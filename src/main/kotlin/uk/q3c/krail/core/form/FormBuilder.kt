package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.data.Binder
import com.vaadin.data.Converter
import com.vaadin.data.Validator
import com.vaadin.ui.AbstractField
import org.apache.commons.lang3.reflect.FieldUtils
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

/**
 * Created by David Sowerby on 09 Jun 2018
 */
interface FormBuilder {
    fun selectFormTypeBuilder(configuration: FormConfiguration): FormTypeBuilder
}

interface FormTypeBuilder {
    var configuration: FormConfiguration
    fun build(): FormComponentSet
}

class SimpleFormTypeBuilder @Inject constructor(
        @field:Transient private val binderFactory: KrailBeanValidationBinderFactory,
        val propertySpecCreator: PropertySpecCreator,
        val formSupport: FormSupport) : FormTypeBuilder {

    override lateinit var configuration: FormConfiguration

    override fun build(): FormComponentSet {
        val sectionConfiguration = configuration.sectionWithName("simple")
        if (sectionConfiguration.entityClass == Any::class) {
            throw FormConfigurationException("entityClass must be specified")
        }
        val binder = binderFactory.create(sectionConfiguration.entityClass.java)
        val builder = SimpleFormSectionBuilder(sectionConfiguration.entityClass, binder, sectionConfiguration, propertySpecCreator, formSupport)
        return builder.build()
    }


}

/**
 * In this context, a property is a property of an entity class, and a component is a UI field (TextBox for example)
 */
class SimpleFormSectionBuilder<BEAN : Any>(entityClass: KClass<BEAN>, val binder: KrailBeanValidationBinder<BEAN>, val configuration: SectionConfiguration, private val propertySpecCreator: PropertySpecCreator, val formSupport: FormSupport) {

    fun build(): FormComponentSet {


        if (configuration.scanEntityClass) {
            val properties = SectionFieldScanner().scan(configuration)
            properties.forEach({ p -> propertySpecCreator.createSpec(p, configuration) })

        }

        val entityProperties = configuration.entityClass.memberProperties.toMutableList()

        for (propertySpecEntry in configuration.properties) {
            val propertySpec = propertySpecEntry.value


            val property = propertyFor(propertySpec.name)

            // if the componentClass has not been explicitly set, read from the property
            val component = if (propertySpec.componentClass == Any::class.java) {
                formSupport.componentFor(propertySpec.propertyType).get()
            } else {
                propertySpec.componentClass.newInstance()
            }
            val presentationClass = formSupport.presentationClassOf(component)

            // In core Vaadin code
            // TARGET or MODEL is the model data type
            // BEAN or SOURCE is the bean type
            // FIELDVALUE or PRESENTATION is the data type used by the component


            doBind(entityClass = configuration.entityClass, presentationClass = presentationClass, modelClass = propertySpec.propertyType, component = component, propertySpec = propertySpec)//,  presentationClass = presentationClass, modelClass = propertySpec.propertyType, component = component)//,entityClass = configuration.entityClass, presentationClass = presentationClass, modelClass = propertySpec.propertyType, binder = binder)

//            binder.forField(component).bind(property.getter, null)
        }
    }


    private fun <BEAN : Any, PRESENTATION : Any, MODEL : Any> doBind(entityClass: KClass<BEAN>, presentationClass: KClass<PRESENTATION>, modelClass: KClass<MODEL>, component: AbstractField<*>, propertySpec: PropertyConfiguration) {
        val converter: Converter<PRESENTATION, MODEL> = formSupport.converterFor(presentationClass = presentationClass, modelClass = modelClass)
        val typedComponent = component(presentationClass, component)
        val binderBuilder = binder.forField(typedComponent).withConverter(converter)

        for (validator in propertySpec.validations) {
            @Suppress("UNCHECKED_CAST")
            binderBuilder.withValidator(validator as Validator<MODEL>)
        }
        binderBuilder.bind(propertySpec.name)
    }


    private fun propertyFor(propertyName: String): KProperty<*> {
        return configuration.entityClass.memberProperties.first { p -> p.name == propertyName }
    }

    private fun <BEAN : Any> getBinderForEntity(entityClass: KClass<out BEAN>): Binder<out BEAN> {
        return Binder(entityClass.java)
    }

    /**
     * This is just to get round type checking
     */
    @Suppress("UNCHECKED_CAST")
    private fun <PRESENTATION : Any> component(c: KClass<PRESENTATION>, c1: AbstractField<*>): AbstractField<PRESENTATION> {
        return c as AbstractField<PRESENTATION>
    }
}

interface PropertySpecCreator {
    fun createSpec(property: Field, configuration: SectionConfiguration)
}

class DefaultPropertySpecCreator @Inject constructor() : PropertySpecCreator {

    override fun createSpec(property: Field, configuration: SectionConfiguration) {
        val spec = configuration.properties[property.name] ?: PropertyConfiguration(name = property.name)
        propertyType(property, spec)
//        fieldClass(property,spec, formSupport)
//        converterClass(property,spec)
        caption(property, spec)
        description(property, spec)
        validations(property, spec)


    }

    private fun propertyType(property: Field, spec: PropertyConfiguration) {
        if (spec.propertyType == Any::class.java) {
            spec.propertyType = property.type.kotlin
        }
    }

//    private fun fieldClass(property: Field, spec: PropertyConfiguration, formSupport: FormSupport) {
//        if (spec.fieldClass == AbstractField::class.java) {
//            spec.fieldClass=formSupport.fieldFor(spec.propertyType)
//        }
//    }

//    private fun converterClass(property: Field, spec: PropertyConfiguration) {
//        if (spec.converterClass == Converter::class.java) {
//            formSupport.converterFor()
//        }
//    }

    private fun caption(property: Field, spec: PropertyConfiguration) {
        if (spec.caption == LabelKey.Unnamed) {
            TODO()
        }
    }

    private fun description(property: Field, spec: PropertyConfiguration) {
        if (spec.description == DescriptionKey.No_description_provided) {
            TODO()
        }
    }

    private fun validations(property: Field, spec: PropertyConfiguration) {
        TODO()
    }
}


class SectionFieldScanner {
    fun scan(configuration: SectionConfiguration): List<Field> {
        val fieldList = FieldUtils.getAllFieldsList(configuration.entityClass.java)
        return fieldList.filter({ f -> configuration.excludedProperties.contains(f.name) })
    }
}

class DefaultFormBuilder @Inject constructor(private val formTypeBuilders: MutableMap<String, Provider<FormTypeBuilder>>) : FormBuilder {
    override fun selectFormTypeBuilder(configuration: FormConfiguration): FormTypeBuilder {
        val typeName = configuration.formType
        val adjustedTypeName =
                when (typeName) {
                    "unnamed", "simple", "" -> "simple"
                    else -> {
                        typeName
                    }
                }

        val provider = formTypeBuilders[adjustedTypeName]
        if (provider != null) {
            val builder = provider.get()
            builder.configuration = configuration
            return builder
        } else {
            throw FormConfigurationException("unrecognised FormTypeBuilder '$typeName'")
        }
    }
}


