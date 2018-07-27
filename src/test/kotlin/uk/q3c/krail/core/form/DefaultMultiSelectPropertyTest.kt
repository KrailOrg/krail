package uk.q3c.krail.core.form

import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldNotThrow
import org.amshove.kluent.shouldThrow
import org.apache.commons.lang3.SerializationUtils
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Created by David Sowerby on 27 Jul 2018
 */
object DefaultMultiSelectPropertyTest : Spek({
    given("property representing a single selection") {

        on("default construction") {
            val v = DefaultMultiSelectProperty(setOf())
            val selectedValue = v.selected()

            it("has no value") {
                v.hasValue().shouldBeFalse()
            }

            it("should have an empty list of permitted values") {
                v.dataProvider.items.shouldBeEmpty()
            }

            it("allows selection of no value") {
                v.allowNoSelection.shouldBeTrue()
            }

            it("should return empty set if select() called") {
                selectedValue.shouldBeEmpty()
            }
        }

        on("setting a permitted value") {
            val v = DefaultMultiSelectProperty(setOf(1, 3, 7))
            v.select(3)

            it("returns the selected value") {
                v.selected().shouldContain(3)
            }
        }


        on("setting a non-permitted value") {
            val v = DefaultMultiSelectProperty(setOf(1, 3, 7))
            val selectResult = { v.select(8) }

            it("does not throw exception, we expect value to be valid") {
                selectResult.shouldNotThrow(MultiSelectException::class)
            }
        }

        on("deselecting when not allowed") {
            val v = DefaultMultiSelectProperty(setOf(1, 3, 7), false)
            v.select(3)
            val deselectResult = { v.deselect(3) }

            it("throws an exception") {
                deselectResult.shouldThrow(MultiSelectException::class)
            }
        }

        on("deselecting when allowed") {
            val v = DefaultMultiSelectProperty(setOf(1, 3, 7))
            v.select(3)
            v.deselect(3)

            it("has no value") {
                v.hasValue().shouldBeFalse()
            }
        }

        on("clearing when not allowed") {
            val v = DefaultMultiSelectProperty(setOf(1, 3, 7), false)
            v.select(3)
            val deselectResult = { v.clear() }

            it("throws an exception") {
                deselectResult.shouldThrow(MultiSelectException::class)
            }
        }

        on("clearing when allowed") {
            val v = DefaultMultiSelectProperty(setOf(1, 3, 7), true)
            v.select(3)
            v.clear()

            it("has no value") {
                v.hasValue().shouldBeFalse()
            }
        }

        on("setting delegated property to valid value") {
            val p = Person(age = 23, name = "Him")
            p.roles = setOf("a", "b")

            it("sets the value") {
                p.roles.shouldContainAll(listOf("a", "b"))
            }
        }

        on("serialisation") {
            val p = Person(age = 23, name = "Him")
            p.roles = setOf("a", "b")

            val output = SerializationUtils.serialize(p)
            val p2: Person = SerializationUtils.deserialize(output)

            it("should restore delegated value") {
                p2.roles.shouldContainAll(listOf("a", "b"))
            }
        }
    }


})