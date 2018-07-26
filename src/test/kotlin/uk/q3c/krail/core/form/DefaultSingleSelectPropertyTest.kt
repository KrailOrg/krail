package uk.q3c.krail.core.form

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.apache.commons.lang3.SerializationUtils
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Created by David Sowerby on 23 Jul 2018
 */
object DefaultSingleSelectPropertyTest : Spek({

    given("property representing a single selection") {

        on("default construction") {
            val v = DefaultSingleSelectProperty(setOf())
            val selectedValue = { v.selected() }

            it("has no value") {
                v.hasValue().shouldBeFalse()
            }

            it("should have an empty list of permitted values") {
                v.permittedValues.shouldBeEmpty()
            }

            it("does not allow selection of no value") {
                v.allowNoSelection.shouldBeFalse()
            }

            it("should throw exception if select() called") {
                selectedValue.shouldThrow(SingleSelectionException::class)
            }
        }

        on("setting a permitted value") {
            val v = DefaultSingleSelectProperty(setOf(1, 3, 7))
            v.select(3)

            it("returns the selected value") {
                v.selected().shouldEqual(3)
            }
        }


        on("setting a non-permitted value") {
            val v = DefaultSingleSelectProperty(setOf(1, 3, 7))
            val selectResult = { v.select(8) }

            it("throws exception") {
                selectResult.shouldThrow(SingleSelectionException::class)
            }
        }

        on("deselecting when not allowed") {
            val v = DefaultSingleSelectProperty(setOf(1, 3, 7))
            v.select(3)
            val deselectResult = { v.deselect() }

            it("throws an exception") {
                deselectResult.shouldThrow(SingleSelectionException::class)
            }
        }

        on("deselecting when allowed") {
            val v = DefaultSingleSelectProperty(setOf(1, 3, 7), true)
            v.select(3)
            v.deselect()

            it("has no value") {
                v.hasValue().shouldBeFalse()
            }
        }

        on("setting delegated property to valid value") {
            val p = Person(age = 23, name = "Him")
            p.pricePlan = 3

            it("sets the value") {
                p.pricePlan.shouldBe(3)
            }
        }

        on("setting delegated property to invalid value") {
            val p = Person(age = 23, name = "Him")
            val result = { p.pricePlan = 0 }

            it("sets the value") {
                result.shouldThrow(SingleSelectionException::class)
            }
        }


        on("serialisation") {
            val p = Person(age = 23, name = "Him")
            p.pricePlan = 3
            val output = SerializationUtils.serialize(p)
            val p2: Person = SerializationUtils.deserialize(output)

            it("should restore delegated value") {
                p.pricePlan.shouldBe(3)
            }
        }
    }
})