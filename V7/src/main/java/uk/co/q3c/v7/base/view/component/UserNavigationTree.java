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
import uk.co.q3c.v7.base.useropt.UserOption;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;

import com.vaadin.data.Property;
import com.vaadin.ui.Tree;

/**
 * A navigation tree for users to find their way around the site. Uses
 * {@link Sitemap} as the site structure. This is naturally a {@link UIScoped}
 * class, as it makes sense for one instance to be in use per browser tab
 * 
 * @author David Sowerby 17 May 2013
 * 
 */
@UIScoped
public class UserNavigationTree extends Tree {
	private static Logger log = LoggerFactory
			.getLogger(UserNavigationTree.class);
	private final CurrentLocale currentLocale;
	private final Sitemap sitemap;
	private int maxLevel;
	private int level;
	private final V7Navigator navigator;
	private final Provider<Subject> subjectPro;
	private boolean sorted;
	private final UserOption userOption;
	public static final String sortedOpt = "sorted";
	public static final String maxLevelOpt = "maxLevel";

	@Inject
	protected UserNavigationTree(Sitemap sitemap, CurrentLocale currentLocale,
			V7Navigator navigator, Provider<Subject> subjectPro, UserOption userOption) {
		super();
		this.sitemap = sitemap;
		this.currentLocale = currentLocale;
		this.navigator = navigator;
		this.subjectPro = subjectPro;
		this.userOption = userOption;
		setImmediate(true);
		setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		// set user option
		sorted = userOption.getOptionAsBoolean(this.getClass().getSimpleName(),
				sortedOpt, false);
		maxLevel = userOption.getOptionAsInt(this.getClass().getSimpleName(),
				maxLevelOpt, -1);
		addValueChangeListener(this);

		loadNodes();

	}

	private void loadNodes() {

		this.removeAllItems();
		List<SitemapNode> roots = sitemap.getRoots();

		for (SitemapNode root : roots) {
			level = 1;
			// doesn't make sense to show the logout page
			if (!root.getLabelKey().equals(StandardPageKey.Logout)) {
				loadNode(null, root);
			}
		}
	}

	/**
	 * Checks each node to ensure that the Subject has permission to view, and
	 * if so, adds it to this tree
	 * 
	 * @param node
	 */
	//FIXME the parent mught be useless since i can get it from the node itself
	private void loadNode(SitemapNode parent, SitemapNode node) {
		// construct the permission
		String uri = node.getUri();

		// if permitted, add it
		if (node.isPermitted(subjectPro.get())) {
			log.debug("user has permission to view URI {}", uri);
			this.addItem(node);
			I18NKey<?> key = node.getLabelKey();

			String caption = key.getValue(currentLocale.getLocale());
			this.setItemCaption(node, caption);
			setParent(node, parent);

			SitemapNode newParentNode = node;
			level++;

			if ((maxLevel < 0) || (level <= maxLevel)) {
				List<SitemapNode> children = sitemap.getChildren(newParentNode);
				if (children.size() == 0) {
					// no children, visual tree should not allow expanding the
					// node
					setChildrenAllowed(newParentNode, false);
				} else {
					for (SitemapNode child : children) {
						if (!child.getLabelKey().equals(StandardPageKey.Logout)) {
							loadNode(newParentNode, child);
						}
					}
				}
			} else {
				// no children, visual tree should not allow expanding the node
				setChildrenAllowed(newParentNode, false);
			}
		} else {
			log.debug(
					"user does not have permission to view {}, page not loaded in to UserNavigationTree",
					uri);
		}
	}

	/**
	 * Returns true if the {@code node} is a leaf as far as this
	 * {@link UserNavigationTree} is concerned. It may be a leaf here, but not
	 * in the {@link #sitemap}, depending on the setting of {@link #maxLevel}
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
	 * Set the maximum level or depth of the tree you want to be visible. 0 is
	 * not allowed, and is ignored. Set to < 0 if you want this tree to display
	 * the full {@link #sitemap}
	 * 
	 * @param level
	 */
	public void setMaxLevel(int maxLevel) {
		if (maxLevel != 0) {
			this.maxLevel = maxLevel;
			loadNodes();
			userOption.setOption(this.getClass().getSimpleName(), maxLevelOpt,
					this.maxLevel);
		}
	}

	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		if (getValue() != null) {
			String url = ((SitemapNode) getValue()).getUri();
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
			userOption.setOption(this.getClass().getSimpleName(), sortedOpt,
					this.sorted);
		}
	}

}
