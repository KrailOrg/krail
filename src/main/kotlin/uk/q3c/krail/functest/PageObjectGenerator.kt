package uk.q3c.krail.functest

import com.google.common.graph.MutableGraph
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.view.component.ComponentIdEntry
import java.io.File

/**
 * Created by David Sowerby on 04 Feb 2018
 */
interface PageObjectGenerator {
    fun generate(model: FunctionalTestSupport, file: File, packageName: String)
}

interface ViewObject
interface PageObject : ViewObject
interface CustomObject : ViewObject

class KotlinPageObjectGenerator : PageObjectGenerator {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    // use Set to avoid duplicates
    val objects: MutableSet<KPOClass> = mutableSetOf()

    override fun generate(model: FunctionalTestSupport, file: File, packageName: String) {
        val buf = StringBuilder()
        buf.append("package $packageName\n\n")
        buf.append("import uk.q3c.krail.functest.*\n\n")

        generateViewObjects(model)
        generatePageObjects(model)

        objects.forEach({ v ->
            buf.append(v)
            buf.append("\n")
        })


        file.createNewFile()
        file.writeText(buf.toString())
        log.info("Page objects generated to ${file.absolutePath}")
    }

    private fun generatePageObjects(model: FunctionalTestSupport) {
        model.uis.values.forEach({ uiEntry ->
            generateObject(uiEntry.root, objectType = "PageObject", idGraph = uiEntry.idGraph)
        })
    }

    private fun generateViewObjects(model: FunctionalTestSupport) {
        model.routes.values.forEach({ routeIdEntry ->
            generateObject(source = routeIdEntry.root, objectType = "ViewObject", idGraph = routeIdEntry.idGraph)
        })
    }

    private fun generateObject(source: ComponentIdEntry, objectType: String, idGraph: MutableGraph<ComponentIdEntry>) {
        val kpoClass = KPOClass(sourceType = source.type, objectInterfaceType = objectType, baseComponent = source.baseComponent)

        idGraph.successors(source).forEach({ node ->
            kpoClass.property(node.name, node.type, node.baseComponent)
            generateObject(node, "CustomObject", idGraph)
        })

        objects.add(kpoClass)
    }

}

data class PropertySpec(val name: String, val type: String, val baseComponent: Boolean)

data class KPOClass(val sourceType: String, val objectInterfaceType: String, val baseComponent: Boolean) {
    private val props: MutableList<PropertySpec> = mutableListOf()
    private val name: String = "${sourceType}Object"

    fun property(name: String, type: String, baseComponent: Boolean) {
        props.add(PropertySpec(name = name, type = type, baseComponent = baseComponent))
    }

    override fun toString(): String {
        val buf = StringBuilder("class ")
        buf.append(name)
        buf.append(" : PageObject {\n\n")
        props.forEach({ p ->
            buf.append("    val ")
            buf.append(p.name)
            if (p.baseComponent) {
                buf.append(" by ")
                buf.append(p.type)
            } else {
                buf.append(" = ")
                buf.append(p.type)
                buf.append("Object")
            }
            buf.append("()\n")
        })
        buf.append("}\n")
        return buf.toString()
    }
}