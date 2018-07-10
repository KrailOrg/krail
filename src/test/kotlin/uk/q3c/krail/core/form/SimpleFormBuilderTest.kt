package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.ui.DateField
import com.vaadin.ui.FormLayout
import com.vaadin.ui.InlineDateField
import com.vaadin.ui.TextField
import io.mockk.mockk
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEmpty
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.guice.InjectorHolder
import uk.q3c.krail.core.i18n.DescriptionKey.No_description_provided
import uk.q3c.krail.core.i18n.KrailI18NModule
import uk.q3c.krail.core.validation.KrailValidationModule
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.I18NKey
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
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 05 Jul 2018
 */
object SimpleFormSectionBuilderTest : Spek({

    given(" a simple form section builder, configured with SimpleFormConfiguration1") {
        lateinit var injector: Injector
        lateinit var builder: SimpleFormSectionBuilder<Person>
        lateinit var binderFactory: KrailBeanValidationBinderFactory
        lateinit var binder: KrailBeanValidationBinder<Person>
        lateinit var formConfiguration: FormConfiguration
        lateinit var configuration: FormSectionConfiguration
        lateinit var propertySpecCreator: PropertySpecCreator
        lateinit var formSupport: FormSupport
        lateinit var translate: Translate

        val formDaoFactory: FormDaoFactory = MockFormDaoFactory()


        beforeEachTest {
            injector = Guice.createInjector(FormModule(), KrailValidationModule(), ConverterModule(), SimpleFormSectionBuilderTestModule())
            InjectorHolder.setInjector(injector)
            binderFactory = injector.getInstance(KrailBeanValidationBinderFactory::class.java)
            formSupport = injector.getInstance(FormSupport::class.java)
            propertySpecCreator = injector.getInstance(PropertySpecCreator::class.java)
            formConfiguration = SimpleFormConfiguration1()
            formConfiguration.config()
            configuration = formConfiguration.sectionWithName("single")
            translate = injector.getInstance(Translate::class.java)
            builder = SimpleFormSectionBuilder(entityClass = Person::class, binderFactory = binderFactory, configuration = configuration, propertySpecCreator = propertySpecCreator, formSupport = formSupport)
        }

        on("creating the section") {
            val serializationTracer = SerializationTracer()
            val section = builder.buildDetail(formDaoFactory, translate)
            section.selectBean(mapOf(Pair("id", testUuid1)))



            it("has the correct number of components") {
                section.propertyMap.size.shouldBe(5)
            }

            it("creates default components where none specified") {
                with(section) {
                    propertyMap["dob"]!!.component.shouldBeInstanceOf(DateField::class.java)
                    propertyMap["title"]!!.component.shouldBeInstanceOf(TextField::class.java)
                    propertyMap["age"]!!.component.shouldBeInstanceOf(TextField::class.java)
                    propertyMap["name"]!!.component.shouldBeInstanceOf(TextField::class.java)
                }
            }

            it("creates a specific component where specified") {
                section.propertyMap["joinDate"]!!.component.shouldBeInstanceOf(InlineDateField::class.java)
            }

            it("assigns correctly inherited styles") {
                with(section.propertyMap.getOrElse("title") { throw NoSuchElementException("title") }) {
                    component.styleName.shouldBeEqualTo("huge borderless")
                }
            }

            it("uses a FormLayout") {
                section.rootComponent.shouldBeInstanceOf(FormLayout::class)
            }

            it("has added the components to the layout") {
                section.rootComponent.componentCount.shouldBe(5)
            }

            it("ignores excluded properties") {
                section.propertyMap["id"].shouldBeNull()
            }

            it("has set captions and descriptions in the property map, but not in component") {
                section.propertyMap["joinDate"]!!.component.caption.shouldBeNull()
                section.propertyMap["joinDate"]!!.captionKey.shouldBe(TestPersonKey.date_joined)
                section.propertyMap["joinDate"]!!.descriptionKey.shouldBe(No_description_provided)
            }


            it("creates a serializable componentSet") {
                serializationTracer.trace(section).shouldNotHaveAnyDynamicFailures()
            }
        }

        on("setting a value that should fail a JSR annotation validator") {
            val section = builder.buildDetail(formDaoFactory, translate)
            section.selectBean(mapOf(Pair("id", testUuid1)))
            (section.propertyMap["age"]!!.component as TextField).value = "34"
            val validationResults = section.binder.validate()

            it("should show a validation error") {
                validationResults.fieldValidationErrors.shouldNotBeEmpty()
            }

            it("should be a max error from the age field") {
                val r = validationResults.fieldValidationErrors.first { e -> e.status.name == "ERROR" }
                r.message.get().shouldBeEqualTo("must be less than or equal to 12")
            }
        }


        on("setting a value that should fail a validator set by configuration") {
            val section = builder.buildDetail(formDaoFactory, translate)
            section.selectBean(mapOf(Pair("id", testUuid1)))
            (section.propertyMap["age"]!!.component as TextField).value = "2"
            val validationResults = section.binder.validate()

            it("should show a validation error") {
                validationResults.fieldValidationErrors.shouldNotBeEmpty()
            }

            it("should be a min error from the age field") {
                val r = validationResults.fieldValidationErrors.first { e -> e.status.name == "ERROR" }
//                r.message.get().shouldBeEqualTo("must be greater than or equal to 3")
                r.message.get().shouldBeEqualTo("Min")
            }
        }
    }
})

