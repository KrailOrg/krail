package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.data.converter.StringToIntegerConverter
import com.vaadin.ui.CheckBox
import com.vaadin.ui.DateField
import com.vaadin.ui.DateTimeField
import com.vaadin.ui.TextField
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.RunningOn
import uk.q3c.krail.core.env.RuntimeEnvironment
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.navigate.sitemap.Sitemap
import uk.q3c.krail.core.persist.DaoModule
import uk.q3c.krail.core.validation.KrailValidationModule
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockCurrentLocale
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.util.guice.DefaultSerializationSupport
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.text.DefaultMessageFormat
import uk.q3c.util.text.MessageFormat2
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Created by David Sowerby on 09 Jun 2018
 */
object FormSupportTest : Spek({

    given("a FormSupport instance") {
        lateinit var injector: Injector
        lateinit var formSupport: FormSupport

        beforeEachTest {
            injector = Guice.createInjector(FormModule(), KrailValidationModule(), FormSupportTestModule(), ConverterModule(), DaoModule())
            formSupport = injector.getInstance(FormSupport::class.java)
        }

        on("requesting converter where presentation and model type are the same ") {

            it("returns an instance of NoConversionConverter ") {
                formSupport.converterFor(String::class, String::class).shouldBeInstanceOf(NoConversionConverter::class.java)
                formSupport.converterFor(LocalDateTime::class, LocalDateTime::class).shouldBeInstanceOf(NoConversionConverter::class.java)
            }
        }

        on("requesting converter where presentation and model type are different ") {

            it("returns an instance of the correct Converter ") {
                formSupport.converterFor(Integer::class, String::class).shouldBeInstanceOf(StringToIntegerConverter::class.java)
            }
        }
        on("requesting a UI Field for a data type") {

            it("returns the correct field type") {
                formSupport.componentFor(String::class).get().shouldBeInstanceOf(TextField::class.java)
                formSupport.componentFor(Int::class).get().shouldBeInstanceOf(TextField::class.java)
                formSupport.componentFor(Boolean::class).get().shouldBeInstanceOf(CheckBox::class.java)
                formSupport.componentFor(LocalDateTime::class).get().shouldBeInstanceOf(DateTimeField::class.java)
                formSupport.componentFor(LocalDate::class).get().shouldBeInstanceOf(DateField::class.java)
            }
        }

        on("requesting a UI Field for a data type which has not been defined") {
            val result = { formSupport.componentFor(Sitemap::class) }

            it("throws exception") {
                result.shouldThrow(DataTypeException::class)
            }
        }
    }
}
)


class FormSupportTestModule : AbstractModule() {
    override fun configure() {
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(RuntimeEnvironment::class.java).annotatedWith(RunningOn::class.java).toInstance(RuntimeEnvironment.SERVLET)
        bind(CurrentLocale::class.java).toInstance(MockCurrentLocale())
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(MessageFormat2::class.java).to(DefaultMessageFormat::class.java)
    }
}