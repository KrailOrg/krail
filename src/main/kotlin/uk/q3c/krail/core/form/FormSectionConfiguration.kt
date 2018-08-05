package uk.q3c.krail.core.form

import com.vaadin.ui.FormLayout
import com.vaadin.ui.Layout
import uk.q3c.krail.core.i18n.NullI18NKey
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable

/**
 ** [entityClass] must always be provided
 *
 *
 * [fieldOrder] is a list of property names in the order they should be displayed in a detail form section.  If this property is empty, a list of property names is scanned from the [entityClass] - their order is indeterminate.  See Java [Class.getDeclaredFields]
 * If [fieldOrder] is specified, only the properties listed are included in the form.
 * [layout] is the layout type to put the fields into
 *
 * [excludedProperties] are removed from the fields no matter how the initial list is generated, but is retained as a hidden column the the Table view
 *
 * [columnOrder] determines the order of columns in the Table.  Properties not listed here have their columns hidden
 *
 * [sampleDescriptionKey] and [sampleCaptionKey] are just sample values of [I18NKey] enums.  This saves having to declare the
 * caption and description keys explicitly in each [PropertyConfiguration]. The property name is used to try and lookup a key (an enum constant)
 * from the sample enum class.  Assuming the sample contains constants for all the property names, all fields will have I18N aware captions and descriptions
 *
 * [name] is there to be used where a [FormBuilder] uses multiple sections

 */
@FormDsl
class FormSectionConfiguration(override val parentConfiguration: ParentConfiguration, val name: String = "standard") : ChildConfiguration, ParentConfiguration, Serializable {

//    val gridConfiguration = TableConfiguration()
//    val detailConfiguration = DetailConfiguration()


    var entityClass: Class<*> = Any::class.java
    override var styleAttributes = StyleAttributes()
    var excludedProperties: Set<String> = LinkedHashSet()

    var columnOrder: Set<String> = LinkedHashSet()

    var layout: Class<out Layout> = FormLayout::class.java
    var fieldOrder: Set<String> = LinkedHashSet()
    var sampleCaptionKey: I18NKey = NullI18NKey.none
    var sampleDescriptionKey: I18NKey = NullI18NKey.none


    val properties: MutableMap<String, PropertyConfiguration> = mutableMapOf()
    val sections: MutableList<FormSectionConfiguration> = mutableListOf()

    private fun getSection(name: String): FormSectionConfiguration {
        return sections.first { s -> s.name == name }
    }

    /**
     * Returns or creates a [PropertyConfiguration] with [name]
     */
    fun property(name: String): PropertyConfiguration {
        return properties[name] ?: newProperty(name)
    }

    private fun newProperty(name: String): PropertyConfiguration {
        val prop = PropertyConfiguration(name, this)
        properties[name] = prop
        return prop
    }

    /**
     * Returns a subsection [FormSectionConfiguration] with [name] - Java fluent API.
     * If none currently exists, an new one is created
     */
    fun section(name: String): FormSectionConfiguration {
        var section: FormSectionConfiguration
        try {
            section = getSection(name)
        } catch (e: NoSuchElementException) {
            section = FormSectionConfiguration(this, name)
            sections.add(section)
        }
        return section
    }

    fun entityClass(entityClass: Class<*>): FormSectionConfiguration {
        this.entityClass = entityClass
        return this
    }

    fun sampleCaptionKey(sampleCaptionKey: I18NKey): FormSectionConfiguration {
        this.sampleCaptionKey = sampleCaptionKey
        return this
    }

    fun sampleDescriptionKey(sampleDescriptionKey: I18NKey): FormSectionConfiguration {
        this.sampleDescriptionKey = sampleDescriptionKey
        return this
    }

    fun layout(layout: Class<out Layout>): FormSectionConfiguration {
        this.layout = layout
        return this
    }

    fun excludedProperties(vararg propertyNames: String): FormSectionConfiguration {
        this.excludedProperties = LinkedHashSet(propertyNames.asList())
        return this
    }

    /**
     * A set of property names do determine the order of columns displayed in table view.  As this is order specific,
     * [columnOrder] must be an ordered Set - probably a [LinkedHashSet]
     */
    fun columnOrder(vararg propertyNames: String): FormSectionConfiguration {
        this.columnOrder = LinkedHashSet(propertyNames.asList())
        return this
    }

    fun fieldOrder(vararg propertyNames: String): FormSectionConfiguration {
        this.fieldOrder = LinkedHashSet(propertyNames.asList())
        return this
    }

    fun styleAttributes(styleAttributes: StyleAttributes): FormSectionConfiguration {
        this.styleAttributes = styleAttributes
        return this
    }
}

class TableConfiguration {

    var hiddenColumns: MutableList<String> = mutableListOf()
    var styleAttributes = StyleAttributes()
}

class DetailConfiguration {
    var styleAttributes = StyleAttributes()

}