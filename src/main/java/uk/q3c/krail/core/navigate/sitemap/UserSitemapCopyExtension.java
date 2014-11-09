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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import uk.q3c.util.SourceTreeWrapper;
import uk.q3c.util.TargetTreeWrapper;
import uk.q3c.util.TreeCopy;
import uk.q3c.util.TreeCopyExtension;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Post processing for the {@link TreeCopy} process from {@link MasterSitemap} to {@link UserSitemap}. Copies the
 * standard key nodes from the master, translating to {@link UserSitemapNode}
 *
 * @author David Sowerby
 * @date 9 Jun 2014
 */
public class UserSitemapCopyExtension implements TreeCopyExtension<MasterSitemapNode, UserSitemapNode> {

    private final MasterSitemap masterSitemap;
    private final UserSitemap userSitemap;

    @Inject
    protected UserSitemapCopyExtension(MasterSitemap masterSitemap, UserSitemap userSitemap) {
        this.masterSitemap = masterSitemap;
        this.userSitemap = userSitemap;
    }

    @Override
    public void invoke(SourceTreeWrapper<MasterSitemapNode> source, TargetTreeWrapper<MasterSitemapNode,
            UserSitemapNode> target, Map<MasterSitemapNode, UserSitemapNode> nodeMap) {

        userSitemap.buildUriMap();
        copyStandardPages(nodeMap);
        loadRedirects();

    }

    private void copyStandardPages(Map<MasterSitemapNode, UserSitemapNode> nodeMap) {
        ImmutableMap<StandardPageKey, MasterSitemapNode> sourcePages = masterSitemap.getStandardPages();

        for (StandardPageKey spk : sourcePages.keySet()) {
            MasterSitemapNode masterNode = sourcePages.get(spk);
            UserSitemapNode userNode = nodeMap.get(masterNode);
            userSitemap.addStandardPage(spk, userNode);
        }

    }

    /**
     * Copies the redirects from the {@link MasterSitemap},. but only adds it to this {@link UserSitemap} if the target
     * exists in this sitemap.
     */
    private void loadRedirects() {
        for (Entry<String, String> entry : masterSitemap.getRedirects()
                                                        .entrySet()) {
            // only add the entry of the target exists
            if (userSitemap.hasUri(entry.getValue())) {
                userSitemap.addRedirect(entry.getKey(), entry.getValue());
            }
        }
    }

}
