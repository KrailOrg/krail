package uk.q3c.krail.core.form

import com.vaadin.ui.TextField
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.view.ViewConfiguration
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.validation.constraints.Pattern

/**
 *
 * A [Form] is considered to be a set of one or more sections, where each section is potentially supported by an entity or collection of entities.
 * So, for example, a master-detail form would have a section for the master and a section for the detail.
 *
 * Each section then contains a number of properties tied to UI Fields
 *
 * A [FormConfiguration] uses the principle of inheritance to minimise the amount of coded configuration required.  Where possible,
 * an element can be configured at the Form level, but then overridden at the section or property level.
 *
 * A [FormConfiguration] contains one or more [SectionConfiguration]s, which contain [PropertyConfiguration]s
 *
 * Created by David Sowerby on 07 Jun 2018
 */
@FormDsl
interface FormConfigurationCommon {
    var styleAttributes: StyleAttributes
}

@FormDsl
interface ChildConfiguration : FormConfigurationCommon {
    val parentConfiguration: ParentConfiguration
}

interface ParentConfiguration : FormConfigurationCommon


@FormDsl
abstract class FormConfiguration : ViewConfiguration, ParentConfiguration, FormConfigurationCommon {


    var formType: String = "simple"  // has to be String to enable users to add new types
    override var styleAttributes = StyleAttributes()

    val sections: MutableList<SectionConfiguration> = mutableListOf()

    abstract fun config()

    fun sectionWithName(name: String): SectionConfiguration {
        return sections.first { s -> s.name == name }
    }

}

class EmptyFormConfiguration : FormConfiguration() {

    override fun config() {
        // do nothing
    }
}


@DslMarker
annotation class FormDsl

class MyForm : FormConfiguration() {

    override fun config() {

        style {
            size = StyleSize.normal
        }
        section {
            excludedProperties = listOf("a", "b")
            style {
                size = StyleSize.normal
            }

            property("first") {
                componentClass = TextField::class.java
                caption = LabelKey.Connection_url_Caption_Style
                mustBeTrue()
                mustBeFalse()


                mustBeNull()
                mustNotBeNull()

                mustMatch("xx", Pattern.Flag.CASE_INSENSITIVE)
                assertTrue()
                assertFalse()
                decimalMax("123.3")
                decimalMin(BigDecimal(43.4))
                future(LocalDateTime.now())
                max(5)
                min(3)
                max(5L)
                min(3L)
                past(LocalDateTime.now())
                past(LocalDate.now())
                pattern("xx", Pattern.Flag.CASE_INSENSITIVE)
                null_()
                notNull()
                size(3, 12)

                style { size = StyleSize.no_change }

            }
        }
    }


}

class FormConfigurationException(msg: String) : RuntimeException(msg)

class FormProperty : Serializable