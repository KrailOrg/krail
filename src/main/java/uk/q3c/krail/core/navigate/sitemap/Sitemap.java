/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.navigate.sitemap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import uk.q3c.krail.core.navigate.NavigationState;
import uk.q3c.krail.core.shiro.PagePermission;
import uk.q3c.util.BasicForest;

import java.util.List;
import java.util.Map;

public interface Sitemap<T extends SitemapNode> {

    public abstract boolean isLoaded();

    public abstract void setLoaded(boolean loaded);

    public abstract void clear();

    public abstract void addStandardPage(StandardPageKey pageKey, T node);

    public abstract BasicForest<T> getForest();

    public abstract void addChild(T parentNode, T childNode);

    public abstract String getRedirectPageFor(NavigationState navigationState);

    public abstract List<T> nodeChainFor(T node);

    public abstract List<T> nodeChainForUri(String uri, boolean allowPartialPath);

    public abstract int getNodeCount();

    public abstract ImmutableList<String> uris();

    public abstract Sitemap<T> addRedirect(String fromPage, String toPage);

    public abstract ImmutableMap<String, String> getRedirects();

    public abstract String getRedirectPageFor(String page);

    public abstract T getRedirectNodeFor(T sourceNode);

    public abstract List<T> nodeChainFor(NavigationState navigationState, boolean allowPartialPath);

    public abstract T nodeFor(NavigationState navigationState);

    public abstract T nodeFor(String uri);

    public abstract List<T> getChildren(T parentNode);

    public abstract List<T> nodeChainForSegments(List<String> segments, boolean allowPartialPath);

    public abstract ImmutableMap<StandardPageKey, T> getStandardPages();

    public abstract T standardPageNode(StandardPageKey pageKey);

    public abstract String standardPageURI(StandardPageKey pageKey);

    public abstract T nodeNearestFor(String uri);

    public abstract T nodeNearestFor(NavigationState navigationState);

    public abstract void removeNode(T node);

    public abstract void addNode(T node);

    public abstract PagePermission pagePermission(T node);

    public abstract NavigationState navigationState(T node);

    public abstract boolean hasUri(NavigationState navigationState);

    public abstract boolean hasUri(String uri);

    public abstract T getParent(T childNode);

    public abstract int getChildCount(T node);

    public abstract List<T> getRoots();

    public abstract List<T> getAllNodes();

    public abstract String uri(T node);

    public abstract boolean containsNode(T node);

    public abstract T getRootFor(T node);

    Map<String, T> getUriMap();

}
