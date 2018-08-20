package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.AbstractField
import com.vaadin.ui.AbstractMultiSelect
import com.vaadin.ui.AbstractSingleSelect
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
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.user.notify.UserNotifier
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

@Suppress("UNCHECKED_CAST")
/**
 * Created by David Sowerby on 05 Jul 2018
 */
object StandardFormSectionBuilderTest : Spek({

    given(" a standard form section builder, configured with StandardFormConfiguration1") {
        lateinit var injector: Injector
        lateinit var builder: StandardFormSectionBuilder<Person>
        lateinit var binderFactory: KrailBeanValidationBinderFactory
        lateinit var formConfiguration: FormConfiguration
        lateinit var configuration: FormSectionConfiguration
        lateinit var propertySpecCreator: PropertyConfigurationCreator
        lateinit var formSupport: FormSupport
        lateinit var translate: Translate
        lateinit var currentLocale: CurrentLocale
        lateinit var formDaoFactory: FormDaoFactory
        lateinit var form: Form
        lateinit var testModule: StandardFormSectionBuilderTestModule
        lateinit var editSaveCancelBuilder: EditSaveCancelBuilder
        lateinit var userNotifier: UserNotifier

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
            editSaveCancelBuilder = mockk(relaxed = true)
            userNotifier = mockk(relaxed = true)
            builder = StandardFormSectionBuilder(entityClass = Person::class, binderFactory = binderFactory, configuration = configuration, propertySpecCreator = propertySpecCreator, formSupport = formSupport, currentLocale = currentLocale, userNotifier = userNotifier)
        }

        on("creating the detail section") {

            every { editSaveCancelBuilder.hasTopComponent() } returns true
            every { editSaveCancelBuilder.hasBottomComponent() } returns true
            every { editSaveCancelBuilder.topComponent() } returns DefaultEditSaveCancel()
            every { editSaveCancelBuilder.bottomComponent() } returns DefaultEditSaveCancel()

            val serializationTracer = SerializationTracer()
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
            section.loadData(mapOf(Pair("id", testUuid1)))
            val layout = (section.rootComponent as FormLayout)


            it("has the correct number of components") {
                layout.getComponent(0).shouldBeInstanceOf(EditSaveCancel::class)
                section.propertyMap.size.shouldBe(7)
                layout.getComponent(8).shouldBeInstanceOf(EditSaveCancel::class)
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

            it("has added the components to the layout, including 2 x EditSaveCancel") {
                section.rootComponent.componentCount.shouldBe(9)
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
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
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
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
            val result = { section.loadData(mapOf()) }

            it("throws a MissingParameterException") {
                result.shouldThrow(MissingParameterException::class)
            }
        }


        on("setting a value that should fail a validator set by configuration") {
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
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
            val grid: Grid<*> = section.rootComponent as Grid<*>
            val selectionModel: GridSelectionModel<Person> = grid.selectionModel as GridSelectionModel<Person>
            selectionModel.select(testModule.person2)

            it("calls the form to change route to the selected id") {
                verify { form.changeRoute(testModule.person2.id) }
            }
        }

        on("detail section should be read only on construction") {
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)

            it("makes components read only") {
                with(section) {
                    (propertyMap["dob"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["title"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["age"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["name"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["pricePlan"]!!.component as AbstractSingleSelect<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["roles"]!!.component as AbstractMultiSelect<*>).isReadOnly.shouldBeTrue()

                }
            }
        }

        on("attempting to load an entity which does not exist") {
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
            val result = { section.loadData(mapOf(Pair("id", "99"))) }

            it("throws a NoSuchElementException") {
                result.shouldThrow(NoSuchElementException::class)
            }
        }

        on(" calling editData()") {
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
            section.editData()

            it("changes to edit mode") {
                section.mode.shouldBe(EditMode.EDIT)
            }

            it("updates field read only states") {
                with(section) {
                    (propertyMap["dob"]!!.component as AbstractField<*>).isReadOnly.shouldBeFalse()
                    (propertyMap["title"]!!.component as AbstractField<*>).isReadOnly.shouldBeFalse()
                    (propertyMap["age"]!!.component as AbstractField<*>).isReadOnly.shouldBeFalse()
                    (propertyMap["name"]!!.component as AbstractField<*>).isReadOnly.shouldBeFalse()
                    (propertyMap["pricePlan"]!!.component as AbstractSingleSelect<*>).isReadOnly.shouldBeFalse()
                    (propertyMap["roles"]!!.component as AbstractMultiSelect<*>).isReadOnly.shouldBeFalse()

                }
            }

        }

        on(" calling cancelData() after editData()") {
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
            section.loadData(mapOf(Pair("id", testUuid1)))
            section.editData()
            (section.propertyMap["age"]!!.component as AbstractField<String>).value = "11"
            section.cancelData()

            it("changes to read only mode") {
                section.mode.shouldBe(EditMode.READ_ONLY)
            }

            it("updates field read only states") {
                with(section) {
                    (propertyMap["dob"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["title"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["age"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["name"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["pricePlan"]!!.component as AbstractSingleSelect<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["roles"]!!.component as AbstractMultiSelect<*>).isReadOnly.shouldBeTrue()

                }
            }

            it("reloads the displayed fields") {
                (section.propertyMap["age"]!!.component as AbstractField<String>).value.shouldBeEqualTo("12")
            }

            it("has not updated the data") {
                formDaoFactory.getDao(Person::class).get(testUuid1).age.shouldBe(12)
            }

        }

        on("calling save data after editing data") {
            val section = builder.buildDetail(formDaoFactory, translate, editSaveCancelBuilder)
            section.loadData(mapOf(Pair("id", testUuid1)))
            section.editData()
            (section.propertyMap["age"]!!.component as AbstractField<String>).value = "11"
            section.saveData()

            it("changes to read only mode") {
                section.mode.shouldBe(EditMode.READ_ONLY)
            }

            it("updates field read only states") {
                with(section) {
                    (propertyMap["dob"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["title"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["age"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["name"]!!.component as AbstractField<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["pricePlan"]!!.component as AbstractSingleSelect<*>).isReadOnly.shouldBeTrue()
                    (propertyMap["roles"]!!.component as AbstractMultiSelect<*>).isReadOnly.shouldBeTrue()

                }
            }

            it("has updated the data") {
                formDaoFactory.getDao(Person::class).get(testUuid1).age.shouldBe(11)
            }
        }

        on("table section should be readonly") {
            val section = builder.buildTable(form, formDaoFactory, translate)

            it("disables Grid editor") {
                section.mode.shouldBe(EditMode.READ_ONLY)
                (section.rootComponent as Grid<*>).editor.isEnabled.shouldBeFalse()
                (section.rootComponent as Grid<*>).columns.forEach { column -> column.isEditable.shouldBeFalse() }
            }
        }


    }
})


private class StandardFormConfiguration1 : FormConfiguration() {
    override fun config() {
        section("single")
                .columnOrder("name", "age", "joinDate")
                .excludedProperties("id")
                .entityClass(Person::class.java)
                .styleAttributes(borderless = StyleBorderless.yes)
                .sampleCaptionKey(TestPersonKey.age)
                .sampleDescriptionKey(TestPersonKey.age)
                .property("title").styleAttributes(size = StyleSize.huge).end()
                .property("joinDate").componentClass(InlineDateField::class.java).caption(TestPersonKey.date_joined).description(TestPersonKey.date_joined).end()
                .property("age").min(3).end()
                .property("pricePlan").fieldType(FieldType.SINGLE_SELECT).selectDataProvider(PricePlanDataProvider::class.java).end()
                .property("roles").fieldType(FieldType.MULTI_SELECT).selectDataProvider(YesNoDataProvider::class.java)
    }

}

private enum class TestPersonKey : I18NKey {

    date_joined, a_title, age, roles
}

class PricePlanDataProvider : ListDataProvider<Int>(listOf(1, 3))


private class StandardFormSectionBuilderTestModule : AbstractModule() {
    val daoFactory: FormDaoFactory = mockk(relaxed = true)
    val dao: FormDao<Person> = mockk(relaxed = true)
    val person1 = Person(name = "mock person 1", age = 12)
    val person2 = Person(id = "2", name = "mock person 2", age = 32)
    val person3 = Person(id = "3", name = "mock person 3", age = 42)
    val people: List<Person> = listOf(person1, person2, person3)

    val navigator: Navigator = mockk(relaxed = true)
    val userNotifier: UserNotifier = mockk(relaxed = true)

    override fun configure() {
        every { daoFactory.getDao(Person::class) } returns dao
        every { dao.get(testUuid1) } returns person1
        every { dao.get("2") } returns person2
        every { dao.get("3") } returns person3
        every { dao.get() } returns people
        every { dao.get("99") } throws (NoSuchElementException())
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(CurrentLocale::class.java).toInstance(MockCurrentLocale())
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(MessageFormat2::class.java).to(DefaultMessageFormat::class.java)
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(FormDaoFactory::class.java).toInstance(daoFactory)
        bind(Navigator::class.java).toInstance(navigator)
        bind(UserNotifier::class.java).toInstance(userNotifier)
    }

}

private class LocalTestI18NModule : KrailI18NModule() {


    override fun define() {
        super.define()
        supportedLocales(Locale.ITALY, Locale.UK, Locale.GERMANY)
        supportedLocales(Locale("de", "CH"))
    }

}



