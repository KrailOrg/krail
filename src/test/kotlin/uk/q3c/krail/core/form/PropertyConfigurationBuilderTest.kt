package uk.q3c.krail.core.form

import io.mockk.mockk
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Created by David Sowerby on 08 Jul 2018
 */
object PropertyConfigurationBuilderTest : Spek({

    given("a SectionBuilder") {
        val builder = PropertyConfigurationBuilder()
        val propertySpecCreator = DefaultPropertyConfigurationCreator()
        val formConfiguration: FormConfiguration = mockk(relaxed = true)


        on("processing a SectionConfiguration with a specified displayOrder") {
            val config1 = FormSectionConfiguration(formConfiguration)
            config1.entityClass = Person::class.java
            config1.fieldOrder = listOf("name", "age", "id")
            config1.excludedProperties = listOf("id")
            builder.build(config1, propertySpecCreator)
            val propertyIterator = config1.properties.iterator()

            it("produces property list in the order specified") {
                propertyIterator.next().value.name.shouldBeEqualTo("name")
                propertyIterator.next().value.name.shouldBeEqualTo("age")
            }

            it("does not contain the excluded property") {
                config1.properties.size.shouldBe(2)
            }

        }

        on("processing a SectionConfiguration with no displayOrder specified") {
            val config1 = FormSectionConfiguration(formConfiguration)
            config1.entityClass = Person::class.java
            config1.excludedProperties = listOf("id")
            builder.build(config1, propertySpecCreator)

            it("contains all the entity properties except the excluded one") {
                config1.properties.size.shouldBe(6)
                //These would throw an exception if not found
                config1.properties.values.first { f -> f.name == "name" }
                config1.properties.values.first { f -> f.name == "age" }
                config1.properties.values.first { f -> f.name == "dob" }
                config1.properties.values.first { f -> f.name == "joinDate" }
                config1.properties.values.first { f -> f.name == "title" }
            }


        }
    }
})


