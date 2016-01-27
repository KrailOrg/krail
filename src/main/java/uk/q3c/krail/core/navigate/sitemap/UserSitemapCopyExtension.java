/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate.sitemap;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.i18n.CurrentLocale;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.navigate.LoginNavigationRule;
import uk.q3c.util.SourceTreeWrapper;
import uk.q3c.util.TargetTreeWrapper;
import uk.q3c.util.TreeCopy;
import uk.q3c.util.TreeCopyExtension;

import java.text.Collator;
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
    private static Logger log = LoggerFactory.getLogger(UserSitemapCopyExtension.class);
    private final UserSitemap userSitemap;
    private MasterSitemap masterSitemap;
    private CurrentLocale currentLocale;
    private Translate translate;
    @Inject
    protected UserSitemapCopyExtension(UserSitemap userSitemap, Translate translate, CurrentLocale currentLocale) {
        this.userSitemap = userSitemap;
        this.translate = translate;
        this.currentLocale = currentLocale;
    }

    public void setMasterSitemap(MasterSitemap masterSitemap) {
        this.masterSitemap = masterSitemap;
    }

    @Override
    public void invoke(SourceTreeWrapper<MasterSitemapNode> source, TargetTreeWrapper<MasterSitemapNode, UserSitemapNode> target, Map<MasterSitemapNode,
            UserSitemapNode> nodeMap) {
        log.debug("invoked");
        userSitemap.buildUriMap();
        copyStandardPages();
        loadRedirects();

    }

    /**
     * All the standard pages are always copied, even though they may not appear in the main uriMap.  The standard pages are often used for comparison in
     * things
     * like {@link LoginNavigationRule}s, so need always to be available, even though they are not necessarily displayed in navigation components
     */
    private void copyStandardPages() {
        log.debug("copying standard pages");
        ImmutableMap<StandardPageKey, MasterSitemapNode> sourcePages = masterSitemap.getStandardPages();
        Collator collator = Collator.getInstance(currentLocale.getLocale());

        for (StandardPageKey spk : sourcePages.keySet()) {
            MasterSitemapNode masterNode = sourcePages.get(spk);
            UserSitemapNode userNode = new UserSitemapNode(masterNode);
            userNode.setLabel(translate.from(masterNode.getLabelKey()));
            userNode.setCollationKey(collator.getCollationKey(userNode.getLabel()));
            userSitemap.addStandardPage(userNode, masterSitemap.uri(masterNode));
        }

    }

    /**
     * Copies the redirects from the {@link MasterSitemap},. but only adds it to this {@link UserSitemap} if the target
     * exists in this sitemap.
     */
    private void loadRedirects() {
        log.debug("loading redirects");
        for (Entry<String, String> entry : masterSitemap.getRedirects()
                                                        .entrySet()) {
            // only add the entry if the target exists
            if (userSitemap.hasUri(entry.getValue())) {
                userSitemap.addRedirect(entry.getKey(), entry.getValue());
            }
        }
    }

}
