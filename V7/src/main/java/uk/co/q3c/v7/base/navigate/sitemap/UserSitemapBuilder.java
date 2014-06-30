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
package uk.co.q3c.v7.base.navigate.sitemap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.SourceTreeWrapper_BasicForest;
import uk.co.q3c.util.TargetTreeWrapper_BasicForest;
import uk.co.q3c.util.TreeCopy;
import uk.co.q3c.v7.base.user.status.UserStatusListener;

import com.google.inject.Inject;

public class UserSitemapBuilder implements UserStatusListener {
	private static Logger log = LoggerFactory.getLogger(UserSitemapBuilder.class);
	private final TreeCopy<MasterSitemapNode, UserSitemapNode> treeCopy;
	private final UserSitemap userSitemap;

	@Inject
	protected UserSitemapBuilder(MasterSitemap masterSitemap, UserSitemap userSitemap,
			UserSitemapNodeModifier nodeModifier, UserSitemapCopyExtension copyExtension) {

		this.userSitemap = userSitemap;
		TargetTreeWrapper_BasicForest<MasterSitemapNode, UserSitemapNode> target = new TargetTreeWrapper_BasicForest<>(
				userSitemap.getForest());
		target.setNodeModifier(nodeModifier);
		SourceTreeWrapper_BasicForest<MasterSitemapNode> source = new SourceTreeWrapper_BasicForest<>(
				masterSitemap.getForest());
		treeCopy = new TreeCopy<>(source, target);
		treeCopy.setExtension(copyExtension);

	}

	public synchronized void build() {
		log.debug("building or rebuilding the map");
		if (!userSitemap.isLoaded()) {
			treeCopy.copy();
			userSitemap.setLoaded(true);
		}
	}

	@Override
	public synchronized void userStatusChanged() {
		userSitemap.clear();
		build();

	}

}
