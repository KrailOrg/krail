package uk.q3c.krail.core.form

import com.vaadin.ui.FormLayout
import com.vaadin.ui.Layout
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * If [scanEntityClass] is true, the class will be scanned for property names and types, with [excludedProperties] ignored.  [properties] is populated from this scan
 * If [scanEntityClass] is false, [properties] must be manually populated
 *
 * [entityClass] must always be provided
 */
@FormDsl
class SectionConfiguration(override val parentConfiguration: ParentConfiguration) : ChildConfiguration, ParentConfiguration, Serializable {
    var name: String = "unnamed"
    var layout: KClass<out Layout> = FormLayout::class
    var displayOrder: MutableList<String> = mutableListOf()  // if specified must contain all required properties
    var scanEntityClass: Boolean = true
    var entityClass: KClass<*> = Any::class
    var excludedProperties: List<String> = listOf() // used only if displayOrder not specified
    val properties: MutableMap<String, PropertyConfiguration> = mutableMapOf()
    override var styleAttributes = StyleAttributes()
    val sections: MutableList<SectionConfiguration> = mutableListOf()

    fun sectionWithName(name: String): SectionConfiguration {
        return sections.first { s -> s.name == name }
    }

    fun propertyWithName(name: String): PropertyConfiguration {
        return properties[name] ?: throw NoSuchElementException("$name is not an element of ${this.name}")
    }
}