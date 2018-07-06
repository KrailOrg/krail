package uk.q3c.krail.core.form

import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Created by David Sowerby on 06 Jul 2018
 */
object StyleAttributeTest : Spek({

    given("StyleAttributes objects of various construction") {
        lateinit var sa: StyleAttributes

        beforeEachTest { sa = StyleAttributes() }

        on("having all values set") {
            with(sa) {
                alignment = StyleAlignment.align_center
                size = StyleSize.huge
                borderless = StyleBorderless.yes
            }

            it("returns correct value") {
                sa.combinedStyle().shouldBeEqualTo("huge borderless align-center")
            }

        }

        on("having mixed values") {
            with(sa) {
                alignment = StyleAlignment.align_center
                size = StyleSize.huge
            }

            it("returns correct value") {
                sa.combinedStyle().shouldBeEqualTo("huge align-center")
            }
        }

        on("having two values not set") {
            with(sa) {
                alignment = StyleAlignment.align_center
            }

            it("returns correct value") {
                sa.combinedStyle().shouldBeEqualTo("align-center")
            }
        }

        on("having no values set") {
            it("returns an empty String") {
                sa.combinedStyle().shouldBeEmpty()
            }
        }


    }
})
