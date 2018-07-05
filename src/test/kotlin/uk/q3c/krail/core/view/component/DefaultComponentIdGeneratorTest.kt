package uk.q3c.krail.core.view.component

import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotContain
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.clazz.DefaultUnenhancedClassIdentifier
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 27 Jan 2018
 */
class DefaultComponentIdGeneratorTest : Spek({
    given("a generator") {
        val generator = DefaultComponentIdGenerator(DefaultUnenhancedClassIdentifier())
        val translate: Translate = mock()
        val message: ViewChangeBusMessage = mock()
        val serializationSupport: SerializationSupport = mock()

        given("a view with a single component") {
            val view = TestView1(translate, serializationSupport)
            view.buildView(message)


            on("generate") {
                val result = generator.generateAndApply(view)

                it("applies id to component") {
                    result.nodes().shouldContain(ComponentIdEntry(name = "box1", id = "TestView1-box1", type = "TextField", baseComponent = true))
                    view.box1.id.shouldBeEqualTo("TestView1-box1")
                }
            }

        }

        given("a view with no components") {
            val view = TestView0(translate, serializationSupport)
            view.buildView(message)

            on("generate") {
                val result = generator.generateAndApply(view)

                it("generates graph with just the view as an entry") {
                    result.nodes().size shouldBe (1)
                    result.nodes().shouldContain(ComponentIdEntry(name = "TestView0", id = "TestView0", type = "TestView0", baseComponent = false))
                }
            }
        }

        given("a view with components and layout components") {
            val view = TestView2(translate, serializationSupport)
            view.buildView(message)

            on("apply") {
                val result = generator.generateAndApply(view)

                it("applies id to component") {
                    result.nodes().shouldContain(ComponentIdEntry(name = "box1", id = "TestView2-box1", type = "TextField", baseComponent = true))
                    view.box1.id.shouldBeEqualTo("TestView2-box1")
                    view.layout1.id.shouldBeNull()
                }
            }
        }

        given("a view with all combinations of components") {
            val view = FullMontyView(translate, serializationSupport)
            view.buildView(message)

            on("generate and apply") {
                val result = generator.generateAndApply(view)

                it("Should have the correct number of total nodes, then only need to check for existence of entries, not absence") {
                    result.nodes().size shouldBe (13)
                }

                it("contains component declared in the View") {
                    val expectedId = "FullMontyView-componentInView"
                    result.nodes().shouldContain(ComponentIdEntry(name = "componentInView", id = expectedId, type = "TextField", baseComponent = true))
                    view.componentInView.id.shouldBeEqualTo(expectedId)
                }

                it("does not contain component declared in the View, excluded by annotation") {
                    result.nodes().shouldNotContain(ComponentIdEntry(name = "componentInViewExcluded", id = "FullMontyView-componentInViewExcluded", type = "TextField", baseComponent = true))
                    view.componentInViewExcluded.id.shouldBeNull()
                }

                it("does not contain a layout without annotation") {
                    result.nodes().shouldNotContain(ComponentIdEntry(name = "layoutInView", id = "FullMontyView-layoutInView", type = "VerticalLayout", baseComponent = false))
                }

                it("does not generate an id for an empty (not constructed) field, nor attempt to apply it") {
                    val expectedId = "FullMontyView-emptyField"
                    result.nodes().shouldNotContain(ComponentIdEntry(name = "emptyField", id = expectedId, type = "TextField", baseComponent = true))
                    ({ view.emptyField }) shouldThrow UninitializedPropertyAccessException::class
                }

                it("generates an id for an Layout annotated with ('true,false'), but does not drill down") {
                    val expectedId = "FullMontyView-layoutWithAnnotationOnlyAssign"
                    result.nodes().shouldContain(ComponentIdEntry(name = "layoutWithAnnotationOnlyAssign", id = expectedId, type = "SpecificLayout", baseComponent = false))
                    view.layoutWithAnnotationOnlyAssign.id shouldBeEqualTo (expectedId)
                }

                it("generates an id for an annotated Layout, but not for its content, with annotation(true,false)") {
                    val expectedId = "FullMontyView-layoutWithAnnotationOnlyAssign"
                    result.nodes().shouldContain(ComponentIdEntry(name = "layoutWithAnnotationOnlyAssign", id = expectedId, type = "SpecificLayout", baseComponent = false))
                    view.layoutWithAnnotationOnlyAssign.id shouldBeEqualTo (expectedId)
                    result.nodes().shouldNotContain(ComponentIdEntry(name = "label", id = "$expectedId-label", type = "Label", baseComponent = true))
                    view.layoutWithAnnotationOnlyAssign.label.id.shouldBeNull()

                }

                it("generated an id for an annotated layout, and its content, with annotation(true,true)") {
                    val expectedId = "FullMontyView-layoutWithAnnotationAssignAndDrilldown"
                    result.nodes().shouldContain(ComponentIdEntry(name = "layoutWithAnnotationAssignAndDrilldown", id = expectedId, type = "SpecificLayout", baseComponent = false))
                    view.layoutWithAnnotationAssignAndDrilldown.id shouldBeEqualTo (expectedId)
                    result.nodes().shouldContain(ComponentIdEntry(name = "label", id = "$expectedId-label", type = "Label", baseComponent = true))
                    view.layoutWithAnnotationAssignAndDrilldown.label.id.shouldBeEqualTo("$expectedId-label")
                }

                it("generated an id for an annotated layout, and its content, with annotation(false,true), and assigns layout id, overriding annotation.assign") {
                    val expectedId = "FullMontyView-layoutWithAnnotationOnlyDrilldown"
                    view.layoutWithAnnotationOnlyDrilldown.id.shouldBeEqualTo(expectedId)
                    result.nodes().shouldContain(ComponentIdEntry(name = "layoutWithAnnotationOnlyDrilldown", id = expectedId, type = "SpecificLayout", baseComponent = false))
                    view.layoutWithAnnotationOnlyDrilldown.label.id.shouldBeEqualTo("FullMontyView-layoutWithAnnotationOnlyDrilldown-label")
                    result.nodes().shouldContain(ComponentIdEntry(name = "label", id = "FullMontyView-layoutWithAnnotationOnlyDrilldown-label", type = "Label", baseComponent = true))
                }

                it("for class annotation (true,true), generated an id and drilled down class annotated composite") {
                    val expectedId = "FullMontyView-annotatedSpecificLayoutBothTrue"
                    result.nodes().shouldContain(ComponentIdEntry(name = "annotatedSpecificLayoutBothTrue", id = expectedId, type = "AnnotatedSpecificLayoutBothTrue", baseComponent = false))
                    view.annotatedSpecificLayoutBothTrue.id shouldBeEqualTo (expectedId)
                    result.nodes().shouldContain(ComponentIdEntry(name = "label", id = "$expectedId-label", type = "Label", baseComponent = true))
                    view.annotatedSpecificLayoutBothTrue.label.id.shouldBeEqualTo("$expectedId-label")
                }


                it("for class annotation (false,true), generates id for contained component, with full includes its immediate parent") {
                    val expectedId = "FullMontyView-annotatedSpecificLayoutDrilldownOnly"
                    result.nodes().shouldContain(ComponentIdEntry(name = "annotatedSpecificLayoutDrilldownOnly", id = expectedId, type = "AnnotatedSpecificLayoutDrilldownOnly", baseComponent = false))
                    view.annotatedSpecificLayoutDrilldownOnly.id.shouldBeEqualTo(expectedId)
                    result.nodes().shouldContain(ComponentIdEntry(name = "label", id = "$expectedId-label", type = "Label", baseComponent = true))
                    view.annotatedSpecificLayoutDrilldownOnly.label.id.shouldBeEqualTo("$expectedId-label")
                }

                it("generates an id a field (with annotation(true,true)) declared as an interface, and a field declared by its implementation") {
                    val expectedId = "FullMontyView-declaredByInterface"
                    println(" ==============================================================")
                    result.nodes().forEach(::println)
                    result.nodes().shouldContain(ComponentIdEntry(name = "declaredByInterface", id = expectedId, type = "SpecialComponentImplementation", baseComponent = false))
                    result.nodes().shouldNotContain(ComponentIdEntry(name = "content", id = "FullMontyView-declaredByInterface-content", type = "Component", baseComponent = true))
                    view.declaredByInterface.id shouldBeEqualTo (expectedId)
                    result.nodes().shouldContain(ComponentIdEntry(name = "label", id = "$expectedId-label", type = "Label", baseComponent = true))
                    view.declaredByInterface.label.id.shouldBeEqualTo("$expectedId-label")
                }
            }
        }


    }
})

