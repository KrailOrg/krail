package uk.q3c.krail.core.form

import com.vaadin.data.Converter
import com.vaadin.ui.AbstractField
import com.vaadin.ui.TextField
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.view.ViewConfiguration
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable

/**
 * Created by David Sowerby on 07 Jun 2018
 */
@FormDsl
abstract class FormConfiguration : ViewConfiguration {

    var formType: String = ""  // has to be String to enable new types by users
    val sections: MutableList<SectionConfiguration> = mutableListOf()

    fun section(name: String = "", init: SectionConfiguration.() -> Unit): SectionConfiguration {
        val config = SectionConfiguration()
        config.init()
        sections.add(config)
        return config
    }

    abstract fun config(): FormConfiguration

}

class SectionConfiguration : Serializable {
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



@DslMarker
annotation class FormDsl

class MyForm : FormConfiguration() {

    override fun config(): FormConfiguration {

        section {
            scanFromEntityClass = false
            excludedProperties = listOf("a", "b")

            property("first") {
                fieldClass = TextField::class.java
                caption = LabelKey.Connection_url_Caption_Style
            }
        }
        return this
    }

}
