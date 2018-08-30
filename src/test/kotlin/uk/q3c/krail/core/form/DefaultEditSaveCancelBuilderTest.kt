package uk.q3c.krail.core.form

import com.google.inject.Provider
import com.vaadin.server.FontAwesome
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
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

            on("getting component") {
                val top = builder.topComponent() as DefaultEditSaveCancel
                val bottom = builder.bottomComponent() as DefaultEditSaveCancel

                it("should have edit, cancel, save buttons in that order") {
                    (top.getComponent(0) as Button).data.shouldEqual(CommonLabelKey.Edit)
                    (top.getComponent(1) as Button).data.shouldEqual(CommonLabelKey.Cancel)
                    (top.getComponent(2) as Button).data.shouldEqual(CommonLabelKey.Save)

                    (bottom.getComponent(0) as Button).data.shouldEqual(CommonLabelKey.Edit)
                    (bottom.getComponent(1) as Button).data.shouldEqual(CommonLabelKey.Cancel)
                    (bottom.getComponent(2) as Button).data.shouldEqual(CommonLabelKey.Save)
                }

                it("should have buttons aligned to the right") {
                    top.getComponentAlignment(top.getComponent(0)).shouldEqual(Alignment.TOP_RIGHT)
                    top.getComponentAlignment(top.getComponent(1)).shouldEqual(Alignment.TOP_RIGHT)
                    top.getComponentAlignment(top.getComponent(2)).shouldEqual(Alignment.TOP_RIGHT)

                    bottom.getComponentAlignment(bottom.getComponent(0)).shouldEqual(Alignment.BOTTOM_RIGHT)
                    bottom.getComponentAlignment(bottom.getComponent(1)).shouldEqual(Alignment.BOTTOM_RIGHT)
                    bottom.getComponentAlignment(bottom.getComponent(2)).shouldEqual(Alignment.BOTTOM_RIGHT)
                }

            }
        }
    }

})