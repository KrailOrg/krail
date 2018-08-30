package uk.q3c.krail.core.ui

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.vaadin.server.ErrorHandler
import com.vaadin.server.UIClassSelectionEvent
import org.amshove.kluent.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import uk.q3c.krail.core.env.RunningOn
import uk.q3c.krail.core.env.RuntimeEnvironment
import uk.q3c.krail.core.i18n.CommonLabelKey.Yes
import uk.q3c.krail.core.i18n.I18NProcessor
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.push.Broadcaster
import uk.q3c.krail.core.push.DefaultKrailPushConfiguration
import uk.q3c.krail.core.push.KrailPushConfiguration
import uk.q3c.krail.core.push.PushMessageRouter
import uk.q3c.krail.core.user.notify.VaadinNotification
import uk.q3c.krail.core.view.component.ApplicationHeader
import uk.q3c.krail.core.view.component.ApplicationLogo
import uk.q3c.krail.core.view.component.LocaleSelector
import uk.q3c.krail.core.view.component.MessageBar
import uk.q3c.krail.core.view.component.PageNavigationPanel
import uk.q3c.krail.core.view.component.UserNavigationMenu
import uk.q3c.krail.core.view.component.UserNavigationTree
import uk.q3c.krail.core.view.component.UserStatusPanel
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.option.Option
import uk.q3c.util.guice.SerializationSupport


/**
 * Created by David Sowerby on 04 Mar 2018
 */
class MockedModule : AbstractModule() {
    val mockNavigator: Navigator = mock()
    val mockErrorHandler: ErrorHandler = mock()
    val mockBroadcaster: Broadcaster = mock()
    val mockPushMessageRouter: PushMessageRouter = mock()
    val mockTranslate: Translate = mock()
    val mockCurrentLocale: CurrentLocale = mock()
    val mockI18NProcessor: I18NProcessor = mock()
    val mockVaadinNotification: VaadinNotification = mock()
    val mockApplicationHeader: ApplicationHeader = mock()
    val mockApplicationLogo: ApplicationLogo = mock()
    val mockLocaleSelector: LocaleSelector = mock()
    val mockMessageBar: MessageBar = mock()
    val mockUserNavigationMenu: UserNavigationMenu = mock()
    val mockUserNavigationTree: UserNavigationTree = mock()
    val mockUserStatusPanel: UserStatusPanel = mock()
    val mockOption: Option = mock()
    val mockSerializationSupport: SerializationSupport = mock()
    val mockPageNavigationPanel: PageNavigationPanel = mock()


    override fun configure() {
        bind(Navigator::class.java).toInstance(mockNavigator)
        bind(ErrorHandler::class.java).toInstance(mockErrorHandler)
        bind(Broadcaster::class.java).toInstance(mockBroadcaster)
        bind(PushMessageRouter::class.java).toInstance(mockPushMessageRouter)
        bind(Translate::class.java).toInstance(mockTranslate)
        bind(CurrentLocale::class.java).toInstance(mockCurrentLocale)
        bind(I18NProcessor::class.java).toInstance(mockI18NProcessor)
        bind(VaadinNotification::class.java).toInstance(mockVaadinNotification)
        bind(ApplicationHeader::class.java).toInstance(mockApplicationHeader)
        bind(ApplicationLogo::class.java).toInstance(mockApplicationLogo)
        bind(LocaleSelector::class.java).toInstance(mockLocaleSelector)
        bind(MessageBar::class.java).toInstance(mockMessageBar)
        bind(UserNavigationMenu::class.java).toInstance(mockUserNavigationMenu)
        bind(UserNavigationTree::class.java).toInstance(mockUserNavigationTree)
        bind(UserStatusPanel::class.java).toInstance(mockUserStatusPanel)
        bind(Option::class.java).toInstance(mockOption)
        bind(SerializationSupport::class.java).toInstance(mockSerializationSupport)
        bind(KrailPushConfiguration::class.java).to(DefaultKrailPushConfiguration::class.java)
        bind(RuntimeEnvironment::class.java).annotatedWith(RunningOn::class.java).toInstance(RuntimeEnvironment.SERVLET)
        bind(PageNavigationPanel::class.java).toInstance(mockPageNavigationPanel)
    }
}


class DefaultUIModuleTest {

    val mockEvent: UIClassSelectionEvent = mock()

    @Test
    fun configureWithDefaults() {
        // given
        val module = DefaultUIModule()

        // when
        val injector = Guice.createInjector(module, MockedModule())
        val applicationTitle = injector.getInstance(ApplicationTitle::class.java)
        val uiProvider = injector.getInstance(ScopedUIProvider::class.java)
        // then
        assertThat(applicationTitle).isNotNull()
        assertThat(applicationTitle.titleKey).isEqualTo(LabelKey.Krail)
        assertThat(uiProvider).isNotNull()

        assertThat(uiProvider.getUIClass(mockEvent)).isEqualTo(DefaultApplicationUI::class.java)
    }

    @Test
    fun configureWithDefaultsChanged() {
        val module = DefaultUIModule().uiClass(BasicUI::class.java).applicationTitleKey(Yes)

        // when
        val injector = Guice.createInjector(module, MockedModule())
        val applicationTitle = injector.getInstance(ApplicationTitle::class.java)
        val uiProvider = injector.getInstance(ScopedUIProvider::class.java)
        // then
        assertThat(applicationTitle).isNotNull()
        assertThat(applicationTitle.titleKey).isEqualTo(Yes)
        assertThat(uiProvider).isNotNull()

        assertThat(uiProvider.getUIClass(mockEvent)).isEqualTo(BasicUI::class.java)
    }


}

