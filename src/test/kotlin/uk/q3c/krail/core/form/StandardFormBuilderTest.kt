package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.ComboBox
import com.vaadin.ui.DateField
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.InlineDateField
import com.vaadin.ui.TextField
import com.vaadin.ui.components.grid.GridSelectionModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.guice.InjectorHolder
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.KrailI18NModule
import uk.q3c.krail.core.i18n.LabelKey
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
import java.util.*
import kotlin.NoSuchElementException

/**
 * Created by David Sowerby on 05 Jul 2018
 */
object StandardFormSectionBuilderTest : Spek({

    given(" a standard form section builder, configured with StandardFormConfiguration1") {
        lateinit var injector: Injector
        lateinit var builder: StandardFormSectionBuilder<Person>
        lateinit var binderFactory: KrailBeanValidationBinderFactory
        lateinit var binder: KrailBeanValidationBinder<Person>
        lateinit var formConfiguration: FormConfiguration
        lateinit var configuration: FormSectionConfiguration
        lateinit var propertySpecCreator: PropertyConfigurationCreator
        lateinit var formSupport: FormSupport
        lateinit var translate: Translate
        lateinit var currentLocale: CurrentLocale
        lateinit var formDaoFactory: FormDaoFactory
        lateinit var form: Form
        lateinit var testModule: StandardFormSectionBuilderTestModule

        beforeEachTest {
            form = mockk(relaxed = true)
            testModule = StandardFormSectionBuilderTestModule()

            injector = Guice.createInjector(FormModule(), KrailValidationModule(), ConverterModule(), testModule)
            InjectorHolder.setInjector(injector)
            formDaoFactory = injector.getInstance(FormDaoFactory::class.java)
            binderFactory = injector.getInstance(KrailBeanValidationBinderFactory::class.java)
            formSupport = injector.getInstance(FormSupport::class.java)
            propertySpecCreator = injector.getInstance(PropertyConfigurationCreator::class.java)
            formConfiguration = StandardFormConfiguration1()
            formConfiguration.config()
            configuration = formConfiguration.section("single")
            translate = injector.getInstance(Translate::class.java)
            currentLocale = injector.getInstance(CurrentLocale::class.java)
            builder = StandardFormSectionBuilder(entityClass = Person::class, binderFactory = binderFactory, configuration = configuration, propertySpecCreator = propertySpecCreator, formSupport = formSupport, currentLocale = currentLocale)
        }

        on("creating the detail section") {

            val serializationTracer = SerializationTracer()
            val section = builder.buildDetail(formDaoFactory, translate)
            section.loadData(mapOf(Pair("id", testUuid1)))



            it("has the correct number of components") {
                section.propertyMap.size.shouldBe(7)
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
                section.rootComponent.componentCount.shouldBe(7)
            }

            it("ignores excluded properties") {
                section.propertyMap["id"].shouldBeNull()
            }

            it("has set captions and descriptions in the property map, but not in component") {
                section.propertyMap["joinDate"]!!.component.caption.shouldBeNull()
                section.propertyMap["joinDate"]!!.captionKey.shouldBe(TestPersonKey.date_joined)
                section.propertyMap["joinDate"]!!.descriptionKey.shouldBe(TestPersonKey.date_joined)
            }

            it("should use specified caption key and description key where given") {
                section.propertyMap["joinDate"]!!.captionKey.shouldBe(TestPersonKey.date_joined)
                section.propertyMap["joinDate"]!!.descriptionKey.shouldBe(TestPersonKey.date_joined)
            }

            it("should derive key from property name where there is a match with sample key") {
                section.propertyMap["age"]!!.captionKey.shouldBe(TestPersonKey.age)
                section.propertyMap["age"]!!.descriptionKey.shouldBe(TestPersonKey.age)
            }

            it("should derive key from a delegate property name where there is a match with sample key") {
                section.propertyMap["roles"]!!.captionKey.shouldBe(TestPersonKey.roles)
                section.propertyMap["roles"]!!.descriptionKey.shouldBe(TestPersonKey.roles)
            }

            it("should use defaults where there is no match of property name in sample key") {
                section.propertyMap["title"]!!.descriptionKey.shouldBe(DescriptionKey.No_description_provided)
                section.propertyMap["title"]!!.captionKey.shouldBe(LabelKey.Unnamed)
            }

            it("has configured a combo box") {
                section.propertyMap["pricePlan"]!!.component is ComboBox<*>
                val cb = section.propertyMap["pricePlan"]!!.component as ComboBox<Int>
                with(cb) {
                    (dataProvider as ListDataProvider<Int>).items.shouldContain(1)
                    (dataProvider as ListDataProvider<Int>).items.shouldContain(3)
                    (dataProvider as ListDataProvider<Int>).items.size.shouldBe(2)
                    isEmptySelectionAllowed.shouldBeFalse()
                }
            }

            it("creates a serializable section") {
                serializationTracer.trace(section).shouldNotHaveAnyDynamicFailures()
            }


        }

        on("setting a value that should fail a JSR annotation validator") {
            val section = builder.buildDetail(formDaoFactory, translate)
            section.loadData(mapOf(Pair("id", testUuid1)))
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

        on("loading data with no 'id' parameter") {
            val section = builder.buildDetail(formDaoFactory, translate)
            val result = { section.loadData(mapOf()) }

            it("throws a MissingParameterException") {
                result.shouldThrow(MissingParameterException::class)
            }
        }
        on("attempting to load an entity which does not exist") {
            val section = builder.buildDetail(formDaoFactory, translate)
            val result = { section.loadData(mapOf(Pair("id", "99"))) }

            it("throws a NoSuchElementException") {
                result.shouldThrow(NoSuchElementException::class)
            }
        }

        on("setting a value that should fail a validator set by configuration") {
            val serializationTracer = SerializationTracer()
            val section = builder.buildDetail(formDaoFactory, translate)
            section.loadData(mapOf(Pair("id", testUuid1)))
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

        on("creating a table section") {
            val serializationTracer = SerializationTracer()
            val section = builder.buildTable(form, formDaoFactory, translate)
            section.loadData(mapOf())
            val grid: Grid<*> = section.rootComponent as Grid<*>


            it("has a read-only grid as the root component") {
                section.rootComponent.shouldBeInstanceOf(Grid::class)
                grid.editor.isEnabled.shouldBeFalse()
            }

            it("hides the columns that are not specified in columnOrder") {
                grid.columns.size.shouldBe(8)
                grid.getColumn("name").isHidden.shouldBeFalse()
                grid.getColumn("age").isHidden.shouldBeFalse()
                grid.getColumn("joinDate").isHidden.shouldBeFalse()
                grid.getColumn("id").isHidden.shouldBeTrue()
                grid.getColumn("dob").isHidden.shouldBeTrue()
                grid.getColumn("title").isHidden.shouldBeTrue()
            }

            it("has captions correctly set") {
                grid.getColumn("age").caption.shouldBeEqualTo("age")
                grid.getColumn("joinDate").caption.shouldBeEqualTo("date joined")
            }
            it("column order is correct") {
                grid.columns[0].id.shouldBeEqualTo("name")
                grid.columns[1].id.shouldBeEqualTo("age")
                grid.columns[2].id.shouldBeEqualTo("joinDate")
            }

            it("table section is a selection model listener") {
                val selectionModel: GridSelectionModel<Person> = grid.selectionModel as GridSelectionModel<Person>
                selectionModel.select(testModule.person2)
            }

            it("creates a serializable section") {
                serializationTracer.trace(section).shouldNotHaveAnyDynamicFailures()
            }

        }

        on("selecting a table item") {
            val section = builder.buildTable(form, formDaoFactory, translate)
            section.loadData(mapOf())
            val grid: Grid<*> = section.rootComponent as Grid<*>
            val selectionModel: GridSelectionModel<Person> = grid.selectionModel as GridSelectionModel<Person>
            selectionModel.select(testModule.person2)

            it("calls the form to change route to the selected id") {
                verify { form.changeRoute(testModule.person2.id) }
            }
        }


    }
})


private class StandardFormConfiguration1 : FormConfiguration() {
    override fun config() {

        val section = FormSectionConfiguration(this, "single")
        section.columnOrder = mutableSetOf("name", "age", "joinDate")
        section.excludedProperties = setOf("id")
        sections.add(section)
        section.entityClass = Person::class.java
        section.styleAttributes.borderless = StyleBorderless.yes
        val titleConfig = PropertyConfiguration("title", section)
        section.properties[titleConfig.name] = titleConfig
        titleConfig.styleAttributes.size = StyleSize.huge
        val joinDateConfig = PropertyConfiguration("joinDate", section)
        section.properties[joinDateConfig.name] = joinDateConfig
        joinDateConfig.componentClass = InlineDateField::class.java
        joinDateConfig.caption = TestPersonKey.date_joined
        joinDateConfig.description = TestPersonKey.date_joined
        section.sampleCaptionKey = TestPersonKey.age
        section.sampleDescriptionKey = TestPersonKey.age
        val rolesConfig = section.property("roles")


        val ageConfig = PropertyConfiguration("age", section)
        ageConfig.min(3)
        section.properties[ageConfig.name] = ageConfig
    }

}

private enum class TestPersonKey : I18NKey {

    date_joined, a_title, age, roles
}


private class StandardFormSectionBuilderTestModule : AbstractModule() {
    val daoFactory: FormDaoFactory = mockk(relaxed = true)
    val dao: FormDao<Person> = mockk(relaxed = true)
    val person1 = Person(name = "mock person 1", age = 12)
    val person2 = Person(id = "2", name = "mock person 2", age = 32)
    val person3 = Person(id = "3", name = "mock person 3", age = 42)
    val people: List<Person> = listOf(person1, person2, person3)

    override fun configure() {
        every { daoFactory.getDao(Person::class) } returns dao
        every { dao.get(testUuid1) } returns person1
        every { dao.get("2") } returns person2
        every { dao.get("3") } returns person3
        every { dao.get() } returns people
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



