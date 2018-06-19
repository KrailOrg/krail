package uk.q3c.krail.core.form

import com.vaadin.data.Converter
import com.vaadin.data.Validator
import com.vaadin.ui.AbstractField
import com.vaadin.ui.TextField
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.view.ViewConfiguration
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable
import kotlin.reflect.KClass

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

    fun sectionWithName(name: String): SectionConfiguration {
        return sections.first { s -> s.name == name }
    }

}

class EmptyFormConfiguration : FormConfiguration() {

    override fun config(): FormConfiguration {
        return this
    }
}

/**
 * If [scanEntityClass] is true, the class will be scanned for property names and types, with [excludedProperties] ignored.  [properties] is populated from this scan
 * If [scanEntityClass] is false, [properties] must be manually populated
 *
 * [entityClass] must always be provided
 */
class SectionConfiguration : Serializable {
    var name: String = "unnamed"
    var scanEntityClass: Boolean = true
    var entityClass = Any::class
    var excludedProperties: List<String> = listOf()
    val properties: MutableMap<String, PropertyConfiguration> = mutableMapOf()

    fun property(name: String, init: PropertyConfiguration.() -> Unit): PropertyConfiguration {
        val propertyConfiguration = PropertyConfiguration(name = name)
        propertyConfiguration.init()
        properties[name] = propertyConfiguration
        return propertyConfiguration
    }
}

/**
 * If [SectionConfiguration] is not being scanned automatically, [PropertyConfiguration] must be fully populated manually.
 *
 * When [SectionConfiguration] is being scanned automatically, any manually specified values take precedence, (thus overriding the defaults) but otherwise:
 *
 * - [propertyType] is taken from the property declaration in the entity class
 * - [componentClass] is selected using [FormSupport.componentFor]
 * - [converterClass] is selected using [FormSupport.converterFor]
 * - [validations] are additive - that is, any manually defined [ValidatorSpec]s are combined with those read from JSR 303 annotations from the entity class.
 *
 *
 * [caption] and [description] must be set manually
 */
@FormDsl
class PropertyConfiguration(val name: String) : Serializable {
    var propertyType: KClass<out Any> = Any::class
    var componentClass: Class<out AbstractField<*>> = AbstractField::class.java
    var converterClass: Class<out Converter<*, *>> = Converter::class.java
    var caption: I18NKey = LabelKey.Unnamed
    var description: I18NKey = DescriptionKey.No_description_provided
    var validations: MutableList<Validator<*>> = mutableListOf()

    fun assertTrue() {
        validations.add(AssertTrueValidator())
    }

    fun assertFalse() {
        validations.add(AssertFalseValidator())
    }
}

class InvalidTypeForValidator(targetClass: KClass<*>, validatorClass: Class<out Validator<*>>) : RuntimeException("$targetClass is an invalid type for $validatorClass")






@DslMarker
annotation class FormDsl

class MyForm : FormConfiguration() {

    override fun config(): FormConfiguration {

        section {
            excludedProperties = listOf("a", "b")

            property("first") {
                componentClass = TextField::class.java
                caption = LabelKey.Connection_url_Caption_Style
                assertTrue()
            }
        }
        return this
    }


}

class FormConfigurationException(msg: String) : RuntimeException(msg)

class FormProperty : Serializable