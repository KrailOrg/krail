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

import com.google.inject.Inject;
import com.vaadin.data.Property;
import com.vaadin.ui.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapChangeListener;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;
import uk.q3c.krail.core.view.KrailViewChangeEvent;
import uk.q3c.krail.core.view.KrailViewChangeListener;
import uk.q3c.util.ID;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

/**
 * A navigation tree for users to find their way around the site. Uses {@link UserSitemap} to provide the structure
 * which in turn provides a view on {@link MasterSitemap} filtered for the current user's selection of locale and
 * authorised pages. Although this seems naturally to be a {@link UIScoped} class it is not currently possible to have
 * a
 * UIScoped Component (see https://github.com/davidsowerby/krail/issues/177)
 *
 * @author David Sowerby 17 May 2013
 * @modified David Sowerby
 */
public class DefaultUserNavigationTree extends Tree implements OptionContext, UserNavigationTree,
        KrailViewChangeListener, UserSitemapChangeListener {


    public enum optionProperty {SORT_ASCENDING, SORT_TYPE, MAX_DEPTH}

    private static Logger log = LoggerFactory.getLogger(DefaultUserNavigationTree.class);
    private final UserSitemap userSitemap;
    private final Navigator navigator;
    private final Option option;
    private final UserNavigationTreeBuilder builder;
    private final UserSitemapSorters sorters;
    private boolean rebuildRequired = true;
    private boolean suppressValueChangeEvents;

    @Inject
    protected DefaultUserNavigationTree(UserSitemap userSitemap, Navigator navigator, Option option,
                                        UserNavigationTreeBuilder builder, UserSitemapSorters sorters) {
        super();
        this.userSitemap = userSitemap;
        this.navigator = navigator;
        this.option = option;
        this.builder = builder;
        this.sorters = sorters;
        option.init(this);
        builder.setUserNavigationTree(this);
        setImmediate(true);
        setItemCaptionMode(ItemCaptionMode.EXPLICIT);
        userSitemap.addListener(this);
        addValueChangeListener(this);
        navigator.addViewChangeListener(this);
        setId(ID.getId(Optional.empty(), this));
        sorters.setOptionSortAscending(getOptionSortAscending());


    }

    public boolean getOptionSortAscending() {
        return option.get(true, optionProperty.SORT_ASCENDING);
    }

    @Override
    public void setOptionSortAscending(boolean ascending) {
        setOptionSortAscending(ascending, true);
    }

    @Override
    public void setOptionSortAscending(boolean ascending, boolean rebuild) {
        sorters.setOptionSortAscending(ascending);
        option.set(ascending, optionProperty.SORT_ASCENDING);
        rebuildRequired = true;
        if (rebuild) {
            build();
        }
    }

    /**
     * See {@link UserNavigationTree#build()}
     */
    @Override
    public void build() {
        if (rebuildRequired) {
            log.debug("rebuilding user navigation tree");
            clear();
            builder.build();
            rebuildRequired = false;
            if (log.isDebugEnabled()) {
                Collection<?> t = this.getItemIds();
                String s = "";
                for (Object o : t) {
                    String itemCaption = getItemCaption(o);
                    s = s + itemCaption + ",";
                }
                log.debug(s);
            }
        } else {
            log.debug("rebuild of user navigation tree is not required");
        }
    }

    @Override
    public void clear() {
        removeAllItems();
    }

    public SortType getOptionSortType() {
        return option.get(SortType.ALPHA, optionProperty.SORT_TYPE);
    }

    @Override
    public void setOptionSortType(SortType sortType) {
        setOptionSortType(sortType, true);
    }

    @Override
    public void setOptionSortType(SortType sortType, boolean rebuild) {
        sorters.setOptionSortType(sortType);
        option.set(sortType, optionProperty.SORT_TYPE);
        rebuildRequired = true;
        if (rebuild) {
            build();
        }
    }

    public UserNavigationTreeBuilder getBuilder() {
        return builder;
    }

    /**
     * Returns true if the {@code node} is a leaf as far as this {@link DefaultUserNavigationTree} is concerned. It may
     * be a leaf here, but not in the {@link #userSitemap}, depending on the setting of {@link #getOptionMaxDepth()}
     *
     * @param node
     *
     * @return
     */
    public boolean isLeaf(UserSitemapNode node) {
        return !areChildrenAllowed(node);
    }

    @Override
    public int getOptionMaxDepth() {
        return option.get(10, optionProperty.MAX_DEPTH);
    }

    /**
     * See {@link UserNavigationTree#setOptionMaxDepth(int)}
     */
    @Override
    public void setOptionMaxDepth(int maxDepth) {
        setOptionMaxDepth(maxDepth, true);
    }

    /**
     * See {@link UserNavigationTree#setOptionMaxDepth(int, boolean)}
     */
    @Override
    public void setOptionMaxDepth(int maxDepth, boolean rebuild) {
        if (maxDepth > 0) {
            option.set(maxDepth, optionProperty.MAX_DEPTH);
            build();
        } else {
            log.warn("Attempt to set max depth value to {}, but has been ignored.  It must be greater than 0. ");
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if (!suppressValueChangeEvents) {
            if (getValue() != null) {
                String url = userSitemap.uri((UserSitemapNode) getValue());
                navigator.navigateTo(url);
            }
        }
    }

    /**
     * @see KrailViewChangeListener#beforeViewChange(KrailViewChangeEvent)
     */
    @Override
    public void beforeViewChange(KrailViewChangeEvent event) {
        //       do nothing
        //
    }

    /**
     * After a navigation change, select the appropriate node.
     *
     * @see KrailViewChangeListener#afterViewChange(KrailViewChangeEvent)
     */
    @Override
    public void afterViewChange(KrailViewChangeEvent event) {
        UserSitemapNode selectedNode = navigator.getCurrentNode();
        UserSitemapNode childNode = selectedNode;

        UserSitemapNode parentNode = (UserSitemapNode) getParent(childNode);

        while (parentNode != null) {
            expandItem(parentNode);
            parentNode = (UserSitemapNode) getParent(parentNode);
        }
        suppressValueChangeEvents = true;
        log.debug("selecting node for uri '{}'", userSitemap.uri(selectedNode));
        this.select(selectedNode);
        suppressValueChangeEvents = false;

    }

    @Override
    public Tree getTree() {
        return this;
    }

    /**
     * Although only {@link UserSitemap} labels (and therefore captions) have changed, the tree may need to be
     * re-sorted to reflect the change in language, so it is easier just to rebuild the tree
     */
    @Override
    public void labelsChanged() {
        rebuildRequired = true;
        build();
    }

    /**
     * {@link UserSitemap} structure has changed, we need to rebuild
     */
    @Override
    public void structureChanged() {
        rebuildRequired = true;
        build();
    }

    @Override
    public Comparator<UserSitemapNode> getSortComparator() {
        return sorters.getSortComparator();
    }

    public boolean isRebuildRequired() {
        return rebuildRequired;
    }

    public void setRebuildRequired(boolean rebuildRequired) {
        this.rebuildRequired = rebuildRequired;
    }

    @Override
    public Option getOption() {
        return option;
    }
}
