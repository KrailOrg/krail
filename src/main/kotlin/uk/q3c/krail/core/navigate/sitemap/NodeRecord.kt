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

import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.view.EmptyViewConfiguration
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.ViewConfiguration
import uk.q3c.krail.i18n.I18NKey
import java.util.*

/**
 * A placeholder for [MasterSitemapNode] information, prior to creation of the immutable [MasterSitemapNode]
 *
 *
 * Created by David Sowerby on 03/03/15.
 */
data class NodeRecord(var uri: String) {
    var labelKey: I18NKey = LabelKey.Unnamed
    var pageAccessControl: PageAccessControl = PageAccessControl.PERMISSION
    var positionIndex = 1 // visible by default
    var roles: MutableList<String> = ArrayList()
    var uriSegment: String = "unspecified"
    var viewClass: Class<out KrailView> = EmptyView::class.java

    var configuration: ViewConfiguration = EmptyViewConfiguration()


    fun addRole(role: String) {
        roles.add(role)
    }
}
