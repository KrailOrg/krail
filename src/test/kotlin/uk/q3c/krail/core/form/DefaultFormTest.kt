package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provider
import com.google.inject.multibindings.MapBinder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldThrow
import org.apache.bval.guice.ValidationModule
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.ServletEnvironmentModule
import uk.q3c.krail.core.env.ServletInjectorLocator
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.persist.FormDaoModule
import uk.q3c.krail.core.validation.KrailValidationModule
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.core.view.ViewConfiguration
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
 * Created by David Sowerby on 20 Jun 2018
 */
object DefaultFormTest : Spek({

    given("a DefaultForm") {
        val formModule = FormModule1()
        val typeBuilder = formModule.formBuilder
        val injector: Injector = Guice.createInjector(formModule, DefaultFormTestModule(), ServletEnvironmentModule(), ValidationModule(), ConverterModule())
        val injectorLocator: InjectorLocator = injector.getInstance(InjectorLocator::class.java)
        injectorLocator.put(injector)
        lateinit var form: DefaultForm
        lateinit var masterSitemapNode: MasterSitemapNode
        lateinit var navigationStateExt: NavigationStateExt


        beforeEachTest {
            form = injector.getInstance(DefaultForm::class.java)
            masterSitemapNode = mockk(relaxed = true)
            val userSitemapNode: UserSitemapNode = mockk(relaxed = true)
            every {
                userSitemapNode.masterNode
            } returns masterSitemapNode
            navigationStateExt = NavigationStateExt(from = null, to = NavigationState(), node = userSitemapNode)


        }

        on("building the form with a ViewConfiguration (instead of Form Configuration)") {
            every {
                masterSitemapNode.viewConfiguration
            } returns AViewConfiguration::class.java

            form.beforeBuild(navigationStateExt)
            val result = { form.buildView() }

            it("throws exception") {
                result.shouldThrow(FormConfigurationException::class)
            }
        }


        on("building the form with EmptyFormConfiguration") {
            every {
                masterSitemapNode.viewConfiguration
            } returns EmptyFormConfiguration::class.java

            form.beforeBuild(navigationStateExt)
            val result = { form.buildView() }

            it("throws exception") {
                result.shouldThrow(FormConfigurationException::class)
            }
        }

        on("building the form with valid FormConfiguration, default form type") {
            every {
                masterSitemapNode.viewConfiguration
            } returns FormConfiguration1::class.java

            form.beforeBuild(navigationStateExt)
            form.buildView()

            it("FormTypeBuilder is invoked") {
                verify { typeBuilder.build(form, any()) }
            }
        }
    }

})

object FormModuleTest : Spek({

    given("a FormModule") {
        lateinit var injector: Injector

        beforeEachTest {
            val module = FormModule()
            injector = Guice.createInjector(module, KrailValidationModule(), ConverterModule(), DefaultFormTestModule(), FormDaoModule())
        }

        on("using module in Guice") {

            val builderProvider = injector.getProvider(FormBuilderSelector::class.java)
            val formConfig = FormConfiguration1()


            it("returns a StandardFormBuilder for 'standard'") {
                builderProvider.get().selectFormBuilder(formConfig).shouldBeInstanceOf(StandardFormBuilder::class)
            }
        }

    }

})


