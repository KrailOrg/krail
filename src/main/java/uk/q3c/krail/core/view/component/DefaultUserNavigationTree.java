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

import com.google.inject.Inject;
import com.vaadin.data.TreeData;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Tree;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.UIBus;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapLabelChangeMessage;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapStructureChangeMessage;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;
import uk.q3c.krail.core.option.VaadinOptionContext;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.eventbus.GlobalMessageBus;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionChangeMessage;
import uk.q3c.krail.option.OptionKey;

import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A navigation tree for users to find their way around the site. Uses {@link UserSitemap} to provide the structure
 * which in turn provides a view on {@link MasterSitemap} filtered for the current user's selection of locale and
 * authorised pages. Although this seems naturally to be a {@link UIScoped} class it is not currently possible to have
 * a
 * UIScoped Component (see https://github.com/davidsowerby/krail/issues/177)
 *
 * @author David Sowerby 17 May 2013
 * @modified David Sowerby 29 May 2015
 */
@Listener
@SubscribeTo({UIBus.class, SessionBus.class, GlobalMessageBus.class})
public class DefaultUserNavigationTree extends Tree<UserSitemapNode> implements VaadinOptionContext, UserNavigationTree, SelectionListener<UserSitemapNode> {

    public static final OptionKey<SortType> optionKeySortType = new OptionKey<>(SortType.ALPHA, DefaultUserNavigationTree.class, LabelKey.Sort_Type,
            DescriptionKey.Sort_Type);
    public static final OptionKey<Boolean> optionKeySortAscending = new OptionKey<>(Boolean.TRUE, DefaultUserNavigationTree.class, LabelKey.Sort_Ascending,
            DescriptionKey.Sort_Ascending);
    public static final OptionKey<Integer> optionKeyMaximumDepth = new OptionKey<>(10, DefaultUserNavigationTree.class, LabelKey.Maxiumum_Depth, DescriptionKey
            .Maximum_Tree_Depth);
    private static Logger log = LoggerFactory.getLogger(DefaultUserNavigationTree.class);
    private final UserSitemap userSitemap;
    private final Navigator navigator;
    private final Option option;
    private final UserNavigationTreeBuilder builder;
    private final UserSitemapSorters sorters;

    private boolean rebuildRequired = true;
    private boolean suppressValueChangeEvents;


    @Inject
    protected DefaultUserNavigationTree(UserSitemap userSitemap, Navigator navigator, Option option, UserNavigationTreeBuilder builder, UserSitemapSorters
            sorters) {
        super();
        this.userSitemap = userSitemap;
        this.navigator = navigator;
        this.option = option;
        this.builder = builder;
        this.sorters = sorters;

        builder.setUserNavigationTree(this);
        // set selection mode before addSelectionListener - the listener is actually added to the SelectionModel, which
        // is different for each selection mode
        setSelectionMode(Grid.SelectionMode.SINGLE);
        addSelectionListener(this);
        sorters.setOptionSortAscending(getOptionSortAscending());



    }

    public final boolean getOptionSortAscending() {
        return option.get(optionKeySortAscending);
    }

    @Override
    public void setOptionSortAscending(boolean ascending) {
        setOptionSortAscending(ascending, true);
    }

    @Override
    public void setOptionSortAscending(boolean ascending, boolean rebuild) {
        sorters.setOptionSortAscending(ascending);
        option.set(optionKeySortAscending, ascending);
        rebuildRequired = true;
        if (rebuild) {
            build();
        }
    }

    /**
     * See {@link UserNavigationTree#build()}
     */
    @SuppressFBWarnings("LO_APPENDED_STRING_IN_FORMAT_STRING") // no other way to do it
    @Override
    public void build() {
        if (rebuildRequired) {
            log.debug("rebuilding user navigation tree");
            clear();
            builder.build();
            rebuildRequired = false;
//            if (log.isDebugEnabled()) {
//                Collection<?> t = this.getItemIds();
//                StringBuilder buf = new StringBuilder();
//                for (Object o : t) {
//                    String itemCaption = getItemCaption(o);
//                    buf.append(itemCaption);
//                    buf.append(',');
//                }
//                log.debug(buf.toString());
//            }
        } else {
            log.debug("rebuild of user navigation tree is not required");
        }
        TreeData<String> td;
    }

    @Override
    public void clear() {
        this.getTreeData().clear();
        this.getDataProvider().refreshAll();
    }

    public SortType getOptionSortType() {
        return option.get(optionKeySortType);
    }

    @Override
    public void setOptionKeySortType(SortType sortType) {
        checkNotNull(sortType);
        setOptionSortType(sortType, true);
    }

    @Override
    public void setOptionSortType(SortType sortType, boolean rebuild) {
        sorters.setOptionKeySortType(sortType);
        option.set(optionKeySortType, sortType);
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
     * @return
     */
    public boolean isLeaf(UserSitemapNode node) {
        return getTreeData().getChildren(node).isEmpty();
    }

    public boolean areChildrenAllowed(UserSitemapNode node) {
        return !isLeaf(node);
    }

    @Override
    public int getOptionMaxDepth() {
        return option.get(optionKeyMaximumDepth);
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
            option.set(optionKeyMaximumDepth, maxDepth);
            build();
        } else {
            log.warn("Attempt to set max depth value to {}, but has been ignored.  It must be greater than 0. ");
        }
    }



    /**
     * After a navigation change, select the appropriate node.
     *
     * @see KrailView for a description of the call sequence
     */
    @Handler
    public void afterViewChange(AfterViewChangeBusMessage busMessage) {
        UserSitemapNode selectedNode = userSitemap.nodeFor(busMessage.getToState());
        UserSitemapNode parentNode = getTreeData().getParent(selectedNode);

        while (parentNode != null) {
            this.expand(parentNode);
            parentNode = getTreeData().getParent(parentNode);
        }
        suppressValueChangeEvents = true;
        log.debug("selecting node for uri '{}'", userSitemap.uri(selectedNode));
        this.select(selectedNode);
        suppressValueChangeEvents = false;

    }

    @Override
    public Tree<UserSitemapNode> getTree() {
        return this;
    }

    /**
     * Although only {@link UserSitemap} labels (and therefore captions) have changed, the tree may need to be
     * re-sorted to reflect the change in language, so it is easier just to rebuild the tree
     */
    @Handler
    public void labelsChanged(UserSitemapLabelChangeMessage busMessage) {
        rebuildRequired = true;
        build();
    }

    /**
     * {@link UserSitemap} structure has changed, we need to rebuild
     */
    @Handler
    public void structureChanged(UserSitemapStructureChangeMessage busMessage) {
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
    public Option optionInstance() {
        return option;
    }

    @Handler
    public void optionValueChanged(OptionChangeMessage<?> message) {
        if (message.getOptionKey().getContext().equals(DefaultUserNavigationTree.class)) {
            rebuildRequired = true;
            build();
        }
    }

    @Override
    public void selectionChange(SelectionEvent<UserSitemapNode> event) {
        if (!suppressValueChangeEvents) {
            if (event.getFirstSelectedItem().isPresent()) {
                String url = userSitemap.uri(event.getFirstSelectedItem().get());
                navigator.navigateTo(url);
            }
        }
    }
}
