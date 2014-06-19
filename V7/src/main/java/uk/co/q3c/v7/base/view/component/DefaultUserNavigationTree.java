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

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
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
public class DefaultUserNavigationTree extends Tree implements UserNavigationTree, V7ViewChangeListener {
	private final UserSitemap userSitemap;
	private final V7Navigator navigator;
	private final UserOption userOption;
	private boolean suppressValueChangeEvents;

	@Inject
	protected DefaultUserNavigationTree(UserSitemap userSitemap, V7Navigator navigator, UserOption userOption) {
		super();
		this.userSitemap = userSitemap;
		this.navigator = navigator;
		this.userOption = userOption;
		setImmediate(true);
		setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		// set user option
		addValueChangeListener(this);
		navigator.addViewChangeListener(this);
		setId(ID.getId(this));

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
		return userOption.getOptionAsInt(this.getClass().getSimpleName(), UserOptionProperty.MAX_DEPTH, -1);
	}

	/**
	 * Set the maximum level or depth of the tree you want to be visible. 0 is not allowed, and is ignored. Set to < 0
	 * if you want this tree to display the full {@link #userSitemap}
	 *
	 * @param level
	 */
	public void setMaxDepth(int maxDepth) {
		if (maxDepth != 0) {
			userOption.setOption(this.getClass().getSimpleName(), UserOptionProperty.MAX_DEPTH, maxDepth);
			// loadNodes();
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
}
