package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.data.Converter
import com.vaadin.data.HasValue
import com.vaadin.data.provider.DataProvider
import com.vaadin.ui.AbstractMultiSelect
import com.vaadin.ui.CheckBoxGroup
import com.vaadin.ui.ComboBox
import com.vaadin.ui.ListSelect
import com.vaadin.ui.RadioButtonGroup
import com.vaadin.ui.SingleSelect
import com.vaadin.ui.TwinColSelect
import net.jodah.typetools.TypeResolver
import uk.q3c.krail.core.ConfigurationException
import uk.q3c.krail.core.form.FieldType.*
import uk.q3c.util.guice.InjectorLocator
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 09 Jun 2018
 */
interface FormSupport {

    /**
     * The presentation class may not return with the same type as [modelClass] - for example an Int may be presented in a [TextField].
     * The class actually used by the component can be retrieved by [presentationValueClassOf]
     */
    fun <PRESENTATIONVALUE : Any, MODEL : Any> converterFor(modelClass: KClass<out MODEL>, presentationValueClass: KClass<out PRESENTATIONVALUE>): Converter<PRESENTATIONVALUE, MODEL>

    fun <PRESENTATIONVALUE : Any, PRESENTATION : HasValue<PRESENTATIONVALUE>, MODEL : Any> converterForComponent(modelClass: KClass<out MODEL>, componentClass: KClass<PRESENTATION>): Converter<PRESENTATIONVALUE, MODEL>
    /**
     * Returns a presentation component to match [propertySpec]
     */
    fun componentFor(propertySpec: PropertyConfiguration): HasValue<*>
}

/**
 * [dataClassToPresentationMap] is provided by one or more Guice modules (see [FormModule])
 * [rendererFactory] is bound in [FormModule] and can be extended with additional [RendererSet] implementations
 * [converterFactory] is bound in [ConverterModule] and can be extended with additional [ConverterSet] implementations
 */
class DefaultFormSupport @Inject constructor(
        private val dataClassToPresentationMap: MutableMap<Class<*>, Provider<HasValue<*>>>,
        private val converterFactory: ConverterFactory, private val injectorLocator: InjectorLocator) :

        FormSupport {


    @Suppress("UNCHECKED_CAST")
    fun <T : Any> componentFor(modelClass: KClass<out T>): HasValue<T> {
        try {
            val provider: Provider<HasValue<*>> = dataClassToPresentationMap.getOrElse(modelClass.java) { throw DataTypeException(modelClass) }
            return provider.get() as HasValue<T>
        } catch (dte: DataTypeException) {
            if (Collection::class.java.isAssignableFrom(dte.clazz.java)) {
                throw ConfigurationException("A property which is a collection should be defined as a MultiSelect property, using PropertyConfiguration.fieldType")
            } else {
                throw ConfigurationException("A mapping of data type to Field component should be provided via a |Guice module, see FormModule for an example")
            }
        }

    }

    /**
     * a [STANDARD] field type will instantiate a [PropertyConfiguration.componentClass] if one is defined, otherwise it will look up the component class from [dataClassToPresentationMap].  The latter is defined via Guice (see [FormModule] for an example)
     * a [SINGLE_SELECT] field type will instantiate a component prescribed by [PropertyConfiguration.singleSelectComponent]
     * a [MULTI_SELECT] field type will instantiate a component prescribed by [PropertyConfiguration.multiSelectComponent]
     */
    override fun componentFor(propertySpec: PropertyConfiguration): HasValue<*> {
        val component: HasValue<*> = when (propertySpec.fieldType) {
            STANDARD -> if (propertySpec.componentClass == HasValue::class.java) {
                componentFor(propertySpec.propertyValueClass.kotlin)
            } else {
                propertySpec.componentClass.newInstance()
            }
            SINGLE_SELECT -> {
                if (propertySpec.selectDataProvider == NoDataProvider::class.java) {
                    throw ConfigurationException("If a property is defined as ${FieldType.SINGLE_SELECT} it must also have a defined 'selectDataProvider'")
                }
                return singleSelect(propertySpec.propertyValueClass, propertySpec)
            }
            MULTI_SELECT -> {
                if (propertySpec.selectDataProvider == NoDataProvider::class.java) {
                    throw ConfigurationException("If a property is defined as ${FieldType.MULTI_SELECT} it must also have a defined 'selectDataProvider'")
                }
                return multiSelect(propertySpec.propertyValueClass, propertySpec)
            }
        }
        return component
    }

    @Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")
    private fun <T : Any> multiSelect(propertyValueClass: Class<T>, propertySpec: PropertyConfiguration): AbstractMultiSelect<T> {
        val injector = injectorLocator.get()
        val dataProvider: DataProvider<T, String> = injector.getInstance(propertySpec.selectDataProvider) as DataProvider<T, String>
        when (propertySpec.multiSelectComponent) {
            MultiSelectComponent.CHECKBOX_GROUP -> {
                val component = CheckBoxGroup<T>()
                component.dataProvider = dataProvider
                return component
            }
            MultiSelectComponent.LIST_SELECT -> {
                val component = ListSelect<T>()
                component.dataProvider = dataProvider
                return component
            }
            MultiSelectComponent.TWIN_COL_SELECT -> {
                val component = TwinColSelect<T>()
                component.dataProvider = dataProvider
                return component
            }
        }
    }

    @Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")
    private fun <T : Any> singleSelect(propertyValueClass: Class<T>, propertySpec: PropertyConfiguration): SingleSelect<T> {
        val injector = injectorLocator.get()
        val dataProvider: DataProvider<T, String> = injector.getInstance(propertySpec.selectDataProvider) as DataProvider<T, String>
        when (propertySpec.singleSelectComponent) {
            SingleSelectComponent.COMBO_BOX -> {
                val component = ComboBox<T>()
                component.setDataProvider(dataProvider)
                component.isEmptySelectionAllowed = false
                return component
            }
            SingleSelectComponent.RADIO_GROUP -> {
                val component = RadioButtonGroup<T>()
                component.dataProvider = dataProvider
                return component
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    override fun <PRESENTATIONVALUE : Any, MODEL : Any> converterFor(modelClass: KClass<out MODEL>, presentationValueClass: KClass<out PRESENTATIONVALUE>): Converter<PRESENTATIONVALUE, MODEL> {
        // for multi select
        if (Set::class.java.isAssignableFrom(presentationValueClass.java)) {
            return NoConversionConverter() as Converter<PRESENTATIONVALUE, MODEL>
        }
        return converterFactory.get(presentationValueClass, modelClass)

    }

    @Suppress("UNCHECKED_CAST")
    override fun <PRESENTATIONVALUE : Any, PRESENTATION : HasValue<PRESENTATIONVALUE>, MODEL : Any> converterForComponent(modelClass: KClass<out MODEL>, componentClass: KClass<PRESENTATION>): Converter<PRESENTATIONVALUE, MODEL> {
        val presentationValueClass: KClass<PRESENTATIONVALUE> = TypeResolver.resolveRawArgument(HasValue::class.java, componentClass.java).kotlin as KClass<PRESENTATIONVALUE>
        return converterFactory.get(presentationValueClass, modelClass)
    }


}

class DataTypeException(val clazz: KClass<*>) : RuntimeException("No presentation class has been defined for $clazz")