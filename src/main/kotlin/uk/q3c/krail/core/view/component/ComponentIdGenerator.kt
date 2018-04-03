package uk.q3c.krail.core.view.component

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import com.google.inject.Inject
import com.vaadin.ui.Accordion
import com.vaadin.ui.Audio
import com.vaadin.ui.BrowserFrame
import com.vaadin.ui.Button
import com.vaadin.ui.CheckBox
import com.vaadin.ui.ColorPicker
import com.vaadin.ui.ColorPickerArea
import com.vaadin.ui.Component
import com.vaadin.ui.DateField
import com.vaadin.ui.DateTimeField
import com.vaadin.ui.DragAndDropWrapper
import com.vaadin.ui.Embedded
import com.vaadin.ui.Flash
import com.vaadin.ui.Grid
import com.vaadin.ui.GridLayout
import com.vaadin.ui.HasComponents
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.HorizontalSplitPanel
import com.vaadin.ui.Image
import com.vaadin.ui.InlineDateField
import com.vaadin.ui.InlineDateTimeField
import com.vaadin.ui.Label
import com.vaadin.ui.Layout
import com.vaadin.ui.Link
import com.vaadin.ui.MenuBar
import com.vaadin.ui.NativeButton
import com.vaadin.ui.Panel
import com.vaadin.ui.PasswordField
import com.vaadin.ui.PopupView
import com.vaadin.ui.ProgressBar
import com.vaadin.ui.RichTextArea
import com.vaadin.ui.Slider
import com.vaadin.ui.TabSheet
import com.vaadin.ui.TextArea
import com.vaadin.ui.TextField
import com.vaadin.ui.Tree
import com.vaadin.ui.TreeGrid
import com.vaadin.ui.UI
import com.vaadin.ui.Upload
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.VerticalSplitPanel
import com.vaadin.ui.Video
import com.vaadin.ui.components.colorpicker.ColorPickerGradient
import com.vaadin.ui.components.colorpicker.ColorPickerGrid
import com.vaadin.ui.components.colorpicker.ColorPickerHistory
import com.vaadin.ui.components.colorpicker.ColorPickerSelect
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.ui.ScopedUI
import uk.q3c.krail.core.view.KrailView
import uk.q3c.util.clazz.UnenhancedClassIdentifier
import java.io.Serializable
import java.lang.reflect.Field


/**
 *
 * Generates Ids for Vaadin components (which then become CSS selectors).  Default implementation provided by [DefaultComponentIdGenerator]
 *
 * Created by David Sowerby on 27 Jan 2018
 */

interface ComponentIdGenerator : Serializable {

    /**
     * Generates a map of field names to ids using [generate] and applies them to the components in [obj]
     */
    fun <T : Any> generateAndApply(obj: T): MutableGraph<ComponentIdEntry>

}

/**
 * Selects a component to receive an annotation, returns:
 *
 * - the value of [AssignComponentId.assign] if annotated (a field annotation overrides a class annotation)
 * - true if annotated with [AssignComponentId.drilldown] set to true (because drilldown without assign causes duplicate ids)
 * - if there is no annotation, false if the component implements [Layout]
 * - if there is no annotation, true for all other components
 *
 *
 */
data class IdAnnotationValues(val annotationPresent: Boolean, val assign: Boolean, val drilldown: Boolean)


class ComponentIDAssignmentFilter(val realClassIdentifier: UnenhancedClassIdentifier) : Serializable {

    fun apply(field: Field, obj: Any): Boolean {
        val log = LoggerFactory.getLogger(this.javaClass.name)
        field.isAccessible = true
        val c = field.get(obj)
        if (c == null) {
            log.warn("Field '${field.name}' must be constructed to be included in id assignment")
            return false
        }
        val cc = c as Component
        val cClazz = realClassIdentifier.getOriginalClassFor(cc)
        var assign = !layoutClasses.contains(cClazz)
        val annotationReader = AnnotationReader(realClassIdentifier)
        val annotationValues: IdAnnotationValues = annotationReader.readAnnotation(field, c)
        if (annotationValues.annotationPresent) {
            assign = annotationValues.assign
        }

        log.debug("Component '${field.name}' selected for id assign is: '$assign'")
        return assign
    }
}

/**
 * Selects a component to drill down into:
 *
 * Annotation has already been read and set by [DefaultComponentIdGenerator.readAnnotation], and if no specific
 * annotation rule has been applied, drilldown is applied if the field type implements [HasComponents], but not if it implements [Layout]
 *
 */
class ComponentIDDrilldownFilter(val realClassIdentifier: UnenhancedClassIdentifier) : Serializable {


