package uk.co.q3c.v7.base.navigate.sitemap;

import java.util.List;
import java.util.Map;

import uk.co.q3c.util.BasicForest;
import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.shiro.PagePermission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface Sitemap<T extends SitemapNode> {

	public abstract void setLoaded(boolean loaded);

	public abstract boolean isLoaded();

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
