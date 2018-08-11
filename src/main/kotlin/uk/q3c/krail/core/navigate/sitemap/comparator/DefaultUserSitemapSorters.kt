/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.navigate.sitemap.comparator

import com.google.common.base.Preconditions.checkNotNull
import com.google.inject.Inject
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import java.util.*

/**
 * A set of comparators which can be used to sort [UserSitemapNode]s, with a lookup key and name to support
 * selection by an end user
 *
 * @author dsowerby
 */
class DefaultUserSitemapSorters @Inject
protected constructor(private val alphaAscending: AlphabeticAscending, private val alphaDescending: AlphabeticDescending,
                      private val insertionAscending: InsertionOrderAscending,
                      private val insertionDescending: InsertionOrderDescending,
                      private val positionAscending: PositionIndexAscending,
                      private val positionDescending: PositionIndexDescending) : UserSitemapNodeSorter {
    var isAscending = true
        private set
    override lateinit var sortComparator: Comparator<UserSitemapNode>
        private set
    var sortType = SortType.ALPHA
        private set

    enum class SortType {
        ALPHA, INSERTION, POSITION
    }

    init {
        select()
    }

    private fun select() {
        when (sortType) {
            DefaultUserSitemapSorters.SortType.ALPHA -> sortComparator = if (isAscending) alphaAscending else alphaDescending
            DefaultUserSitemapSorters.SortType.INSERTION -> sortComparator = if (isAscending) insertionAscending else insertionDescending
            DefaultUserSitemapSorters.SortType.POSITION -> sortComparator = if (isAscending) positionAscending else positionDescending
        }
    }

    override fun setOptionSortAscending(ascending: Boolean) {
        this.isAscending = ascending
        select()
    }

    override fun setOptionKeySortType(sortType: SortType) {
        checkNotNull(sortType)
        this.sortType = sortType
        select()
    }

}
