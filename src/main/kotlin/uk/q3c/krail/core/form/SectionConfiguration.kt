package uk.q3c.krail.core.form

import java.io.Serializable

/**
 * If [scanEntityClass] is true, the class will be scanned for property names and types, with [excludedProperties] ignored.  [properties] is populated from this scan
 * If [scanEntityClass] is false, [properties] must be manually populated
 *
 * [entityClass] must always be provided
 */
@FormDsl
class SectionConfiguration(override val parentConfiguration: ParentConfiguration) : ChildConfiguration, ParentConfiguration, Serializable {
    var name: String = "unnamed"
    var scanEntityClass: Boolean = true
    var entityClass = Any::class
    var excludedProperties: List<String> = listOf()
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