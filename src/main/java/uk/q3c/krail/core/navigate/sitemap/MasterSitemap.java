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

/**
 * Encapsulates the site layout. Individual "virtual pages" are represented by {@link MasterSitemapNode} instances.
 * This
 * map is built by one or more implementations of {@link SitemapLoader}, and is one of the fundamental building blocks
 * of the application, as it maps out pages, URIs and Views.
 * <p>
 * <p>
 * Because of it use as such a fundamental building block, an instance of this class has to be created early in the
 * application start up process. To avoid complex logic and dependencies within Guice modules, the building of the
 * {@link MasterSitemap} is managed by the {@link SitemapService}
 * <p>
 * Simple URI redirects can be added using {@link #addRedirect(String, String)}
 * <p>
 * If a duplicate entry is received (that is, a second entry for the same URI), the later entry will overwrite the
 * earlier entry
 * <p>
 * This MasterSitemap is complemented by instances of {@link UserSitemap}, which provides a user specific view of the
 * the {@link MasterSitemap}
 *
 * @author David Sowerby 19 May 2013
 */
public interface MasterSitemap extends Sitemap<MasterSitemapNode> {

    String getReport();

    void setReport(String report);


    MasterSitemapNode append(NodeRecord nodeRecord);

    /**
     * Replaces node with newNode
     *
     * @param node    the node to replace
     * @param newNode the replacement
     */
    void replaceNode(MasterSitemapNode node, MasterSitemapNode newNode);
}
