package uk.q3c.krail.core.form

import java.io.Serializable

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