package uk.q3c.krail.core.navigate

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Module
import com.google.inject.Provider
import com.google.inject.Singleton
import com.google.inject.TypeLiteral
import com.vaadin.server.Page
import com.vaadin.ui.UI
import com.vaadin.util.CurrentInstance
import fixture.ReferenceUserSitemap
import fixture.TestViewChangeListener
import fixture.testviews2.ViewA11
import fixture.testviews2.ViewB1
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.apache.shiro.authz.UnauthorizedException
import org.apache.shiro.subject.Subject
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.slf4j.LoggerFactory
import uk.q3c.krail.config.ConfigurationFileModule
import uk.q3c.krail.core.config.KrailApplicationConfigurationModule
import uk.q3c.krail.core.env.ServletEnvironmentModule
import uk.q3c.krail.core.eventbus.UIBus
import uk.q3c.krail.core.eventbus.UIBusProvider
import uk.q3c.krail.core.eventbus.VaadinEventBusModule
import uk.q3c.krail.core.guice.InjectorHolder
import uk.q3c.krail.core.guice.uiscope.UIKey
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.i18n.MessageKey
import uk.q3c.krail.core.monitor.PageLoadingMessage
import uk.q3c.krail.core.monitor.PageReadyMessage
import uk.q3c.krail.core.navigate.sitemap.DefaultUserSitemap
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap
import uk.q3c.krail.core.navigate.sitemap.SitemapModule
import uk.q3c.krail.core.navigate.sitemap.SitemapService
import uk.q3c.krail.core.navigate.sitemap.UserSitemap
import uk.q3c.krail.core.navigate.sitemap.UserSitemapBuilder
import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.shiro.PageAccessController
import uk.q3c.krail.core.shiro.PagePermission
import uk.q3c.krail.core.shiro.SubjectProvider
import uk.q3c.krail.core.ui.ScopedUI
import uk.q3c.krail.core.ui.ScopedUIProvider
import uk.q3c.krail.core.user.LoginView
import uk.q3c.krail.core.user.UserSitemapRebuilt
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.core.user.status.UserStatusChangeSource
import uk.q3c.krail.core.view.BeforeViewChangeBusMessage
import uk.q3c.krail.core.view.DefaultErrorView
import uk.q3c.krail.core.view.DefaultNavigationView
import uk.q3c.krail.core.view.ErrorView
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.NavigationView
import uk.q3c.krail.core.view.ViewFactory
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage
import uk.q3c.krail.core.view.component.ComponentIdGenerator
import uk.q3c.krail.core.view.component.PageNavigationPanel
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.eventbus.SubscribeTo
import uk.q3c.krail.eventbus.mbassador.EventBusModule
import uk.q3c.krail.i18n.test.TestI18NModule
import uk.q3c.krail.option.mock.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule
import uk.q3c.krail.util.UtilsModule
import uk.q3c.util.UtilModule
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.guice.SerializationSupportModule
import java.io.IOException
import java.io.ObjectInputStream
import java.util.*

/**
 * Created by David Sowerby on 03 Jul 2018
 */
