package uk.q3c.krail.core.form

import com.vaadin.data.Converter
import com.vaadin.ui.AbstractField
import com.vaadin.ui.TextField
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable

/**
 * Created by David Sowerby on 07 Jun 2018
 */
@FormDsl
class FormConfiguration : Serializable {

    var scanFromEntityClass: Boolean = true
    var excludedProperties: List<String> = listOf()
    val properties: MutableList<PropertyConfiguration> = mutableListOf()

    fun property(name: String, init: PropertyConfiguration.() -> Unit): PropertyConfiguration {
        val propertyConfiguration = PropertyConfiguration(name = name)
        propertyConfiguration.init()
        properties.add(propertyConfiguration)
        return propertyConfiguration
    }
}

@FormDsl
class PropertyConfiguration(val name: String) : Serializable {
    var propertyType: Class<out Any> = String::class.java
    var fieldClass: Class<out AbstractField<*>> = AbstractField::class.java
    var converterClass: Class<out Converter<*, *>> = Converter::class.java
    var caption: I18NKey = LabelKey.Unnamed
    var description: I18NKey = DescriptionKey.No_description_provided
    var readOnly: Boolean = false
}

fun formConfig(init: FormConfiguration.() -> Unit): FormConfiguration {
    val fc = FormConfiguration()
    fc.init()
    return fc
}

val fc = formConfig {

    scanFromEntityClass = false
    excludedProperties = listOf("a", "b")

    property("first") {
        fieldClass = TextField::class.java
        caption = LabelKey.Connection_url_Caption_Style
    }
}

@DslMarker
annotation class FormDsl

fun fieldComponentLookup(propertyConfiguration: PropertyConfiguration): AbstractField<*> {
    if (propertyConfiguration.fieldClass == AbstractField::class.java) {
        return when (propertyConfiguration.propertyType) {
            String::class.java -> TextField::class.java.newInstance()
            else -> {
                throw UnknownDataTypeException(propertyConfiguration.propertyType)
            }
        }
    } else {
        return propertyConfiguration.fieldClass.newInstance()
    }
}

class UnknownDataTypeException(t: Class<out Any>) : RuntimeException("No field component defined for data type $t")

fun converterLookup() {

}