package uk.q3c.krail.core.navigate

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey
import uk.q3c.krail.core.navigate.sitemap.UserSitemap
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.user.status.UserStatusChangeSource

/**
 * Created by David Sowerby on 11 Jun 2018
 */
object DefaultLoginNavigationRuleTest : Spek({

    given("a Login navigation rule") {
        lateinit var navigator: Navigator
        lateinit var source: UserStatusChangeSource
        lateinit var rule: DefaultLoginNavigationRule
        lateinit var loginNode: UserSitemapNode
        lateinit var previousNavigationState: NavigationState
        lateinit var homeNavigationState: NavigationState
        lateinit var currentNavigationState: NavigationState
        lateinit var logoutNode: UserSitemapNode
        lateinit var previousNode: UserSitemapNode
        lateinit var uriHandler: URIFragmentHandler
        lateinit var userSitemap: UserSitemap

        beforeEachTest {
            navigator = mockk(relaxed = true)
            source = mockk(relaxed = true)
            rule = mockk(relaxed = true)
            loginNode = mockk(relaxed = true)
            previousNavigationState = mockk(relaxed = true)
            homeNavigationState = mockk(relaxed = true)
            currentNavigationState = mockk(relaxed = true)
            logoutNode = mockk(relaxed = true)
            previousNode = mockk(relaxed = true)
            uriHandler = mockk(relaxed = true)
            userSitemap = mockk(relaxed = true)

            rule = DefaultLoginNavigationRule(userSitemap, uriHandler)

            every { userSitemap.standardPageNode(StandardPageKey.Log_In) } returns loginNode
            every { userSitemap.standardPageNode(StandardPageKey.Log_Out) } returns logoutNode
            every { navigator.currentNavigationState } returns currentNavigationState
        }

        on("not on login page") {
            every { userSitemap.isLoginUri(navigator.currentNavigationState) } returns false
            val expected = rule.changedNavigationState(navigator, source)

            it("evaluates to false") {
                expected.isPresent.shouldBeFalse()
            }
        }



        on(" on_login_page_no_previous") {
            //given
            every { userSitemap.isLoginUri(navigator.currentNavigationState) } returns (true)
            every { navigator.previousNode } returns (null)
            every { uriHandler.navigationState(any<String>()) } returns (homeNavigationState)
            //when
            val expected = rule.changedNavigationState(navigator, source)
            //then
            expected.isPresent.shouldBeTrue()
            expected.get().shouldBe(homeNavigationState)
        }


        on(" login_page_has_previous") {
            //given

            every { userSitemap.isLoginUri(navigator.currentNavigationState) } returns (true)
            every { navigator.previousNode } returns (previousNode)
            every { navigator.previousNavigationState } returns (previousNavigationState)
            //when
            val expected = rule.changedNavigationState(navigator, source)
            //then
            expected.isPresent.shouldBeTrue()
            expected.get().shouldBe(previousNavigationState)
        }


        on(" on_login_page_previous_was_logout") {
            //given
            every { userSitemap.isLoginUri(navigator.currentNavigationState) } returns (true)
            every { navigator.previousNode } returns (logoutNode)
            every { uriHandler.navigationState(any<String>()) } returns (homeNavigationState)
            //when
            val expected = rule.changedNavigationState(navigator, source)
            //then
            expected.isPresent.shouldBeTrue()
            expected.get().shouldBe(homeNavigationState)
        }
    }
}) 


    
