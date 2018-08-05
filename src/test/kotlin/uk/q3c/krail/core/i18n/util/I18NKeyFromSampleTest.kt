package uk.q3c.krail.core.i18n.util

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.util.I18NKeyFromSample

/**
 * Created by David Sowerby on 05 Aug 2018
 */
object I18NKeyFromSampleTest : Spek({

    given("a sample key") {
        val sample = WigglyKey.id

        on("looking for lower case constant name") {
            val result = I18NKeyFromSample().keyFromName("first", sample)

            it("returns the correct key") {
                result.shouldEqual(WigglyKey.first)
            }

        }

        on("looking for camel cased name to underscored enum") {
            val result = I18NKeyFromSample().keyFromName("firstName", sample)

            it("returns the correct key") {
                result.shouldEqual(WigglyKey.first_name)
            }

        }

        on("looking for capitalised case name with underscore") {
            val result = I18NKeyFromSample().keyFromName("first_name", sample)

            it("returns the correct key") {
                result.shouldEqual(WigglyKey.first_name)
            }

        }

        on("looking for camel case name to camelCase enum") {
            val result = I18NKeyFromSample().keyFromName("thirdName", sample)

            it("returns the correct key") {
                result.shouldEqual(WigglyKey.thirdName)
            }
        }

        on("looking for camel case name to camelCase enum with underscore upper case") {
            val result = I18NKeyFromSample().keyFromName("secondName", sample)

            it("returns the correct key") {
                result.shouldEqual(WigglyKey.Second_Name)
            }
        }

        on("looking for invalid name") {
            val result = { I18NKeyFromSample().keyFromName("softly", sample) }

            it("throws IllegalArgumentException") {
                result.shouldThrow(IllegalArgumentException::class)
            }
        }
    }
})


private enum class WigglyKey : I18NKey {
    id, first, first_name, Second_Name, thirdName
}