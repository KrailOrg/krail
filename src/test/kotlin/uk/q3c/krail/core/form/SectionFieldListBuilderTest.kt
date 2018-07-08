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
object SectionFieldListBuilderTest : Spek({

    given("a SectionFieldListBuilder") {
        val builder = SectionFieldListBuilder()
        val formConfiguration: FormConfiguration = mockk(relaxed = true)


        on("processing a SectionConfiguration with a specified displayOrder") {
            val config1 = SectionConfiguration(formConfiguration)
            config1.entityClass = Person::class
            config1.displayOrder = listOf("name", "age", "id")
            config1.excludedProperties = listOf("id")
            val result = builder.build(config1)

            it("produces field list in the order specified") {
                result[0].name.shouldBeEqualTo("name")
                result[1].name.shouldBeEqualTo("age")
            }

            it("does not contain the excluded property") {
                result.size.shouldBe(2)
            }

        }

        on("processing a SectionConfiguration with no displayOrder specified") {
            val config1 = SectionConfiguration(formConfiguration)
            config1.entityClass = Person::class
            config1.excludedProperties = listOf("id")
            val result = builder.build(config1)

            it("contains all the entity properties except the excluded one") {
                result.size.shouldBe(5)
                //These would throw an exception if not found
                result.first { f -> f.name == "name" }
                result.first { f -> f.name == "age" }
                result.first { f -> f.name == "dob" }
                result.first { f -> f.name == "joinDate" }
                result.first { f -> f.name == "title" }
            }


        }
    }
})


