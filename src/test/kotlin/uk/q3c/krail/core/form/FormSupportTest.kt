package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.vaadin.data.converter.StringToIntegerConverter
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.CheckBox
import com.vaadin.ui.CheckBoxGroup
import com.vaadin.ui.ComboBox
import com.vaadin.ui.DateField
import com.vaadin.ui.DateTimeField
import com.vaadin.ui.ListSelect
import com.vaadin.ui.RadioButtonGroup
import com.vaadin.ui.TextField
import com.vaadin.ui.TwinColSelect
import io.mockk.mockk
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.ConfigurationException
import uk.q3c.krail.core.env.RunningOn
import uk.q3c.krail.core.env.RuntimeEnvironment
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.guice.InjectorHolder
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.Sitemap
import uk.q3c.krail.core.persist.FormDaoModule
import uk.q3c.krail.core.user.notify.UserNotifier
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
        lateinit var formConfiguration: FormConfiguration

        beforeEachTest {
            injector = Guice.createInjector(FormModule(), KrailValidationModule(), FormSupportTestModule(), ConverterModule(), FormDaoModule())
            InjectorHolder.setInjector(injector)
            formSupport = injector.getInstance(FormSupport::class.java)
            formConfiguration = mockk(relaxed = true)
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
        on("requesting a UI Field from standard data type class") {

            it("returns the correct field type") {
                (formSupport as DefaultFormSupport).componentFor(String::class).shouldBeInstanceOf(TextField::class.java)
                (formSupport as DefaultFormSupport).componentFor(Int::class).shouldBeInstanceOf(TextField::class.java)
                (formSupport as DefaultFormSupport).componentFor(Boolean::class).shouldBeInstanceOf(CheckBox::class.java)
                (formSupport as DefaultFormSupport).componentFor(LocalDateTime::class).shouldBeInstanceOf(DateTimeField::class.java)
                (formSupport as DefaultFormSupport).componentFor(LocalDate::class).shouldBeInstanceOf(DateField::class.java)
            }
        }

        on("requesting a UI Field from PropertyConfiguration, standard data type") {
            val component = formSupport.componentFor(propertyConfigStandard(formConfiguration))

            it("returns the correct field type") {
                component.shouldBeInstanceOf(TextField::class.java)
            }
        }

        on("requesting a UI Field from PropertyConfiguration, single select data type, without defined data provider") {
            val result = { formSupport.componentFor(propertyConfigSingleSelect(formConfiguration)) }

            it("throws ConfigurationException") {
                result.shouldThrow(ConfigurationException::class)
            }
        }

        on("requesting a UI Field from PropertyConfiguration, single select data type") {
            val component = formSupport.componentFor(propertyConfigSingleSelect(formConfiguration).selectDataProvider(YesNoDataProvider::class.java))

            it("returns the default single select field type") {
                component.shouldBeInstanceOf(ComboBox::class.java)
            }

            it("should have data provider set") {
                ((component as ComboBox).dataProvider as ListDataProvider).items.shouldContainAll(listOf("Yes", "No"))
            }
        }

        on("requesting a UI Field from PropertyConfiguration, single select data type with explicit presentation type") {
            val component = formSupport.componentFor(propertyConfigSingleSelect(formConfiguration).selectDataProvider(YesNoDataProvider::class.java).singleSelectComponent(SingleSelectComponent.RADIO_GROUP))

            it("returns the default single select field type") {
                component.shouldBeInstanceOf(RadioButtonGroup::class.java)
            }

            it("should have data provider set") {
                ((component as RadioButtonGroup).dataProvider as ListDataProvider).items.shouldContainAll(listOf("Yes", "No"))
            }
        }

        on("requesting a UI Field from PropertyConfiguration, multi select data type") {
            val component = formSupport.componentFor(propertyConfigMultiSelect(formConfiguration).selectDataProvider(YesNoDataProvider::class.java))

            it("returns the default multi select field type") {
                component.shouldBeInstanceOf(CheckBoxGroup::class.java)
            }

            it("should have data provider set") {
                ((component as CheckBoxGroup<*>).dataProvider as ListDataProvider).items.shouldContainAll(listOf("Yes", "No"))
            }
        }

        on("requesting a UI Field from PropertyConfiguration, multi select data type, componet explicitly set to LIST_SELECT ") {
            val component = formSupport.componentFor(propertyConfigMultiSelect(formConfiguration).selectDataProvider(YesNoDataProvider::class.java).multiSelectComponent(MultiSelectComponent.LIST_SELECT))

            it("returns the default multi select field type") {
                component.shouldBeInstanceOf(ListSelect::class.java)
            }

            it("should have data provider set") {
                ((component as ListSelect<*>).dataProvider as ListDataProvider).items.shouldContainAll(listOf("Yes", "No"))
            }
        }

        on("requesting a UI Field from PropertyConfiguration, multi select data type, componet explicitly set to TWIN_COL_SELECT ") {
            val component = formSupport.componentFor(propertyConfigMultiSelect(formConfiguration).selectDataProvider(YesNoDataProvider::class.java).multiSelectComponent(MultiSelectComponent.TWIN_COL_SELECT))

            it("returns the default multi select field type") {
                component.shouldBeInstanceOf(TwinColSelect::class.java)
            }

            it("should have data provider set") {
                ((component as TwinColSelect<*>).dataProvider as ListDataProvider).items.shouldContainAll(listOf("Yes", "No"))
            }
        }

        on("requesting a UI Field from PropertyConfiguration, multi select data type, without defined data provider") {
            val result = { formSupport.componentFor(propertyConfigMultiSelect(formConfiguration)) }

            it("throws ConfigurationException") {
                result.shouldThrow(ConfigurationException::class)
            }
        }

        on("requesting a UI Field for a data type which has not been defined") {
            val result = { (formSupport as DefaultFormSupport).componentFor(Sitemap::class) }

            it("throws exception") {
                result.shouldThrow(ConfigurationException::class)
            }
        }
    }
}
)


private class FormSupportTestModule : AbstractModule() {
    val navigator: Navigator = mockk(relaxed = true)
    val userNotifier: UserNotifier = mockk(relaxed = true)
    override fun configure() {
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(RuntimeEnvironment::class.java).annotatedWith(RunningOn::class.java).toInstance(RuntimeEnvironment.SERVLET)
        bind(CurrentLocale::class.java).toInstance(MockCurrentLocale())
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(MessageFormat2::class.java).to(DefaultMessageFormat::class.java)
        bind(Navigator::class.java).toInstance(navigator)
        bind(UserNotifier::class.java).toInstance(userNotifier)
    }
}

private fun propertyConfigStandard(parentConfiguration: ParentConfiguration): PropertyConfiguration {
    val config = PropertyConfiguration(name = "any", parentConfiguration = parentConfiguration)
    config.propertyValueClass(String::class.java)
    return config
}

private fun propertyConfigSingleSelect(parentConfiguration: ParentConfiguration): PropertyConfiguration {
    val config = PropertyConfiguration(name = "any", parentConfiguration = parentConfiguration)
    config.fieldType(FieldType.SINGLE_SELECT)
    return config
}

private fun propertyConfigMultiSelect(parentConfiguration: ParentConfiguration): PropertyConfiguration {
    val config = PropertyConfiguration(name = "any", parentConfiguration = parentConfiguration)
    config.fieldType(FieldType.MULTI_SELECT)
    return config
}

