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

import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.i18n.I18NKey

/**
 * Comparator which can be used to sort SitemapNode by collation key order, based on [UserSitemapNode.collationKey].
 * This enables sorting by Locale sensitive labels, as the collation key is set to reflect the current locale
 */
class AlphabeticAscending : UserSitemapNodeComparator {

    override fun compare(o1: UserSitemapNode, o2: UserSitemapNode): Int {
        return o1.collationKey!!.compareTo(o2.collationKey)
    }

    override fun nameKey(): I18NKey {
        return LabelKey.Alphabetic_Ascending
    }

}
