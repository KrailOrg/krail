package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.vaadin.data.Converter
import com.vaadin.data.HasValue
import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.Component
import com.vaadin.ui.Grid
import net.jodah.typetools.TypeResolver
import org.apache.commons.lang3.reflect.FieldUtils
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import java.io.Serializable
import java.lang.reflect.Field
import kotlin.reflect.KClass


/**
 * In this context, a property is a property of an entity class, and a component is a UI field (TextField for example)
 *
 * The values returned from [buildDetail] and [buildTable] give access to the components created by this build process,
 * so that they can be modified if required.
 *
 */
class StandardFormSectionBuilder<BEAN : Any>(
        val entityClass: KClass<BEAN>,
        val binderFactory: KrailBeanValidationBinderFactory,
        val configuration: FormSectionConfiguration,
        private val currentLocale: CurrentLocale,
        private val propertySpecCreator: PropertyConfigurationCreator,
        val formSupport: FormSupport) {

    var binder: KrailBeanValidationBinder<BEAN> = binderFactory.create(entityClass)

    @Suppress("UNCHECKED_CAST")
    fun buildTable(form: Form, formDaoFactory: FormDaoFactory, translate: Translate): FormSection {
        PropertyConfigurationBuilder().build(configuration, propertySpecCreator)
//        val propertySet: PropertySet<BEAN> = BeanPropertySet.get(configuration.entityClass.java, false, PropertyFilterDefinition(1,listOf() )) as PropertySet<BEAN>
//        val grid = Grid<BEAN>(propertySet) this constructor is protected - could sub-class
        val grid = Grid(configuration.entityClass) as Grid<BEAN>
        grid.locale = currentLocale.locale
        grid.setSelectionMode(Grid.SelectionMode.SINGLE)
        grid.setColumnOrder(*(configuration.columnOrder.toTypedArray()))

        // hide any columns not listed in columnOrder
        grid.columns.forEach { c ->
            if (!configuration.columnOrder.contains(c.id)) {
                c.isHidden = true
            }
        }
        val formDao: FormDao<BEAN> = formDaoFactory.getDao(configuration.entityClass.kotlin) as FormDao<BEAN>
        configuration.columnOrder.forEach { c -> grid.getColumn(c).isHidden = false }


        // set the captions from the config
        configuration.properties.forEach { (k, v) ->
            if (v.caption != LabelKey.Unnamed) {
                grid.getColumn(k).caption = translate.from(v.caption)
            }
        }
        val fts = FormTableSection(form, grid, formDao)
        grid.addSelectionListener(fts)
        return fts
    }

//    @Suppress("UNCHECKED_CAST")
//    private fun <G : Grid<BEAN>> buildTableColumns(grid: G) {
//        for (propertySpecEntry in configuration.properties) {
//            val propertyConfig = propertySpecEntry.value
//
//            // we use the specified renderer if there is one, otherwise get the default from FormSupport
//            val renderer: AbstractRenderer<in Any, out Any> = if (propertyConfig.columnRendererClass == AbstractRenderer::class) {
//                formSupport.rendererFor(propertyConfig.propertyValueClass.kotlin, grid as Grid<Any>)
//            } else {
//                propertyConfig.columnRendererClass.newInstance() as AbstractRenderer<in Any, out Any> // TODO how do we set the locale for this - Renderer does not surface locale
//            }
//
//            val column: Grid.Column<BEAN, *> = grid.addColumn(propertyConfig.name, renderer)
//
//        }
//    }


    fun buildDetail(formDaoFactory: FormDaoFactory, translate: Translate): FormDetailSection<BEAN> {
        val componentMap: MutableMap<String, DetailPropertyInfo> = mutableMapOf()

        PropertyConfigurationBuilder().build(configuration, propertySpecCreator)

        for (propertySpecEntry in configuration.properties) {
            val propertySpec = propertySpecEntry.value
            propertySpec.merge()

            // if the componentClass has not been explicitly set, read from the property
            val component: HasValue<*> = if (propertySpec.componentClass == HasValue::class.java) {
                formSupport.componentFor(propertySpec.propertyValueClass.kotlin).get()
            } else {
                propertySpec.componentClass.newInstance()
            }
            if (component is AbstractComponent) {
                component.styleName = propertySpec.styleAttributes.combinedStyle()
                componentMap[propertySpec.name] = DetailPropertyInfo(component = component, captionKey = propertySpec.caption, descriptionKey = propertySpec.description, isDelegate = propertySpec.isDelegate)
            }
            // we have a component but we need to know the type of data it requires so we can select the right converter
            val presentationValueClass = TypeResolver.resolveRawArgument(HasValue::class.java, component.javaClass).kotlin
            doBind(propertySpec.propertyValueClass.kotlin, presentationValueClass, component, propertySpec, translate)

        }
        val layout = configuration.layout.newInstance()
        componentMap.forEach { entry -> layout.addComponent(entry.value.component) }
        return FormDetailSection(componentMap, layout, binder, formDaoFactory.getDao(entityClass = entityClass))


    }


    @Suppress("UNCHECKED_CAST")
    private fun <MODEL : Any, PRESENTATIONVALUE : Any, PRESENTATION : HasValue<PRESENTATIONVALUE>> doBind(@Suppress("UNUSED_PARAMETER") modelClass: KClass<MODEL>, presentationValueClass: KClass<PRESENTATIONVALUE>, component: HasValue<*>, propertySpec: PropertyConfiguration, translate: Translate) {
        val typedComponent: PRESENTATION = component as PRESENTATION
        val binderBuilder = binder.forField(typedComponent)

        if (SelectPropertyDelegate::class.java.isAssignableFrom(modelClass.java)) {
            binderBuilder.bind(propertySpec.name)
        } else {
            val converter: Converter<PRESENTATIONVALUE, MODEL> = formSupport.converterFor(presentationValueClass = presentationValueClass, modelClass = propertySpec.propertyValueClass.kotlin as KClass<MODEL>)
            val binderBuilderWithConverter = binderBuilder.withConverter(converter)
            propertySpec.validations.forEach { v ->
                val validator = v as KrailValidator<in MODEL>
                validator.translate = translate
                binderBuilderWithConverter.withValidator(validator)
            }

            binderBuilderWithConverter.bind(propertySpec.name)
        }
    }
}


