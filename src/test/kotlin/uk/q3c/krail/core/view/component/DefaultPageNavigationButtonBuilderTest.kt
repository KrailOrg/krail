package uk.q3c.krail.core.view.component

import com.google.inject.Provider
import com.vaadin.ui.themes.ValoTheme
import fixture.ReferenceUserSitemap
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.eventbus.SessionBusProvider
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.navigate.sitemap.DefaultUserSitemapNodeSorter
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 09 Aug 2018
 */
object DefaultPageNavigationButtonBuilderTest : Spek({

    given("a builder") {
        lateinit var builder: DefaultPageNavigationButtonBuilder
        val navigatorProvider: Provider<Navigator> = mockk(relaxed = true)
        val navigator: Navigator = mockk(relaxed = true)
        val serializationSupport: SerializationSupport = mockk(relaxed = true)
        lateinit var sitemap: ReferenceUserSitemap
        val uriHandler = StrictURIFragmentHandler()
        val sessionBusProvider: SessionBusProvider = mockk(relaxed = true)
        val translate = MockTranslate()
        var nodeList: List<UserSitemapNode> = listOf()

        beforeEachTest {
            sitemap = ReferenceUserSitemap(translate, uriHandler, sessionBusProvider, serializationSupport)
            sitemap.populate()
            nodeList = sitemap.getChildren(sitemap.a11Node).filter { node -> NoNavFilter().accept(node) }
            every { navigatorProvider.get() } returns navigator
            builder = DefaultPageNavigationButtonBuilder(nodeSorter = DefaultUserSitemapNodeSorter())
        }

        on("building from a node which has sub-pages, default sort order") {
            val currentNode: UserSitemapNode = sitemap.a11Node!!
            val buttons = builder.createButtons(navigator = navigator, buttonOptions = ButtonOptions(ValoTheme.BUTTON_FRIENDLY), nodeList = nodeList)

            it("creates 3 standard NavigationButtons, one of 4 sub-pages is excluded") {
                buttons.size.shouldEqual(3)
                buttons[0].shouldBeInstanceOf(NavigationButton::class)
                buttons[1].shouldBeInstanceOf(NavigationButton::class)
                buttons[2].shouldBeInstanceOf(NavigationButton::class)
            }

            it("creates the caption for each button") {
                buttons[0].caption.shouldEqual("ViewA113")
                buttons[1].caption.shouldEqual("ViewA112")
                buttons[2].caption.shouldEqual("ViewA114")
            }

            it("has set the node for the navigation buttons") {
                buttons[0].node.shouldEqual(sitemap.a113Node)
                buttons[1].node.shouldEqual(sitemap.a112Node)
                buttons[2].node.shouldEqual(sitemap.a114Node)
            }
        }
    }
})