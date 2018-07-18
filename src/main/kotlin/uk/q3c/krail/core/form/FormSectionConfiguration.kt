package uk.q3c.krail.core.form

import com.vaadin.ui.FormLayout
import com.vaadin.ui.Layout
import java.io.Serializable
import kotlin.reflect.KClass

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
 * [name] is there to be used where a [FormBuilder] uses multiple sections

 */
@FormDsl
class FormSectionConfiguration(override val parentConfiguration: ParentConfiguration) : ChildConfiguration, ParentConfiguration, Serializable {

//    val gridConfiguration = TableConfiguration()
//    val detailConfiguration = DetailConfiguration()

    var name: String = "standard"
    var entityClass: KClass<*> = Any::class
    override var styleAttributes = StyleAttributes()
    var excludedProperties: List<String> = listOf() // used only if displayOrder not specified

    var columnOrder: MutableSet<String> = mutableSetOf()

    var layout: KClass<out Layout> = FormLayout::class
    var fieldOrder: List<String> = mutableListOf()  // if specified must contain all required properties


    val properties: MutableMap<String, PropertyConfiguration> = mutableMapOf()
    val sections: MutableList<FormSectionConfiguration> = mutableListOf()

    fun sectionWithName(name: String): FormSectionConfiguration {
        return sections.first { s -> s.name == name }
    }

    fun propertyWithName(name: String): PropertyConfiguration {
        return properties[name] ?: throw NoSuchElementException("$name is not an element of ${this.name}")
    }
}

class TableConfiguration {

    var hiddenColumns: MutableList<String> = mutableListOf()
    var styleAttributes = StyleAttributes()
}

class DetailConfiguration {
    var styleAttributes = StyleAttributes()

}