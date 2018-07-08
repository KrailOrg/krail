package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.ui.DateField
import com.vaadin.ui.FormLayout
import com.vaadin.ui.InlineDateField
import com.vaadin.ui.TextField
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.guice.InjectorHolder
import uk.q3c.krail.core.i18n.DescriptionKey.No_description_provided
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
        lateinit var configuration: SectionConfiguration
        lateinit var propertySpecCreator: PropertySpecCreator
        lateinit var formSupport: FormSupport
        lateinit var person: Person


        beforeEachTest {
            injector = Guice.createInjector(FormModule(), KrailValidationModule(), ConverterModule(), SimpleFormSectionBuilderTestModule())
            InjectorHolder.setInjector(injector)
            binderFactory = injector.getInstance(KrailBeanValidationBinderFactory::class.java)
            formSupport = injector.getInstance(FormSupport::class.java)
            propertySpecCreator = injector.getInstance(PropertySpecCreator::class.java)
            formConfiguration = SimpleFormConfiguration1()
            formConfiguration.config()
            configuration = formConfiguration.sectionWithName("single")
            builder = SimpleFormSectionBuilder(entityClass = Person::class, binderFactory = binderFactory, configuration = configuration, propertySpecCreator = propertySpecCreator, formSupport = formSupport)
            person = Person(title = "Mr", name = "Wiggly", age = 10)
        }

        on("creating the section") {
            val serializationTracer = SerializationTracer()
            val componentSet = builder.build()



            it("has the correct number of components") {
                componentSet.propertyMap.size.shouldBe(5)
            }

            it("creates default components where none specified") {
                with(componentSet) {
                    propertyMap["dob"]!!.component.shouldBeInstanceOf(DateField::class.java)
                    propertyMap["title"]!!.component.shouldBeInstanceOf(TextField::class.java)
                    propertyMap["age"]!!.component.shouldBeInstanceOf(TextField::class.java)
                    propertyMap["name"]!!.component.shouldBeInstanceOf(TextField::class.java)
                }
            }

            it("creates a specific component where specified") {
                componentSet.propertyMap["joinDate"]!!.component.shouldBeInstanceOf(InlineDateField::class.java)
            }

            it("assigns correctly inherited styles") {
                with(componentSet.propertyMap.getOrElse("title") { throw NoSuchElementException("title") }) {
                    component.styleName.shouldBeEqualTo("huge borderless")
                }
            }

            it("uses a FormLayout") {
                componentSet.rootComponent.shouldBeInstanceOf(FormLayout::class)
            }

            it("has added the components to the layout") {
                componentSet.rootComponent.componentCount.shouldBe(5)
            }

            it("ignores excluded properties") {
                componentSet.propertyMap["id"].shouldBeNull()
            }

            it("has set captions and descriptions in the property map, but not in component") {
                componentSet.propertyMap["joinDate"]!!.component.caption.shouldBeNull()
                componentSet.propertyMap["joinDate"]!!.captionKey.shouldBe(TestPersonKey.date_joined)
                componentSet.propertyMap["joinDate"]!!.descriptionKey.shouldBe(No_description_provided)
            }

            it("adds a validator from JSR annotations") {
                componentSet.propertyMap["age"]!!.component
                TODO()
            }

            it("adds a validator defined  by the configuration") {
                TODO()
            }

            it("creates a serializable componentSet") {
                serializationTracer.trace(componentSet).shouldNotHaveAnyDynamicFailures()
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
        val section = SectionConfiguration(this)
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

        section.excludedProperties = listOf("id")

    }

}

private enum class TestPersonKey : I18NKey {

    date_joined, a_title
}


private class SimpleFormSectionBuilderTestModule : AbstractModule() {

    override fun configure() {
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(CurrentLocale::class.java).toInstance(MockCurrentLocale())
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(MessageFormat2::class.java).to(DefaultMessageFormat::class.java)
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
    }

}