    fun apply(field: Field, obj: Any): Boolean {
        val log = LoggerFactory.getLogger(this.javaClass.name)
        field.isAccessible = true
        val c = field.get(obj)
        if (c == null) {
            log.warn("Field '${field.name}' must be constructed to be included in drilldown")
            return false
        }
        val cc = c as Component

        var drilldown = true
        val annotationReader = AnnotationReader(realClassIdentifier)
        val annotationValues: IdAnnotationValues = annotationReader.readAnnotation(field, c)

        if (annotationValues.annotationPresent) {
            drilldown = annotationValues.drilldown
        }

        val cClazz = realClassIdentifier.getOriginalClassFor(cc)
        if (layoutClasses.contains(cClazz) || baseVaadinComponents.contains(cClazz)) {
            drilldown = false
        }

        log.debug("Component '${field.name}' selected for id drilldown is: '$drilldown'")
        return drilldown
    }

}

data class ComponentIdEntry(val name: String, val id: String, val type: String, val baseComponent: Boolean)

/**
 * Creates an id of the form *SomeView-component-nestedcomponent* using the following logic, provided in part by [ComponentIDAssignmentFilter] and [ComponentIDDrilldownFilter]:
 *
 * - All instances of Component are allocated an id, except those which implement [Layout]
 * - All components which implement [HasComponents], except those which implement [Layout], are drilled down to find further components
 *
 * This can be modified by using [AssignComponentId], which can be applied to either class or field. When the annotation is used:
 *
 * - if EITHER *assign* or *drilldown* is true, an id is assigned (drilldown without assign can cause duplicate ids)
 * - if *assign* is false, no id is assigned, even if would otherwise have been.
 * - if *drilldown* is false, no drilldown occurs, even if it would otherwise have done
 *
 * The generator is generally invoked on the instance of a [View] or [UI], but, and components should already be constructed.  If a field has not been constructed it will simply be ignored
 *
 */
class DefaultComponentIdGenerator @Inject constructor(private val realClassIdentifier: UnenhancedClassIdentifier) : ComponentIdGenerator {


    private val assignmentFilter = ComponentIDAssignmentFilter(realClassIdentifier)
    private val drilldownFilter = ComponentIDDrilldownFilter(realClassIdentifier)
    lateinit var graph: MutableGraph<ComponentIdEntry>


    private fun <T : Any> doDrillDown(parentEntry: ComponentIdEntry, clazz: Class<out T>, apply: Boolean, obj: T) {
        // sort the fields into those which need an id assigned, and those which need to be drilled down into
        // the same field may be in both lists
        val componentFields = collectAllComponents(clazz, obj)
        val assigns = componentFields.filter { assignmentFilter.apply(it, obj) }
        val drilldowns = componentFields.filter { drilldownFilter.apply(it, obj) }

        // We want to provide a mapping back to the caller, so the id assignments can be used elsewhere

        // construct the id, and if apply is true, apply it
        assigns.forEach({ f ->
            f.isAccessible = true
            val c = f.get(obj)
            if (c != null) {
                val entry = entryFor(parentEntry, f, c as Component)
                log.debug("adding node ${parentEntry.id} to graph")
                graph.addNode(entry)
                log.debug("placing edge from ${parentEntry.id} to ${entry.id}")
                graph.putEdge(parentEntry, entry)

                if (apply) {
                    log.debug("Assigning id '${entry.id}' to component ${f.name}")
                    c.id = entry.id
                }

            } else {
                log.warn("Component for field '${f.name}' has not been constructed, id cannot be applied")
            }

        })

        // drill down with prefix set to "this" component / View / UI
        drilldowns.forEach({ f ->
            log.debug("drilling down into {}", f.name)
            f.isAccessible = true
            val c = f.get(obj)
            if (c != null) {
                val thisEntry = entryFor(parentEntry, f, c as Component)
                doDrillDown(thisEntry, realClassIdentifier.getOriginalClassFor(c), apply, c)
            } else {
                log.warn("Component for field '${f.name}' has not been constructed, cannot drill down")
            }
        })
    }

    /**
     * When we have a component which extends a base component (for example, the DefaultNavigationMenu extends MenuBar)
     * we want to treat it as the base component - for example, we want the property in the ViewObject to be:
     *
     * val menu = MenuBar()
     *
     *
     * Otherwise we end up with a mismatch between the id of the property in the ViewObject and the actual id, because
     * the structures are not the same
     */
    private fun entryFor(parentEntry: ComponentIdEntry, f: Field, component: Component): ComponentIdEntry {
        val id = "${parentEntry.id}-${f.name}"
        if (isBaseComponent(component)) {
            return ComponentIdEntry(name = f.name, id = id, type = component::class.java.simpleName, baseComponent = true)
        }
        if (isBaseComponentSubClass(component)) {
            return ComponentIdEntry(name = f.name, id = id, type = component::class.java.superclass.simpleName, baseComponent = true)
        }
        return ComponentIdEntry(name = f.name, id = id, type = component::class.java.simpleName, baseComponent = false)
    }

