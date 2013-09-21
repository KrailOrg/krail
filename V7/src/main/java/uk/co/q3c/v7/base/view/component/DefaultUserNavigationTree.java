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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.CollationKeyOrder;
import uk.co.q3c.v7.base.navigate.InsertionOrder;
import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapURIConverter;
import uk.co.q3c.v7.base.shiro.DefaultURIPermissionFactory;
import uk.co.q3c.v7.base.shiro.LoginStatusHandler;
import uk.co.q3c.v7.base.shiro.LoginStatusListener;
import uk.co.q3c.v7.base.shiro.URIViewPermission;
import uk.co.q3c.v7.base.useropt.UserOption;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;

import com.vaadin.data.Property;
import com.vaadin.ui.Tree;

/**
 * A navigation tree for users to find their way around the site. Uses {@link Sitemap} as the site structure. Although
 * this seems naturally to be a {@link UIScoped} class it is not currently possible to have a UIScoped Component (see
 * https://github.com/davidsowerby/v7/issues/177)
 * 
 * @author David Sowerby 17 May 2013
 * 
 */
public class DefaultUserNavigationTree extends Tree implements UserNavigationTree, V7ViewChangeListener,
		LoginStatusListener {
	private static Logger log = LoggerFactory.getLogger(DefaultUserNavigationTree.class);
	private final CurrentLocale currentLocale;
	private final Sitemap sitemap;
	private int maxLevel;
	private int level;
	private final V7Navigator navigator;
	private final Provider<Subject> subjectPro;
	private final DefaultURIPermissionFactory uriPermissionFactory;
	private boolean sorted;
	private final UserOption userOption;
	private final SitemapURIConverter sitemapURIConverter;
	public static final String sortedOpt = "sorted";
	public static final String maxLevelOpt = "maxLevel";

	@Inject
	protected DefaultUserNavigationTree(Sitemap sitemap, CurrentLocale currentLocale, V7Navigator navigator,
			Provider<Subject> subjectPro, DefaultURIPermissionFactory uriPermissionFactory, UserOption userOption,
			SitemapURIConverter sitemapURIConverter, LoginStatusHandler loginStatusHandler) {
		super();
		this.sitemap = sitemap;
		this.currentLocale = currentLocale;
		this.navigator = navigator;
		this.subjectPro = subjectPro;
		this.uriPermissionFactory = uriPermissionFactory;
		this.userOption = userOption;
		this.sitemapURIConverter = sitemapURIConverter;
		setImmediate(true);
		setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		// set user option
		sorted = userOption.getOptionAsBoolean(this.getClass().getSimpleName(), sortedOpt, false);
		maxLevel = userOption.getOptionAsInt(this.getClass().getSimpleName(), maxLevelOpt, -1);
		addValueChangeListener(this);
		navigator.addViewChangeListener(this);
		setId(ID.getId(this));
		loginStatusHandler.addListener(this);
		loginStatusChange(loginStatusHandler.subjectIsAuthenticated(), loginStatusHandler.subjectName());
		loadNodes();

	}

	private void loadNodes() {

		this.removeAllItems();
		List<SitemapNode> nodeList = sitemap.getRoots();

		// which order, sorted or insertion?
		if (sorted) {
			log.debug("'sorted' is true, sorting by collation key");
			Collections.sort(nodeList, new CollationKeyOrder());
		} else {
			log.debug("'sorted' is false, using insertion order");
			Collections.sort(nodeList, new InsertionOrder());
		}

		for (SitemapNode node : nodeList) {
			level = 1;
			// doesn't make sense to show the logout page
			if (!node.getLabelKey().equals(StandardPageKey.Logout)) {
				loadNode(null, node, node.equals(sitemap.getPublicRootNode()));
			}
		}
	}

	/**
	 * Checks each node to ensure that the Subject has permission to view, and if so, adds it to this tree
	 * 
	 * @param parentNode
	 * @param childNode
	 */
	private void loadNode(SitemapNode parentNode, SitemapNode childNode, boolean publicBranch) {
		// construct the permission
		String uri = sitemap.uri(childNode);
		URIViewPermission pagePermissionRequired = uriPermissionFactory.createViewPermission(uri);

		// if permitted, add it
		if (publicBranch || subjectPro.get().isPermitted(pagePermissionRequired)) {
			log.debug("user has permission to view URI {}", uri);
			this.addItem(childNode);
			I18NKey<?> key = childNode.getLabelKey();

			String caption = key.getValue(currentLocale.getLocale());
			this.setItemCaption(childNode, caption);
			setParent(childNode, parentNode);

			SitemapNode newParentNode = childNode;
			level++;

			if ((maxLevel < 0) || (level <= maxLevel)) {
				List<SitemapNode> children = sitemap.getChildren(newParentNode);
				if (children.size() == 0) {
					// no children, visual tree should not allow expanding the node
					setChildrenAllowed(newParentNode, false);
				} else {
					// which order, sorted or insertion?
					if (sorted) {
						Collections.sort(children, new CollationKeyOrder());
					} else {
						Collections.sort(children, new InsertionOrder());
					}
				}
				for (SitemapNode child : children) {
					if (!child.getLabelKey().equals(StandardPageKey.Logout)) {
						loadNode(newParentNode, child, publicBranch);
					}
				}

			} else {
				// no children, visual tree should not allow expanding the node
				setChildrenAllowed(newParentNode, false);
			}
		} else {
			log.debug("user does not have permission to view {}, page not loaded in to UserNavigationTree", uri);
		}
	}

	/**
	 * Returns true if the {@code node} is a leaf as far as this {@link DefaultUserNavigationTree} is concerned. It may
	 * be a leaf here, but not in the {@link #sitemap}, depending on the setting of {@link #maxLevel}
	 * 
	 * @param node
	 * @return
	 */
	public boolean isLeaf(SitemapNode node) {
		return !areChildrenAllowed(node);
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	/**
	 * Set the maximum level or depth of the tree you want to be visible. 0 is not allowed, and is ignored. Set to < 0
	 * if you want this tree to display the full {@link #sitemap}
	 * 
	 * @param level
	 */
	public void setMaxLevel(int maxLevel) {
		if (maxLevel != 0) {
			this.maxLevel = maxLevel;
			loadNodes();
			userOption.setOption(this.getClass().getSimpleName(), maxLevelOpt, this.maxLevel);
		}
	}

	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		if (getValue() != null) {
			String url = sitemap.uri((SitemapNode) getValue());
			navigator.navigateTo(url);
		}
	}

	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
		if (sorted != this.sorted) {
			this.sorted = sorted;
			loadNodes();
			userOption.setOption(this.getClass().getSimpleName(), sortedOpt, this.sorted);
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
		SitemapNode selectedNode = sitemapURIConverter.nodeForUri(navigator.getNavigationState(), false);
		// shouldn't get null, but just in case
		if (selectedNode != null) {
			this.select(selectedNode);
		}
	}

	@Override
	public void loginStatusChange(boolean status, String name) {

	}

}
