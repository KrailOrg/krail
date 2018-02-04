package uk.q3c.krail.functest

import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created by David Sowerby on 04 Feb 2018
 */
interface PageObjectGenerator {
    fun generate(model: FunctionalTestSupport, file: File, packageName: String)
}

interface ViewObject
interface PageObject

class KotlinPageObjectGenerator : PageObjectGenerator {
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    override fun generate(model: FunctionalTestSupport, file: File, packageName: String) {
        val buf = StringBuilder()
        buf.append("package $packageName\n\n")
        buf.append("import uk.q3c.krail.functest.*\n\n")

        val viewObjects = generateViewObjects(model)

        viewObjects.forEach({ v ->
            buf.append(v)
            buf.append("\n")
        })

        val pageObjects = generatePageObjects(model)
        pageObjects.forEach({ p ->
            buf.append(p)
            buf.append("\n")
        })

        file.createNewFile()
        file.writeText(buf.toString())
        log.info("Page objects generated to ${file.absolutePath}")
    }

    private fun generatePageObjects(model: FunctionalTestSupport): MutableSet<KPOClass> {
        val pageObjects: MutableSet<KPOClass> = mutableSetOf()
        model.uis.values.forEach({ uiEntry ->
            val kpoClass = KPOClass(uiEntry.uiName)
            uiEntry.idGraph.nodes().forEach({ node ->
                if (node.name != kpoClass.uiName) {
                    kpoClass.property(node.name, node.type)
                }
            })
            pageObjects.add(kpoClass)
        })
        return pageObjects
    }

    private fun generateViewObjects(model: FunctionalTestSupport): MutableSet<KVOClass> {
        // use a set in case the same view is used on more than one route
        val viewObjects: MutableSet<KVOClass> = mutableSetOf()
        model.routes.values.forEach({ routeIdEntry ->
            val kvoClass = KVOClass(routeIdEntry.viewName)
            routeIdEntry.idGraph.nodes().forEach({ node ->
                if (node.name != kvoClass.viewName) {
                    kvoClass.property(node.name, node.type)
                }
            })
            viewObjects.add(kvoClass)
        })
        return viewObjects
    }


}


data class KVOClass(val viewName: String) {
    private val props: MutableMap<String, String> = mutableMapOf()
    private val name: String = "${viewName}Object"

    fun property(name: String, type: String) {
        props[name] = type
    }

    override fun toString(): String {
        val buf = StringBuilder("class ")
        buf.append(name)
        buf.append(" : ViewObject {\n\n")
        props.forEach({ (k, v) ->
            buf.append("    val ")
            buf.append(k)
            buf.append(" by ")
            buf.append(v)
            buf.append("()\n")
        })
        buf.append("}\n")
        return buf.toString()
    }
}

data class KPOClass(val uiName: String) {
    private val props: MutableMap<String, String> = mutableMapOf()
    private val name: String = "${uiName}Object"

    fun property(name: String, type: String) {
        props[name] = type
    }

    override fun toString(): String {
        val buf = StringBuilder("class ")
        buf.append(name)
        buf.append(" : PageObject {\n\n")
        props.forEach({ (k, v) ->
            buf.append("    val ")
            buf.append(k)
            buf.append(" by ")
            buf.append(v)
            buf.append("()\n")
        })
        buf.append("}\n")
        return buf.toString()
    }
}