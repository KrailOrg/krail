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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.Sitemap;
import uk.co.q3c.v7.base.navigate.SitemapNode;
import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.DefaultURIPermissionFactory;
import uk.co.q3c.v7.base.shiro.URIViewPermission;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKeys;

import com.vaadin.data.Property;
import com.vaadin.ui.Tree;

/**
 * A navigation tree for users to find their way around the site. Uses {@link Sitemap} as the site structure. This is
 * naturally a {@link UIScoped} class, as it makes sense for one instance to be in use per browser tab
 * 
 * @author David Sowerby 17 May 2013
 * 
 */
@UIScoped
public class UserNavigationTree extends Tree {
	private static Logger log = LoggerFactory.getLogger(UserNavigationTree.class);
	private final CurrentLocale currentLocale;
	private final Sitemap sitemap;
	private int maxLevel = -1;
	private int level;
	private final V7Navigator navigator;
	private final Provider<Subject> subjectPro;
	private final DefaultURIPermissionFactory uriPermissionFactory;

	@Inject
	protected UserNavigationTree(Sitemap sitemap, CurrentLocale currentLocale, V7Navigator navigator,
			Provider<Subject> subjectPro, DefaultURIPermissionFactory uriPermissionFactory) {
		super();
		this.sitemap = sitemap;
		this.currentLocale = currentLocale;
		this.navigator = navigator;
		this.subjectPro = subjectPro;
		this.uriPermissionFactory = uriPermissionFactory;
		setImmediate(true);
		setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		addValueChangeListener(this);
		loadNodes();

	}

	private void loadNodes() {
		this.removeAllItems();
		List<SitemapNode> nodeList = sitemap.getRoots();

		for (SitemapNode node : nodeList) {
			level = 1;
			// doesn't make sense to show the logout page
			if (!node.getLabelKey().equals(StandardPageKey.Logout)) {
				{
					loadNode(null, node, node.equals(sitemap.getPublicRootNode()));
				}
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
			I18NKeys<?> key = (I18NKeys<?>) childNode.getLabelKey();

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
	 * Returns true if the {@code node} is a leaf as far as this {@link UserNavigationTree} is concerned. It may be a
	 * leaf here, but not in the {@link #sitemap}, depending on the setting of {@link #maxLevel}
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
		}
	}

	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		if (getValue() != null) {
			String url = sitemap.uri((SitemapNode) getValue());
			navigator.navigateTo(url);
		}
	}

}
