package uk.q3c.krail.core.form

import com.vaadin.data.converter.StringToIntegerConverter
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.InlineDateField
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert
import uk.q3c.krail.i18n.test.TestLabelKey

/**
 * Created by David Sowerby on 01 Aug 2018
 */
object FormConfigurationTest_FluentJava : Spek({

    given("a predefined FormConfiguration object") {
        lateinit var config: FormConfiguration

        beforeEachTest {
            config = FormConfigurationExample1()
        }

        on("config") {
            config.config()

            it("has all the correct property values") {
                config.formType.shouldBeEqualTo("other")
                config.styleAttributes.alignment.shouldEqual(StyleAlignment.align_center)
                val section = config.section("a")
                section.shouldNotBeNull()
                val subsection = section.section("a-1")
                subsection.shouldNotBeNull()
                with(subsection) {
                    layout.shouldEqual(HorizontalLayout::class.java)
                    columnOrder.shouldContainExactly(listOf("q", "a"))
                    entityClass.shouldEqual(Person::class.java)
                    fieldOrder.shouldContainExactly(listOf("b", "c", "d"))
                    excludedProperties.shouldContainAll(listOf("x"))
                    styleAttributes.size.shouldEqual(StyleSize.huge)
                }
                with(section.property("p1")) {
                    caption.shouldEqual(TestLabelKey.Login)
                    description.shouldEqual(TestLabelKey.Opt)
                    converterClass.shouldEqual(StringToIntegerConverter::class.java)
                    componentClass.shouldEqual(InlineDateField::class.java)
                    validators.size.shouldEqual(2)
                    validators[0].shouldBeInstanceOf(MustBeFalse::class.java)
                    validators[1].shouldBeInstanceOf(MustBeTrue::class.java)
                    propertyValueClass.shouldEqual(Boolean::class.java)
                }
            }
        }

    }

})


infix fun <T> Iterable<T>.shouldContainExactly(things: Iterable<T>) = this.apply {
    val thisIter = this.iterator()
    try {
        things.forEach { e -> e.shouldEqual(thisIter.next()) }
        if (thisIter.hasNext()) {
            Assert.fail("Contains additional elements(s): $this, but should be $things")
        }
    } catch (e: NoSuchElementException) {
        Assert.fail("$this is missing one or more elements, it should be: $things")
    }

}
