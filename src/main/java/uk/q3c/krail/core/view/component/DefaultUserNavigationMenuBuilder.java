/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.vaadin.ui.MenuBar.MenuItem;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.util.*;
import uk.q3c.util.TreeCopy.SortOption;

import java.util.ArrayList;
import java.util.List;

public class DefaultUserNavigationMenuBuilder implements UserNavigationMenuBuilder {

    private final UserSitemap userSitemap;
    private final Navigator navigator;
    private UserNavigationMenu userNavigationMenu;

    @Inject
    protected DefaultUserNavigationMenuBuilder(UserSitemap userSitemap, Navigator navigator) {
        this.userSitemap = userSitemap;
        this.navigator = navigator;
    }

    @Override
    public void build() {
        SourceTreeWrapper_BasicForest<UserSitemapNode> source = new SourceTreeWrapper_BasicForest<>(userSitemap
                .getForest());
        TargetTreeWrapper_MenuBar<UserSitemapNode, MenuItem> target = new TargetTreeWrapper_MenuBar<>
                (userNavigationMenu.getMenuBar());
        UserSitemapNodeCaption nodeCaptionReader = new UserSitemapNodeCaption();
        target.setCaptionReader(nodeCaptionReader);
        MenuBarNodeModifier nodeModifier = new MenuBarNodeModifier(userNavigationMenu.getMenuBar(), navigator,
                nodeCaptionReader);
        target.setNodeModifier(nodeModifier);

        TreeCopy<UserSitemapNode, MenuItem> treeCopy = new TreeCopy<>(source, target);
        treeCopy.setSortOption(SortOption.SORT_SOURCE_NODES);
        treeCopy.setMaxDepth(userNavigationMenu.getOptionMaxDepth());
        treeCopy.setSorted(userNavigationMenu.isSorted());
        List<NodeFilter> filters = new ArrayList<>();
        defineFilters(filters);
        applyFilters(treeCopy, filters);
        treeCopy.copy();
    }

    /**
     * Excludes nodes with positionIndex < 0 and the log out page.  Override this to use different filters
     *
     * @param filters
     *         the filters to apply
     */
    protected void defineFilters(List<NodeFilter> filters) {
        filters.add(new LogoutPageFilter());
        filters.add(new NoNavFilter());
    }

    /**
     * Applies the filters defined by {@link #defineFilters(List)}
     *
     * @param treeCopy
     *         the copy object
     * @param filters
     *         the filters to apply
     */
    private void applyFilters(TreeCopy<UserSitemapNode, MenuItem> treeCopy, List<NodeFilter> filters) {
        for (NodeFilter filter : filters) {
            treeCopy.addSourceFilter(filter);
        }
    }

    @Override
    public UserNavigationMenu getUserNavigationMenu() {
        return userNavigationMenu;
    }

    @Override
    public void setUserNavigationMenu(UserNavigationMenu userNavigationMenu) {
        this.userNavigationMenu = userNavigationMenu;
    }

}
