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

import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable

/**
 * A simple data class to hold an entry for the Sitemap for use with a [DirectSitemapModule]. Note that if [pageAccessControl] is [PageAccessControl.ROLES], then roles must be set to a non-empty value, but there is no check for this until the SitemapChecker is invoked. This allows a
 * set of Sitemap errors to be captured at once rather than one at a time.
 *
 * Roles are only used if [pageAccessControl] is [PageAccessControl.ROLES]
 *
 * @param moduleName
 * the name of the Guice module the entry was made in
 * @param viewClass
 * the class of KrailView used to display the page
 * @param labelKey
 * the I18Nkey used to describe the node, typically in a navigation component
 * @param pageAccessControl
 * the type of page control to use
 * @param roles
 * a comma separated list of roles, used only if pageAccessControl is [PageAccessControl.ROLES]
 * @param positionIndex
 * the position of a page in relation to its siblings.  Used as a sort order, relative numbering does not need to be sequential. A positionIndex < 0
 * indicates that the page should not be displayed in a navigation component
 *
 * @author David Sowerby
 */
class DirectSitemapEntry @JvmOverloads constructor(
        val moduleName: String,
        var viewClass: Class<out KrailView>? = null,
        val labelKey: I18NKey,
        val pageAccessControl: PageAccessControl,
        val roles: String? = null,
        val positionIndex: Int = 0
)


    : Serializable
