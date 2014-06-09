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

import uk.co.q3c.util.DefaultNodeModifier;
import uk.co.q3c.util.SourceTreeWrapper_BasicForest;
import uk.co.q3c.util.TargetTreeWrapper_MenuBar;
import uk.co.q3c.util.TreeCopier;
import uk.co.q3c.util.UserSitemapNodeCaption;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;

import com.google.inject.Inject;
import com.vaadin.ui.MenuBar;

public class UserNavigationMenu2 extends MenuBar implements ApplicationMenu {

	private final UserSitemap userSitemap;
	private final int maxDepth = 1000;

	@Inject
	protected UserNavigationMenu2(UserSitemap userSitemap) {
		super();
		this.userSitemap = userSitemap;
		loadNodes();
	}

	private void loadNodes() {
		this.removeItems();
		SourceTreeWrapper_BasicForest<UserSitemapNode, MenuItem> source = new SourceTreeWrapper_BasicForest<>(
				userSitemap.getForest());
		TargetTreeWrapper_MenuBar<UserSitemapNode> target = new TargetTreeWrapper_MenuBar<>(this);
		UserSitemapNodeCaption nodeCaptionReader = new UserSitemapNodeCaption();
		target.setCaptionReader(nodeCaptionReader);
		target.setNodeModifier(new DefaultNodeModifier<UserSitemapNode, MenuItem>());

		TreeCopier<UserSitemapNode, MenuItem> copier = new TreeCopier<>(source, target);
		copier.setMaxDepth(maxDepth);

		copier.addSourceFilter(new LogoutPageFilter());
		copier.copy();
	}
}
