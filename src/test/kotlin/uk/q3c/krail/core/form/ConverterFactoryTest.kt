package uk.q3c.krail.core.form

import com.vaadin.data.ValueContext
import com.vaadin.ui.TextField
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.util.serial.tracer.SerializationTracer
import java.util.*

/**
 * Created by David Sowerby on 09 Jun 2018
 */
object ConverterFactoryTest : Spek({

    given("KrailConverterErrorMessageProvider ") {
        lateinit var emp: KrailConverterErrorMessageProvider
        val translate = MockTranslate()
        translate.returnNameOnly = false

        beforeEachTest {
            emp = KrailConverterErrorMessageProvider(translate)
        }

        on("round trip serialisation") {
            val tracer = SerializationTracer().trace(emp)

            it("should not error") {
                tracer.shouldNotHaveAnyDynamicFailures()
            }
        }

        on("apply without setting message key") {
            val context = ValueContext()
            val result = { emp.apply(context) }

            it("throws a not initialized exception") {
                result.shouldThrow(UninitializedPropertyAccessException::class)
            }
        }

        on("apply with component with Locale set") {
            val field = TextField()
            field.locale = Locale.GERMAN
            val context = ValueContext(field)
            val result = emp.setMessage(ConverterKey.Must_be_a_number).apply(context)

            it("should translate using component locale") {
                result.shouldBeEqualTo("Must be a number-en-GB-Optional[de]")
            }
        }

        on("apply with component without Locale set") {
            val field = TextField()
            val context = ValueContext(field)
            val result = emp.setMessage(ConverterKey.Must_be_a_number).apply(context)

            it("should translate using component locale") {
                result.shouldBeEqualTo("Must be a number-en-GB")
            }
        }
    }

})