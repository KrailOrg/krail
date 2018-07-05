package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.RunningOn
import uk.q3c.krail.core.env.RuntimeEnvironment
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockCurrentLocale
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.util.guice.DefaultSerializationSupport
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.text.DefaultMessageFormat
import uk.q3c.util.text.MessageFormat2

/**
 * Created by David Sowerby on 09 Jun 2018
 */
object FormSupportTest : Spek({

    given("a FormSupport instance") {
        //        val injector = Guice.createInjector(FormModule(), KrailValidationModule(), FormSupportTestModule(), ConverterModule())
//        val formSupport = injector.getInstance(FormSupport::class.java)

        on("requesting converter where presentation and model type are the same ") {

            it("returns an instance of NoConversionConverter ") {
                //                formSupport.converterFor(String::class.java, TextField()).shouldBeInstanceOf(NoConversionConverter::class.java)
//                formSupport.converterFor(LocalDateTime::class.java, DateTimeField()).shouldBeInstanceOf(NoConversionConverter::class.java)
//            }
            }

            on("requesting converter where presentation and model type are different ") {

                it("returns an instance of the correct Converter ") {
                    //                formSupport.converterFor(Integer::class.java, TextField()).shouldBeInstanceOf(StringToIntegerConverter::class.java)
                }
            }

            on("requesting a UI Field for a data type") {

                it("returns the correct field type") {
                    //                formSupport.fieldFor(String::class.java).get().shouldBeInstanceOf(TextField::class.java)
//                formSupport.fieldFor(Integer::class.java).get().shouldBeInstanceOf(TextField::class.java)
//                formSupport.fieldFor(Boolean::class.java).get().shouldBeInstanceOf(CheckBox::class.java)
//                formSupport.fieldFor(LocalDateTime::class.java).get().shouldBeInstanceOf(DateTimeField::class.java)
//                formSupport.fieldFor(LocalDate::class.java).get().shouldBeInstanceOf(DateField::class.java)
                }
            }

            on("requesting a UI Field for a data type which has not been defined") {
                //            val result = { formSupport.fieldFor(Sitemap::class.java) }
//
//            it("throws exception") {
//                result.shouldThrow(DataTypeException::class)
//            }
            }
        }
    }
})


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