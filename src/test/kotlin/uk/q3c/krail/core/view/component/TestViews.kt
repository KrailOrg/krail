package uk.q3c.krail.core.view.component

import com.google.inject.Inject
import com.vaadin.ui.Component
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport

/**
 * Created by David Sowerby on 27 Jan 2018
 */


class TestView0 @Inject constructor(translate: Translate, serialisationSupport: SerializationSupport) : ViewBase(translate, serialisationSupport) {


    override fun doBuild() {

    }

}

class TestView1 @Inject constructor(translate: Translate, serialisationSupport: SerializationSupport) : ViewBase(translate, serialisationSupport) {
    lateinit var box1: TextField
    override fun doBuild() {
        box1 = TextField()
    }
}

class TestView2 @Inject constructor(translate: Translate, serialisationSupport: SerializationSupport) : ViewBase(translate, serialisationSupport) {
    lateinit var box1: TextField
    lateinit var layout1: VerticalLayout
    override fun doBuild() {
        box1 = TextField()
        layout1 = VerticalLayout(box1)
    }
}


class SpecificLayout : VerticalLayout() {
    val label: Label = Label("Label in vertical layout")
}

@AssignComponentId(assign = true, drilldown = true)
class AnnotatedSpecificLayoutBothTrue : VerticalLayout() {
    val label: Label = Label("Label in vertical layout")
}

@AssignComponentId(assign = false, drilldown = true)
class AnnotatedSpecificLayoutDrilldownOnly : VerticalLayout() {
    val label: Label = Label("Label in vertical layout")
}

class SpecialComponentImplementation : SpecialComponentInterface, Panel() {
    override val label = Label("boo")
}


interface SpecialComponentInterface : Component {
    val label: Label
}


class FullMontyView @Inject constructor(translate: Translate, serialisationSupport: SerializationSupport) : ViewBase(translate, serialisationSupport) {
    lateinit var componentInView: TextField
    @AssignComponentId(assign = false, drilldown = false)
    lateinit var componentInViewExcluded: TextField
    lateinit var layoutInView: VerticalLayout
    lateinit var emptyField: TextField // not constructed
    @AssignComponentId(assign = true, drilldown = false)
    lateinit var layoutWithAnnotationOnlyAssign: SpecificLayout
    @AssignComponentId(assign = true, drilldown = true)
    lateinit var layoutWithAnnotationAssignAndDrilldown: SpecificLayout
    @AssignComponentId(assign = false, drilldown = true)
    lateinit var layoutWithAnnotationOnlyDrilldown: SpecificLayout

    lateinit var annotatedSpecificLayoutBothTrue: AnnotatedSpecificLayoutBothTrue
    lateinit var annotatedSpecificLayoutDrilldownOnly: AnnotatedSpecificLayoutDrilldownOnly

    @AssignComponentId(assign = true, drilldown = true)
    lateinit var declaredByInterface: SpecialComponentInterface


    override fun doBuild() {
        componentInView = TextField()
        componentInViewExcluded = TextField()
        layoutInView = VerticalLayout(componentInView)
        layoutWithAnnotationOnlyAssign = SpecificLayout()
        layoutWithAnnotationAssignAndDrilldown = SpecificLayout()
        layoutWithAnnotationOnlyDrilldown = SpecificLayout()

        annotatedSpecificLayoutBothTrue = AnnotatedSpecificLayoutBothTrue()
        annotatedSpecificLayoutDrilldownOnly = AnnotatedSpecificLayoutDrilldownOnly()
        declaredByInterface = SpecialComponentImplementation()
    }

}
