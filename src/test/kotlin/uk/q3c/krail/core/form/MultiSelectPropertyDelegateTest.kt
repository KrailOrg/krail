package uk.q3c.krail.core.form

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.CheckBoxGroup
import com.vaadin.ui.Label
import com.vaadin.ui.ListSelect
import com.vaadin.ui.TwinColSelect
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Created by David Sowerby on 27 Jul 2018
 */
object MultiSelectPropertyDelegateTest : Spek({

    given("a delegate") {
        lateinit var delegate: MultiSelectPropertyDelegate<Person, String>
        lateinit var person: Person

        beforeEachTest {
            person = Person(name = "him", age = 43)
            delegate = MultiSelectPropertyDelegate(setOf("a", "b"))
        }

        on("getValue without value being set") {
            val prop = person::roles
            val value = delegate.getValue(person, prop)

            it("should return empty list") {
                value.shouldBeEmpty()
            }
        }

        on("setting a valid value") {
            val prop = person::pricePlan
            delegate.setValue(person, prop, setOf("c", "d"))

            it("sets the value") {
                delegate.getValue(person, prop).shouldContainAll(listOf("c", "d"))
            }
        }

        on("configuring a ListSelect") {
            val component = ListSelect<String>()
            delegate.configureComponent(component)

            it("set the allowable items") {
                (component.dataProvider as ListDataProvider<String>).items.shouldContainAll(listOf("a", "b"))
            }
        }

        on("configuring a TwinColSelect") {
            val component = TwinColSelect<String>()
            delegate.configureComponent(component)

            it("set the allowable items") {
                (component.dataProvider as ListDataProvider<String>).items.shouldContainAll(listOf("a", "b"))
            }
        }


        on("configuring a CheckBoxGroup") {
            val component = CheckBoxGroup<String>()
            delegate.configureComponent(component)

            it("set the allowable items") {
                (component.dataProvider as ListDataProvider<String>).items.shouldContainAll(listOf("a", "b"))
            }
        }

        on("calling configure with unsupported component") {
            val component = Label()
            val result = { delegate.configureComponent(component) }

            it("should throw exception") {
                result.shouldThrow(MultiSelectException::class)
            }
        }
    }

})