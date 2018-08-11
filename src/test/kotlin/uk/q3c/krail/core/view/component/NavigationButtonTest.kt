package uk.q3c.krail.core.view.component

import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode

/**
 * Created by David Sowerby on 10 Aug 2018
 */
object NavigationButtonTest : Spek({

    given("a NavigationButton") {
        val navigator: Navigator = mockk(relaxed = true)
        val node: UserSitemapNode = mockk(relaxed = true)
        val button = NavigationButton(node, navigator)

        on("click") {
            button.click()

            it("calls navigator to navigate to node") {
                verify { navigator.navigateTo(node) }
            }
        }
    }
})