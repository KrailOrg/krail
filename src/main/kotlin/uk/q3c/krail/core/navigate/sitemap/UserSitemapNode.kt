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
package uk.q3c.krail.core.navigate.sitemap

import uk.q3c.krail.i18n.Translate
import java.text.CollationKey
import java.text.Collator
import java.util.*

/**
 * To enable locale sensitive sorting of nodes - for example within a UserNavigationTree - a collation key from
 * [Collator] is added by the [translate] method. This means the collation key
 * is generally created only once, but is available for sorting as often as needed. The collation key will only need to
 * be updated if locale or labelKey changes. This approach also takes advantage of the improved performance of the
 * collation key sorting (http://docs.oracle.com/javase/tutorial/i18n/text/perform.html)
 *
 *
 * The [MasterSitemapNode.positionIndex] is copied into this, to offer the developer the potential to provide a different presentation order in
 * navigation components, depending on some characteristic of the user.
 *
 * Set [positionIndex] to <0 to prevent this node from being shown in Navigation components
 */
data class UserSitemapNode constructor(val masterNode: MasterSitemapNode) : SitemapNode by masterNode, NodeVisitable, Comparable<UserSitemapNode> {


    @Transient
    var collationKey: CollationKey? = null
    lateinit var label: String
    override var positionIndex = 1//visible by default;


    init {
        this.positionIndex = masterNode.positionIndex
    }

    /**
     * Updates the [label] and [collationKey] for the `locale`
     */
    @Deprecated("Setting the collation key is managed by DefaultUserSitemap")
    fun translate(translate: Translate, locale: Locale, collator: Collator) {
        val key = masterNode.labelKey
        label = translate.from(key, locale)
        collationKey = collator.getCollationKey(label)
    }

    override fun toString(): String {
        return label
    }

    /**
     * Although [collationKey] is nullable, is should only be null immediately after deserialization - the key is then reset by [DefaultUserSitemap.readObject]
     */
    override fun compareTo(other: UserSitemapNode): Int {
        return collationKey!!.compareTo(other.collationKey)
    }

//    override fun equals(other: Any?): Boolean {
//        if (this === other) {
//            return true
//        }
//        if (other !is UserSitemapNode) {
//            return false
//        }
//
//        val that = other as UserSitemapNode?
//
//        return !if (masterNode != null) masterNode != that!!.masterNode else that!!.masterNode != null
//
//    }

//    override fun hashCode(): Int {
//        return masterNode.hashCode() ?: 0
//    }

    override fun accept(visitor: NodeVisitor<NodeVisitable>) {
        visitor.visit(this)
    }

}
