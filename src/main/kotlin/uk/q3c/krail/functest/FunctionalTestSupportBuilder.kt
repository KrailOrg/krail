package uk.q3c.krail.functest

import com.google.common.graph.MutableGraph
import com.google.inject.Inject
import com.google.inject.Injector
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap
import uk.q3c.krail.core.navigate.sitemap.SitemapService
import uk.q3c.krail.core.navigate.sitemap.set.MasterSitemapQueue
import uk.q3c.krail.core.view.ViewFactory
import uk.q3c.krail.core.view.component.ComponentIdEntry
import uk.q3c.krail.core.view.component.ComponentIdGenerator

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
        val uiCreator: UICreator) : FunctionalTestSupportBuilder {

    val masterSitemap: MasterSitemap

    init {
        sitemapService.start()
        //take a reference and keep it in case current model changes
        masterSitemap = masterSitemapQueue.currentModel
    }


    override fun generate(): FunctionalTestSupport {
        // create a node, then use it to wrap the result of the component id scan?
        val routes = mutableMapOf<String, RouteIdEntry>()
        val uis = mutableMapOf<String, UIIdEntry>()

        val uiClasses = uiCreator.definedUIClasses()

        uiClasses.forEach({ c ->
            val className = c.javaClass.simpleName
            val ui = uiCreator.getInstanceOf(c)
            val graph = componentIdGenerator.generateAndApply(ui)
            uis[className] = UIIdEntry(className, graph)
        })

        masterSitemap.allNodes.forEach({ node ->
            val view = viewFactory.get(node.viewClass)
            val graph = componentIdGenerator.generateAndApply(view)
            val route = masterSitemap.uri(node)
            val entry = RouteIdEntry(route = route, idGraph = graph)
            routes[route] = entry

        })
        return FunctionalTestSupport(uis, routes)
    }
}

data class RouteIdEntry(val route: String, val idGraph: MutableGraph<ComponentIdEntry>)

data class UIIdEntry(val uiName: String, val idGraph: MutableGraph<ComponentIdEntry>)

data class FunctionalTestSupport(val uis: Map<String, UIIdEntry>, val routes: Map<String, RouteIdEntry>)

