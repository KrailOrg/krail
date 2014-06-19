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

import uk.co.q3c.util.SourceTreeWrapper;
import uk.co.q3c.util.SourceTreeWrapper_BasicForest;
import uk.co.q3c.util.TargetTreeWrapper_VaadinTree;
import uk.co.q3c.util.TreeCopy;
import uk.co.q3c.util.UserSitemapNodeCaption;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;

public class UserNavigationTreeBuilder {

	private final TreeCopy<UserSitemapNode, UserSitemapNode> treeCopy;
	private final UserSitemap userSitemap;

	protected UserNavigationTreeBuilder(UserSitemap userSitemap, UserNavigationTree userNavigationTree) {
		this.userSitemap = userSitemap;
		userNavigationTree.clear();

		SourceTreeWrapper<UserSitemapNode> source = new SourceTreeWrapper_BasicForest<>(userSitemap.getForest());
		TargetTreeWrapper_VaadinTree<UserSitemapNode, UserSitemapNode> target = new TargetTreeWrapper_VaadinTree<>(
				userNavigationTree.getTree());
		target.setCaptionReader(new UserSitemapNodeCaption());
		TreeCopy<UserSitemapNode, UserSitemapNode> copy = new TreeCopy<>(source, target);
		copy.setMaxDepth(maxDepth);
		copy.addSourceFilter(new LogoutPageFilter());
	}
}
