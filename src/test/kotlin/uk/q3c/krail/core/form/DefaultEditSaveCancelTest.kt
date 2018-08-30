package uk.q3c.krail.core.form

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
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

            it("has button visibility set up as read only mode") {
                esc.editButton.isVisible.shouldBeTrue()
                esc.saveButton.isVisible.shouldBeFalse()
                esc.cancelButton.isVisible.shouldBeFalse()

            }

            it("has button order of edit cancel save") {
                esc.getComponent(0).shouldBe(esc.editButton)
                esc.getComponent(1).shouldBe(esc.cancelButton)
                esc.getComponent(2).shouldBe(esc.saveButton)
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
                esc.editButton.isVisible.shouldBeFalse()
                esc.saveButton.isVisible.shouldBeTrue()
                esc.cancelButton.isVisible.shouldBeTrue()
            }
        }

        on("updating while section is in read only mode") {

            every { section.mode } returns EditMode.READ_ONLY
            esc.updateButtonVisibility()

            it("has button visibility set up as read only mode") {
                esc.editButton.isVisible.shouldBeTrue()
                esc.saveButton.isVisible.shouldBeFalse()
                esc.cancelButton.isVisible.shouldBeFalse()
            }
        }

    }
})