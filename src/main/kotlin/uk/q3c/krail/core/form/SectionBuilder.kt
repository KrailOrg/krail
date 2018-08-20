package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.vaadin.data.Converter
import com.vaadin.data.HasValue
import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.AbstractOrderedLayout
import com.vaadin.ui.Component
import com.vaadin.ui.Grid
import net.jodah.typetools.TypeResolver
import org.apache.commons.lang3.reflect.FieldUtils
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.NullI18NKey
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.util.I18NKeyFromSample
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
class StandardFormSectionBuilder<BEAN : Entity>(
        val entityClass: KClass<BEAN>,
        val binderFactory: KrailBeanValidationBinderFactory,
        val configuration: FormSectionConfiguration,
        private val currentLocale: CurrentLocale,
        private val propertySpecCreator: PropertyConfigurationCreator,
        val formSupport: FormSupport,
        val userNotifier: UserNotifier) {

    var binder: KrailBeanValidationBinder<BEAN> = binderFactory.create(entityClass)

    @Suppress("UNCHECKED_CAST")
    fun buildTable(form: Form, formDaoFactory: FormDaoFactory, translate: Translate): FormSection {
        PropertyConfigurationBuilder().build(configuration, propertySpecCreator)
//        val propertySet: PropertySet<BEAN> = BeanPropertySet.get(configuration.entityClass.java, false, PropertyFilterDefinition(1,listOf() )) as PropertySet<BEAN>
//        val grid = Grid<BEAN>(propertySet) this constructor is protected - could sub-class
        val grid = Grid(configuration.entityClass) as Grid<BEAN>
        val formDao: FormDao<BEAN> = formDaoFactory.getDao(configuration.entityClass.kotlin) as FormDao<BEAN>
        val items = formDao.get()
        grid.setItems(items)
        grid.locale = currentLocale.locale
        grid.setSelectionMode(Grid.SelectionMode.SINGLE)
        grid.setColumnOrder(*(configuration.columnOrder.toTypedArray()))

        // hide any columns not listed in columnOrder
        grid.columns.forEach { c ->
            if (!configuration.columnOrder.contains(c.id)) {
                c.isHidden = true
            }
        }

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


    fun buildDetail(formDaoFactory: FormDaoFactory, translate: Translate, editSaveCancelBuilder: EditSaveCancelBuilder): FormDetailSection<BEAN> {
        val componentMap: MutableMap<String, DetailPropertyInfo> = mutableMapOf()

        PropertyConfigurationBuilder().build(configuration, propertySpecCreator)
        val dao = formDaoFactory.getDao(entityClass = entityClass)
        for (propertySpecEntry in configuration.properties) {
            val propertySpec = propertySpecEntry.value
            propertySpec.merge()
            val component = formSupport.componentFor(propertySpec)
            if (component is AbstractComponent) {
                component.styleName = propertySpec.styleAttributes.combinedStyle()
                componentMap[propertySpec.name] = DetailPropertyInfo(component = component, captionKey = propertySpec.caption, descriptionKey = propertySpec.description)
            }
            // we have a component but we need to know the type of data it requires so we can select the right converter
            val presentationValueClass = when (propertySpec.fieldType) {
                FieldType.STANDARD -> TypeResolver.resolveRawArgument(HasValue::class.java, component.javaClass).kotlin
                FieldType.SINGLE_SELECT -> propertySpec.propertyValueClass.kotlin
                FieldType.MULTI_SELECT -> Set::class

            }
            doBind(propertySpec.propertyValueClass.kotlin, presentationValueClass, component, propertySpec, translate)

        }
        val layout = configuration.layout.newInstance()

        // We need to keep EditSaveCancel component references so that the buttons can be re-translated in the event
        // of a language change
        val escList: MutableList<EditSaveCancel> = mutableListOf()
        // if there is a top aligned EditSaveCancel add it here
        if (editSaveCancelBuilder.hasTopComponent()) {
            addEsc(layout, editSaveCancelBuilder.topComponent(), escList)
        }

        // add fields for properties to the layout in the order specified by configuration.fieldOrder, unless it is empty,
        // in which case we use component map - this will return fields in the order Java returned fields by reflection,
        // and is therefore not predictable
        if (configuration.fieldOrder.isEmpty()) {
            configuration.fieldOrder = componentMap.keys
        }
        configuration.fieldOrder.forEach { p ->
            val entry = componentMap[p]
            if (entry != null) {
                layout.addComponent(entry.component)
            }
        }

        // if there is a bottom aligned EditSaveCancel add it here
        if (editSaveCancelBuilder.hasBottomComponent()) {
            addEsc(layout, editSaveCancelBuilder.bottomComponent(), escList)
        }

        return FormDetailSection(componentMap, layout, binder, dao, escList, userNotifier)


    }

    private fun addEsc(layout: AbstractOrderedLayout, esc: EditSaveCancel, escList: MutableList<EditSaveCancel>) {
        layout.addComponent(esc)
        escList.add(esc)
    }


    @Suppress("UNCHECKED_CAST")
    private fun <MODEL : Any, PRESENTATIONVALUE : Any, PRESENTATION : HasValue<PRESENTATIONVALUE>> doBind(@Suppress("UNUSED_PARAMETER") modelClass: KClass<MODEL>, presentationValueClass: KClass<PRESENTATIONVALUE>, component: HasValue<*>, propertySpec: PropertyConfiguration, translate: Translate) {
        val typedComponent: PRESENTATION = component as PRESENTATION
        val binderBuilder = binder.forField(typedComponent)

        val converter: Converter<PRESENTATIONVALUE, MODEL> = formSupport.converterFor(presentationValueClass = presentationValueClass, modelClass = propertySpec.propertyValueClass.kotlin as KClass<MODEL>)

        val binderBuilderWithConverter = binderBuilder.withConverter(converter)
        propertySpec.validators.forEach { v ->
            val validator = v as KrailValidator<in MODEL>
            validator.translate = translate
            binderBuilderWithConverter.withValidator(validator)
        }

        binderBuilderWithConverter.bind(propertySpec.name)
    }
}


data class DetailPropertyInfo(val captionKey: I18NKey, val descriptionKey: I18NKey, val component: Component) : Serializable


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
    fun createConfiguration(property: Field, sectionConfiguration: FormSectionConfiguration)
}