object FormConstructionTest : Spek({
    given("") {
        lateinit var injector: Injector
        lateinit var form: Form
        lateinit var formModule: FormModule2
        lateinit var formBuilder: FormBuilder
        val fromState: NavigationState = mockk(relaxed = true)
        val toStateWithIdParams: NavigationState = mockk(relaxed = true)
        every { toStateWithIdParams.parameters } returns mapOf(Pair("id", "23"))
        val toStateWithoutIdParams: NavigationState = mockk(relaxed = true)
        every { toStateWithoutIdParams.parameters } returns mapOf(Pair("otherParam", "23"))
        lateinit var masterSitemapNode: MasterSitemapNode
        val userSitemapNode: UserSitemapNode = mockk(relaxed = true)
        val navigationStateWithIdParams = NavigationStateExt(from = fromState, to = toStateWithIdParams, node = userSitemapNode)
        val navigationStateWithoutIdParams = NavigationStateExt(from = fromState, to = toStateWithoutIdParams, node = userSitemapNode)

        beforeEachTest {
            formModule = FormModule2()
            formBuilder = mockk(relaxed = true)
            every { formModule.formBuilderSelector.selectFormBuilder(any()) } returns formBuilder
            masterSitemapNode = mockk(relaxed = true)
            every { masterSitemapNode.viewConfiguration } returns FormConfiguration1::class.java
            every { userSitemapNode.masterNode } returns masterSitemapNode
            injector = Guice.createInjector(formModule, KrailValidationModule(), ConverterModule(), DefaultFormTestModule(), FormDaoModule())
            form = injector.getInstance(DefaultForm::class.java)
        }

        on("entering the form with an id param") {
            form.beforeBuild(navigationStateWithIdParams)
            form.buildView()

            it("calls the builder to provider a detail view") {
                verify { formBuilder.build(form, navigationStateWithIdParams) }
            }
        }

        on("entering the form without an id param") {
            form.beforeBuild(navigationStateWithoutIdParams)
            form.buildView()

            it("calls the builder to provider a table view") {
                verify { formBuilder.build(form, navigationStateWithoutIdParams) }
            }
        }
    }
})

object FormChangeRouteSpek : Spek({

    given("Mocked inputs") {
        val translate: Translate = mockk(relaxed = true)
        val serializationSupport: SerializationSupport = mockk(relaxed = true)
        val uriFragmentHandler = StrictURIFragmentHandler()
        val navigator: Navigator = mockk(relaxed = true)
        val currentLocale: CurrentLocale = mockk(relaxed = true)
        val formBuilderSelectorProvider: Provider<FormBuilderSelector> = mockk(relaxed = true)
        val form = DefaultForm(translate, serializationSupport, navigator, uriFragmentHandler, currentLocale, formBuilderSelectorProvider)
        val userSitemapNode: UserSitemapNode = mockk(relaxed = true)
        val navStateTo = NavigationState().fragment("person")
        navStateTo.update(uriFragmentHandler)
        val navState = NavigationStateExt(NavigationState(), navStateTo, userSitemapNode)

        on("invoking a route by new id") {
            val expectedState = NavigationState().fragment("person/id=1")
            expectedState.update(uriFragmentHandler)
            form.beforeBuild(navState)
            form.changeRoute("1")

            it("navigates to detail with id set in params") {
                verify { navigator.navigateTo(expectedState) }
            }
        }
    }
})


class DefaultFormTestModule : AbstractModule() {

    val navigator: Navigator = mockk(relaxed = true)

    override fun configure() {
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(CurrentLocale::class.java).toInstance(MockCurrentLocale())
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
        bind(MessageFormat2::class.java).to(DefaultMessageFormat::class.java)
        bind(InjectorLocator::class.java).to(ServletInjectorLocator::class.java)
        bind(Navigator::class.java).toInstance(navigator)
    }

}


class AViewConfiguration : ViewConfiguration

class FormConfiguration1 : FormConfiguration() {

    override fun config() {
        section {
            entityClass = Person::class.java
        }
    }

}

fun selectConfiguration(masterSitemapNode: MasterSitemapNode, configClass: Class<out FormConfiguration>) {
    every {
        masterSitemapNode.viewConfiguration
    } returns configClass
}

class FormModule1 : FormModule() {
    val formBuilder: StandardFormBuilder = mockk(relaxed = true)

    override fun bindFormBuilders(formBuilderLookup: MapBinder<String, FormBuilder>) {
        formBuilderLookup.addBinding("standard").toInstance(formBuilder)
    }
}


class FormModule2 : FormModule() {
    val formBuilderSelector: FormBuilderSelector = mockk(relaxed = true)

    override fun bindFormBuilderSelector() {
        bind(FormBuilderSelector::class.java).toInstance(formBuilderSelector)
    }
}