    private fun <T> collectAllComponents(clazz: Class<out T>, obj: Any): List<Field> {
        var classToScan: Class<out Any?> = clazz
        log.debug("scanning '{}' to generate component ids", classToScan.name)
        val fields: MutableList<Field> = mutableListOf()
        try {
            var done = false
            while (!done) {
                fields.addAll(classToScan.declaredFields.filter({ f -> componentFilter(f, obj) }))
                done = (classToScan == Any::class.java) || (classToScan.superclass == null)
                if (!done) {
                    classToScan = classToScan.superclass
                }
            }
        } catch (iae: IllegalAccessException) {
            log.error("reflective scan of components failed", iae)
        }
        log.debug("${fields.size} fields selected")
        return fields
    }

    private fun isBaseComponent(component: Component): Boolean {
        return baseVaadinComponents.contains(component::class.java)
    }

    private fun isBaseComponentSubClass(component: Component): Boolean {
        return baseVaadinComponents.contains(component::class.java.superclass)
    }


    private val log = LoggerFactory.getLogger(this.javaClass.name)

    private fun componentFilter(field: Field, obj: Any): Boolean {
        if (field.name == "parent") {
            return false
        }
        if (obj is KrailView) {
            if (field.name == "rootComponent") {
                return false
            }
        }
        if (obj is ScopedUI) {
            if (field.name == "screenLayout") {
                return false
            }
            if (field.name == "viewDisplayPanel") {
                return false
            }
            if (field.name == "scrollIntoView") {
                return false
            }
            if (field.name == "content") {
                return false
            }
            if (field.name == "pendingFocus") {
                return false
            }
        }

        return (Component::class.java.isAssignableFrom(field.type))
    }

    private fun <T : Any> generateAndApply(clazz: Class<out T>, apply: Boolean, obj: T) {
        val entry = ComponentIdEntry(clazz.simpleName, clazz.simpleName, clazz.simpleName, false)
        graph = GraphBuilder.directed().build<ComponentIdEntry>()
        graph.addNode(entry)
        doDrillDown(parentEntry = entry, clazz = clazz, apply = apply, obj = obj)
    }


    override fun <T : Any> generateAndApply(obj: T): MutableGraph<ComponentIdEntry> {
        val clazz = realClassIdentifier.getOriginalClassFor(obj)
        generateAndApply(clazz = clazz, apply = true, obj = obj)
        return graph
    }


}

/**
 * See [ComponentIdGenerator] and [DefaultComponentIdGenerator]
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AssignComponentId(val assign: Boolean = true, val drilldown: Boolean = true)


class AnnotationReader(val realClassIdentifier: UnenhancedClassIdentifier) {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    /**
     * Reads the values of annotation(s) present on the field or class.  Field takes precedence.  Looks for class annotation
     * on the instance type, as it may be a subclass (or implementation) of the declared field type.
     *
     */
    fun readAnnotation(f: Field, c: Component): IdAnnotationValues {
        f.isAccessible = true
        // no component, so no point trying to assign

        val cClazz = realClassIdentifier.getOriginalClassFor(c)
        var annotation = cClazz.getAnnotation(AssignComponentId::class.java)

        // override with field annotation if there is one
        if (f.isAnnotationPresent(AssignComponentId::class.java)) {
            annotation = f.getAnnotation(AssignComponentId::class.java)
        }
        var assign = true
        var drilldown = true

        if (annotation != null) {
            assign = annotation.assign
            drilldown = annotation.drilldown
        }

        if (drilldown) assign = true

        return IdAnnotationValues(annotationPresent = annotation != null, assign = assign, drilldown = drilldown)
    }
}


private val baseVaadinComponents: List<Class<out Component>> = listOf(Button::class.java, TabSheet::class.java,
        Accordion::class.java,
        BrowserFrame::class.java,
        Flash::class.java,
        Image::class.java,
        ColorPicker::class.java,
        ColorPickerArea::class.java,
        DateField::class.java,
        InlineDateField::class.java,
        DateTimeField::class.java,
        InlineDateTimeField::class.java,
        TextArea::class.java,
        TextField::class.java,
        PasswordField::class.java,
        CheckBox::class.java,
        ColorPickerGradient::class.java,
        ColorPickerGrid::class.java,
        ColorPickerHistory::class.java,
        ColorPickerSelect::class.java,
        RichTextArea::class.java,
        Slider::class.java,
        Button::class.java,
        NativeButton::class.java,
        Grid::class.java,
        TreeGrid::class.java,
        Audio::class.java,
        Video::class.java,
        Tree::class.java,
        DragAndDropWrapper::class.java,
        Embedded::class.java,
        Label::class.java,
        Link::class.java,
        MenuBar::class.java,
        PopupView::class.java,
        ProgressBar::class.java,
        Upload::class.java)

private val layoutClasses: List<Class<out Component>> = listOf(
        Panel::class.java,
        VerticalLayout::class.java,
        HorizontalLayout::class.java,
        GridLayout::class.java,
        VerticalSplitPanel::class.java,
        HorizontalSplitPanel::class.java
)