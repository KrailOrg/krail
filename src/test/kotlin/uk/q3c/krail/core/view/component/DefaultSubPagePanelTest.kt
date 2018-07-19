package uk.q3c.krail.core.view.component

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.Provider
import fixture.ReferenceUserSitemap
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldContainAll
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
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters
import uk.q3c.krail.core.shiro.DefaultShiroModule
import uk.q3c.krail.core.user.UserModule
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.krail.option.Option
import uk.q3c.krail.option.mock.MockOption
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
object DefaultSubPagePanelTest : Spek({
    given("a breadcrumb and Injector") {
        lateinit var panel: DefaultSubPagePanel
        lateinit var injector: Injector
        lateinit var userSitemap: ReferenceUserSitemap
        lateinit var navigator: Navigator
        lateinit var serializationSupport: SerializationSupport
        lateinit var navigatorProvider: Provider<Navigator>
        lateinit var userSitemapProvider: Provider<UserSitemap>
        lateinit var currentLocale: CurrentLocale
        lateinit var optionProvider: Provider<Option>
        lateinit var sorters: UserSitemapSorters
        lateinit var option: Option

        beforeEachTest {
            Locale.setDefault(Locale.UK)

            injector = Guice.createInjector(moduleSet())

            currentLocale = injector.getInstance(CurrentLocale::class.java)
            sorters = mockk(relaxed = true)

            navigatorProvider = mockk()
            navigator = mockk(relaxed = true)
            every { navigatorProvider.get() } returns navigator

            optionProvider = mockk()
            option = mockk(relaxed = true)
            every { optionProvider.get() } returns option
            every { option.get(DefaultSubPagePanel.optionSortAscending) } returns true
            every { option.get(DefaultSubPagePanel.optionSortType) } returns DefaultUserSitemapSorters.SortType.ALPHA

            serializationSupport = injector.getInstance(SerializationSupport::class.java)

            userSitemap = injector.getInstance(ReferenceUserSitemap::class.java)
            userSitemap.populate()

            userSitemapProvider = mockk()
            every { userSitemapProvider.get() } returns userSitemap

            every { navigator.currentNode } returnsMany listOf(userSitemap.a111Node(), userSitemap.publicNode())


            panel = DefaultSubPagePanel(navigatorProvider = navigatorProvider, userSitemapProvider = userSitemapProvider, serializationSupport = serializationSupport, optionProvider = optionProvider, sorters = sorters)
        }

        on("navigating to a leaf node") {

            panel.moveToNavigationState()

            it("displays no buttons") {
                with(panel) {
                    buttons.size.shouldBe(0)
                    componentCount.shouldBe(0)

                }
            }
        }

        on("navigation state has multiple, unfiltered child pages") {
            panel.moveToNavigationState()
            panel.moveToNavigationState()
            val nodes = nodesFromButtons(panel.buttons)
            val expected = ArrayList(userSitemap.publicSortedAlphaAscending())
            expected.add(userSitemap.logoutNode())

            it("") {
                nodes.size.shouldBe(expected.size)
                nodes.shouldContainAll(expected)
            }
        }

        on("navigation state has multiple, filtered child pages") {
            val filter = LogoutPageFilter()
            panel.addFilter(filter)
            panel.moveToNavigationState()
            panel.moveToNavigationState()
            val nodes = nodesFromButtons(panel.buttons)
            val expected = listOf(userSitemap.loginNode(), userSitemap.aNode(), userSitemap.publicHomeNode())

            it("") {
                nodes.size.shouldBe(expected.size)
                nodes.shouldContainAll(expected)
            }
        }

        on("setting option values") {
            panel.moveToNavigationState()
            panel.moveToNavigationState()
            panel.setOptionKeySortType(DefaultUserSitemapSorters.SortType.INSERTION)
            panel.setOptionSortAscending(true)

            it("sets the options") {
                verify {
                    option.set(DefaultSubPagePanel.optionSortAscending, true)
                    option.set(DefaultSubPagePanel.optionSortType, DefaultUserSitemapSorters.SortType.INSERTION)
                    sorters.setOptionSortAscending(true)
                    sorters.setOptionKeySortType(DefaultUserSitemapSorters.SortType.INSERTION)
                }
                panel.isRebuildRequired().shouldBeFalse()  // it rebuilds

            }
        }

        on("changing locale") {
            panel.moveToNavigationState()
            panel.moveToNavigationState()
            currentLocale.locale = Locale.GERMANY
            panel.localeChanged(LocaleChangeBusMessage(this, Locale.GERMANY))

            it("changes caption language") {
                panel.buttons[0].caption.shouldBe("Öffentliche Startseite")

            }
        }

    }

})


private fun moduleSet(): List<Module> {
    return listOf(LocalOptionModule(),
            InMemoryModule(),
            VaadinSessionScopeModule(),
            VaadinEventBusModule(),
            TestUIScopeModule(),
            UtilModule(),
            UtilsModule(),
            UserModule(),
            LocalTestI18NModule2(),
            DefaultShiroModule(),
            SerializationSupportModule(),
            ServletEnvironmentModule(),
            otherBindings())
}

private class LocalTestI18NModule2 : KrailI18NModule() {


    override fun define() {
        super.define()
        supportedLocales(Locale.ITALY, Locale.UK, Locale.GERMANY)
        supportedLocales(Locale("de", "CH"))
    }

}

private class LocalOptionModule : TestOptionModule() {
    override fun bindOption() {
        bind(Option::class.java).toInstance(MockOption())
    }

}


private fun otherBindings(): AbstractModule {
    return object : AbstractModule() {
        override fun configure() {
            bind(URIFragmentHandler::class.java).to(StrictURIFragmentHandler::class.java)
            bind(UserSitemapSorters::class.java).to(DefaultUserSitemapSorters::class.java)
        }
    }
}

/**
 * There may be more buttons than nodes, as buttons are re-used and just made not visible if not needed, so only
 * copy nodes from buttons which are visible.
 *
 * @param buttons
 *
 * @return
 */
internal fun nodesFromButtons(buttons: List<NavigationButton>): List<UserSitemapNode> {
    val nodes = ArrayList<UserSitemapNode>()
    for (button in buttons) {
        if (button.isVisible) {
            nodes.add(button.node)
        }
    }
    return nodes
}