object DefaultNavigatorTest : Spek({

    given("we want to test navigation using Guice where possible") {
        lateinit var injector: Injector
        lateinit var builder: UserSitemapBuilder
        lateinit var changeListener: TestViewChangeListener
        lateinit var userSitemap: ReferenceUserSitemap
        lateinit var uiProvider: ScopedUIProvider
        lateinit var scopedUI: ScopedUI
        lateinit var browserPage: Page
        lateinit var errorView: ErrorView
        lateinit var subject: Subject
        lateinit var subjectProvider: SubjectProvider
        lateinit var errorViewProvider: Provider<ErrorView>
        lateinit var userSitemapProvider: Provider<UserSitemap>
        lateinit var invalidURIHandler: InvalidURIHandler
        lateinit var userNotifier: UserNotifier
        lateinit var uriHandler: StrictURIFragmentHandler
        lateinit var sitemapService: SitemapService
        lateinit var pageAccessController: PageAccessController
        lateinit var viewFactory: TestViewFactory
        lateinit var navigator: DefaultNavigator
        lateinit var logoutNavigationRule: LogoutNavigationRule
        lateinit var loginNavigationRule: LoginNavigationRule
        lateinit var eventBusProvider: UIBusProvider
        lateinit var defaultViewChangeRule: DefaultViewChangeRule
        lateinit var componentIdGenerator: ComponentIdGenerator
        lateinit var masterSitemap: MasterSitemap
        lateinit var messageBus: MessageBus
        lateinit var serialisationSupport: SerializationSupport
        lateinit var navigatorDeps: NavigatorDeps
        lateinit var logoutSource: UserStatusChangeSource
        lateinit var loginSource: UserStatusChangeSource
        lateinit var listener4: MockListener

        beforeEachTest {
            injector = createInjector()
            InjectorHolder.setInjector(injector)
            userSitemap = injector.getInstance(ReferenceUserSitemap::class.java)
            userSitemap.populate()

            builder = mockk(relaxed = true)
            uiProvider = mockk(relaxed = true)
            scopedUI = mockk(relaxed = true)
            browserPage = mockk(relaxed = true)
            errorView = mockk(relaxed = true)
            subject = mockk(relaxed = true)
            subjectProvider = mockk(relaxed = true)
            errorViewProvider = mockk(relaxed = true)
            userSitemapProvider = mockk(relaxed = true)
            userNotifier = mockk(relaxed = true)
            messageBus = mockk(relaxed = true)
            serialisationSupport = mockk(relaxed = true)
            sitemapService = mockk(relaxed = true)
            logoutSource = mockk(relaxed = true)
            loginSource = mockk(relaxed = true)
            loginNavigationRule = mockk(relaxed = true)
            logoutNavigationRule = mockk(relaxed = true)


            invalidURIHandler = DefaultInvalidURIHandler(userNotifier)

            every { builder.userSitemap } returns userSitemap
            every { uiProvider.get() } returns scopedUI
            every { scopedUI.page } returns browserPage
            every { scopedUI.instanceKey } returns CurrentInstance.get(UIKey::class.java)
            every { scopedUI.uiId } returns 99
            every { errorViewProvider.get() } returns errorView
            every { subjectProvider.get() } returns subject
            every { userSitemapProvider.get() } returns userSitemap


            UI.setCurrent(scopedUI)

            listener4 = injector.getInstance(MockListener::class.java)  // must use Guice to subscribe to bus
            uriHandler = injector.getInstance(StrictURIFragmentHandler::class.java)
            pageAccessController = injector.getInstance(PageAccessController::class.java)
            viewFactory = injector.getInstance(TestViewFactory::class.java)

            eventBusProvider = injector.getInstance(UIBusProvider::class.java)
            defaultViewChangeRule = injector.getInstance(DefaultViewChangeRule::class.java)
            componentIdGenerator = injector.getInstance(ComponentIdGenerator::class.java)
            masterSitemap = injector.getInstance(MasterSitemap::class.java)
            changeListener = injector.getInstance(TestViewChangeListener::class.java)

            navigatorDeps = NavigatorDeps(uriHandler, sitemapService, subjectProvider, pageAccessController, uiProvider, viewFactory, builder,
                    loginNavigationRule, logoutNavigationRule, eventBusProvider, defaultViewChangeRule, invalidURIHandler, componentIdGenerator, masterSitemap, messageBus, serialisationSupport)

        }

        on("calling init") {
            navigator = createNavigator(navigatorDeps)

            it("starts the sitemap service and builds the user sitemap") {
                verify { sitemapService.start() }
                verify { builder.build() }
            }
        }

        on("navigating to login page") {
            navigator = createNavigator(navigatorDeps)
            navigator.navigateTo(userSitemap.loginFragment)

            it("selects the LoginView") {
                verify { scopedUI.changeView(any<LoginView>()) }
            }
        }

        on("logging out") {
            val userHasLoggedOut = UserSitemapRebuilt(false, logoutSource)
            navigator = createNavigator(navigatorDeps)
            every { logoutNavigationRule.changedNavigationState(navigator, logoutSource) } returns Optional.empty()
            navigator.handleUserSitemapRebuilt(userHasLoggedOut)

            it("invokes the LogOutNavigationRule") {
                verify { logoutNavigationRule.changedNavigationState(navigator, logoutSource) }
            }
        }

        on("logging in") {
            val userHasLoggedIn = UserSitemapRebuilt(true, loginSource)
            navigator = createNavigator(navigatorDeps)
            every { loginNavigationRule.changedNavigationState(navigator, loginSource) } returns Optional.empty()
            navigator.handleUserSitemapRebuilt(userHasLoggedIn)

            it("invokes the LogInNavigationRule") {
                verify { loginNavigationRule.changedNavigationState(navigator, loginSource) }
            }
        }

        on("navigating to a specific url") {
            navigator = createNavigator(navigatorDeps)
            navigator.navigateTo(userSitemap.a11Fragment)

            it("changes to the correct view and, issues Page messages in the correct order") {
                verifyOrder {
                    messageBus.publishASync(any<PageLoadingMessage>())
                    scopedUI.changeView(any<ViewA11>())
                    messageBus.publishASync(any<PageReadyMessage>())
                }
            }


            it("has the correct state") {
                navigator.currentNode.shouldBe(userSitemap.a11Node)
                navigator.currentNavigationState.fragment.shouldBeEqualTo(userSitemap.a11Fragment)
            }
        }

        on("navigating to an empty page, with params") {
            navigator = createNavigator(navigatorDeps)
            val fragment = "/id=2/age=5"
            navigator.navigateTo(fragment)

            it("goes to public home page, and carries params") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo("public/home/id=2/age=5")
            }
        }

        on("navigating in response to a PageEvent") {
            val page: Page = mockk(relaxed = true)
            val event = Page.PopStateEvent(page, "https://localhost:8080/krailapp/#${userSitemap.a11Fragment}")
            navigator = createNavigator(navigatorDeps)
            navigator.uriChanged(event)

            it("navigates to the correct url") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(userSitemap.a11Fragment)
            }
        }


        on("navigating to an invalid URI") {
            navigator = createNavigator(navigatorDeps)
            val page = "public/view3"
            navigator.navigateTo(page)

            it("issues a user notification") {
                verify { userNotifier.notifyInformation(MessageKey.Invalid_URI, page) }
            }
        }


//        fun getEvent(eventKey: String): ViewChangeBusMessage {
//            return changeListener.getMessage(eventKey)
//        }

        on("getting the current navigation state") {
            navigator = createNavigator(navigatorDeps)
            navigator.navigateTo(userSitemap.a1Fragment)

            it("is as expected") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(userSitemap.a1Fragment)
            }
        }

        on("getting the params from the URI") {
            navigator = createNavigator(navigatorDeps)
            val page1 = userSitemap.a1Fragment
            val fragment1 = "$page1/id=2/age=5"
            navigator.navigateTo(fragment1)

            it("returns the correct values") {
                navigator.navigationParams.shouldEqual(listOf("id=2", "age=5"))
            }
        }

        on("navigating to a node") {
            navigator = createNavigator(navigatorDeps)
            navigator.navigateTo(userSitemap.a11Node())

            it("has the correct navigation state") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(userSitemap.a11Fragment)
            }

            it("issues PageLoading and Ready messages in the correct order") {
                verifyOrder {
                    messageBus.publishASync(any<PageLoadingMessage>())
                    scopedUI.changeView(any<ViewA11>())
                    messageBus.publishASync(any<PageReadyMessage>())
                }

            }
        }


        on("starting from scratch") {
            navigator = createNavigator(navigatorDeps)

            it("starts with everything at null") {
                navigator.currentView.shouldEqual(scopedUI.view) // a very odd problem with mock here, in debugger it returns null, but in code returns a view from somewhere
                navigator.currentNavigationState.shouldBeNull()
                navigator.previousNavigationState.shouldBeNull()
                navigator.previousNode.shouldBeNull()
            }
        }

        on("navigating") {
            navigator = createNavigator(navigatorDeps)
            val page1 = userSitemap.a1Fragment
            val fragment1 = "$page1/id=1"

            val page2 = userSitemap.a11Fragment
            val fragment2 = "$page2/id=2"

            navigator.navigateTo(fragment1)
            navigator.navigateTo(fragment2)

            it("previous position is retained") {
                navigator.previousNavigationState.fragment.shouldBeEqualTo(fragment1)
            }
        }

        on("clearing history, previous state is null") {
            navigator = createNavigator(navigatorDeps)
            val page1 = userSitemap.a1Fragment
            val fragment1 = "$page1/id=1"

            val page2 = userSitemap.a11Fragment
            val fragment2 = "$page2/id=2"

            navigator.navigateTo(fragment1)
            navigator.navigateTo(fragment2)
            navigator.clearHistory()

            it("previous position is cleared") {
                navigator.previousNavigationState.shouldBeNull()
            }
        }

        on("listener blocking change") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a11Fragment
            listener4.cancelBefore = true
            navigator.navigateTo(userSitemap.a1Node)
            val startState = navigator.currentNavigationState
            navigator.navigateTo(page)
            val endState = navigator.currentNavigationState

            it("does not change page") {
                endState.shouldEqual(startState)
            }
        }

        on("setting a redirect") {
            navigator = createNavigator(navigatorDeps)
            val page = "wiggly"
            val page2 = userSitemap.a1Fragment

            userSitemap.addRedirect(page, page2)
            navigator.navigateTo(page)

            it("redirects ") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(page2)
            }
        }

        on("navigating to a NavigationState") {
            navigator = createNavigator(navigatorDeps)

            val page = userSitemap.a11Fragment
            val navigationState = uriHandler.navigationState(page)
            navigator.navigateTo(navigationState)

            it("navigates to the correct page") {
                verifyOrder {
                    messageBus.publishASync(any<PageLoadingMessage>())
                    scopedUI.changeView(any<ViewA11>())
                    messageBus.publishASync(any<PageReadyMessage>())
                }
            }
        }


        /**
         * [PageAccessControl.USER] requires that the user either authenticated or remembered
         */

        on("navigating to a page with 'user' permission, and user is authenticated") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a111Fragment
            every { subject.isAuthenticated } returns true
            every { subject.isRemembered } returns false

            navigator.navigateTo(page)

            it("allows navigation") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(page)
            }

        }

        on("navigating to a page with 'user' permission, and user is remembered") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a111Fragment
            every { subject.isAuthenticated } returns false
            every { subject.isRemembered } returns true

            navigator.navigateTo(page)

            it("allows navigation") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(page)
            }

        }

        on("navigating to a page with 'user' permission, and user neither remembered nor authenticated") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a111Fragment
            every { subject.isAuthenticated } returns false
            every { subject.isRemembered } returns false


            val result = { navigator.navigateTo(page) }

            it("does not allow navigation") {
                result.shouldThrow(UnauthorizedException::class)
            }

        }

        /**
         * [PageAccessControl.AUTHENTICATION] requires that the user is authenticated, but does not require authorisation
         */

        on("navigating to a page with 'authentication' permission, and user is authenticated") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a113Fragment
            every { subject.isAuthenticated } returns true
            every { subject.isRemembered } returns false

            navigator.navigateTo(page)

            it("allows navigation") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(page)
            }

        }

        on("navigating to a page with 'authentication' permission, and user is remembered") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a113Fragment
            every { subject.isAuthenticated } returns false
            every { subject.isRemembered } returns true

            val result = { navigator.navigateTo(page) }

            it("does not allow navigation") {
                result.shouldThrow(UnauthorizedException::class)
            }

        }

        on("navigating to a page with 'authentication' permission, and user is neither authenticated nor remembered") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a113Fragment
            every { subject.isAuthenticated } returns false
            every { subject.isRemembered } returns false

            val result = { navigator.navigateTo(page) }

            it("does not allow navigation") {
                result.shouldThrow(UnauthorizedException::class)
            }

        }

        /**
         * [PageAccessControl.GUEST] requires that the user is NOT authenticated and NOT remembered
         */

        on("navigating to a page with 'guest' permission, and user is authenticated") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a112Fragment
            every { subject.isAuthenticated } returns true
            every { subject.isRemembered } returns false

            val result = { navigator.navigateTo(page) }

            it("does not allow navigation") {
                result.shouldThrow(UnauthorizedException::class)
            }

        }

        on("navigating to a page with 'guest' permission, and user is remembered") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a112Fragment
            every { subject.isAuthenticated } returns false
            every { subject.isRemembered } returns true

            val result = { navigator.navigateTo(page) }

            it("does not allow navigation") {
                result.shouldThrow(UnauthorizedException::class)
            }

        }


        on("navigating to a page with 'guest' permission, and user is neither authenticated nor remembered") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a112Fragment
            every { subject.isAuthenticated } returns false
            every { subject.isRemembered } returns false

            navigator.navigateTo(page)

            it("allows navigation") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(page)
            }

        }


        /**
         * [PageAccessControl.PERMISSION] requires that the user is authorised
         */

        on("navigating to a page with 'permission' permission, and user is authorised") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.b1Fragment
            every { subject.isAuthenticated } returns true
            every { subject.isRemembered } returns false
            every { subject.isPermitted(any<PagePermission>()) } returns true

            navigator.navigateTo(page)

            it("allows navigation") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(page)
            }

        }

        on("navigating to a page with 'permission' permission, and user is not authorised") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.b1Fragment
            every { subject.isAuthenticated } returns true
            every { subject.isRemembered } returns false
            every { subject.isPermitted(any<PagePermission>()) } returns false

            val result = { navigator.navigateTo(page) }

            it("does not allow navigation") {
                result.shouldThrow(UnauthorizedException::class)
            }

        }

        /**
         * [PageAccessControl.ROLES] requires that the user is has all the roles required
         */

        on("navigating to a page with 'roles' permission, and user is authorised") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a114Fragment
            every { subject.isAuthenticated } returns true
            every { subject.isRemembered } returns false
            every { subject.hasAllRoles(any()) } returns true

            navigator.navigateTo(page)

            it("allows navigation") {
                navigator.currentNavigationState.fragment.shouldBeEqualTo(page)
            }

        }

        on("navigating to a page with 'roles' permission, and user is not authorised") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a114Fragment
            every { subject.isAuthenticated } returns true
            every { subject.isRemembered } returns false
            every { subject.hasAllRoles(any()) } returns false

            val result = { navigator.navigateTo(page) }

            it("does not allow navigation") {
                result.shouldThrow(UnauthorizedException::class)
            }

        }

        on("navigating to a page") {
            val expectedOrder = listOf("beforeViewChange", "afterViewChange")
            val expectedViewCalls = listOf("init", "beforeBuild", "buildView", "afterBuild")
            navigator = createNavigator(navigatorDeps)
            val page0 = userSitemap.bFragment
            val page = userSitemap.b1Fragment
            every { subject.isAuthenticated } returns true
            every { subject.isRemembered } returns false
            every { subject.isPermitted(any<PagePermission>()) } returns true

            navigator.navigateTo(page0)
            navigator.navigateTo(page)
            val view = viewFactory.lastView as ViewB1

            it("listeners receive messages in the correct order") {
                changeListener.getCalls().size.shouldBe(6)
                val iter = changeListener.calls.iterator()
                iter.next().key.shouldBeEqualTo("beforeViewChange")
                iter.next().key.shouldBeEqualTo("init")
                iter.next().key.shouldBeEqualTo("beforeBuild")
                iter.next().key.shouldBeEqualTo("buildView")
                iter.next().key.shouldBeEqualTo("afterBuild")
                iter.next().key.shouldBeEqualTo("afterViewChange")
            }

            it("has the correct content in the event") {
                (changeListener.calls["beforeViewChange"] as BeforeViewChangeBusMessage).toState.fragment.shouldBeEqualTo(userSitemap.b1Fragment)
                (changeListener.calls["beforeViewChange"] as BeforeViewChangeBusMessage).fromState.fragment.shouldBeEqualTo(userSitemap.bFragment)
                (changeListener.calls["afterViewChange"] as AfterViewChangeBusMessage).toState.fragment.shouldBeEqualTo(userSitemap.b1Fragment)
                (changeListener.calls["afterViewChange"] as AfterViewChangeBusMessage).fromState.fragment.shouldBeEqualTo(userSitemap.bFragment)
            }

            it("calls the view methods in the correct order") {
                view.calls.shouldEqual(expectedViewCalls)
            }
        }

        on("requesting navigation to the current page") {
            navigator = createNavigator(navigatorDeps)
            val page = userSitemap.a1Fragment
            navigator.navigateTo(page)
            changeListener.clear()
            navigator.navigateTo(page)


            it("ignores the request") {
                changeListener.calls.shouldBeEmpty()
            }
        }


    }


})

