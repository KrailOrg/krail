package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.data.Converter
import com.vaadin.data.HasValue
import com.vaadin.ui.Component
import net.jodah.typetools.TypeResolver
import org.apache.commons.lang3.reflect.FieldUtils
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import java.io.Serializable
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Created by David Sowerby on 09 Jun 2018
 */
interface FormTypeSelector {
    fun selectFormTypeBuilder(configuration: FormConfiguration): FormBuilder
}

interface FormBuilder {
    var configuration: FormConfiguration
    fun build(): FormSection<*>
}

class SimpleFormBuilder @Inject constructor(
        @field:Transient private val binderFactory: KrailBeanValidationBinderFactory,
        private val propertySpecCreator: PropertySpecCreator,
        private val formSupport: FormSupport,
        private val formDaoFactory: FormDaoFactory, val translate: Translate) : FormBuilder {

    override lateinit var configuration: FormConfiguration

    override fun build(): FormSection<*> {

        val sectionConfiguration = configuration.sectionWithName("simple")
        if (sectionConfiguration.entityClass == Any::class) {
            throw FormConfigurationException("entityClass must be specified")
        }
        val sectionBuilder = SimpleFormSectionBuilder(entityClass = sectionConfiguration.entityClass, binderFactory = binderFactory, propertySpecCreator = propertySpecCreator, formSupport = formSupport, configuration = sectionConfiguration)
        return sectionBuilder.buildDetail(formDaoFactory, translate)
    }
}


/**
 * In this context, a property is a property of an entity class, and a component is a UI field (TextField for example)
 */
class SimpleFormSectionBuilder<BEAN : Any>(val entityClass: KClass<BEAN>, val binderFactory: KrailBeanValidationBinderFactory, val configuration: FormSectionConfiguration, private val propertySpecCreator: PropertySpecCreator, val formSupport: FormSupport) {

    val binder = binderFactory.create(entityClass)
    fun buildDetail(formDaoFactory: FormDaoFactory, translate: Translate): FormSection<BEAN> {
        val componentMap: MutableMap<String, PropertyInfo> = mutableMapOf()

        if (configuration.scanEntityClass) {
            val properties = SectionFieldListBuilder().build(configuration)
            properties.forEach { p -> propertySpecCreator.createSpec(p, configuration) }

        }

        for (propertySpecEntry in configuration.properties) {
            val propertySpec = propertySpecEntry.value
            propertySpec.merge()

            // if the componentClass has not been explicitly set, read from the property
            val component: HasValue<*> = if (propertySpec.componentClass == HasValue::class.java) {
                formSupport.componentFor(propertySpec.propertyValueClass).get()
            } else {
                propertySpec.componentClass.newInstance()
            }
            if (component is Component) {
                component.styleName = propertySpec.styleAttributes.combinedStyle()
                componentMap[propertySpec.name] = PropertyInfo(component = component, captionKey = propertySpec.caption, descriptionKey = propertySpec.description)
            }
            // we have a component but we need to know the type of data it requires so we can select the right converter
            val presentationValueClass = TypeResolver.resolveRawArgument(HasValue::class.java, component.javaClass).kotlin
            doBind(propertySpec.propertyValueClass, presentationValueClass, component, propertySpec, translate)

        }
        val layout = configuration.layout.createInstance()
        componentMap.forEach { entry -> layout.addComponent(entry.value.component) }
        return FormSection(componentMap, layout, binder, formDaoFactory.getDao(entityClass = entityClass))


    }


    @Suppress("UNCHECKED_CAST")
    private fun <MODEL : Any, PRESENTATIONVALUE : Any, PRESENTATION : HasValue<PRESENTATIONVALUE>> doBind(modelClass: KClass<MODEL>, presentationValueClass: KClass<PRESENTATIONVALUE>, component: HasValue<*>, propertySpec: PropertyConfiguration, translate: Translate) {
        val typedComponent: PRESENTATION = component as PRESENTATION
        val binderBuilder = binder.forField(typedComponent)
        val converter: Converter<PRESENTATIONVALUE, MODEL> = formSupport.converterFor(presentationValueClass = presentationValueClass, modelClass = propertySpec.propertyValueClass as KClass<MODEL>)
        val binderBuilderWithConverter = binderBuilder.withConverter(converter)
        propertySpec.validations.forEach { v ->
            val validator = v as KrailValidator<in MODEL>
            validator.translate = translate
            binderBuilderWithConverter.withValidator(validator)
        }

        binderBuilderWithConverter.bind(propertySpec.name)
    }
}


data class PropertyInfo(val captionKey: I18NKey, val descriptionKey: I18NKey, val component: Component) : Serializable


interface PropertySpecCreator {
    fun createSpec(property: Field, configuration: FormSectionConfiguration)
}

class DefaultPropertySpecCreator @Inject constructor() : PropertySpecCreator {

    override fun createSpec(property: Field, configuration: FormSectionConfiguration) {
        val spec = configuration.properties[property.name]
                ?: PropertyConfiguration(name = property.name, parentConfiguration = configuration)
        configuration.properties[property.name] = spec
        propertyType(property, spec)
//        fieldClass(property,spec, formSupport)
//        converterClass(property,spec)
        caption(property, spec)
        description(property, spec)
        validations(property, spec)


    }

    private fun propertyType(property: Field, spec: PropertyConfiguration) {
        if (spec.propertyValueClass == Any::class) {
            spec.propertyValueClass = property.type.kotlin
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
            // TODO use a sample key combined with property name
        }
    }

    private fun description(property: Field, spec: PropertyConfiguration) {
        if (spec.description == DescriptionKey.No_description_provided) {
            // TODO use a sample key combined with property name
        }
    }

    private fun validations(property: Field, spec: PropertyConfiguration) {
        // TODO
    }
}


class SectionFieldListBuilder {
    fun build(configuration: FormSectionConfiguration): List<Field> {
        if (configuration.displayOrder.isEmpty()) {
            return FieldUtils.getAllFieldsList(configuration.entityClass.java).filter { f -> !(configuration.excludedProperties.contains(f.name)) }
        } else {
            val fieldList: MutableList<Field> = mutableListOf()
            val entityClass = configuration.entityClass
            configuration.displayOrder
                    .filter { p -> !(configuration.excludedProperties.contains(p)) }
                    .forEach { p ->
                        val field: Field = FieldUtils.getDeclaredField(entityClass.java, p, true)
                        fieldList.add(field)
                    }
            return fieldList
        }
    }
}

class DefaultFormTypeSelector @Inject constructor(private val formTypeBuilders: MutableMap<String, Provider<FormBuilder>>) : FormTypeSelector {
    override fun selectFormTypeBuilder(configuration: FormConfiguration): FormBuilder {
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


