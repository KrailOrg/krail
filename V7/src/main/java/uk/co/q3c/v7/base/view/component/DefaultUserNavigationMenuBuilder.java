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
package uk.co.q3c.v7.base.view.component;

import uk.co.q3c.util.MenuItemComparator;
import uk.co.q3c.util.SourceTreeWrapper_BasicForest;
import uk.co.q3c.util.TargetTreeWrapper_MenuBar;
import uk.co.q3c.util.TreeCopy;
import uk.co.q3c.util.TreeCopy.SortOption;
import uk.co.q3c.util.UserSitemapNodeCaption;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;

import com.google.inject.Inject;
import com.vaadin.ui.MenuBar.MenuItem;

public class DefaultUserNavigationMenuBuilder implements UserNavigationMenuBuilder {

	private final UserSitemap userSitemap;
	private UserNavigationMenu userNavigationMenu;
	private final V7Navigator navigator;

	@Inject
	protected DefaultUserNavigationMenuBuilder(UserSitemap userSitemap, V7Navigator navigator) {
		this.userSitemap = userSitemap;
		this.navigator = navigator;
	}

	@Override
	public void build() {
		SourceTreeWrapper_BasicForest<UserSitemapNode> source = new SourceTreeWrapper_BasicForest<>(
				userSitemap.getForest());
		TargetTreeWrapper_MenuBar<UserSitemapNode, MenuItem> target = new TargetTreeWrapper_MenuBar<>(
				userNavigationMenu.getMenuBar());
		UserSitemapNodeCaption nodeCaptionReader = new UserSitemapNodeCaption();
		target.setCaptionReader(nodeCaptionReader);
		MenuBarNodeModifier nodeModifier = new MenuBarNodeModifier(userNavigationMenu.getMenuBar(), navigator,
				nodeCaptionReader);
		target.setNodeModifier(nodeModifier);

		TreeCopy<UserSitemapNode, MenuItem> treeCopy = new TreeCopy<>(source, target);
		treeCopy.setSortOption(SortOption.SORT_SOURCE_NODES);
		treeCopy.setMaxDepth(userNavigationMenu.getMaxDepth());
		treeCopy.setSorted(userNavigationMenu.isSorted());
		treeCopy.setTargetSortComparator(new MenuItemComparator());

		treeCopy.addSourceFilter(new LogoutPageFilter());
		treeCopy.copy();
	}

	@Override
	public UserNavigationMenu getUserNavigationMenu() {
		return userNavigationMenu;
	}

	@Override
	public void setUserNavigationMenu(UserNavigationMenu userNavigationMenu) {
		this.userNavigationMenu = userNavigationMenu;
	}

}
