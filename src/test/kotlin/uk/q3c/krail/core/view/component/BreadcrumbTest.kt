package uk.q3c.krail.core.view.component

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.Provider
import com.vaadin.server.VaadinSession
import fixture.ReferenceUserSitemap
import fixture.testviews2.EmptyAfterViewChangeBusMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.ServletEnvironmentModule
import uk.q3c.krail.core.eventbus.VaadinEventBusModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.i18n.KrailI18NModule
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.navigate.URIFragmentHandler
import uk.q3c.krail.core.navigate.sitemap.UserSitemap
import uk.q3c.krail.core.shiro.DefaultShiroModule
import uk.q3c.krail.core.user.UserModule
import uk.q3c.krail.core.vaadin.MockVaadinSession
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.krail.option.mock.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule
import uk.q3c.krail.util.UtilsModule
import uk.q3c.util.UtilModule
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.guice.SerializationSupportModule
import java.util.*

/**
 * Created by David Sowerby on 29 Apr 2018
 */
object BreadcrumbTest : Spek({
    given("a breadcrumb and Injector") {
        lateinit var breadcrumb: DefaultBreadcrumb
        lateinit var injector: Injector
        lateinit var userSitemap: ReferenceUserSitemap
        lateinit var navigator: Navigator
        lateinit var serializationSupport: SerializationSupport
        lateinit var navigatorProvider: Provider<Navigator>
        lateinit var userSitemapProvider: Provider<UserSitemap>
        lateinit var currentLocale: CurrentLocale

        beforeEachTest {
            VaadinSession.setCurrent(MockVaadinSession())
            Locale.setDefault(Locale.UK)

            injector = Guice.createInjector(moduleSet())

            currentLocale = injector.getInstance(CurrentLocale::class.java)

            navigatorProvider = mockk()
            navigator = mockk(relaxed = true)
            every { navigatorProvider.get() } returns navigator

            serializationSupport = injector.getInstance(SerializationSupport::class.java)

            userSitemap = injector.getInstance(ReferenceUserSitemap::class.java)
            userSitemap.populate()

            userSitemapProvider = mockk()
            every { userSitemapProvider.get() } returns userSitemap

            every { navigator.currentNode } returnsMany listOf(userSitemap.a11Node(), userSitemap.b1Node())

            breadcrumb = DefaultBreadcrumb(navigatorProvider, userSitemapProvider, serializationSupport)
        }

        on("navigating to public node a11Node") {

            breadcrumb.moveToNavigationState()

            it("displays the correct buttons") {
                with(breadcrumb) {
                    buttons.size.shouldBe(4)
                    componentCount.shouldBe(4)
                    buttons[0].caption.shouldEqual(userSitemap.publicNode().label)
                    buttons[1].caption.shouldEqual(userSitemap.aNode().label)
                    buttons[2].caption.shouldEqual(userSitemap.a1Node().label)
                    buttons[3].caption.shouldEqual(userSitemap.a11Node().label)

                    buttons[0].node.shouldEqual(userSitemap.publicNode())
                    buttons[1].node.shouldEqual(userSitemap.aNode())
                    buttons[2].node.shouldEqual(userSitemap.a1Node())
                    buttons[3].node.shouldEqual(userSitemap.a11Node())

                    buttons[0].isVisible.shouldBeTrue()
                    buttons[1].isVisible.shouldBeTrue()
                    buttons[2].isVisible.shouldBeTrue()
                    buttons[3].isVisible.shouldBeTrue()
                }
            }
        }

        on("re-using the same buttons, receiving an AfterViewChangeMessage") {

            breadcrumb.moveToNavigationState()
            breadcrumb.afterViewChange(EmptyAfterViewChangeBusMessage())

            it("displays the correct buttons") {
                with(breadcrumb) {
                    buttons.size.shouldBe(4)
                    componentCount.shouldBe(4)
                    buttons[0].caption.shouldEqual(userSitemap.privateNode().label)
                    buttons[1].caption.shouldEqual(userSitemap.bNode().label)
                    buttons[2].caption.shouldEqual(userSitemap.b1Node().label)

                    buttons[0].node.shouldEqual(userSitemap.privateNode())
                    buttons[1].node.shouldEqual(userSitemap.bNode())
                    buttons[2].node.shouldEqual(userSitemap.b1Node())

                    buttons[0].isVisible.shouldBeTrue()
                    buttons[1].isVisible.shouldBeTrue()
                    buttons[2].isVisible.shouldBeTrue()
                    buttons[3].isVisible.shouldBeFalse()
                }

            }
        }

        on("clicking a button") {
            breadcrumb.moveToNavigationState()
            breadcrumb.buttons[1].click()

            it("should invoke the navigator") {
                verify { navigator.navigateTo(breadcrumb.buttons[1].node) }
            }
        }

        on("changing currentLocale") {
            breadcrumb.moveToNavigationState()
            currentLocale.locale = Locale.GERMANY
            breadcrumb.localeChanged(LocaleChangeBusMessage(this, Locale.GERMANY))

            it("changes button caption") {
                breadcrumb.buttons[0].caption.shouldEqual("Öffentlich")
            }
        }
    }

})


private fun moduleSet(): List<Module> {
    return listOf(TestOptionModule(),
            InMemoryModule(),
            VaadinSessionScopeModule(),
            VaadinEventBusModule(),
            TestUIScopeModule(),
            UtilModule(),
            UtilsModule(),
            UserModule(),
            LocalTestI18NModule(),
            DefaultShiroModule(),
            SerializationSupportModule(),
            ServletEnvironmentModule(),
            otherBindings())
}

private class LocalTestI18NModule : KrailI18NModule() {


    override fun define() {
        super.define()
        supportedLocales(Locale.ITALY, Locale.UK, Locale.GERMANY)
        supportedLocales(Locale("de", "CH"))
    }

}

private fun otherBindings(): AbstractModule {
    return object : AbstractModule() {
        override fun configure() {
            bind(URIFragmentHandler::class.java).to(StrictURIFragmentHandler::class.java)
        }
    }
}

