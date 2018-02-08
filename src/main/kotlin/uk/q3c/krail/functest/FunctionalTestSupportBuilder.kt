package uk.q3c.krail.functest

import com.google.common.graph.MutableGraph
import com.google.gson.GsonBuilder
import com.google.inject.Inject
import com.google.inject.Injector
import org.slf4j.LoggerFactory
import uk.q3c.krail.config.ConfigurationException
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap
import uk.q3c.krail.core.navigate.sitemap.SitemapService
import uk.q3c.krail.core.navigate.sitemap.set.MasterSitemapQueue
import uk.q3c.krail.core.ui.ScopedUI
import uk.q3c.krail.core.view.ViewFactory
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage
import uk.q3c.krail.core.view.component.ComponentIdEntry
import uk.q3c.krail.core.view.component.ComponentIdGenerator
import uk.q3c.util.clazz.UnenhancedClassIdentifier
import java.io.File

/**
 * Created by David Sowerby on 03 Feb 2018
 */
interface FunctionalTestSupportBuilder {
    fun generate(): FunctionalTestSupport
}

class DefaultFunctionalTestSupportBuilder @Inject constructor(
        sitemapService: SitemapService,
        masterSitemapQueue: MasterSitemapQueue,
        val injector: Injector,
        val componentIdGenerator: ComponentIdGenerator,
        val viewFactory: ViewFactory,
        val uiCreator: UICreator,
        val realClassNameIdentifier: UnenhancedClassIdentifier) : FunctionalTestSupportBuilder {

    val masterSitemap: MasterSitemap
    val uiClasses = uiCreator.definedUIClasses()
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    init {
        sitemapService.start()
        //take a reference and keep it in case current model changes
        masterSitemap = masterSitemapQueue.currentModel
    }

    private fun defaultUI(): ScopedUI {
        if (uiClasses.isEmpty()) {
            throw ConfigurationException("At least one UI class must be defined")
        }
        if (uiClasses.size > 1) {
            throw UnsupportedOperationException("Multiple UI types not yet supported, see issues 664 and 665")
        }
        return uiCreator.getInstanceOf(uiClasses[0])
    }

    override fun generate(): FunctionalTestSupport {
        // create a node, then use it to wrap the result of the component id scan?
        val routes = RouteMap(1, mutableMapOf<String, RouteIdEntry>())
        val ui = defaultUI()
        ui.screenLayout()
        val uiGraph = componentIdGenerator.generateAndApply(ui)
        val className = realClassNameIdentifier.getOriginalClassFor(ui).simpleName
        val uiId = ComponentIdEntry(name = className, id = className, type = className, baseComponent = false)
        val uiIdEntry = UIIdEntry(uiId = uiId)

        val views: MutableMap<ViewIdEntry, MutableGraph<ComponentIdEntry>> = mutableMapOf()
        val uis: MutableMap<UIIdEntry, MutableGraph<ComponentIdEntry>> = mutableMapOf()
        uis[uiIdEntry] = uiGraph

        masterSitemap.allNodes.forEach({ node ->
            if (node.viewClass != null) {
                val view = viewFactory.get(node.viewClass)
                view.buildView(AfterViewChangeBusMessage(NavigationState(), NavigationState()))
                val graph = componentIdGenerator.generateAndApply(view)
                val route = masterSitemap.uri(node)
                val viewName = realClassNameIdentifier.getOriginalClassFor(view).simpleName
                val viewId = ComponentIdEntry(name = viewName, id = viewName, type = viewName, baseComponent = false)
                val viewEntry = ViewIdEntry(viewId = viewId)
                val entry = RouteIdEntry(route = route, uiId = uiIdEntry, viewId = viewEntry)
                routes.map[route] = entry
                views[viewEntry] = graph
            } else {
                log.debug("entry for route '${masterSitemap.uri(node)}' not created, because no view is defined for it")
            }

        })
        return FunctionalTestSupport(routes, uis, views)
    }
}

data class ViewIdEntry(val viewId: ComponentIdEntry)
data class UIIdEntry(val uiId: ComponentIdEntry)
/**
 * Holds view and UI ids for a specified route.  Currently there is only ever one view and one UI per route, but that will need to change
 * once Krail core is updated to provide multiple view/UI options for each route (Issues 664 and 665)
 */
data class RouteIdEntry(val route: String, val uiId: UIIdEntry, val viewId: ViewIdEntry)

/**
 * [routeMap] provides a mapping of route to the ids of the UI and View used for that route.  [uis] and [views] are directed
 * graphs of component structure within a UI or View, used primariliy to generate Page Objects for Functional Testing
 */
data class FunctionalTestSupport(val routeMap: RouteMap, val uis: Map<UIIdEntry, MutableGraph<ComponentIdEntry>>, val views: Map<ViewIdEntry, MutableGraph<ComponentIdEntry>>)

/**
 * Describes the relationship between routes, and Views and UIs
 */
data class RouteMap(val version: Int, val map: MutableMap<String, RouteIdEntry>) {
    fun viewFor(route: String): ViewIdEntry {
        val routeEntry = map[route] ?: throw ConfigurationException("There is no entry for route $route")
        return routeEntry.viewId
    }

    fun uiFor(route: String): UIIdEntry {
        val routeEntry = map[route] ?: throw ConfigurationException("There is no entry for route $route")
        return routeEntry.uiId
    }
}

fun RouteMap.toJson(f: File) {
    val gson = GsonBuilder().disableHtmlEscaping().create()
    val json = gson.toJson(this)
    f.writeText(json)
}

fun routeMapFromJson(f: File): RouteMap {
    val gson = GsonBuilder().disableHtmlEscaping().create()
    val json = f.readText()
    val routeMap = gson.fromJson(json, RouteMap::class.java)
    return routeMap
}