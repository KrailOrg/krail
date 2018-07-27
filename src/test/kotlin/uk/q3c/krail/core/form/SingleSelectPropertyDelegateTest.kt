package uk.q3c.krail.core.form

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Label
import com.vaadin.ui.RadioButtonGroup
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Created by David Sowerby on 27 Jul 2018
 */
object SingleSelectPropertyDelegateTest : Spek({

    given("a delegate") {
        lateinit var delegate: SingleSelectPropertyDelegate<Person, Int>
        lateinit var person: Person

        beforeEachTest {
            person = Person(name = "him", age = 43)
            delegate = SingleSelectPropertyDelegate(setOf(7, 9))
        }

        on("getValue without value being set") {
            val prop = person::pricePlan
            val value = { delegate.getValue(person, prop) }

            it("should throw exception") {
                value.shouldThrow((SingleSelectException::class))
            }
        }

        on("setting a valid value") {
            val prop = person::pricePlan
            delegate.setValue(person, prop, 9)

            it("sets the value") {
                delegate.getValue(person, prop).shouldBe(9)
            }
        }

        on("setting an invalid valid value") {
            val prop = person::pricePlan
            delegate.setValue(person, prop, 13)

            it("still sets the value, as values should always be valid") {
                delegate.getValue(person, prop).shouldBe(13)
            }
        }

        on("configuring a ComboBox") {
            val combo = ComboBox<Int>()
            delegate.configureComponent(combo)

            it("set the allowable items and whether empty is allowed") {
                (combo.dataProvider as ListDataProvider<Int>).items.shouldContainAll(listOf(7, 9))
                combo.isEmptySelectionAllowed.shouldBe(false)
            }
        }

        on("configuring a RadioGroup") {
            val radioGroup = RadioButtonGroup<Int>()
            delegate.configureComponent(radioGroup)

            it("set the allowable items") {
                (radioGroup.dataProvider as ListDataProvider<Int>).items.shouldContainAll(listOf(7, 9))
            }
        }

        on("calling configure with unsupported component") {
            val component = Label()
            val result = { delegate.configureComponent(component) }

            it("should throw exception") {
                result.shouldThrow(SingleSelectException::class)
            }
        }
    }

})