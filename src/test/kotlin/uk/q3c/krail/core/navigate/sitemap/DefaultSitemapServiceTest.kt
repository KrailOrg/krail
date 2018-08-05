package uk.q3c.krail.core.navigate.sitemap

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.config.ApplicationConfiguration
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.Descriptions
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.navigate.URIFragmentHandler
import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.vaadin.MockVaadinSession
import uk.q3c.krail.core.vaadin.createMockVaadinSession
import uk.q3c.krail.core.view.PublicHomeView
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockCurrentLocale
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.krail.service.Cause
import uk.q3c.krail.service.State
import uk.q3c.krail.util.DefaultResourceUtils
import uk.q3c.krail.util.ResourceUtils
import uk.q3c.util.clazz.ClassNameUtils
import uk.q3c.util.clazz.DefaultClassNameUtils
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.text.DefaultMessageFormat
import uk.q3c.util.text.MessageFormat2

/**
 * Created by David Sowerby on 10 May 2018
 */
object DefaultSitemapServiceTest : Spek({

    given("a Guice built sitemap service") {
        val resourceUtils: ResourceUtils = DefaultResourceUtils()
        lateinit var mockVaadinSession: MockVaadinSession
        lateinit var injector: Injector
        lateinit var config: ApplicationConfiguration
        lateinit var service: SitemapService
        lateinit var testModule: LocalTestModule

        beforeEachTest {
            testModule = LocalTestModule()
            mockVaadinSession = createMockVaadinSession()
            config = testModule.applicationConfiguration
            val modules = guiceModules()
            modules.add(testModule)
            injector = Guice.createInjector(modules)
            service = injector.getInstance(SitemapService::class.java)
        }
        afterEachTest {
            mockVaadinSession.clear()
        }

        on("service construction") {
            it("has name and description set") {
                service.nameKey.shouldEqual(LabelKey.Sitemap_Service)
                service.descriptionKey.shouldEqual(DescriptionKey.Sitemap_Service)
                service.name.shouldEqual("Sitemap Service")
                val descriptions = Descriptions()
                descriptions.keyClass = DescriptionKey::class.java
                descriptions.load()
                descriptions.map.get(service.descriptionKey).shouldEqual("This service creates the Sitemap using options from the application configuration")
            }
        }



        on("sources being empty") {
            every<List<String>> { config.getPropertyValue(SITEMAP_SOURCES, any()) } returns arrayListOf(SitemapSourceType.ANNOTATION.name, SitemapSourceType.DIRECT.name)
            service.start()

            it("uses default sources") {
                service.state.shouldEqual(State.RUNNING)
                service.sourceTypes.size.shouldBe(2)
                service.sourceTypes.shouldContain(SitemapSourceType.ANNOTATION)
                service.sourceTypes.shouldContain(SitemapSourceType.DIRECT)
            }
        }

        on("sources being defined") {
            every<List<String>> { config.getPropertyValue(SITEMAP_SOURCES, any()) } returns arrayListOf(SitemapSourceType.ANNOTATION.name)
            service.start()
            it("uses defined sources") {
                service.state.shouldEqual(State.RUNNING)
                service.sourceTypes.size.shouldBe(1)
                service.sourceTypes.shouldContain(SitemapSourceType.ANNOTATION)
            }
        }

        on("only invalid source defined") {
            every<List<String>> { config.getPropertyValue(SITEMAP_SOURCES, any()) } returns arrayListOf("rubbish")
            service.start()
            it("uses defined sources") {
                service.state.shouldEqual(State.FAILED)
                service.cause.shouldBe(Cause.FAILED_TO_START)
                service.sourceTypes.size.shouldBe(0)
            }
        }
    }

})

private fun guiceModules(): MutableList<Module> {
    return mutableListOf(TestDirectSitemapModule())
}

private class TestDirectSitemapModule : DirectSitemapModule() {

    override fun define() {
        addEntry("direct", LabelKey.Home_Page, PageAccessControl.PUBLIC)
        addEntry("direct/a", LabelKey.Home_Page, PageAccessControl.PUBLIC, PublicHomeView::class.java)
        addRedirect("direct", "direct/a")
    }
}

private class LocalTestModule : SitemapModule() {
    val applicationConfiguration: ApplicationConfiguration = mockk(relaxed = true)
    val messageBus: MessageBus = mockk(relaxed = true)
    val serialisationSupport: SerializationSupport = mockk()
    val userSitemap: UserSitemap = mockk(relaxed = true)

    override fun configure() {
        super.configure()
        bind(ApplicationConfiguration::class.java).toInstance(applicationConfiguration)
        bind(URIFragmentHandler::class.java).to(StrictURIFragmentHandler::class.java)
        bind(MessageBus::class.java).toInstance(messageBus)
        bind(CurrentLocale::class.java).toInstance(MockCurrentLocale())
        bind(Translate::class.java).toInstance(MockTranslate())
        bind(ClassNameUtils::class.java).to(DefaultClassNameUtils::class.java)
        bind(SerializationSupport::class.java).toInstance(serialisationSupport)
        bind(MessageFormat2::class.java).to(DefaultMessageFormat::class.java)
    }


    override fun bindUserSitemap() {
        bind(UserSitemap::class.java).toInstance(userSitemap)
    }
}


// UIScopeModule::class.java, ViewModule::class.java, VaadinEventBusModule::class.java,
//ShiroVaadinModule::class.java, TestKrailI18NModule::class.java, , UserModule::class.java, KrailApplicationConfigurationModule::class.java, DefaultShiroModule::class.java,
//DefaultComponentModule::class.java, InMemoryModule::class.java, StandardPagesModule::class.java, VaadinSessionScopeModule::class.java,
//NavigationModule::class.java, ServletEnvironmentModule::class.java, SerializationSupportModule::class.java, PushModule::class.java, EventBusModule::class.java, UtilsModule::class.java, UtilModule::class.java, DefaultUIModule::class.java, TestOptionModule::class.java