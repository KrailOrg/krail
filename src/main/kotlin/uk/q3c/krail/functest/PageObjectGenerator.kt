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

interface ViewObject {
    val id: String
}
interface PageObject : ViewObject
interface CustomObject : ViewObject

abstract class AbstractCustomObject(override val id: String) : CustomObject

abstract class AbstractViewObject : ViewObject {
    override val id: String = browser.view.id
}

abstract class AbstractPageObject : PageObject {
    override val id: String = browser.page.id
}

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
        model.uis.forEach({ (k, v) ->
            generateObject(source = k.uiId, objectType = "PageObject", idGraph = v)
        })
    }

    private fun generateViewObjects(model: FunctionalTestSupport) {
        model.views.forEach({ (k, v) ->
            generateObject(source = k.viewId, objectType = "ViewObject", idGraph = v)
        })
    }

    private fun generateObject(source: ComponentIdEntry, objectType: String, idGraph: MutableGraph<ComponentIdEntry>) {
        val kpoClass = KPOClass(sourceType = source.type, objectInterfaceType = objectType, baseComponent = source.baseComponent)

        idGraph.successors(source).forEach({ node ->
            kpoClass.property(node.name, node.type, node.baseComponent, node.id)
            if (!node.baseComponent) {
                generateObject(node, "CustomObject", idGraph)
            }
        })

        objects.add(kpoClass)
    }

}

data class PropertySpec(val name: String, val type: String, val baseComponent: Boolean, val id: String)

data class KPOClass(val sourceType: String, val objectInterfaceType: String, val baseComponent: Boolean) {
    private val props: MutableList<PropertySpec> = mutableListOf()
    private val name: String = "${sourceType}Object"

    fun property(name: String, type: String, baseComponent: Boolean, id: String) {
        props.add(PropertySpec(name = name, type = type, baseComponent = baseComponent, id = id))
    }

    override fun toString(): String {
        val buf = StringBuilder("class ")
        buf.append(name)
        if (objectInterfaceType == "CustomObject") {
            buf.append("(override val id:String)")
            buf.append(" : AbstractCustomObject (id)")
        } else {
            buf.append("() : Abstract$objectInterfaceType()")
        }

        buf.append(" {\n\n")
        props.forEach({ p ->
            buf.append("    val ")
            buf.append(p.name)
            if (p.baseComponent) {
                buf.append(" by ")
                buf.append(p.type)
                buf.append("()\n")
            } else {
                buf.append(" = ")
                buf.append(p.type)
                buf.append("Object (\"${p.id}\"")
                buf.append(")\n")
            }

        })
        buf.append("}\n")
        return buf.toString()
    }
}