fun createNavigator(deps: NavigatorDeps): DefaultNavigator {
    with(deps) {
        val navigator = DefaultNavigator(uriHandler, sitemapService, subjectProvider, pageAccessController, uiProvider, viewFactory, builder,
                loginNavigationRule, logoutNavigationRule, eventBusProvider, defaultViewChangeRule, invalidURIHandler, componentIdGenerator, masterSitemap, messageBus, serialisationSupport)
        navigator.init()
        return navigator
    }
}

class NavigatorDeps(val uriHandler: StrictURIFragmentHandler, val sitemapService: SitemapService, val subjectProvider: SubjectProvider, val pageAccessController: PageAccessController, val uiProvider: ScopedUIProvider, val viewFactory: ViewFactory, val builder: UserSitemapBuilder, val loginNavigationRule: LoginNavigationRule, val logoutNavigationRule: LogoutNavigationRule, val eventBusProvider: UIBusProvider, val defaultViewChangeRule: DefaultViewChangeRule, val invalidURIHandler: InvalidURIHandler, val componentIdGenerator: ComponentIdGenerator, val masterSitemap: MasterSitemap, val messageBus: MessageBus, val serialisationSupport: SerializationSupport)


fun createInjector(): Injector {
    val modules: List<Module> = listOf(DefaultNavigatorTestModule(), VaadinSessionScopeModule(), TestI18NModule(), InMemoryModule(), TestOptionModule(), VaadinEventBusModule(), ConfigurationFileModule(),
            TestUIScopeModule(), ServletEnvironmentModule(), SerializationSupportModule(), EventBusModule(), SitemapModule(), UtilModule(), UtilsModule(), KrailApplicationConfigurationModule())

    return Guice.createInjector(modules)
}


