package uk.q3c.krail.core.view.component

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.shouldEqual
import org.apache.shiro.subject.Subject
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey
import uk.q3c.krail.core.shiro.SubjectProvider
import uk.q3c.krail.core.user.LoginLabelKey
import uk.q3c.krail.core.user.UserHasLoggedIn
import uk.q3c.krail.core.user.UserHasLoggedOut
import uk.q3c.krail.core.user.status.UserStatusChangeSource
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockTranslate
import java.util.*

/**
 * Created by David Sowerby on 11 Mar 2018
 */
object DefaultUserStatusPanelTest : Spek({

    given("a user status panel") {
        val source: UserStatusChangeSource = mock()
        lateinit var panel: DefaultUserStatusPanel
        lateinit var subject: Subject
        lateinit var subjectProvider: SubjectProvider
        lateinit var navigator: Navigator
        lateinit var currentLocale: CurrentLocale
        lateinit var translate: Translate



        beforeEachTest {
            subject = mock()
            subjectProvider = mock()
            navigator = mock()
            translate = MockTranslate()
            currentLocale = mock()
            whenever(subjectProvider.get()).thenReturn(subject)
            panel = DefaultUserStatusPanel(navigator = navigator, subjectProvider = subjectProvider, translate = translate, currentLocale = currentLocale)
        }

        given("an unknown subject") {

            on("building with unknown subject") {
                //                whenever(subject.isRemembered).thenReturn(false)
//                whenever(subject.isAuthenticated).thenReturn(false)
//                whenever(subject.principal).thenReturn(null)
                panel.configureDisplay()

                it("shows the correct labels") {
                    panel.actionLabel.shouldEqual("log in")
                    panel.userId.shouldEqual("Guest")
                }
            }

            on("clicking 'log in'") {
                panel.login_logout_Button.click()

                it("should navigate to the login page") {
                    verify(navigator).navigateTo(StandardPageKey.Log_In)
                }
            }
        }

//        given(" a remembered subject") {
//
//            on(" building with a remembered subject") {
//                whenever(subject.isRemembered).thenReturn(true)
//                whenever(subject.isAuthenticated).thenReturn(false)
//                whenever(subject.principal).thenReturn("userId")
//                panel.configureDisplay()
//
//                it("shows the correct labels") {
//                    panel.actionLabel.shouldEqual("log in")
//                    panel.userId.shouldEqual("Guest") // rememberMe not yet supported
//                }
//            }
//
//        }

        given("a user has logged in") {
            on(" building with an authenticated subject") {
                panel.handleUserHasLoggedIn(UserHasLoggedIn(aggregateId = "david@somewhere.com", knownAs = "david", source = source))

                it("shows the correct labels") {
                    panel.actionLabel.shouldEqual("log out")
                    panel.userId.shouldEqual("david") // rememberMe not yet supported
                }
            }

            on("clicking 'log out' while logged in") {
                panel.handleUserHasLoggedIn(UserHasLoggedIn(aggregateId = "david@somewhere.com", knownAs = "david", source = source))
                panel.login_logout_Button.click()

                it("logout is invoked") {
                    verify(subjectProvider).logout(any())
                }
            }

        }

        given("a user has logged out") {
            on(" building with an anonymous subject") {
                panel.handleUserHasLoggedOut(UserHasLoggedOut(aggregateId = "david@somewhere.com", knownAs = "david", source = source))
                panel.configureDisplay()

                it("shows the correct labels") {
                    panel.actionLabel.shouldEqual("log in")
                    panel.userId.shouldEqual("Guest")
                }
            }
        }

        given("a Locale change") {
            on("receiving locale change event") {
                val changeSource: UserStatusChangeSource = mock()
                translate = mock()
                whenever(translate.from(LabelKey.Guest)).thenReturn("Guest")
                whenever(translate.from(LoginLabelKey.Log_In)).thenReturn("log in")
                panel = DefaultUserStatusPanel(navigator = navigator, subjectProvider = subjectProvider, translate = translate, currentLocale = currentLocale)
                panel.localeChanged(LocaleChangeBusMessage(changeSource, Locale.GERMANY))

                it("rebuilds the display") {
                    verify(translate, times(2)).from(LoginLabelKey.Log_In) // TODO Cannot figure out why this is called twice - once would be correct
                }
            }
        }

    }

})




