package uk.q3c.krail.core.form

import com.vaadin.data.Converter
import com.vaadin.data.HasValue
import com.vaadin.data.provider.DataProvider
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable
import kotlin.reflect.KClass


enum class StyleSize(val value: String) {
    tiny("tiny"), small("small"), normal("normal"), large("large"), huge("huge"), no_change("")
}

enum class StyleAlignment(val value: String) {
    align_left(""), align_center("align-center"), align_right("align-right"), no_change("")
}

enum class StyleBorderless(val value: String) {
    yes("borderless"), no(""), no_change("")
}

@FormDsl
class StyleAttributes(var size: StyleSize = StyleSize.no_change,
                      var borderless: StyleBorderless = StyleBorderless.no_change,
                      var alignment: StyleAlignment = StyleAlignment.no_change) : Serializable {


    fun merge(fromParent: StyleAttributes) {
        if (alignment == StyleAlignment.no_change) {
            alignment = fromParent.alignment
        }
        if (borderless == StyleBorderless.no_change) {
            borderless = fromParent.borderless
        }
        if (size == StyleSize.no_change) {
            size = fromParent.size
        }
    }


    fun combinedStyle(): String {
        val all = arrayOf(size.value, borderless.value, alignment.value)
        return all.joinToString(separator = " ").replace("  ", " ").trim()
    }

    fun size(size: StyleSize): StyleAttributes {
        this.size = size
        return this
    }

    fun borderless(borderless: StyleBorderless): StyleAttributes {
        this.borderless = borderless
        return this
    }

    fun alignment(alignment: StyleAlignment): StyleAttributes {
        this.alignment = alignment
        return this
    }

}

/**
 * If [FormSectionConfiguration] is not being scanned automatically, [PropertyConfiguration] must be fully populated manually.
 *
 * When [FormSectionConfiguration] is being scanned automatically, any manually specified values take precedence, (thus overriding the defaults) but otherwise:
 *
 * - [propertyValueClass] is taken from the property declaration in the entity class
 * - [componentClass] is selected using [FormSupport.componentFor]
 * - [converterClass] is selected using [FormSupport.converterFor]
 * - [validators] are additive - that is, any manually defined [KrailValidator]s are combined with those read from JSR 303 annotations from the entity class.
 *
 * When setting validation, http://piotrnowicki.com/2011/02/float-and-double-in-java-inaccurate-result/
 *
 * The manner in which [caption] and [description] are set is determined by the implementation of [PropertyConfigurationCreator].  For the default implementation, see [DefaultPropertyConfigurationCreator.caption], which enables two methods
 *
 * [singleSelectComponent] is not used unless [fieldType] is [FieldType.SINGLE_SELECT]
 * [multiSelectComponent] is not used unless [fieldType] is [FieldType.MULTI_SELECT]
 * [selectDataProvider] is not used unles field type is either [FieldType.SINGLE_SELECT] or [FieldType.MULTI_SELECT]
 *
 * Java Class is used rather than Kotlin KClass, because KClass is not serializable
 */
@FormDsl
class PropertyConfiguration(val name: String, override val parentConfiguration: ParentConfiguration) : ChildConfiguration, FormConfigurationCommon, Serializable {
    var fieldType = FieldType.STANDARD
    var singleSelectComponent = SingleSelectComponent.COMBO_BOX
    var multiSelectComponent = MultiSelectComponent.CHECKBOX_GROUP
    var selectDataProvider: Class<out DataProvider<*, *>> = NoDataProvider::class.java
    var propertyValueClass: Class<out Any> = Any::class.java
    var componentClass: Class<out HasValue<*>> = HasValue::class.java
    var converterClass: Class<out Converter<*, *>> = Converter::class.java
    var caption: I18NKey = LabelKey.Unnamed
    var description: I18NKey = DescriptionKey.No_description_provided
    var validators: MutableList<KrailValidator<*>> = mutableListOf()
    override var styleAttributes = StyleAttributes()

    fun singleSelectComponent(component: SingleSelectComponent): PropertyConfiguration {
        this.singleSelectComponent = component
        return this
    }

    fun end(): FormSectionConfiguration {
        return parentConfiguration as FormSectionConfiguration
    }

    fun multiSelectComponent(component: MultiSelectComponent): PropertyConfiguration {
        this.multiSelectComponent = component
        return this
    }

    fun merge() {
        var formCompleted = false
        var inheritedConfiguration = parentConfiguration
        while (!formCompleted) {
            styleAttributes.merge(inheritedConfiguration.styleAttributes)
            if (inheritedConfiguration is FormConfiguration) {
                formCompleted = true
            } else {
                inheritedConfiguration = (inheritedConfiguration as ChildConfiguration).parentConfiguration
            }

        }
    }

    fun styleAttributes(styleAttributes: StyleAttributes): PropertyConfiguration {
        this.styleAttributes = styleAttributes
        return this
    }

    fun styleAttributes(size: StyleSize = StyleSize.no_change,
                        borderless: StyleBorderless = StyleBorderless.no_change,
                        alignment: StyleAlignment = StyleAlignment.no_change): PropertyConfiguration {
        this.styleAttributes = StyleAttributes(size, borderless, alignment)
        return this
    }

    fun caption(caption: I18NKey): PropertyConfiguration {
        this.caption = caption
        return this
    }

    fun validators(validations: MutableList<KrailValidator<*>>): PropertyConfiguration {
        this.validators = validations
        return this
    }

    fun validator(validator: KrailValidator<*>): PropertyConfiguration {
        this.validators.add(validator)
        return this
    }

    fun selectDataProvider(dataProviderClass: Class<out DataProvider<*, *>>): PropertyConfiguration {
        this.selectDataProvider = dataProviderClass
        return this
    }

    fun fieldType(fieldType: FieldType): PropertyConfiguration {
        this.fieldType = fieldType
        return this
    }

    fun description(description: I18NKey): PropertyConfiguration {
        this.description = description
        return this
    }

    fun converterClass(converterClass: Class<out Converter<*, *>>): PropertyConfiguration {
        this.converterClass = converterClass
        return this
    }

    fun propertyValueClass(propertyValueClass: Class<out Any>): PropertyConfiguration {
        this.propertyValueClass = propertyValueClass
        return this
    }

    fun componentClass(componentClass: Class<out HasValue<*>>): PropertyConfiguration {
        this.componentClass = componentClass
        return this
    }
}

class InvalidTypeForValidator(targetClass: KClass<*>, validatorType: String) : RuntimeException("$targetClass is an invalid type for a $validatorType")
class InvalidValueForValidator(msg: String) : RuntimeException(msg)

enum class FieldType { STANDARD, SINGLE_SELECT, MULTI_SELECT }

enum class SingleSelectComponent { COMBO_BOX, RADIO_GROUP }
enum class MultiSelectComponent { CHECKBOX_GROUP, LIST_SELECT, TWIN_COL_SELECT }

/**
 * Equivalent to using null
 */
interface NoDataProvider : DataProvider<Any, Any>