internal class DefaultNavigatorTestModule : AbstractModule() {
    val pnp: PageNavigationPanel = mockk(relaxed = true)

    override fun configure() {
        bind(ErrorView::class.java).to(DefaultErrorView::class.java)
        bind(URIFragmentHandler::class.java).to(StrictURIFragmentHandler::class.java)
        bind(UserSitemap::class.java).to(DefaultUserSitemap::class.java)
        bind(LoginNavigationRule::class.java).to(DefaultLoginNavigationRule::class.java)
        bind(LogoutNavigationRule::class.java).to(DefaultLogoutNavigationRule::class.java)
        bind(NavigationView::class.java).to(DefaultNavigationView::class.java)
        bind(PageNavigationPanel::class.java).toInstance(pnp)
    }

}


@Singleton
@Listener
@SubscribeTo(UIBus::class)
internal class TestViewChangeListener {

    var calls: MutableMap<String, ViewChangeBusMessage> = LinkedHashMap()

    @Handler
    fun beforeViewChange(busMessage: BeforeViewChangeBusMessage) {
        calls["beforeViewChange"] = busMessage
    }

    /**
     * Invoked after the view is changed. If a `beforeViewChange`
     * method blocked the view change, this method is not called. Be careful of
     * unbounded recursion if you decide to change the view again in the
     * listener.
     *
     * @param busMessage view change event
     */
    @Handler
    fun afterViewChange(busMessage: AfterViewChangeBusMessage) {
        calls["afterViewChange"] = busMessage
    }

