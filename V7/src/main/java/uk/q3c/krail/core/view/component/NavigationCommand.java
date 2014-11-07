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
package uk.q3c.krail.core.view.component;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;

public class NavigationCommand implements MenuBar.Command {

    private final UserSitemapNode node;
    private final Navigator navigator;

    public NavigationCommand(Navigator navigator, UserSitemapNode node) {
        this.node = node;
        this.navigator = navigator;
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
        navigator.navigateTo(node);
    }

    public UserSitemapNode getNode() {
        return node;
    }

    public Navigator getNavigator() {
        return navigator;
    }

}
