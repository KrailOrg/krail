package uk.q3c.krail.core.navigate.sitemap

import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortDirection.NORMAL
import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortDirection.REVERSED
import uk.q3c.krail.core.navigate.sitemap.UserSitemapSortType.*
import uk.q3c.krail.core.navigate.sitemap.comparator.AlphabeticAscending
import uk.q3c.krail.core.navigate.sitemap.comparator.AlphabeticDescending
import uk.q3c.krail.core.navigate.sitemap.comparator.InsertionOrderAscending
import uk.q3c.krail.core.navigate.sitemap.comparator.InsertionOrderDescending
import uk.q3c.krail.core.navigate.sitemap.comparator.PositionIndexAscending
import uk.q3c.krail.core.navigate.sitemap.comparator.PositionIndexDescending
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapNodeComparator
import java.io.Serializable

/**
 * Sort a list of [UserSitemapNode]
 *
 * Created by David Sowerby on 09 Aug 2018
 */
interface UserSitemapNodeSorter : Serializable {
    fun sort(nodes: List<UserSitemapNode>, sortMode: UserSitemapNodeSortMode): List<UserSitemapNode>

}


class DefaultUserSitemapNodeSorter : UserSitemapNodeSorter {
    override fun sort(nodes: List<UserSitemapNode>, sortMode: UserSitemapNodeSortMode): List<UserSitemapNode> {
        val comparator: UserSitemapNodeComparator =
                when (sortMode) {
                    UserSitemapNodeSortMode(type = ALPHA, direction = NORMAL) -> AlphabeticAscending()
                    UserSitemapNodeSortMode(type = ALPHA, direction = REVERSED) -> AlphabeticDescending()
                    UserSitemapNodeSortMode(type = POSITION, direction = NORMAL) -> PositionIndexAscending()
                    UserSitemapNodeSortMode(type = POSITION, direction = REVERSED) -> PositionIndexDescending()
                    UserSitemapNodeSortMode(type = INSERTION, direction = NORMAL) -> InsertionOrderAscending()
                    UserSitemapNodeSortMode(type = INSERTION, direction = REVERSED) -> InsertionOrderDescending()
                    else -> {
                        throw UserSitemapSortException("Unrecognised sort mode")
                    }
                }
        return nodes.sortedWith(comparator)
    }
}

class UserSitemapSortException(msg: String) : RuntimeException(msg)

enum class UserSitemapSortType {
    ALPHA, INSERTION, POSITION, NONE
}

enum class UserSitemapSortDirection {
    NORMAL, REVERSED
}


data class UserSitemapNodeSortMode @JvmOverloads constructor(val type: UserSitemapSortType = POSITION, val direction: UserSitemapSortDirection = NORMAL) : Serializable