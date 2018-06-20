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


@DslMarker
annotation class FormDsl

class MyForm : FormConfiguration() {

    override fun config(): FormConfiguration {

        section {
            excludedProperties = listOf("a", "b")

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

            }
        }
        return this
    }


}

class FormConfigurationException(msg: String) : RuntimeException(msg)

class FormProperty : Serializable