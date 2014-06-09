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

import java.util.Map;

import uk.co.q3c.util.SourceTreeWrapper;
import uk.co.q3c.util.TargetTreeWrapper;
import uk.co.q3c.util.TreeCopierExtension;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * Post processing for the copy process from {@link MasterSitemap} to {@link UserSitemap}. Copies the standard key nodes
 * from the master, translating to {@link UserSitemapNode}
 *
 * @author David Sowerby
 * @date 9 Jun 2014
 */
public class UserSitemapCopyExtension implements TreeCopierExtension<MasterSitemapNode, UserSitemapNode> {

	private final MasterSitemap masterSitemap;
	private final UserSitemap userSitemap;

	@Inject
	protected UserSitemapCopyExtension(MasterSitemap masterSitemap, UserSitemap userSitemap) {
		this.masterSitemap = masterSitemap;
		this.userSitemap = userSitemap;
	}

	@Override
	public void invoke(SourceTreeWrapper<MasterSitemapNode> source,
			TargetTreeWrapper<MasterSitemapNode, UserSitemapNode> target,
			Map<MasterSitemapNode, UserSitemapNode> nodeMap) {

		ImmutableMap<StandardPageKey, MasterSitemapNode> sourcePages = masterSitemap.getStandardPages();

		for (StandardPageKey spk : sourcePages.keySet()) {
			MasterSitemapNode masterNode = sourcePages.get(spk);
			UserSitemapNode userNode = nodeMap.get(masterNode);
			userSitemap.addStandardPage(spk, userNode);
		}

	}

}
