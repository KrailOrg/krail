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

package uk.q3c.krail.core.view.component;

import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.util.DefaultNodeModifier;

import static com.google.common.base.Preconditions.*;

public class UserNavigationTreeNodeModifier extends DefaultNodeModifier<UserSitemapNode, UserSitemapNode> {

    private final UserNavigationTree tree;
    private UserSitemap userSitemap;

    public UserNavigationTreeNodeModifier(UserNavigationTree tree, UserSitemap userSitemap) {
        super();
        this.tree = tree;
        this.userSitemap = userSitemap;
    }

    @Override
    public void setLeaf(UserSitemapNode targetNode) {
        checkNotNull(targetNode);
        UserSitemapNode sourceNode = sourceNodeFor(targetNode);
        doSetLeaf(targetNode, userSitemap.hasNoVisibleChildren(sourceNode));
    }

    @Override
    public void forceSetLeaf(UserSitemapNode targetNode) {
        doSetLeaf(targetNode, true);
    }

    private void doSetLeaf(UserSitemapNode targetNode, boolean isLeaf) {
        tree.getTree()
            .setChildrenAllowed(targetNode, !isLeaf);
    }
}
