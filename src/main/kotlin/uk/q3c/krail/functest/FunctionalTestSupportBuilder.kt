package uk.q3c.krail.functest

import com.google.common.graph.MutableGraph
import com.google.inject.Inject
import com.google.inject.Injector
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap
import uk.q3c.krail.core.navigate.sitemap.SitemapService
import uk.q3c.krail.core.navigate.sitemap.set.MasterSitemapQueue
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
    private val log = LoggerFactory.getLogger(this.javaClass.name)

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
            val className = c.simpleName
            val ui = uiCreator.getInstanceOf(c)
            ui.screenLayout()
            val graph = componentIdGenerator.generateAndApply(ui)
            val root = ComponentIdEntry(name = className, id = className, type = className, baseComponent = false)
            uis[className] = UIIdEntry(className, graph, root)
        })

        masterSitemap.allNodes.forEach({ node ->
            if (node.viewClass != null) {
                val view = viewFactory.get(node.viewClass)
                view.buildView(AfterViewChangeBusMessage(NavigationState(), NavigationState()))
                val graph = componentIdGenerator.generateAndApply(view)
                val route = masterSitemap.uri(node)
                val viewName = realClassNameIdentifier.getOriginalClassFor(view).simpleName
                val root = ComponentIdEntry(name = viewName, id = viewName, type = viewName, baseComponent = false)
                val entry = RouteIdEntry(route = route, viewName = viewName, idGraph = graph, root = root)

                routes[route] = entry
            } else {
                log.debug("entry for route '${masterSitemap.uri(node)}' not created, because no view is defined for it")
            }

        })
        return FunctionalTestSupport(uis, routes)
    }
}

data class RouteIdEntry(val route: String, val viewName: String, val idGraph: MutableGraph<ComponentIdEntry>, val root: ComponentIdEntry)

data class UIIdEntry(val uiName: String, val idGraph: MutableGraph<ComponentIdEntry>, val root: ComponentIdEntry)

data class FunctionalTestSupport(val uis: Map<String, UIIdEntry>, val routes: Map<String, RouteIdEntry>)

