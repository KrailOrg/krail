package uk.q3c.krail.core.ui

import com.vaadin.server.ErrorHandler
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldContain
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.env.RuntimeEnvironment
import uk.q3c.krail.core.i18n.I18NProcessor
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.push.Broadcaster
import uk.q3c.krail.core.push.DefaultKrailPushConfiguration
import uk.q3c.krail.core.push.PushMessageRouter
import uk.q3c.krail.core.view.DefaultPublicHomeView
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.krail.i18n.test.MockCurrentLocale
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.krail.option.Option
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.testutil.LogMonitor

/**
 * Created by David Sowerby on 04 Sep 2018
 */
object ScopedUITest2 : Spek({

    given("a scoped UI") {
        lateinit var ui: ScopedUI
        lateinit var applicationTitle: ApplicationTitle
        lateinit var navigator: Navigator
        lateinit var errorHandler: ErrorHandler
        lateinit var broadcaster: Broadcaster
        lateinit var pushMessageRouter: PushMessageRouter
        val translate = MockTranslate()
        val currentLocale = MockCurrentLocale()
        lateinit var translator: I18NProcessor
        lateinit var option: Option
        lateinit var serializationSupport: SerializationSupport
        var logMonitor = LogMonitor()
        val pushConfiguration = DefaultKrailPushConfiguration(RuntimeEnvironment.SERVLET)

        beforeEachTest {
            applicationTitle = mockk(relaxed = true)
            navigator = mockk(relaxed = true)
            errorHandler = mockk(relaxed = true)
            broadcaster = mockk(relaxed = true)
            pushMessageRouter = mockk(relaxed = true)
            translator = mockk(relaxed = true)
            option = mockk(relaxed = true)
            serializationSupport = mockk(relaxed = true)
            logMonitor = LogMonitor()
            logMonitor.addClassFilter(ScopedUI::class.java)
            every { applicationTitle.titleKey } returns uk.q3c.krail.core.i18n.LabelKey.Krail
            ui = BasicUI(navigator, errorHandler, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator, serializationSupport, pushConfiguration)
        }

        afterEachTest {
            logMonitor.close()
        }

        on("changing view") {
            val view = DefaultPublicHomeView(translate, serializationSupport)
            view.buildView()
            ui.changeView(view)

            it("updates the page title") {
                logMonitor.debugLogs().shouldContain("Page title set to 'Krail Home'") // we cannot access the page title directly
            }
        }

        on("changing locale") {
            val busMessage: LocaleChangeBusMessage = mockk(relaxed = true)
            val view = DefaultPublicHomeView(translate, serializationSupport)
            view.buildView()
            ui.localeChanged(busMessage)

            it("updates the page title") {
                logMonitor.debugLogs().shouldContain("Page title set to 'Krail'") // we cannot access the page title directly
            }
        }

    }
})