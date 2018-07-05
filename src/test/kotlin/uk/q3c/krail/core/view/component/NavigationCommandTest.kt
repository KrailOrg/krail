package uk.q3c.krail.core.view.component

import com.vaadin.ui.MenuBar
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode

/**
 * Created by David Sowerby on 11 Jun 2018
 */
object NavigationCommandTest : Spek({

    given("a NavigationCommand") {
        lateinit var command: NavigationCommand
        lateinit var node: UserSitemapNode
        lateinit var navigator: Navigator
        lateinit var menuItem: MenuBar.MenuItem

        beforeEachTest {
            node = mockk(relaxed = true)
            navigator = mockk(relaxed = true)
            menuItem = mockk(relaxed = true)
            command = NavigationCommand(navigator, node)
        }

        on("invoking command") {
            command.menuSelected(menuItem)
            it("calls the navigator to navigate to target") {
                verify { navigator.navigateTo(node) }
            }
        }

    }

})