/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.view.component

import com.vaadin.ui.Button
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode

/**
 * Vaadin Button encapsulating a [UserSitemapNode].  When [click] is invoked, calls [Navigator.navigateTo] to move to [node]
 *
 *
 * @author David Sowerby
 */
class NavigationButton constructor(var node: UserSitemapNode, val navigator: Navigator) : Button(), Button.ClickListener {

    init {
        this.caption = node.label
        this.addClickListener(this)
        this.id = "navigationbutton-${node.uriSegment}" // TODO this needs to be conditional on using ids https://github.com/KrailOrg/krail/issues/662

    }

    override fun buttonClick(event: ClickEvent) {
        if (isEnabled) {
            navigator.navigateTo(node)
        }
    }


}
