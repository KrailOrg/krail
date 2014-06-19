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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.BasicForest;
import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.shiro.PagePermission;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public abstract class Sitemap<T extends SitemapNode> {
	private static Logger log = LoggerFactory.getLogger(Sitemap.class);
	protected BasicForest<T> forest;
	protected final URIFragmentHandler uriHandler;
	protected final Map<String, T> uriMap = new LinkedHashMap<>();
	protected final Map<StandardPageKey, T> standardPages = new HashMap<>();
	// Uses LinkedHashMap to retain insertion order
	protected final Map<String, String> redirects = new LinkedHashMap<>();
	private boolean loaded;

	protected Sitemap(URIFragmentHandler uriHandler) {
		super();
		this.uriHandler = uriHandler;
		forest = new BasicForest<>();

	}

	/**
	 * Delegates to {@link BasicForest#getRootFor(Object)}
	 *
	 * @param node
	 * @return
	 */
	public synchronized T getRootFor(T node) {
		return forest.getRootFor(node);
	}

	/**
	 * Delegates to {@link BasicForest#containsNode(Object)}
	 *
	 * @param newParentNode
	 * @return
	 */
	public synchronized boolean containsNode(T node) {
		return forest.containsNode(node);
	}

	/**
	 * Returns the full URI for {@code node}
	 *
	 * @param node
	 * @return
	 */
	public synchronized String uri(T node) {
		checkNotNull(node);
		StringBuilder buf = new StringBuilder(node.getUriSegment());
		prependParent(node, buf);
		return buf.toString();
	}

	/**
	 * Recursively prepends the parent URI segment of {@code node}, until the full URI has been built
	 */
	private void prependParent(T node, StringBuilder buf) {
		T parentNode = forest.getParent(node);
		if (parentNode != null) {
			buf.insert(0, "/");
			buf.insert(0, parentNode.getUriSegment());
			prependParent(parentNode, buf);
		}
	}

	/**
	 * Delegates to {@link BasicForest#getAllNodes()}
	 *
	 * @return
	 */
	public synchronized List<T> getAllNodes() {
		return forest.getAllNodes();
	}

	public synchronized List<T> getRoots() {
		return forest.getRoots();
	}

	/**
	 * Delegates to {@link BasicForest#getChildCount(Object)}
	 *
	 * @param node
	 * @return
	 */

	public synchronized int getChildCount(T node) {
		return forest.getChildCount(node);
	}

	/**
	 * Returns the parent of {@code node}. Will be null if {@code node} has no parent (that is, it is a root node)
	 *
	 * @param node
	 * @return
	 */
	public synchronized T getParent(T childNode) {
		return forest.getParent(childNode);
	}

	/**
	 * Returns true if the sitemap contains {@code uri}. Only the virtual page part of the URI is used, parameters are
	 * ignored
	 *
	 * @param uri
	 * @return
	 */
	public synchronized boolean hasUri(String uri) {
		NavigationState navigationState = uriHandler.navigationState(uri);
		return hasUri(navigationState);
	}

	/**
	 * Returns true if the sitemap contains the URI represented by virtual page part of {@code navigationState}.
	 *
	 * @param uri
	 * @return
	 */
	public synchronized boolean hasUri(NavigationState navigationState) {
		return uriMap.keySet().contains(navigationState.getVirtualPage());
	}

	/**
	 * Returns a {@link NavigationState} object representing the URI for the {@code node}
	 *
	 * @param node
	 * @return
	 */
	public synchronized NavigationState navigationState(T node) {
		return uriHandler.navigationState(uri(node));
	}

	@Override
	public synchronized String toString() {
		return forest.toString();
	}

	/**
	 * Returns a {@link PagePermission} object for {@code node}
	 *
	 * @param node
	 * @return
	 */
	public synchronized PagePermission pagePermission(T node) {
		return new PagePermission(navigationState(node));
	}

	/**
	 * Adds {@code node} to the {@link Sitemap}. {@code node} cannot be null
	 *
	 * @param node
	 */
	public synchronized void addNode(T node) {
		checkNotNull(node);
		addChild(null, node);
	}

	public synchronized void removeNode(T node) {
		String uri = uri(node);
		forest.removeNode(node);
		uriMap.remove(uri);
	}

	/**
	 * Returns the {@link SitemapNode} associated with {@code navigationState}, or the closest available if one cannot
	 * be found for the full URI. "Closest" means the node which matches the most segments of the URI. Returns null if
	 * no match at all is found
	 *
	 * @param navigationState
	 * @return
	 */
	public synchronized T nodeNearestFor(NavigationState navigationState) {
		List<String> segments = new ArrayList<>(navigationState.getPathSegments());
		T node = null;
		Joiner joiner = Joiner.on("/");
		while ((segments.size() > 0) && (node == null)) {
			String path = joiner.join(segments);
			node = uriMap.get(path);
			segments.remove(segments.size() - 1);
		}
		return node;
	}

	/**
	 * Returns the {@link SitemapNode} associated with {@code uri}, or the closest available if one cannot be found for
	 * the full URI. "Closest" means the node which matches the most segments of the URI. Returns null if no match at
	 * all is found
	 *
	 * @param uri
	 * @return
	 */
	public synchronized T nodeNearestFor(String uri) {
		return nodeNearestFor(uriHandler.navigationState(uri));
	}

	public synchronized String standardPageURI(StandardPageKey pageKey) {
		checkNotNull(pageKey);
		T sitemapNode = standardPages.get(pageKey);
		if (sitemapNode != null) {
			return uri(sitemapNode);
		} else {
			throw new SitemapException("No node found for StandardPageKey " + pageKey);
		}

	}

	public synchronized T standardPageNode(StandardPageKey pageKey) {
		return standardPages.get(pageKey);
	}

	public synchronized ImmutableMap<StandardPageKey, T> getStandardPages() {
		return ImmutableMap.copyOf(standardPages);
	}

	protected abstract T createNode(String segment);

	private T findNodeBySegment(List<T> nodes, String segment, boolean createIfAbsent) {
		T foundNode = null;
		for (T node : nodes) {
			if (node.getUriSegment().equals(segment)) {
				foundNode = node;
				break;
			}
		}

		if ((foundNode == null) && (createIfAbsent)) {
			foundNode = createNode(segment);
		}
		return foundNode;
	}

	/**
	 * Returns a list of {@link SitemapNode} matching the {@code segments} provided. If there is an incomplete match (a
	 * segment cannot be found) then:
	 * <ol>
	 * <li>if {@code allowPartialPath} is true a list of nodes is returned correct to the longest path possible.
	 * <li>if {@code allowPartialPath} is false an empty list is returned
	 *
	 * @param segments
	 * @return
	 */

	public synchronized List<T> nodeChainForSegments(List<String> segments, boolean allowPartialPath) {
		List<T> nodeChain = new ArrayList<>();
		int i = 0;
		String currentSegment = null;
		List<T> nodes = forest.getRoots();
		boolean segmentNotFound = false;
		T node = null;
		while ((i < segments.size()) && (!segmentNotFound)) {
			currentSegment = segments.get(i);
			node = findNodeBySegment(nodes, currentSegment, false);
			if (node != null) {
				nodeChain.add(node);
				nodes = forest.getChildren(node);
				i++;
			} else {
				segmentNotFound = true;
			}

		}
		if (segmentNotFound && !allowPartialPath) {
			nodeChain.clear();
		}
		return nodeChain;
	}

	/**
	 * Delegates to {@link BasicForest#getChildren(Object)}
	 *
	 * @param newParentNode
	 * @return
	 */
	public synchronized List<T> getChildren(T parentNode) {
		return forest.getChildren(parentNode);

	}

	/**
	 * Returns the {@link SitemapNode} associated with {@code uri}, or null if none found
	 *
	 * @param uri
	 * @return
	 */
	public synchronized T nodeFor(String uri) {
		return uriMap.get(uriHandler.navigationState(uri).getVirtualPage());
	}

	/**
	 * Returns the {@link SitemapNode} associated with {@code navigationState}, or null if none found
	 *
	 * @param navigationState
	 * @return
	 */
	public synchronized T nodeFor(NavigationState navigationState) {
		if (navigationState == null) {
			return null;
		}
		return uriMap.get(navigationState.getVirtualPage());
	}

	/**
	 * returns a list of {@link SitemapNode} matching the virtual page of the {@code navigationState} provided. Uses the
	 * {@link URIFragmentHandler} to get URI path segments and {@link Sitemap} to obtain the node chain.
	 * {@code allowPartialPath} determines how a partial match is handled (see
	 * {@link Sitemap#nodeChainForSegments(List, boolean)} javadoc
	 *
	 * @param uri
	 * @return
	 */
	public synchronized List<T> nodeChainFor(NavigationState navigationState, boolean allowPartialPath) {
		List<String> segments = navigationState.getPathSegments();
		List<T> nodeChain = nodeChainForSegments(segments, allowPartialPath);
		return nodeChain;
	}

	/**
	 * Returns a redirect for sourceNode if there is one, null if there is not. Allows for multiple levels of redirect
	 *
	 * @return
	 */
	public synchronized T getRedirectNodeFor(T sourceNode) {
		String sourceUri = uri(sourceNode);

		String redirectPageFor = getRedirectPageFor(sourceUri);
		return nodeFor(redirectPageFor);
	}

	/**
	 * If the {@code page} has been redirected, return the page it has been redirected to, otherwise, just return
	 * {@code page}. Allows for multiple levels of redirect
	 *
	 * @param page
	 * @return
	 */
	public synchronized String getRedirectPageFor(String page) {

		String p = redirects.get(page);
		if (p == null) {
			return page;
		}
		String p1 = null;
		while (p != null) {
			p1 = p;
			p = redirects.get(p1);
		}

		return p1;
	}

	/**
	 * Safe copy of redirects
	 *
	 * @return
	 */
	public synchronized ImmutableMap<String, String> getRedirects() {
		return ImmutableMap.copyOf(redirects);

	}

	/**
	 * Adds a redirect from {@code fromPage} to {@code toPage}. No checking is done of the validity or structure of the
	 * parameters. {@code toPage} is not checked for existence within the map, this is done by the
	 * {@link SitemapChecker} once assembly of the {@link MasterSitemap} is complete
	 *
	 * @param fromPage
	 * @param toPage
	 * @return
	 */
	public synchronized Sitemap<T> addRedirect(String fromPage, String toPage) {
		redirects.put(fromPage, toPage);
		return this;
	}

	/**
	 * Returns a safe copy of all the URIs contained in the sitemap.
	 *
	 * @return
	 */
	public synchronized ImmutableList<String> uris() {
		return ImmutableList.copyOf(uriMap.keySet());
	}

	public synchronized int getNodeCount() {
		return forest.getNodeCount();
	}

	/**
	 * returns a list of {@link SitemapNode} matching the virtual page of the {@code navigationState} provided. Uses the
	 * {@link URIFragmentHandler} to get URI path segments and {@link Sitemap} to obtain the node chain.
	 * {@code allowPartialPath} determines how a partial match is handled (see
	 * {@link Sitemap#nodeChainForSegments(List, boolean)} javadoc
	 *
	 * @param uri
	 * @return
	 */
	public synchronized List<T> nodeChainForUri(String uri, boolean allowPartialPath) {
		return nodeChainFor(uriHandler.navigationState(uri), allowPartialPath);
	}

	/**
	 * Returns a list of nodes which form the chain from this {@code node} to its root in the {@link Sitemap}. The list
	 * includes {@code node}
	 *
	 * @param node
	 * @return
	 */
	public synchronized List<T> nodeChainFor(T node) {
		List<T> nodes = new ArrayList<>();
		nodes.add(node);
		T parent = this.getParent(node);
		while (parent != null) {
			nodes.add(0, parent);
			parent = this.getParent(parent);
		}
		return nodes;
	}

	/**
	 * If the virtual page represented by {@code navigationState} has been redirected, return the page it has been
	 * redirected to, otherwise, just return the virtual page unchanged. Allows for multiple levels of redirect.
	 *
	 * @param browserPage
	 * @return
	 */
	public synchronized String getRedirectPageFor(NavigationState navigationState) {
		String virtualPage = navigationState.getVirtualPage();
		return getRedirectPageFor(virtualPage);
	}

	/**
	 * Adds the {@code childNode} to the {@code parentNode}. If either of the nodes do not currently exist in the
	 * {@link Sitemap} they will be added to it.
	 * <p>
	 * The node id is set to {@link #nextNodeId()} for any node which is not already in the Sitemap
	 * <p>
	 * {@code parentNode} may be null<br>
	 * {@code childNode} cannot be null
	 *
	 * @param parentNode
	 * @param childNode
	 */
	public synchronized void addChild(T parentNode, T childNode) {
		checkNotNull(childNode);
		if ((parentNode != null) && (!containsNode(parentNode))) {
			forest.addNode(parentNode);
			String newUri = uri(parentNode);
			setId(parentNode);
			uriMap.put(newUri, parentNode);
		}

		// remove the child node - it may be moving from one parent to another
		if (containsNode(childNode)) {
			removeNode(childNode);
		}

		setId(childNode);

		// add it to structure first, otherwise the uri will be wrong
		forest.addChild(parentNode, childNode);
		uriMap.put(uri(childNode), childNode);

	}

	protected abstract void setId(T node);

	public BasicForest<T> getForest() {
		return forest;
	}

	public void addStandardPage(StandardPageKey pageKey, T node) {
		standardPages.put(pageKey, node);
	}

	public void clear() {
		forest.clear();
		standardPages.clear();
		redirects.clear();
		loaded = false;
		log.debug("sitemap cleared");
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

}
