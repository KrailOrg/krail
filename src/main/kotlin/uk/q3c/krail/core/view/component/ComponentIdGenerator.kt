package uk.q3c.krail.core.view.component

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import com.google.inject.Inject
import com.vaadin.ui.Component
import com.vaadin.ui.Layout
import org.slf4j.LoggerFactory
import uk.q3c.util.clazz.UnenhancedClassIdentifier
import java.lang.reflect.Field
import java.util.function.Predicate


/**
 * Created by David Sowerby on 27 Jan 2018
 */

interface ComponentIdGenerator {

    /**
     * Selects which components have ids assigned to them.  See [ComponentIDAssignmentFilter]
     */
    val assignmentFilter: Predicate<Field>

    /**
     * Selects which components are drilled into for further components. See [ComponentIDDrilldownFilter]
     */
    val drilldownFilter: Predicate<Field>

    /**
     * Generates a map of field names to ids for the given class, using [assignmentFilter] and [drilldownFilter]
     */
    fun <T> generate(clazz: Class<out T>): MutableGraph<ComponentIdEntry>

    /**
     * Generates a map of field names to ids using [generate] and applies them to the components in [obj]
     */
    fun <T> generateAndApply(obj: T): MutableGraph<ComponentIdEntry>

}

/**
 * Selects a component to receive an annotation, returns:
 *
 * - the value of [AssignComponentId.assign] if annotated (a field annotation overrides a class annotation)
 * - if there is no annotation, false if the component implements Layout
 * - if there is no annotation, true for all other components, unless annotated otherwise
 *
 */
class ComponentIDAssignmentFilter : Predicate<Field> {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    override fun test(t: Field): Boolean {

        // look for class annotation
        var annotation = t.type.getAnnotation(AssignComponentId::class.java)

        // overridden by field annotation if there is one
        if (t.isAnnotationPresent(AssignComponentId::class.java)) {
            annotation = t.getAnnotation(AssignComponentId::class.java)
        }

        if (annotation != null) {
            log.debug("Component '${t.name}' selected for id assign is: '${annotation.assign}'")
            return annotation.assign
        }

        // No assignment to Layouts unless annotated
        val result = !Layout::class.java.isAssignableFrom(t.type)
        log.debug("Component '${t.name}' selected by default for id assign is: '$result'")
        return result
    }
}

/**
 * Selects a component to drill down into:
 *
 * - the value of [AssignComponentId.drilldown] if annotated (a field annotation overrides a class annotation)
 * - if the declared field is an interface, no drill down is possible, and is rejected
 * - if there is no annotation, no drill down
 *
 *
 */
class ComponentIDDrilldownFilter : Predicate<Field> {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    override fun test(t: Field): Boolean {
        // look for class annotation
        var annotation = t.type.getAnnotation(AssignComponentId::class.java)

        // overridden by field annotation if there is one
        if (t.isAnnotationPresent(AssignComponentId::class.java)) {
            annotation = t.getAnnotation(AssignComponentId::class.java)
        }

        var drilldown = if (annotation != null) {
            annotation.drilldown
        } else {
            false
        }

        // we can never drilldown into an interface (is that true of Kotlin?)
        if (t.type.isInterface) {
            drilldown = false
            log.warn("Cannot drill down into {}, it is an interface", t.name)
        }

        return drilldown
    }

}

data class ComponentIdEntry(val name: String, val id: String, val type: String)


class DefaultComponentIdGenerator @Inject constructor(private val realClassIdentifier: UnenhancedClassIdentifier) : ComponentIdGenerator {

    override val assignmentFilter: Predicate<Field> = ComponentIDAssignmentFilter()
    override val drilldownFilter: Predicate<Field> = ComponentIDDrilldownFilter()
    lateinit var graph: MutableGraph<ComponentIdEntry>


    private fun <T> doDrillDown(parentEntry: ComponentIdEntry, clazz: Class<out T>, apply: Boolean, obj: T?) {
        // sort the fields into those which need an id assigned, and those which need to be drilled down into
        // the same field may be in both lists
        val componentFields = collectAllComponents(clazz)
        val assigns = componentFields.filter { assignmentFilter.test(it) }
        val drilldowns = componentFields.filter { drilldownFilter.test(it) }

        // We want to provide a mapping back to the caller, so the id assignments can be used elsewhere

        // construct the id, and if assign is true, apply it
        assigns.forEach({ f ->
            val entry = entryFor(parentEntry, f)
            graph.addNode(entry)
            graph.putEdge(parentEntry, entry)

            if (apply) {
                if (obj != null) {
                    f.isAccessible = true
                    val c = f.get(obj)
                    if (c != null) {
                        log.debug("Assigning id '${entry.id}' to component ${f.name}")
                        (c as Component).id = entry.id
                    } else {
                        log.warn("Component with id '{}' has not been constructed, id cannot be applied", entry.id)
                    }
                }
            }
        })

        // drill down with prefix set to "this" component / View / UI
        drilldowns.forEach({ f ->
            log.debug("drilling down into {}", f.name)
            f.isAccessible = true
            val c = if (obj == null) {
                null
            } else {
                f.get(obj)
            }
            val thisEntry = entryFor(parentEntry, f)

            // we have to check, otherwise thisEntry will be added to the graph even though it is not to be assigned
            if (assigns.contains(f)) {
                doDrillDown(thisEntry, f.type, apply, c)
            } else {
                doDrillDown(parentEntry, f.type, apply, c)
            }

        })
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
            while (classToScan != Any::class.java) {
                fields.addAll(classToScan.declaredFields.filter({ f -> componentFilter(f) }))
                classToScan = classToScan.superclass
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

    private fun <T> generateAndApply(clazz: Class<out T>, apply: Boolean, obj: T?) {
        val entry = ComponentIdEntry(clazz.simpleName, clazz.simpleName, clazz.simpleName)
        graph = GraphBuilder.directed().build<ComponentIdEntry>()
        graph.addNode(entry)
        doDrillDown(parentEntry = entry, clazz = clazz, apply = apply, obj = obj)
    }


    override fun <T> generate(clazz: Class<out T>): MutableGraph<ComponentIdEntry> {
        generateAndApply(clazz = clazz, apply = false, obj = null)
        return graph
    }


    override fun <T> generateAndApply(obj: T): MutableGraph<ComponentIdEntry> {
        val clazz = realClassIdentifier.getOriginalClassFor(obj)
        generateAndApply(clazz = clazz, apply = true, obj = obj)
        return graph
    }


}

/**
 * Annotation to modify the default behaviour of the [ComponentIdGenerator]
- if *assign* is true, an id is assigned, even if it would otherwise have been excluded,
- if *assign* is false, an id is not assigned, even if it would otherwise have been included,
- if *drilldown* is true, the generator drills down (by reflection) into the annotated component to look for any other `Component` fields
- if *drilldown* is false, no drilldown occurs
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AssignComponentId(val assign: Boolean = true, val drilldown: Boolean = false)


