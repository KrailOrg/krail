package uk.q3c.krail.core.form

import org.amshove.kluent.shouldBe
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Created by David Sowerby on 23 Jun 2018
 */
object PropertyConfigurationMergeTest : Spek({

    given(" a style reader") {

        on("reading a property with all style attributes changed") {
            val config = TestFormConfig1()
            config.config()
            val section = config.section("simple")
            val subSection = section.section("subSection")
            val propertyConfig = subSection.property("allChanged")
            propertyConfig.merge()
            val result = propertyConfig.styleAttributes

            it("returns the attributes defined by the property") {
                result.alignment.shouldBe(StyleAlignment.align_center)
                result.size.shouldBe(StyleSize.small)
                result.borderless.shouldBe(StyleBorderless.no)
            }
        }

        on("reading a property which inherits attributes from parent chain") {
            val config = TestFormConfig2()
            config.config()
            val section = config.section("simple")
            val subSection = section.section("subSection")
            val propertyConfig = subSection.property("someChanged")
            propertyConfig.merge()
            val result = propertyConfig.styleAttributes

            it("returns a composite of correct values") {
                result.alignment.shouldBe(StyleAlignment.align_right)
                result.size.shouldBe(StyleSize.small)
                result.borderless.shouldBe(StyleBorderless.no)
            }
        }

        on("reading a property which has no style attributes set anywhere in the chain") {
            val config = TestFormConfig3()
            config.config()
            val section = config.section("simple")
            val subSection = section.section("subSection")
            val propertyConfig = subSection.property("someChanged")
            propertyConfig.merge()
            val result = propertyConfig.styleAttributes

            it("returns a composite of correct values") {
                result.alignment.shouldBe(StyleAlignment.no_change)
                result.size.shouldBe(StyleSize.no_change)
                result.borderless.shouldBe(StyleBorderless.no_change)
            }
        }
    }
})


private class TestFormConfig1 : FormConfiguration() {
    override fun config() {
        val section = FormSectionConfiguration(this, "simple")
        sections.add(section)

        val subSection = FormSectionConfiguration(section, "subSection")
        section.sections.add(subSection)

        val propConfig = PropertyConfiguration("allChanged", subSection)
        subSection.properties["allChanged"] = propConfig

        propConfig.styleAttributes.alignment = StyleAlignment.align_center
        propConfig.styleAttributes.size = StyleSize.small
        propConfig.styleAttributes.borderless = StyleBorderless.no

    }

}

private class TestFormConfig2 : FormConfiguration() {
    override fun config() {
        styleAttributes.alignment = StyleAlignment.align_right
        styleAttributes.size = StyleSize.huge
        val section = FormSectionConfiguration(this, "simple")
        sections.add(section)

        section.styleAttributes.size = StyleSize.small
        val subSection = FormSectionConfiguration(section, "subSection")
        subSection.styleAttributes.borderless = StyleBorderless.yes
        section.sections.add(subSection)

        val propConfig = PropertyConfiguration("someChanged", subSection)
        propConfig.styleAttributes.borderless = StyleBorderless.no
        subSection.properties["someChanged"] = propConfig
    }
}

private class TestFormConfig3 : FormConfiguration() {
    override fun config() {
        val section = FormSectionConfiguration(this, "simple")
        sections.add(section)

        val subSection = FormSectionConfiguration(section, "subSection")
        section.sections.add(subSection)

        val propConfig = PropertyConfiguration("someChanged", subSection)
        subSection.properties["someChanged"] = propConfig
    }
}