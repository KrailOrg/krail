package uk.q3c.krail.core.view.component

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import com.google.inject.Inject
import com.vaadin.ui.Component
import com.vaadin.ui.HasComponents
import com.vaadin.ui.Layout
import org.slf4j.LoggerFactory
import uk.q3c.util.clazz.UnenhancedClassIdentifier
import java.lang.reflect.Field


/**
 *
 * Generates Ids for Vaadin components (which then become CSS selectors).  Default implementation provided by [DefaultComponentIdGenerator]
 *
 * Created by David Sowerby on 27 Jan 2018
 */

interface ComponentIdGenerator {

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


class ComponentIDAssignmentFilter {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    fun apply(field: Field, annotationValues: IdAnnotationValues): Boolean {

        var assign = !Layout::class.java.isAssignableFrom(field.type)
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
 * - If the Field type implements [HasComponents] but does not implement [Layout], returns true, unless overruled by annotation
 * - the value of [AssignComponentId.drilldown] if annotated (a field annotation overrides a class annotation)
 *
 */
class ComponentIDDrilldownFilter {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    fun apply(field: Field, annotationValues: IdAnnotationValues): Boolean {
        var drilldown = HasComponents::class.java.isAssignableFrom(field.type)

        if (annotationValues.annotationPresent) {
            drilldown = annotationValues.drilldown
        }
        log.debug("Component '${field.name}' selected for id drilldown is: '$drilldown'")
        return drilldown
    }

}

data class ComponentIdEntry(val name: String, val id: String, val type: String)

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

    private val assignmentFilter = ComponentIDAssignmentFilter()
    private val drilldownFilter = ComponentIDDrilldownFilter()
    lateinit var graph: MutableGraph<ComponentIdEntry>


    private fun <T : Any> doDrillDown(parentEntry: ComponentIdEntry, clazz: Class<out T>, apply: Boolean, obj: T) {
        // sort the fields into those which need an id assigned, and those which need to be drilled down into
        // the same field may be in both lists
        val componentFields = collectAllComponents(clazz)
        val assigns = componentFields.filter { assignmentFilter.apply(it, readAnnotation(it, obj)) }
        val drilldowns = componentFields.filter { drilldownFilter.apply(it, readAnnotation(it, obj)) }

        // We want to provide a mapping back to the caller, so the id assignments can be used elsewhere

        // construct the id, and if assign is true, apply it
        assigns.forEach({ f ->
            val entry = entryFor(parentEntry, f)
            graph.addNode(entry)
            graph.putEdge(parentEntry, entry)

            if (apply) {
                f.isAccessible = true
                val c = f.get(obj)
                if (c != null) {
                    log.debug("Assigning id '${entry.id}' to component ${f.name}")
                    (c as Component).id = entry.id
                } else {
                    log.warn("Component with id '{}' has not been constructed, id cannot be applied", entry.id)
                }
            }
        })

        // drill down with prefix set to "this" component / View / UI
        drilldowns.forEach({ f ->
            log.debug("drilling down into {}", f.name)
            f.isAccessible = true
            val c = f.get(obj)
            val thisEntry = entryFor(parentEntry, f)
            doDrillDown(thisEntry, c.javaClass, apply, c)

        })
    }

    /**
     * Reads the values of annotation(s) present on the field or class.  Field takes precedence.  Looks for class annotation
     * on the instance type, as it may be a subclass (or implementation) of the declared field type
     */
    private fun readAnnotation(f: Field, obj: Any): IdAnnotationValues {
        f.isAccessible = true
        // no component, so no point trying to assign
        val c = f.get(obj)
        if (c == null) {
            log.debug("Field ${f.name} has not been constructed, so we cannot assign an id to it")
            return IdAnnotationValues(false, false, false)
        }

        var annotation = c.javaClass.getAnnotation(AssignComponentId::class.java)

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


    private fun entryFor(parentEntry: ComponentIdEntry, f: Field): ComponentIdEntry {
        val id = "${parentEntry.id}-${f.name}"
        return ComponentIdEntry(name = f.name, id = id, type = f.type.simpleName)
    }

    private fun <T> collectAllComponents(clazz: Class<out T>): List<Field> {
        var classToScan: Class<out Any?> = clazz
        log.debug("scanning '{}' to generate component ids", classToScan.name)
        val fields: MutableList<Field> = mutableListOf()
        try {
            var done = false
            while (!done) {
                fields.addAll(classToScan.declaredFields.filter({ f -> componentFilter(f) }))
                done = (classToScan == Any::class.java) || (classToScan.superclass == null)
                if (!done) {
                    classToScan = classToScan.superclass
                }
            }
        } catch (iae: IllegalAccessException) {
            log.error("reflective scan of components failed", iae)
        }
        return fields
    }


    private val log = LoggerFactory.getLogger(this.javaClass.name)

    private fun componentFilter(field: Field): Boolean {
        val t = field.type
        if (field.name == "rootComponent") {
            return false
        }
        if (field.name == "parent") {
            return false
        }
        return (Component::class.java.isAssignableFrom(t))
    }

    private fun <T : Any> generateAndApply(clazz: Class<out T>, apply: Boolean, obj: T) {
        val entry = ComponentIdEntry(clazz.simpleName, clazz.simpleName, clazz.simpleName)
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


