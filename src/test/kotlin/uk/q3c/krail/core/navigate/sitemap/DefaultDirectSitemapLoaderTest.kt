package uk.q3c.krail.core.navigate.sitemap

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import fixture.testviews2.OptionsView
import fixture.testviews2.ViewA
import fixture.testviews2.ViewA1
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.config.ConfigurationFileModule
import uk.q3c.krail.core.config.KrailApplicationConfigurationModule
import uk.q3c.krail.core.env.ServletEnvironmentModule
import uk.q3c.krail.core.eventbus.VaadinEventBusModule
import uk.q3c.krail.core.form.FormConfiguration
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.i18n.KrailI18NModule
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.NavigationModule
import uk.q3c.krail.core.push.PushModule
import uk.q3c.krail.core.shiro.DefaultShiroModule
import uk.q3c.krail.core.shiro.PageAccessControl.*
import uk.q3c.krail.core.shiro.ShiroVaadinModule
import uk.q3c.krail.core.ui.DataTypeModule
import uk.q3c.krail.core.ui.DefaultUIModule
import uk.q3c.krail.core.user.UserModule
import uk.q3c.krail.core.view.EmptyViewConfiguration
import uk.q3c.krail.core.view.ViewModule
import uk.q3c.krail.core.view.component.DefaultComponentModule
import uk.q3c.krail.eventbus.mbassador.EventBusModule
import uk.q3c.krail.i18n.test.TestLabelKey
import uk.q3c.krail.option.mock.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.util.UtilsModule
import uk.q3c.util.UtilModule
import uk.q3c.util.guice.SerializationSupportModule

/**
 * Created by David Sowerby on 01 Aug 2018
 */
object DefaultDirectSitemapLoaderTest : Spek({

    given(" a Guice constructed DirectSitemapLoader") {

        lateinit var injector: Injector
        lateinit var map: Map<String, DirectSitemapEntry>
        lateinit var loader: DirectSitemapLoader
        lateinit var sitemap: MasterSitemap

        beforeEachTest {
            injector = Guice.createInjector(testModules())
            loader = injector.getInstance(DirectSitemapLoader::class.java)
            sitemap = injector.getInstance(MasterSitemap::class.java)
            map = loader.pageMap

        }

        on("loading") {
            val result = loader.load(sitemap)

            it("has correctly assembled pages") {
                result.shouldBeTrue()
                sitemap.hasUri(page1).shouldBeTrue()
                sitemap.hasUri(page2).shouldBeTrue()
                sitemap.hasUri(page3).shouldBeTrue()
                sitemap.nodeCount.shouldEqual(5)
            }

            it("has provided complete configuration detail to the Sitemap") {
                with(map[page1]!!) {
                    labelKey.shouldEqual(LabelKey.Authorisation)
                    pageAccessControl.shouldEqual(PERMISSION)
                    viewClass.shouldEqual(ViewA::class.java)
                    viewConfiguration.shouldEqual(EmptyViewConfiguration::class.java)
                    roles.shouldEqual("")
                    positionIndex.shouldEqual(1)
                    moduleName.shouldEqual("TestDirectSitemapModule_A")
                }

                with(map[page2]!!) {
                    labelKey.shouldEqual(TestLabelKey.Opt)
                    pageAccessControl.shouldEqual(PUBLIC)
                    viewClass.shouldEqual(OptionsView::class.java)
                    viewConfiguration.shouldEqual(OptionViewConfiguration::class.java)
                    roles.shouldEqual("")
                    positionIndex.shouldEqual(3)
                    moduleName.shouldEqual("TestDirectSitemapModule_B")
                }

                with(map[page3]!!) {
                    labelKey.shouldEqual(TestLabelKey.MoneyInOut)
                    pageAccessControl.shouldEqual(ROLES)
                    viewClass.shouldEqual(ViewA1::class.java)
                    viewConfiguration.shouldEqual(EmptyViewConfiguration::class.java)
                    roles.shouldEqual("admin manager")
                    positionIndex.shouldEqual(1)
                    moduleName.shouldEqual("TestDirectSitemapModule_B")
                }
            }
        }

    }

})


val page1 = "private/page1"
val page2 = "public/options"
val page3 = "public/options/detail"

private fun testModules(): List<Module> {
    return listOf(TestDirectSitemapModule_A(), TestDirectSitemapModule_B(), UIScopeModule(), ViewModule(), ConfigurationFileModule(),
            ShiroVaadinModule(), KrailI18NModule(), SitemapModule(), UserModule(), InMemoryModule(), TestOptionModule(),
            KrailApplicationConfigurationModule(), DefaultShiroModule(), DefaultComponentModule(), VaadinSessionScopeModule(), NavigationModule(),
            VaadinEventBusModule(), SerializationSupportModule(), ServletEnvironmentModule(), PushModule(), EventBusModule(), UtilsModule(), DefaultUIModule(), DataTypeModule(), UtilsModule(), UtilModule())
}

private class TestDirectSitemapModule_A : DirectSitemapModule() {

    override fun define() {
        addEntry(page1, LabelKey.Authorisation, PERMISSION, ViewA::class.java)
    }

}

private class TestDirectSitemapModule_B : DirectSitemapModule() {

    override fun define() {
        addEntry(page2, TestLabelKey.Opt, PUBLIC, OptionsView::class.java, OptionViewConfiguration::class.java, "", 3)
        addEntry(page3, TestLabelKey.MoneyInOut, ROLES, ViewA1::class.java, EmptyViewConfiguration::class.java, "admin manager")
    }
}

private class OptionViewConfiguration : FormConfiguration() {
    override fun config() {
    }

}