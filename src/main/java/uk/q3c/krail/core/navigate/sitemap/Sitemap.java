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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import uk.q3c.krail.core.navigate.NavigationState;
import uk.q3c.krail.core.shiro.PagePermission;
import uk.q3c.util.forest.BasicForest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Sitemap<T extends SitemapNode> extends Serializable {

    boolean isLoaded();

    void setLoaded(boolean loaded);

    void addStandardPage(T node, String uri);

    void clear();

    BasicForest<T> getForest();

    void addChild(T parentNode, T childNode);

    String getRedirectPageFor(NavigationState navigationState);

    List<T> nodeChainFor(T node);

    List<T> nodeChainForUri(String uri, boolean allowPartialPath);

    int getNodeCount();

    ImmutableList<String> uris();

    Sitemap<T> addRedirect(String fromPage, String toPage);

    ImmutableMap<String, String> getRedirects();

    String getRedirectPageFor(String page);

    T getRedirectNodeFor(T sourceNode);

    List<T> nodeChainFor(NavigationState navigationState, boolean allowPartialPath);

    T nodeFor(NavigationState navigationState);

    T nodeFor(String uri);

    List<T> getChildren(T parentNode);

    List<T> nodeChainForSegments(List<String> segments, boolean allowPartialPath);

    ImmutableMap<StandardPageKey, T> getStandardPages();

    T standardPageNode(StandardPageKey pageKey);

    String standardPageURI(StandardPageKey pageKey);

    T nodeNearestFor(String uri);

    T nodeNearestFor(NavigationState navigationState);

    void removeNode(T node);

    void addNode(T node);

    PagePermission pagePermission(T node);

    NavigationState navigationState(T node);

    boolean hasUri(NavigationState navigationState);

    boolean hasUri(String uri);

    T getParent(T childNode);

    /**
     * Returns the number of children {@code node} has.  Throws an exception if {@code node} is not in the sitemap
     *
     * @param node the node whose children you are counting
     * @return SitemapException if {@code node} is not in the sitemap
     */
    int getChildCount(T node);

    List<T> getRoots();

    List<T> getAllNodes();

    String uri(T node);

    boolean containsNode(T node);

    boolean isLocked();

    void lock();

    T getRootFor(T node);

    Map<String, T> getUriMap();

    boolean isLoginUri(NavigationState navigationState);

    boolean isLogoutUri(NavigationState navigationState);

    boolean isPrivateHomeUri(NavigationState navigationState);

    boolean isPublicHomeUri(NavigationState navigationState);

}
