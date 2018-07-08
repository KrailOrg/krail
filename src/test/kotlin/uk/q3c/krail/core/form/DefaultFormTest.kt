package uk.q3c.krail.core.form

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.multibindings.MapBinder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldThrow
import org.apache.bval.guice.ValidationModule
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.ServletEnvironmentModule
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.core.view.ViewConfiguration
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.util.guice.DefaultSerializationSupport
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 20 Jun 2018
 */
object DefaultFormStartupTest : Spek({

    given("a DefaultForm") {
        val formModule = FormModule1()
        val typeBuilder = formModule.typeBuilder
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
                verify { typeBuilder.build() }
            }
        }
    }

})


class DefaultFormTestModule : AbstractModule() {
    override fun configure() {
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(SerializationSupport::class.java).to(DefaultSerializationSupport::class.java)
    }

}


class AViewConfiguration : ViewConfiguration

class FormConfiguration1 : FormConfiguration() {
    override fun config() {
        section("single") {

        }
    }

}

fun selectConfiguration(masterSitemapNode: MasterSitemapNode, configClass: Class<out FormConfiguration>) {
    every {
        masterSitemapNode.viewConfiguration
    } returns configClass
}

class FormModule1 : FormModule() {
    val typeBuilder: SimpleFormBuilder = mockk(relaxed = true)

    override fun bindFormTypeBuilders(formTypeBuilderLookup: MapBinder<String, FormBuilder>) {
        formTypeBuilderLookup.addBinding("simple").toInstance(typeBuilder)
    }
}