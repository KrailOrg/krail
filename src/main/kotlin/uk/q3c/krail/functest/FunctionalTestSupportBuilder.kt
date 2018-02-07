package uk.q3c.krail.functest

import com.google.common.graph.MutableGraph
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
        val routes = mutableMapOf<String, RouteIdEntry>()
        val ui = defaultUI()
        ui.screenLayout()
        val uiGraph = componentIdGenerator.generateAndApply(ui)
        val className = realClassNameIdentifier.getOriginalClassFor(ui).simpleName
        val uiId = ComponentIdEntry(name = className, id = className, type = className, baseComponent = false)
        val uiIdEntry = UIIdEntry(uiId = uiId, idGraph = uiGraph)

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
                val viewEntry = ViewIdEntry(viewId = viewId, idGraph = graph)
                val entry = RouteIdEntry(route = route, uiId = uiIdEntry, viewId = viewEntry)
                routes[route] = entry
                views[viewEntry] = graph
            } else {
                log.debug("entry for route '${masterSitemap.uri(node)}' not created, because no view is defined for it")
            }

        })
        return FunctionalTestSupport(routes, uis, views)
    }
}

data class ViewIdEntry(val viewId: ComponentIdEntry, val idGraph: MutableGraph<ComponentIdEntry>)
data class UIIdEntry(val uiId: ComponentIdEntry, val idGraph: MutableGraph<ComponentIdEntry>)
/**
 * Holds view and UI ids for a specified route.  Currently there is only ever one view and one UI per route, but that will need to change
 * once Krail core is updated to provide multiple view/UI options for each route (Issues 664 and 665)
 */
data class RouteIdEntry(val route: String, val uiId: UIIdEntry, val viewId: ViewIdEntry)

/**
 * [routes] provides a mapping of route to the ids of the UI and View used for that route.  Ids are then used to lookup the view and ui details
 */
data class FunctionalTestSupport(val routes: Map<String, RouteIdEntry>, val uis: Map<UIIdEntry, MutableGraph<ComponentIdEntry>>, val views: Map<ViewIdEntry, MutableGraph<ComponentIdEntry>>) {
    fun viewFor(route: String): ViewIdEntry {
        val routeEntry = routes[route] ?: throw ConfigurationException("There is no entry for route $route")
        return routeEntry.viewId
    }

    fun uiFor(route: String): UIIdEntry {
        val routeEntry = routes[route] ?: throw ConfigurationException("There is no entry for route $route")
        return routeEntry.uiId
    }
}

