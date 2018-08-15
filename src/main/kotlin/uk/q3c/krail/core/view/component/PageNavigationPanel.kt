package uk.q3c.krail.core.view.component

import com.google.inject.Inject
import com.vaadin.shared.ui.AlignmentInfo
import com.vaadin.shared.ui.Orientation
import com.vaadin.shared.ui.Orientation.HORIZONTAL
import com.vaadin.shared.ui.Orientation.VERTICAL
import com.vaadin.ui.Alignment
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.eventbus.SessionBusProvider
import uk.q3c.krail.core.eventbus.UIBusProvider
import uk.q3c.krail.core.navigate.sitemap.UserSitemapLabelChangeMessage
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNodeSortMode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortType.NONE
import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortType.POSITION
import uk.q3c.krail.core.navigate.sitemap.UserSitemapStructureChangeMessage
import uk.q3c.krail.core.ui.ScopedUIProvider
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.core.view.component.NodeSelection.SUB_PAGES


interface NavigationAwareComponent : Component {

    /**
     * Update to reflect the current navigation state.  The easiest way to implement this is to use a message handler
     * for [AfterViewChangeBusMessage] to invoke this method.  See [DefaultPageNavigationPanel.afterViewChange] for an example
     */
    fun update()
}


/**
 * [orientation] determines whether the buttons are laid out vertically or horizontally.  Default is vertical
 * [nodeSelection] determines whether buttons represent:
 *
 * - the sub-pages of the current node (minus those which are excluded using [UserSitemapNode.positionIndex])
 * - the parent chain of the current node (the 'breadcrumb')
 *
 * Default is [SUB_PAGES]
 *
 * [buttonOptions] determines the button style and sort order
 *
 *
 * Invoke the [update] method to build the buttons.  Typically invoked in [ViewBase.doBuild]
 *
 *
 * Created by David Sowerby on 10 Aug 2018
 */

interface PageNavigationPanel : NavigationAwareComponent {
    var orientation: Orientation
    var nodeSelection: NodeSelection
    var buttonOptions: ButtonOptions
    var buttonAlignment: Alignment


    /**
     * Convenience method.  Sets properties so that a breadcrumb using links is produced.
     */
    fun configureAsBreadcrumb()

    /**
     * Convenience method.  Sets properties so that a horizontal sub-page panel, using links, is produced.
     * Default sort mode is by [UserSitemapNode.positionIndex]
     */
    fun configureAsSubPagePanel()

    fun configureAsSubPagePanel(sort: UserSitemapNodeSortMode)
}

@Listener
class DefaultPageNavigationPanel @Inject constructor(uiBusProvider: UIBusProvider,
                                                     sessionBusProvider: SessionBusProvider,
                                                     val uiProvider: ScopedUIProvider,
                                                     private val pageNavigationButtonBuilder: PageNavigationButtonBuilder) : PageNavigationPanel, Panel() {


    override var orientation: Orientation = VERTICAL
    override var nodeSelection: NodeSelection = NodeSelection.SUB_PAGES
    override var buttonOptions: ButtonOptions = ButtonOptions()
    override var buttonAlignment: Alignment = Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER)
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private lateinit var buttons: List<NavigationButton>

    init {
        uiBusProvider.get().subscribe(this)
        sessionBusProvider.get().subscribe(this)
    }

    /**
     * Build the buttons as configured.  Default configuration creates a set of buttons in a [VerticalLayout], sorted by
     * [UserSitemapNode.positionIndex]
     */
    override fun update() {
        val thisUI = uiProvider.get()
        val navigator = thisUI.krailNavigator
        val currentNode = navigator.currentNode
        if (currentNode == null) {
            log.warn("Navigator is not ready yet")
        } else {
            val nodeList = when (nodeSelection) {
                NodeSelection.SUB_PAGES -> {
                    val filter = NoNavFilter()
                    navigator.subNodes().filter { node -> filter.accept(node) }
                }
                NodeSelection.BREADCRUMB -> navigator.nodeChainForCurrentNode()
            }
            buttons = pageNavigationButtonBuilder.createButtons(nodeList, buttonOptions, navigator)
            val layout = when (orientation) {
                VERTICAL -> VerticalLayout()
                HORIZONTAL -> HorizontalLayout()
            }
            buttons.forEach { b ->
                layout.addComponent(b)
                layout.setComponentAlignment(b, buttonAlignment)
            }
            this.content = layout
        }
    }

    override fun configureAsBreadcrumb() {
        orientation = HORIZONTAL
        buttonAlignment = Alignment(AlignmentInfo.Bits.ALIGNMENT_LEFT)
        buttonOptions = ButtonOptions(ValoTheme.BUTTON_LINK, UserSitemapNodeSortMode(NONE))
        nodeSelection = NodeSelection.BREADCRUMB
    }

    override fun configureAsSubPagePanel() {
        configureAsSubPagePanel(UserSitemapNodeSortMode(POSITION))
    }

    override fun configureAsSubPagePanel(sort: UserSitemapNodeSortMode) {
        orientation = HORIZONTAL
        buttonAlignment = Alignment(AlignmentInfo.Bits.ALIGNMENT_LEFT)
        buttonOptions = ButtonOptions(ValoTheme.BUTTON_LINK, UserSitemapNodeSortMode(POSITION))
        nodeSelection = SUB_PAGES
    }


    @Suppress("UNUSED_PARAMETER")
    @Handler
    fun labelsChanged(busMessage: UserSitemapLabelChangeMessage) {
        log.debug("UserSitemapLabelChangeMessage changed message received")
        update()

    }

    @Suppress("UNUSED_PARAMETER")
    @Handler
    fun structureChanged(busMessage: UserSitemapStructureChangeMessage) {
        log.debug("UserSitemapStructureChangeMessage changed message received")
        update()
    }

    @Suppress("UNUSED_PARAMETER")
    @Handler
    fun afterViewChange(msg: AfterViewChangeBusMessage) {
        update()
    }

}


enum class NodeSelection { BREADCRUMB, SUB_PAGES }