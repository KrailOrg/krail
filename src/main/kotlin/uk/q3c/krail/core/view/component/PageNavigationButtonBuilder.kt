package uk.q3c.krail.core.view.component

import com.google.inject.Inject
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNodeSortMode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNodeSorter
import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortType
import java.io.Serializable

/**
 * Creates a list of [NavigationButton].
 *
 * Created by David Sowerby on 09 Aug 2018
 */
interface PageNavigationButtonBuilder : Serializable {

    /**
     * Creates a list of [NavigationButton].  Button captions are set from [UserSitemapNode.label]. [buttonOptions]
     * determine button style and order
     */
    fun createButtons(nodeList: List<UserSitemapNode>, buttonOptions: ButtonOptions, navigator: Navigator): List<NavigationButton>
}

class DefaultPageNavigationButtonBuilder @Inject constructor(private val nodeSorter: UserSitemapNodeSorter)

    : PageNavigationButtonBuilder {


    override fun createButtons(nodeList: List<UserSitemapNode>, buttonOptions: ButtonOptions, navigator: Navigator): List<NavigationButton> {
        val buttonList: MutableList<NavigationButton> = mutableListOf()
        val sortedList = if (buttonOptions.sortMode.type == UserSitemapSortType.NONE) {
            nodeList
        } else {
            nodeSorter.sort(nodeList, buttonOptions.sortMode)
        }

        for (node in sortedList) {
            buttonList.add(createButton(node, buttonOptions, navigator))
        }
        return buttonList
    }

    private fun createButton(childNode: UserSitemapNode, buttonOptions: ButtonOptions, navigator: Navigator): NavigationButton {
        val button = NavigationButton(childNode, navigator)
        button.addStyleName(buttonOptions.style)
        return button
    }

}


data class ButtonOptions(val style: String = "", val sortMode: UserSitemapNodeSortMode = UserSitemapNodeSortMode()) : Serializable