package uk.q3c.krail.core.sysadmin

import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.view.NavigationStateExt
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
        lateinit var navigationStateExt: NavigationStateExt

        beforeEachTest {
            navigator = mockk(relaxed = true)
            serializationSupport = mockk()
            navigationStateExt = mockk(relaxed = true)
            view = SystemAdminView(navigator, MockTranslate(), serializationSupport)
            view.beforeBuild(navigationStateExt)
        }


        on("clicking build report button") {
            view.buildView()
            view.buildReportBtn.click()

            it("navigates to sitemap report") {
                verify {
                    navigator.navigateTo("system-admin/sitemap-build-report")
                }
            }
        }
    }

})


