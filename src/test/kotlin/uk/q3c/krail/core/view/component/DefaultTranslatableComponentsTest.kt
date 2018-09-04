package uk.q3c.krail.core.view.component

import com.vaadin.server.FontAwesome
import com.vaadin.ui.Button
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.eventbus.SessionBusProvider
import uk.q3c.krail.core.i18n.CommonLabelKey
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.krail.i18n.test.MockCurrentLocale
import uk.q3c.krail.i18n.test.MockTranslate

/**
 * Created by David Sowerby on 04 Sep 2018
 */
object DefaultTranslatableComponentsTest : Spek({

    given(" a default instance of TranslatableComponents") {
        lateinit var dtc: DefaultTranslatableComponents
        val translate = MockTranslate()
        val currentLocale = MockCurrentLocale()
        val iconFactory: IconFactory = mockk(relaxed = true)
        val sessionBusProvider: SessionBusProvider = mockk(relaxed = true)

        beforeEachTest {
            dtc = DefaultTranslatableComponents(translate, currentLocale, iconFactory, sessionBusProvider)
        }

        on("adding an entry") {
            every { iconFactory.iconFor(CommonLabelKey.Settings) } returns FontAwesome.GEAR
            every { iconFactory.iconFor(CommonLabelKey.Yes) } returns FontAwesome.THUMBS_UP
            val button1 = Button()
            val button2 = Button()
            val button3 = Button()
            dtc.addEntry(component = button1, captionKey = CommonLabelKey.Settings, descriptionKey = CommonLabelKey.Settings)
            dtc.addEntry(component = button2, captionKey = CommonLabelKey.Title, descriptionKey = CommonLabelKey.Title, useIcon = false)
            dtc.addEntry(component = button3, captionKey = CommonLabelKey._NullKey_, descriptionKey = CommonLabelKey.Yes)


            it("retains the component with I18NKey information") {
                dtc.components.containsKey(button1)
                dtc.components.containsKey(button2)
                dtc.components.containsKey(button3)
                dtc.components[button1]?.captionKey.shouldEqual(CommonLabelKey.Settings)
                dtc.components[button1]?.descriptionKey.shouldEqual(CommonLabelKey.Settings)
            }

            it("sets the component icon where useIcon is true") {
                button1.icon.shouldEqual(FontAwesome.GEAR)
            }

            it("does not set the component icon, or call the iconFactory, where 'useIcon' is false") {
                button2.icon.shouldBeNull()
                verify(exactly = 0) { iconFactory.iconFor(CommonLabelKey.Title) }
            }

            it("uses the description key to look up icon if caption key is 'null key'") {
                button3.icon.shouldBe(FontAwesome.THUMBS_UP)
            }

        }

        on("getting a Locale change message") {
            val localeMessage: LocaleChangeBusMessage = mockk(relaxed = true)
            val button1 = Button()
            val button2 = Button()
            dtc.addEntry(component = button1, captionKey = CommonLabelKey.Settings, descriptionKey = CommonLabelKey.Settings, useIcon = true)
            dtc.addEntry(component = button2, captionKey = CommonLabelKey.Title, descriptionKey = CommonLabelKey.Title, useIcon = true)
            dtc.localeChanged(localeMessage)

            it("translates component captions and descriptions") {
                button1.caption.shouldBeEqualTo("Settings")
                button1.description.shouldBeEqualTo("Settings")
                button2.caption.shouldBeEqualTo("Title")
                button2.description.shouldBeEqualTo("Title")
            }
        }
    }
})