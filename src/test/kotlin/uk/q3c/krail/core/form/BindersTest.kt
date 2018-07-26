package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.data.converter.StringToIntegerConverter
import com.vaadin.ui.TextField
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNull
import org.apache.bval.guice.ValidationModule
import org.apache.bval.jsr303.ApacheFactoryContext
import org.apache.bval.jsr303.ClassValidator
import org.apache.commons.lang3.reflect.FieldUtils
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.RunningOn
import uk.q3c.krail.core.env.RuntimeEnvironment
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.guice.InjectorHolder
import uk.q3c.krail.core.persist.MapDbFormDaoFactory
import uk.q3c.krail.core.validation.KrailInterpolator
import uk.q3c.krail.core.validation.KrailValidationModule
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockCurrentLocale
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.util.guice.DefaultSerializationSupport
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.serial.tracer.SerializationTracer
import uk.q3c.util.text.DefaultMessageFormat
import uk.q3c.util.text.MessageFormat2
import java.io.Serializable
import java.time.LocalDate
import javax.validation.constraints.Max

/**
 * Created by David Sowerby on 26 May 2018
 */
object BindersTest : Spek({
    given("The Guice bindings for KrailBeanValidationBinderFactory") {
        lateinit var injector: Injector
        lateinit var nameField: TextField
        lateinit var ageField: TextField

        beforeEachTest {
            injector = Guice.createInjector(FormModule(), KrailValidationModule(), TestSupportModule(), ConverterModule())
            InjectorHolder.setInjector(injector)
            nameField = TextField()
            ageField = TextField()
        }

        on("creating a binder via its factory and binding a Field") {
            val binderFactory = injector.getInstance(KrailBeanValidationBinderFactory::class.java)
            val binder = binderFactory.create(Person::class)
            binder.bind(nameField, "name")
            binder.bean = Person(title = "Mr", name = "Wiggly", age = 11)
            binder.forField(ageField).withConverter(StringToIntegerConverter("Rubbish error message"))
                    .bind("age")
            val tracer = SerializationTracer()

            it("is Serializable") {
                tracer.trace(binder).shouldNotHaveAnyDynamicFailures()
            }

            it("also has Serializable factory") {
                tracer.trace(binderFactory).shouldNotHaveAnyDynamicFailures()
            }

            it("validates without error") {
                binder.validate().beanValidationErrors.shouldBeEmpty()
            }
        }

        on("validating an incorrect value") {
            val binderFactory = injector.getInstance(KrailBeanValidationBinderFactory::class.java)
            val binder = binderFactory.create(Person::class)
            binder.bind(nameField, "name")
            binder.forField(ageField).withConverter(StringToIntegerConverter("Rubbish error message"))
                    .bind("age")
            binder.bean = Person(title = "Mr", name = "Wiggly", age = 34)
            val validationStatus = binder.validate()


            it("identifies the error") {
                validationStatus.fieldValidationErrors[0].message.get().shouldBeEqualTo("must be less than or equal to 12")
            }
        }

        on("constructing a validator") {
            val beanValidatorFactory = injector.getInstance(KrailBeanValidatorFactory::class.java)
            val beanValidator = beanValidatorFactory.create(Person::class.java, "age")
            val javaxValidator = beanValidator.javaxBeanValidator
            val contextField = FieldUtils.getField(ClassValidator::class.java, "factoryContext", true)
            val contextFieldValue = contextField.get(javaxValidator) as ApacheFactoryContext


            it("it uses the KrailInterpolator") {

                javaxValidator.shouldNotBeNull()
                contextFieldValue.messageInterpolator.shouldBeInstanceOf(KrailInterpolator::class.java)
            }
        }

    }

    given("The Guice bindings for KrailBeanValidatorFactory") {
        val injector = Guice.createInjector(FormModule(), ValidationModule(), TestSupportModule(), ConverterModule())
        InjectorHolder.setInjector(injector)
        val nameField = TextField()

        on("constructing KrailBeanValidatorFactory") {
            val factory = injector.getInstance(KrailBeanValidatorFactory::class.java)

            it("is Serializable") {
                SerializationTracer().trace(factory).shouldNotHaveAnyDynamicFailures()
            }
        }
    }


})


const val testUuid1 = "123e4567-e89b-12d3-a456-556642440000"

class Person(
        override var id: String = testUuid1,
        var title: String = "Mr",
        var name: String,
        @field:Max(12)
        var age: Int,
        var joinDate: LocalDate = LocalDate.parse("2010-12-31"),
        var dob: LocalDate = LocalDate.parse("1999-12-31")) : Serializable, Entity {

    var pricePlan: Int by SingleSelectPropertyDelegate<Person, Int>(setOf(1, 3))

    init {
        pricePlan = 3
    }
}




private class TestSupportModule : AbstractModule() {
    override fun configure() {
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(RuntimeEnvironment::class.java).annotatedWith(RunningOn::class.java).toInstance(RuntimeEnvironment.SERVLET)
        bind(CurrentLocale::class.java).toInstance(MockCurrentLocale())
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(MessageFormat2::class.java).to(DefaultMessageFormat::class.java)
        bind(FormDaoFactory::class.java).to(MapDbFormDaoFactory::class.java).asEagerSingleton()
    }

}