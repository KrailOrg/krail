package uk.q3c.krail.core.form

import com.vaadin.ui.FormLayout
import com.vaadin.ui.Layout
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * If [scanEntityClass] is true, the class will be scanned for property names and types, with [excludedProperties] ignored.  [properties] is populated from this scan
 * If [scanEntityClass] is false, [properties] must be manually populated
 *
 * [displayOrder] is a list of property names in the order they should be displayed.  If this property is empty, a list of property names is scanned from the [entityClass] - their order is indeterminate.  See Java [Class.getDeclaredFields]
 * If [displayOrder] is specified, only the properties listed are included in the form.
 *
 * [excludedProperties] are removed from the property list no matter how the initial list is generated
 *
 *
 * [entityClass] must always be provided
 */
@FormDsl
class FormSectionConfiguration(override val parentConfiguration: ParentConfiguration) : ChildConfiguration, ParentConfiguration, Serializable {
    var name: String = "unnamed"
    var layout: KClass<out Layout> = FormLayout::class
    var displayOrder: List<String> = mutableListOf()  // if specified must contain all required properties
    var scanEntityClass: Boolean = true
    var entityClass: KClass<*> = Any::class
    var excludedProperties: List<String> = listOf() // used only if displayOrder not specified
    val properties: MutableMap<String, PropertyConfiguration> = mutableMapOf()
    override var styleAttributes = StyleAttributes()
    val sections: MutableList<FormSectionConfiguration> = mutableListOf()

    fun sectionWithName(name: String): FormSectionConfiguration {
        return sections.first { s -> s.name == name }
    }

    fun propertyWithName(name: String): PropertyConfiguration {
        return properties[name] ?: throw NoSuchElementException("$name is not an element of ${this.name}")
    }
}