class DefaultPropertyConfigurationCreator @Inject constructor() : PropertyConfigurationCreator {

    override fun createConfiguration(property: Field, sectionConfiguration: FormSectionConfiguration) {
        if (property.name == "\$\$delegatedProperties") {
            return
        }
        val delegateId = "\$delegate"
        val refinedName = property.name.removeSuffix(delegateId)


        val spec = sectionConfiguration.properties[refinedName]
                ?: PropertyConfiguration(name = refinedName, parentConfiguration = sectionConfiguration)
        sectionConfiguration.properties[refinedName] = spec
        propertyType(property, spec)

//        fieldClass(property,spec, formSupport)
//        converterClass(property,spec)
        caption(property, spec, sectionConfiguration.sampleCaptionKey)
        description(property, spec, sectionConfiguration.sampleDescriptionKey)
        validations(property, spec)


    }

    private fun propertyType(property: Field, spec: PropertyConfiguration) {
        if (spec.propertyValueClass == Any::class.java) {
            spec.propertyValueClass = property.type
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

    /**
     * A key is derived from the following sequence
     *
     *  - If a key has been explicitly set in the [spec], use that
     *  - If no key has been explicitly set in the [spec], but there is a genuine [sampleCaptionKey], then try getting a key from that sample by an Enum.valueOf(property.name)
     *  - If no key has been found, use the default value, [LabelKey.Unnamed]
     */

    private fun caption(property: Field, spec: PropertyConfiguration, sampleCaptionKey: I18NKey) {
        if (spec.caption == LabelKey.Unnamed) {
            if (sampleCaptionKey != NullI18NKey.none) {
                try {
                    val key = I18NKeyFromSample().keyFromName(property.name.removeSuffix("\$delegate"), sampleCaptionKey)
                    spec.caption = key
                } catch (e: Exception) {
                    // do nothing, if conversion fails just stick with LabelKey.Unnamed
                }

            }
        }
    }

    /**
     * Same process as [caption]
     */
    private fun description(property: Field, spec: PropertyConfiguration, sampleDescriptionKey: I18NKey) {
        if (spec.description == DescriptionKey.No_description_provided) {
            if (sampleDescriptionKey != NullI18NKey.none) {
                try {
                    val key = I18NKeyFromSample().keyFromName(property.name.removeSuffix("\$delegate"), sampleDescriptionKey)
                    spec.description = key
                } catch (e: Exception) {
                    // do nothing, if conversion fails just stick with LabelKey.Unnamed
                }

            }
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
                        try {
                            val field: Field? = FieldUtils.getDeclaredField(entityClass, p, true)
                            if (field == null) {
                                val f2: Field = FieldUtils.getDeclaredField(entityClass, "$p\$delegate", true)
                                listOfFields.add(f2)
                            } else {
                                listOfFields.add(field)
                            }
                        } catch (e: Exception) {
                            throw FormConfigurationException("Property $p does not exist in $entityClass, but has been declared in fieldOrder")
                        }
                    }
            listOfFields
        }
    }
}