    fun addCall(call: String, busMessage: ViewChangeBusMessage) {
        calls[call] = busMessage
    }


    fun getMessage(eventKey: String): ViewChangeBusMessage {
        return calls[eventKey] ?: throw NoSuchElementException("No message for $eventKey")
    }

    fun clear() {
        calls.clear()
    }
}

@Listener
@SubscribeTo(UIBus::class)
class FakeListener {

    internal var callsBefore: Int = 0
    internal var callsAfter: Int = 0

    @Handler
    fun beforeViewChange(event: BeforeViewChangeBusMessage) {
        callsBefore++
    }

    @Handler
    fun afterViewChange(event: BeforeViewChangeBusMessage) {
        callsAfter++
    }
}

@Listener
@SubscribeTo(UIBus::class)
internal class MockListener {
    var cancelBefore = false
    var cancelAfter = false

    @Handler
    fun beforeViewChange(event: BeforeViewChangeBusMessage) {
        if (cancelBefore) {
            event.cancel()
        }
    }

    @Handler
    fun afterViewChange(event: BeforeViewChangeBusMessage) {
        if (cancelAfter) {
            event.cancel()
        }
    }
}

class TestViewFactory @Inject constructor(
        private val serializationSupport: SerializationSupport,
        private val injectorLocator: InjectorLocator)

    : ViewFactory {
    private var log = LoggerFactory.getLogger(TestViewFactory::class.java)
    lateinit var lastView: KrailView

    override fun <T : KrailView> get(viewClass: Class<T>): T {
        val typeLiteral = TypeLiteral.get(viewClass)
        val key = Key.get(typeLiteral)
        log.debug("getting or retrieving instance of {}", viewClass)
        val view = injectorLocator.get().getInstance(key)
        log.debug("Calling view.init()")
        view.init()
        lastView = view
        return view
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        log = LoggerFactory.getLogger(TestViewFactory::class.java)
        serializationSupport.deserialize(this)
    }
}