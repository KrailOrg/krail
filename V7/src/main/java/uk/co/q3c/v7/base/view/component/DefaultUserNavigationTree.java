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
package uk.co.q3c.v7.base.view.component;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapChangeListener;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.UserSitemapSorters;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionProperty;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;

import com.google.inject.Inject;
import com.vaadin.data.Property;
import com.vaadin.ui.Tree;

/**
 * A navigation tree for users to find their way around the site. Uses {@link UserSitemap} to provide the structure
 * which in turn provides a view on {@link MasterSitemap} filtered for the current user's selection of locale and
 * authorised pages. Although this seems naturally to be a {@link UIScoped} class it is not currently possible to have a
 * UIScoped Component (see https://github.com/davidsowerby/v7/issues/177)
 *
 * @modified David Sowerby
 * @author David Sowerby 17 May 2013
 *
 */
public class DefaultUserNavigationTree extends Tree implements UserNavigationTree, V7ViewChangeListener,
		UserSitemapChangeListener {
	private static Logger log = LoggerFactory.getLogger(DefaultUserNavigationTree.class);
	private final UserSitemap userSitemap;
	private final V7Navigator navigator;
	private final UserOption userOption;
	private boolean suppressValueChangeEvents;
	private final UserNavigationTreeBuilder builder;
	private final UserSitemapSorters sorters;
	private boolean rebuildRequired = true;

	@Inject
	protected DefaultUserNavigationTree(UserSitemap userSitemap, V7Navigator navigator, UserOption userOption,
			UserNavigationTreeBuilder builder, UserSitemapSorters sorters) {
		super();
		this.userSitemap = userSitemap;
		this.navigator = navigator;
		this.userOption = userOption;
		this.builder = builder;
		this.sorters = sorters;
		builder.setUserNavigationTree(this);
		setImmediate(true);
		setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		userSitemap.addListener(this);
		addValueChangeListener(this);
		navigator.addViewChangeListener(this);
		setId(ID.getId(this));
		boolean ascending = userOption.getOptionAsBoolean(this.getClass().getSimpleName(),
				UserOptionProperty.SORT_ASCENDING, true);

		SortType sortType = (SortType) userOption.getOptionAsEnum(this.getClass().getSimpleName(),
				UserOptionProperty.SORT_TYPE, SortType.ALPHA);

		setSortAscending(ascending, false);
		setSortType(sortType, false);

	}

	public UserNavigationTreeBuilder getBuilder() {
		return builder;
	}

	/**
	 * Returns true if the {@code node} is a leaf as far as this {@link DefaultUserNavigationTree} is concerned. It may
	 * be a leaf here, but not in the {@link #userSitemap}, depending on the setting of {@link #maxLevel}
	 *
	 * @param node
	 * @return
	 */
	public boolean isLeaf(UserSitemapNode node) {
		return !areChildrenAllowed(node);
	}

	@Override
	public int getMaxDepth() {
		return userOption.getOptionAsInt(this.getClass().getSimpleName(), UserOptionProperty.MAX_DEPTH, 10);
	}

	/**
	 * See {@link UserNavigationTree#setMaxDepth(int)}
	 */
	@Override
	public void setMaxDepth(int maxDepth) {
		setMaxDepth(maxDepth, true);
	}

	/**
	 * See {@link UserNavigationTree#setMaxDepth(int, boolean)}
	 */
	@Override
	public void setMaxDepth(int maxDepth, boolean rebuild) {
		if (maxDepth > 0) {
			userOption.setOption(this.getClass().getSimpleName(), UserOptionProperty.MAX_DEPTH, maxDepth);
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
	 *
	 * @see uk.co.q3c.v7.base.view.V7ViewChangeListener#beforeViewChange(uk.co.q3c.v7.base.view.V7ViewChangeEvent)
	 */
	@Override
	public boolean beforeViewChange(V7ViewChangeEvent event) {
		return true; // do nothing, and don't block
	}

	/**
	 * After a navigation change, select the appropriate node.
	 *
	 * @see uk.co.q3c.v7.base.view.V7ViewChangeListener#afterViewChange(uk.co.q3c.v7.base.view.V7ViewChangeEvent)
	 */
	@Override
	public void afterViewChange(V7ViewChangeEvent event) {
		UserSitemapNode selectedNode = navigator.getCurrentNode();
		UserSitemapNode childNode = selectedNode;
		UserSitemapNode parentNode = (UserSitemapNode) getParent(childNode);
		while (parentNode != null) {
			expandItem(parentNode);
			parentNode = (UserSitemapNode) getParent(parentNode);
		}
		suppressValueChangeEvents = true;
		this.select(selectedNode);
		suppressValueChangeEvents = false;

	}

	@Override
	public Tree getTree() {
		return this;
	}

	@Override
	public void clear() {
		removeAllItems();
	}

	/**
	 * See {@link UserNavigationTree#build()}
	 */
	@Override
	public void build() {
		if (rebuildRequired) {
			log.debug("rebuilding");
			clear();
			builder.build();
			rebuildRequired = false;
		} else {
			log.debug("rebuild not required");
		}
	}

	/**
	 * Although only {@link UserSitemap} labels (and therefore captions) have changed, the tree may need to be re-sorted
	 * to reflect the change in language, so it is easier just to rebuild the tree
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
	public void setSortAscending(boolean ascending) {
		setSortAscending(ascending, true);
	}

	@Override
	public void setSortAscending(boolean ascending, boolean rebuild) {
		sorters.setSortAscending(ascending);
		userOption.setOption(this.getClass().getSimpleName(), UserOptionProperty.SORT_ASCENDING, ascending);
		rebuildRequired = true;
		if (rebuild) {
			build();
		}
	}

	@Override
	public void setSortType(SortType sortType) {
		setSortType(sortType, true);
	}

	@Override
	public void setSortType(SortType sortType, boolean rebuild) {
		sorters.setSortType(sortType);
		userOption.setOption(this.getClass().getSimpleName(), UserOptionProperty.SORT_TYPE, sortType);
		rebuildRequired = true;
		if (rebuild) {
			build();
		}
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

}