data class DetailPropertyInfo(val captionKey: I18NKey, val descriptionKey: I18NKey, val component: Component, val isDelegate: Boolean) : Serializable


/**
 * In order to automatically create components that look reasonable we need a way to estimate a good width for them
 * This also applies to column widths
 */
interface ComponentWidthCalculator {

    /**
     * Returns the estimated width in *em* for a component of type [componentClass] when displaying a String of [size] characters
     */
    fun widthForStringSize(size: Int = 1, componentClass: KClass<Any>): Int

    /**
     * Returns the estimated width in *em* for a component of type [componentClass] when displaying a number with [maxValue]
     */
    fun widthForInteger(maxValue: Int = 1, componentClass: KClass<Any>): Int

    fun widthForInteger(maxValue: Long = 1, componentClass: KClass<Any>): Int

    /**
     * Returns the estimated width in *em* for a component of type [componentClass] when displaying a number with [maxValue]
     * and [decimalPlaces]
     */
    fun widthForDecimal(maxValue: Int = 1, decimalPlaces: Int = 2, componentClass: KClass<Any>): Int

}

interface PropertyConfigurationCreator {
    fun createConfiguration(property: Field, configuration: FormSectionConfiguration)
}

class DefaultPropertyConfigurationCreator @Inject constructor() : PropertyConfigurationCreator {

    override fun createConfiguration(property: Field, configuration: FormSectionConfiguration) {
        if (property.name == "\$\$delegatedProperties") {
            return
        }
        val delegateId = "\$delegate"
        val refinedName = property.name.removeSuffix(delegateId)


        val spec = configuration.properties[refinedName]
                ?: PropertyConfiguration(name = refinedName, parentConfiguration = configuration)
        configuration.properties[property.name] = spec
        propertyType(property, spec)
//        fieldClass(property,spec, formSupport)
//        converterClass(property,spec)
        caption(property, spec)
        description(property, spec)
        validations(property, spec)


    }

    private fun propertyType(property: Field, spec: PropertyConfiguration) {
        if (spec.propertyValueClass == Any::class.java) {
            spec.propertyValueClass = property.type
        }
        val delegateId = "\$delegate"
        spec.isDelegate = property.name.contains(delegateId)

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

/**
 * Utility class to provide a list of Java Field instances from the entity class.  These are needed to get the field name and type
 *
 */
class PropertyConfigurationBuilder {

    /**
     * Builds a set of [PropertyConfiguration]s for use with both the table and detail sections.  These are attached directly
     * to the SectionConfiguration
     *
     * */


    fun build(configuration: FormSectionConfiguration, propertyConfigurationCreator: PropertyConfigurationCreator) {

        val properties: List<Field> = buildFieldList(configuration)
        properties.forEach { p -> propertyConfigurationCreator.createConfiguration(p, configuration) }

    }


    /**
     * If [FormSectionConfiguration.fieldOrder] has not been specified, scans the entity class for fields and uses them.
     *
     * In either case, [FormSectionConfiguration.excludedProperties] are removed from the returned list
     */
    private fun buildFieldList(configuration: FormSectionConfiguration): List<Field> {
        return if (configuration.fieldOrder.isEmpty()) {
            FieldUtils.getAllFieldsList(configuration.entityClass).filter { f -> !(configuration.excludedProperties.contains(f.name)) }
        } else {
            val listOfFields: MutableList<Field> = mutableListOf()
            val entityClass = configuration.entityClass
            configuration.fieldOrder
                    .filter { p -> !(configuration.excludedProperties.contains(p)) }
                    .forEach { p ->
                        val field: Field = FieldUtils.getDeclaredField(entityClass, p, true)
                        listOfFields.add(field)
                    }
            listOfFields
        }
    }
}