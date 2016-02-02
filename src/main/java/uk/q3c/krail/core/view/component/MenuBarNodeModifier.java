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

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import org.apache.commons.collections15.map.HashedMap;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.util.CaptionReader;
import uk.q3c.util.NodeModifier;
import uk.q3c.util.TreeCopyException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MenuBarNodeModifier implements NodeModifier<UserSitemapNode, MenuItem> {

    private final MenuBar menuBar;
    private final Map<MenuItem, UserSitemapNode> targetLookup = new HashedMap<>();
    private final Navigator navigator;
    private final CaptionReader<UserSitemapNode> captionReader;
    private UserSitemap userSitemap;

    public MenuBarNodeModifier(MenuBar menuBar, Navigator navigator, CaptionReader<UserSitemapNode> captionReader, UserSitemap userSitemap) {
        this.menuBar = menuBar;
        this.navigator = navigator;
        this.captionReader = captionReader;
        this.userSitemap = userSitemap;
    }

    @Override
    public MenuItem create(MenuItem parentNode, @Nonnull UserSitemapNode sourceNode) {
        checkNotNull(sourceNode);
        checkNotNull(captionReader, "This implementation requires a caption reader");
        MenuItem newTargetNode = null;
        if (parentNode == null) {
            newTargetNode = menuBar.addItem(captionReader.getCaption(sourceNode), null);
        } else {
            newTargetNode = parentNode.addItem(captionReader.getCaption(sourceNode), null);
        }
        targetLookup.put(newTargetNode, sourceNode);
        return newTargetNode;
    }

    @Override
    public boolean attachOnCreate() {
        return true;
    }

    @Override
    public void setLeaf(@Nonnull MenuItem targetNode) {
        checkNotNull(targetNode);
        UserSitemapNode sourceNode = sourceNodeFor(targetNode);
        if (userSitemap.hasNoVisibleChildren(sourceNode)) {
            doSetLeaf(sourceNode, targetNode);
        }
    }

    @Override
    public void forceSetLeaf(@Nonnull MenuItem targetNode) {
        doSetLeaf(sourceNodeFor(targetNode), targetNode);
    }

    private void doSetLeaf(UserSitemapNode sourceNode, MenuItem targetNode) {
        NavigationCommand command = new NavigationCommand(navigator, sourceNode);
        targetNode.setCommand(command);
    }


    @Override
    public UserSitemapNode sourceNodeFor(@Nonnull MenuItem targetNode) {
        checkNotNull(targetNode);
        return targetLookup.get(targetNode);
    }

    @Override
    public void setCaption(@Nonnull MenuItem targetNode, String caption) {
        throw new TreeCopyException("Caption can only be set while MenuItem is being created");

    }

    @Override
    public void sortChildren(@Nullable MenuItem parentNode, @Nonnull Comparator<MenuItem> comparator) {
        List<MenuItem> children = (parentNode == null) ? menuBar.getItems() : parentNode.getChildren();
        if (children != null) {
            Collections.sort(children, comparator);
        }

    }

}
