package uk.q3c.krail.core.form

import com.vaadin.ui.HorizontalLayout
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBeNull
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Created by David Sowerby on 17 Aug 2018
 */

object DefaultEditSaveCancelTest : Spek({

    given("an instance of DefaultEditSaveCancel") {
        lateinit var esc: DefaultEditSaveCancel
        lateinit var section: FormDetailSection<*>

        beforeEachTest {
            esc = DefaultEditSaveCancel()
            section = mockk(relaxed = true)
            esc.section = section
        }

        on("construction") {

            it("has created all the buttons") {
                esc.editButton.shouldNotBeNull()
                esc.saveButton.shouldNotBeNull()
                esc.cancelButton.shouldNotBeNull()

            }

            it("layout is empty") {
                (esc.content as HorizontalLayout).componentCount.shouldBe(0)
            }

        }

        on("clicking 'edit' button") {

            esc.editButton.click()

            it("notifies the section") {
                verify { section.editData() }
            }


        }

        on("clicking 'save' button") {
            esc.saveButton.click()

            it("notifies the section") {
                verify { section.saveData() }
            }


        }

        on("clicking 'cancel' button while in edit mode") {
            esc.cancelButton.click()

            it("notifies the section") {
                verify { section.cancelData() }
            }

        }

        on("updating while section is in edit mode") {

            every { section.mode } returns EditMode.EDIT
            esc.updateButtonVisibility()

            it("has button visibility set up as edit mode") {
                (esc.content as HorizontalLayout).getComponent(0).shouldBe(esc.cancelButton)
                (esc.content as HorizontalLayout).getComponent(1).shouldBe(esc.saveButton)
                (esc.content as HorizontalLayout).componentCount.shouldBe(2)
            }
        }

        on("updating while section is in read only mode") {

            every { section.mode } returns EditMode.READ_ONLY
            esc.updateButtonVisibility()

            it("has button visibility set up as read only mode") {
                (esc.content as HorizontalLayout).getComponent(0).shouldBe(esc.editButton)
                esc.componentCount.shouldBe(1)
            }
        }

    }
})