//object SimpleFormTypeBuilderTest : Spek({
//
//    TODO()
//})


private class SimpleFormConfiguration1 : FormConfiguration() {
    override fun config() {
        val section = FormSectionConfiguration(this)
        section.name = "single"
        sections.add(section)
        section.entityClass = Person::class
        section.styleAttributes.borderless = StyleBorderless.yes
        val titleConfig = PropertyConfiguration("title", section)
        section.properties[titleConfig.name] = titleConfig
        titleConfig.styleAttributes.size = StyleSize.huge
        val joinDateConfig = PropertyConfiguration("joinDate", section)
        section.properties[joinDateConfig.name] = joinDateConfig
        joinDateConfig.componentClass = InlineDateField::class.java
        joinDateConfig.caption = TestPersonKey.date_joined


        val ageConfig = PropertyConfiguration("age", section)
        ageConfig.min(3)
        section.properties[ageConfig.name] = ageConfig
    }

}

private enum class TestPersonKey : I18NKey {

    date_joined, a_title
}


private class SimpleFormSectionBuilderTestModule : AbstractModule() {

    val daoFactory: FormDaoFactory = mockk()

    override fun configure() {
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(CurrentLocale::class.java).toInstance(MockCurrentLocale())
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(MessageFormat2::class.java).to(DefaultMessageFormat::class.java)
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(FormDaoFactory::class.java).toInstance(daoFactory)
    }

}

private class LocalTestI18NModule : KrailI18NModule() {


    override fun define() {
        super.define()
        supportedLocales(Locale.ITALY, Locale.UK, Locale.GERMANY)
        supportedLocales(Locale("de", "CH"))
    }

}

class MockPersonFormDao : FormDao<Person>, Serializable {
    override fun getList(parameters: Map<String, String>): Person {
        TODO()
    }

    override fun create(): Person {
        TODO()
    }

    override fun delete(parameters: Map<String, String>): Person {
        TODO()
    }

    val people: MutableList<Person> = mutableListOf()

    override fun get(parameters: Map<String, String>): Person {
        return Person(title = "Mr", name = "Wiggly", age = 10)
    }
}

class MockFormDaoFactory : FormDaoFactory {
    override fun <T : Any> getDao(entityClass: KClass<T>): FormDao<T> {
        if (entityClass == Person::class) {
            return MockPersonFormDao() as FormDao<T>
        } else {
            throw IllegalArgumentException("only supports Person")
        }
    }

}