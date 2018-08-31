package uk.q3c.krail.core.form

import com.google.inject.Provider
import com.vaadin.server.FontAwesome
import com.vaadin.ui.Button
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.i18n.CommonLabelKey
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.core.view.component.DefaultIconFactory
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 17 Aug 2018
 */
object DefaultEditSaveCancelBuilderTest : Spek({

    given("a DefaultEditSaveCancelBuilder") {
        lateinit var builder: DefaultEditSaveCancelBuilder
        lateinit var serializationSupport: SerializationSupport

        given("a default configuration") {
            lateinit var escProvider: Provider<EditSaveCancel>
            lateinit var userNotifier: UserNotifier



            beforeEachTest {
                serializationSupport = mockk(relaxed = true)
                userNotifier = mockk(relaxed = true)
                escProvider = mockk(relaxed = true)
                every { escProvider.get() }.returnsMany(listOf(DefaultEditSaveCancel(), DefaultEditSaveCancel()))
                builder = DefaultEditSaveCancelBuilder(editSaveCancelProvider = escProvider, serializationSupport = serializationSupport, userNotifier = userNotifier, iconFactory = DefaultIconFactory())
            }

            on("construction") {
                it("has top right and bottom right position set") {
                    builder.hasTopComponent().shouldBeTrue()
                    builder.hasBottomComponent().shouldBeTrue()
                }
            }

            on("configuring edit button") {
                val button = Button()
                builder.editButton(button)

                it("should set button styles and icon") {
                    button.styleName.shouldContain("friendly")
                    button.icon.shouldEqual(FontAwesome.EDIT)
                }

                it("should have I18NKey in data") {
                    button.data.shouldEqual(CommonLabelKey.Edit)
                }
            }

            on("configuring save button") {
                val button = Button()
                builder.saveButton(button)

                it("should set button styles and icon") {
                    button.styleName.shouldContain("friendly")
                    button.styleName.shouldContain("primary")
                    button.icon.shouldEqual(FontAwesome.SAVE)
                }

                it("should have I18NKey in data") {
                    button.data.shouldEqual(CommonLabelKey.Save)
                }
            }

            on("configuring cancel button") {
                val button = Button()
                builder.cancelButton(button)

                it("should set button styles and icon") {
                    button.styleName.shouldContain("danger")
                    button.icon.shouldEqual(FontAwesome.TIMES_CIRCLE)
                }

                it("should have I18NKey in data") {
                    button.data.shouldEqual(CommonLabelKey.Cancel)
                }
            }

            on("getting top component") {
                val top = (builder.topComponent() as DefaultEditSaveCancel)

                it("should have edit, cancel, save buttons created") {
                    top.editButton.shouldNotBeNull()
                    top.saveButton.shouldNotBeNull()
                    top.cancelButton.shouldNotBeNull()
                }

            }

            on("getting bottom component") {
                val top = (builder.bottomComponent() as DefaultEditSaveCancel)

                it("should have edit, cancel, save buttons created") {
                    top.editButton.shouldNotBeNull()
                    top.saveButton.shouldNotBeNull()
                    top.cancelButton.shouldNotBeNull()
                }

            }
        }
    }

})