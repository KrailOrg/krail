package uk.q3c.krail.core.sysadmin

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.MessageKey
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.util.Experimental
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 12 Jun 2018
 */
object I18NViewTest : Spek({
    given("an I18NView") {
        lateinit var navigationStateExt: NavigationStateExt
        lateinit var userNotifier: UserNotifier
        lateinit var translate: Translate
        lateinit var serializationSupport: SerializationSupport

        beforeEachTest {
            navigationStateExt = mockk(relaxed = true)
            userNotifier = mockk(relaxed = true)
            translate = mockk(relaxed = true)
            serializationSupport = mockk(relaxed = true)
        }

        on("checking class annotation") {
            it("should be annotated experimental") {
                I18NView::class.java.isAnnotationPresent(Experimental::class.java).shouldBeTrue()
            }
        }

        on("building the view") {
            val view = I18NView(userNotifier, translate, serializationSupport)
            every { translate.from(MessageKey.Setup_I18NKey_export, LabelKey.Export) } returns "instruction1 text"
            every { translate.from(MessageKey.All_Keys_exported) } returns "instruction2 text"
            view.beforeBuild(navigationStateExt)
            view.buildView()

            it("has the correct component values") {
                view.rootComponent.shouldNotBeNull()
                view.instructions1.value.shouldBeEqualTo("instruction1 text")
                view.instructions2.value.shouldBeEqualTo("\ninstruction2 text")
                view.localeList.shouldNotBeNull()
            }
        }

        on("exporting") {
            val view = I18NView(userNotifier, translate, serializationSupport)
            view.beforeBuild(navigationStateExt)
            view.buildView()
            view.exportButton.click()

            it("displays 'not yet implemented") {
                userNotifier.notifyInformation(LabelKey.This_feature_has_not_been_implemented)
            }

        }
    }
})