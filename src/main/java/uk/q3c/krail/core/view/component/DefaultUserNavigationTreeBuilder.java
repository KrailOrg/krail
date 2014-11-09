/*
 * Copyright (C) 2014 David Sowerby
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

import com.google.inject.Inject;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.util.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultUserNavigationTreeBuilder implements UserNavigationTreeBuilder {

    private final UserSitemap userSitemap;
    private UserNavigationTree userNavigationTree;

    @Inject
    protected DefaultUserNavigationTreeBuilder(UserSitemap userSitemap) {
        this.userSitemap = userSitemap;
    }

    @Override
    public void build() {
        SourceTreeWrapper<UserSitemapNode> source = new SourceTreeWrapper_BasicForest<>(userSitemap.getForest());
        TargetTreeWrapper_VaadinTree<UserSitemapNode, UserSitemapNode> target = new TargetTreeWrapper_VaadinTree<>
                (userNavigationTree.getTree());
        TreeCopy<UserSitemapNode, UserSitemapNode> treeCopy = new TreeCopy<>(source, target);
        userNavigationTree.clear();
        target.setCaptionReader(new UserSitemapNodeCaption());
        target.setNodeModifier(new UserNavigationTreeNodeModifier(userNavigationTree));
        treeCopy.setTargetSortComparator(userNavigationTree.getSortComparator());
        treeCopy.setMaxDepth(userNavigationTree.getMaxDepth());
        List<NodeFilter> filters = new ArrayList<>();
        defineFilters(filters);
        applyFilters(treeCopy, filters);
        treeCopy.copy();
    }

    protected void defineFilters(List<NodeFilter> filters) {
        filters.add(new LogoutPageFilter());
    }

    private void applyFilters(TreeCopy<UserSitemapNode, UserSitemapNode> treeCopy, List<NodeFilter> filters) {
        for (NodeFilter filter : filters) {
            treeCopy.addSourceFilter(filter);
        }
    }

    @Override
    public UserNavigationTree getUserNavigationTree() {
        return userNavigationTree;
    }

    @Override
    public void setUserNavigationTree(UserNavigationTree userNavigationTree) {
        this.userNavigationTree = userNavigationTree;
    }
}
