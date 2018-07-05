package uk.q3c.krail.core.sysadmin

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.test.MockTranslate
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 16 Apr 2018
 */
object SystemAdminViewTest : Spek({

    given(" A systemAdminView") {
        lateinit var navigator: Navigator
        lateinit var view: SystemAdminView
        lateinit var serializationSupport: SerializationSupport
        lateinit var busMessage: ViewChangeBusMessage

        beforeEachTest {
            navigator = mockk()
            serializationSupport = mockk()
            busMessage = mockk()
            view = SystemAdminView(navigator, MockTranslate(), serializationSupport)
        }

        on("doBuild") {
            view.buildView(busMessage)

            it("button has been created") {
                view.buildReportBtn  // will throw lateinit exception if not
            }
        }

        on("clicking build report button") {
            every {
                navigator.navigateTo("system-admin/sitemap-build-report")
            } returns

                    view.buildView(busMessage)
            view.buildReportBtn.click()

            it("navigates to sitemap report") {
                verify {
                    navigator.navigateTo("system-admin/sitemap-build-report")
                }
            }
        }
    }

})


