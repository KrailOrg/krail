/*
 * Copyright (C) 2013 David Sowerby
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

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.co.q3c.util.BasicForest;
import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.shiro.PagePermission;
import uk.co.q3c.v7.i18n.Translate;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Encapsulates the site layout. Individual "virtual pages" are represented by {@link SitemapNode} instances. This map
 * is built by one or more implementations of {@link SitemapLoader}, and is one of the fundamental building blocks of
 * the application, as it maps out pages, URIs and Views.
 * <p>
 * <p>
 * Because of it use as such a fundamental building block, an instance of this class has to be created early in the
 * application start up process. To avoid complex logic and dependencies within Guice modules, the building of the
 * {@link Sitemap} is managed by the {@link SitemapService}
 * <p>
 * Simple URI redirects can be added using {@link #addRedirect(String, String)}
 * <p>
 * If a duplicate entry is received (that is, a second entry for the same URI), the later entry will overwrite the
 * earlier entry
 * 
 * @see SitemapURIConverter
 * 
 * @author David Sowerby 19 May 2013
 * 
 */
@Singleton
public class Sitemap {

	private int nextNodeId = 0;
	private final Map<StandardPageKey, SitemapNode> standardPages = new HashMap<>();
	private String report;
	// Uses LinkedHashMap to retain insertion order
	private final Map<String, String> redirects = new LinkedHashMap<>();
	private final BasicForest<SitemapNode> forest;
	private final Map<String, SitemapNode> uriMap = new LinkedHashMap<>();
	private final URIFragmentHandler uriHandler;
	private final Translate translate;

	@Inject
	public Sitemap(URIFragmentHandler uriHandler, Translate translate) {
		super();
		this.uriHandler = uriHandler;
		this.translate = translate;
		forest = new BasicForest<>();
	}

	/**
	 * Returns the full URI for {@code node}
	 * 
	 * @param node
	 * @return
	 */
	public String uri(SitemapNode node) {
		checkNotNull(node);
		StringBuilder buf = new StringBuilder(node.getUriSegment());
		prependParent(node, buf);
		return buf.toString();
	}

	/**
	 * Recursively prepends the parent URI segment of {@code node}, until the full URI has been built
	 */
	private void prependParent(SitemapNode node, StringBuilder buf) {
		SitemapNode parentNode = forest.getParent(node);
		if (parentNode != null) {
			buf.insert(0, "/");
			buf.insert(0, parentNode.getUriSegment());
			prependParent(parentNode, buf);
		}
	}

	/**
	 * creates a SiteMapNode and appends it to the map according to the {@code navigationState} given, then returns it.
	 * If a node already exists at that location it is returned. If there are gaps in the structure, nodes are created
	 * to fill them (the same idea as forcing directory creation on a file path). An empty (not null) URI is allowed.
	 * This represents the site base URI without any further qualification.
	 * 
	 * 
	 * @param uri
	 * @return
	 */
	public SitemapNode append(NavigationState navigationState) {

		// if there is already a node for this navigation state, there is nothing to do, just return it
		if (hasUri(navigationState)) {
			return nodeFor(navigationState);
		}

		// take a copy to protect the parameter
		NavigationState navState = uriHandler.navigationState(navigationState.getFragment());

		// loop and remove the trailing segment each time until we find a matching node
		// or run out of segments
		List<String> segments = navState.getPathSegments();
		SitemapNode node = null;
		while ((segments.size() > 0) && (node == null)) {
			segments.remove(segments.size() - 1);
			String path = Joiner.on("/").join(segments);
			node = nodeFor(path);
		}

		// if we never found a matching node, we must be starting a new root, parent will be null
		// and the start index will be 0
		int startIndex = segments.size();

		// reset the segments
		segments = new ArrayList<>(navigationState.getPathSegments());

		SitemapNode parentNode = null;

		if (startIndex != 0) {
			parentNode = node;
		}

		SitemapNode childNode = null;
		for (int i = startIndex; i < segments.size(); i++) {
			String segment = segments.get(i);
			childNode = new SitemapNode();
			childNode.setUriSegment(segment);
			childNode.setTranslate(translate);
			addChild(parentNode, childNode);
			parentNode = childNode;
		}
		if (childNode.getLabelKey() instanceof StandardPageKey) {
			StandardPageKey spk = (StandardPageKey) childNode.getLabelKey();
			standardPages.put(spk, childNode);
		}
		return childNode;
	}

	public SitemapNode append(String uri) {
		return append(uriHandler.navigationState(uri));
	}

	private SitemapNode findNodeBySegment(List<SitemapNode> nodes, String segment, boolean createIfAbsent) {
		SitemapNode foundNode = null;
		for (SitemapNode node : nodes) {
			if (node.getUriSegment().equals(segment)) {
				foundNode = node;
				break;
			}
		}

		if ((foundNode == null) && (createIfAbsent)) {
			foundNode = new SitemapNode();
			foundNode.setUriSegment(segment);

		}
		return foundNode;
	}

	/**
	 * Adds the {@code childNode} to the {@code parentNode}. If either of the nodes do not currently exist in the
	 * {@link Sitemap} they will be added to it.
	 * <p>
	 * The node id is set to {@link #nextNodeId()} for any node which is not already in the Sitemap
	 * 
	 * @param parentNode
	 * @param childNode
	 */
	public void addChild(SitemapNode parentNode, SitemapNode childNode) {
		checkNotNull(childNode);
		if ((parentNode != null) && (!containsNode(parentNode))) {
			forest.addNode(parentNode);
			String newUri = uri(parentNode);
			parentNode.setId(nextNodeId());
			uriMap.put(newUri, parentNode);
		}

		// remove the child node - it may be moving from one parent to another
		if (containsNode(childNode)) {
			removeNode(childNode);
		}

		childNode.setId(nextNodeId());

		// add it to structure first, otherwise the uri will be wrong
		forest.addChild(parentNode, childNode);
		uriMap.put(uri(childNode), childNode);

	}

	/**
	 * gets what would be the full URI for {@code childNode} if it were attached to {@code parentNode}, without actually
	 * adding the node to the {@link Sitemap}
	 * 
	 * @param node
	 * @return
	 */
	// private String provisionalUri(SitemapNode parentNode, SitemapNode childNode) {
	// StringBuilder buf = new StringBuilder(childNode.getUriSegment());
	// prependParent(parentNode, buf);
	// return buf.toString();
	// }

	private void removeNode(SitemapNode node) {
		String uri = uri(node);
		forest.removeNode(node);
		uriMap.remove(uri);
	}

	public String standardPageURI(StandardPageKey pageKey) {
		checkNotNull(pageKey);
		SitemapNode sitemapNode = standardPages.get(pageKey);
		if (sitemapNode != null) {
			return uri(sitemapNode);
		} else {
			throw new SitemapException("No node found for StandardPageKey " + pageKey);
		}

	}

	public SitemapNode standardPageNode(StandardPageKey pageKey) {
		return standardPages.get(pageKey);
	}

	private int nextNodeId() {
		nextNodeId++;
		return nextNodeId;
	}

	public ImmutableMap<StandardPageKey, SitemapNode> getStandardPages() {
		return ImmutableMap.copyOf(standardPages);
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getReport() {
		return report;
	}

	/**
	 * If the {@code page} has been redirected, return the page it has been redirected to, otherwise, just return
	 * {@code page}. Allows for multiple levels of redirect
	 * 
	 * @param page
	 * @return
	 */
	public String getRedirectPageFor(String page) {

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
	 * If the virtual page represented by {@code navigationState} has been redirected, return the page it has been
	 * redirected to, otherwise, just return the virtual page unchanged. Allows for multiple levels of redirect.
	 * 
	 * @param page
	 * @return
	 */
	public String getRedirectPageFor(NavigationState navigationState) {
		String virtualPage = navigationState.getVirtualPage();
		return getRedirectPageFor(virtualPage);
	}

	/**
	 * Safe copy of redirects
	 * 
	 * @return
	 */
	public ImmutableMap<String, String> getRedirects() {
		return ImmutableMap.copyOf(redirects);

	}

	public Sitemap addRedirect(String fromPage, String toPage) {
		redirects.put(fromPage, toPage);
		return this;
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

	public List<SitemapNode> nodeChainForSegments(List<String> segments, boolean allowPartialPath) {
		List<SitemapNode> nodeChain = new ArrayList<>();
		int i = 0;
		String currentSegment = null;
		List<SitemapNode> nodes = forest.getRoots();
		boolean segmentNotFound = false;
		SitemapNode node = null;
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
	 * Returns a safe copy of all the URIs contained in the sitemap.
	 * 
	 * @return
	 */
	public ImmutableList<String> uris() {
		return ImmutableList.copyOf(uriMap.keySet());
	}

	/**
	 * Returns true if the sitemap contains {@code uri}. Only the virtual page part of the URI is used, parameters are
	 * ignored
	 * 
	 * @param uri
	 * @return
	 */
	public boolean hasUri(String uri) {
		NavigationState navigationState = uriHandler.navigationState(uri);
		return hasUri(navigationState);
	}

	/**
	 * Returns true if the sitemap contains the URI represented by virtual page part of {@code navigationState}.
	 * 
	 * @param uri
	 * @return
	 */
	public boolean hasUri(NavigationState navigationState) {
		return uriMap.keySet().contains(navigationState.getVirtualPage());
	}

	public int getNodeCount() {
		return forest.getNodeCount();
	}

	/**
	 * Returns the parent of {@code node}. Will be null if {@code node} has no parent (that is, it is a root node)
	 * 
	 * @param node
	 * @return
	 */
	public SitemapNode getParent(SitemapNode node) {
		return forest.getParent(node);
	}

	/**
	 * Delegates to {@link BasicForest#getRoots()}
	 * 
	 * @return
	 */
	public List<SitemapNode> getRoots() {
		return forest.getRoots();
	}

	/**
	 * Delegates to {@link BasicForest#getRootFor(Object)}
	 * 
	 * @param node
	 * @return
	 */
	public SitemapNode getRootFor(SitemapNode node) {
		return forest.getRootFor(node);
	}

	/**
	 * Delegates to {@link BasicForest#getChildCount(Object)}
	 * 
	 * @param node
	 * @return
	 */

	public int getChildCount(SitemapNode node) {
		return forest.getChildCount(node);
	}

	/**
	 * Delegates to {@link BasicForest#getAllNodes()}
	 * 
	 * @return
	 */
	public List<SitemapNode> getAllNodes() {
		return forest.getAllNodes();
	}

	/**
	 * Delegates to {@link BasicForest#getChildren(Object)}
	 * 
	 * @param newParentNode
	 * @return
	 */
	public List<SitemapNode> getChildren(SitemapNode parentNode) {
		return forest.getChildren(parentNode);

	}

	/**
	 * Delegates to {@link BasicForest#containsNode(Object)}
	 * 
	 * @param newParentNode
	 * @return
	 */
	public boolean containsNode(SitemapNode node) {
		return forest.containsNode(node);
	}

	/**
	 * Returns the {@link SitemapNode} associated with {@code uri}, or null if none found
	 * 
	 * @param uri
	 * @return
	 */
	public SitemapNode nodeFor(String uri) {
		return uriMap.get(uriHandler.navigationState(uri).getVirtualPage());
	}

	/**
	 * Returns the {@link SitemapNode} associated with {@code navigationState}, or null if none found
	 * 
	 * @param navigationState
	 * @return
	 */
	public SitemapNode nodeFor(NavigationState navigationState) {
		return uriMap.get(navigationState.getVirtualPage());
	}

	/**
	 * Returns the {@link SitemapNode} associated with {@code uri}, or the closest available if one cannot be found for
	 * the full URI. "Closest" means the node which matches the most segments of the URI. Returns null if no match at
	 * all is found
	 * 
	 * @param uri
	 * @return
	 */
	public SitemapNode nodeNearestFor(String uri) {
		return nodeNearestFor(uriHandler.navigationState(uri));
	}

	/**
	 * Returns the {@link SitemapNode} associated with {@code navigationState}, or the closest available if one cannot
	 * be found for the full URI. "Closest" means the node which matches the most segments of the URI. Returns null if
	 * no match at all is found
	 * 
	 * @param navigationState
	 * @return
	 */
	public SitemapNode nodeNearestFor(NavigationState navigationState) {
		List<String> segments = new ArrayList<>(navigationState.getPathSegments());
		SitemapNode node = null;
		Joiner joiner = Joiner.on("/");
		while ((segments.size() > 0) && (node == null)) {
			String path = joiner.join(segments);
			node = uriMap.get(path);
			segments.remove(segments.size() - 1);
		}
		return node;
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
	public List<SitemapNode> nodeChainForUri(String uri, boolean allowPartialPath) {
		return nodeChainFor(uriHandler.navigationState(uri), allowPartialPath);
	}

	/**
	 * Returns a list of nodes which form the chain from this {@code node} to its root in the {@link Sitemap}. The list
	 * includes {@code node}
	 * 
	 * @param node
	 * @return
	 */
	public List<SitemapNode> nodeChainFor(SitemapNode node) {
		List<SitemapNode> nodes = new ArrayList<>();
		nodes.add(node);
		SitemapNode parent = this.getParent(node);
		while (parent != null) {
			nodes.add(0, parent);
			parent = this.getParent(parent);
		}
		return nodes;
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
	public List<SitemapNode> nodeChainFor(NavigationState navigationState, boolean allowPartialPath) {
		List<String> segments = navigationState.getPathSegments();
		List<SitemapNode> nodeChain = nodeChainForSegments(segments, allowPartialPath);
		return nodeChain;
	}

	public void addStandardPage(StandardPageKey pageKey, SitemapNode node) {
		standardPages.put(pageKey, node);
	}

	@Override
	public String toString() {
		return forest.toString();
	}

	/**
	 * Returns a {@link NavigationState} object representing the URI for the {@code node}
	 * 
	 * @param node
	 * @return
	 */
	public NavigationState navigationState(SitemapNode node) {
		return uriHandler.navigationState(uri(node));
	}

	/**
	 * Returns a {@link PagePermission} object for {@code node}
	 * 
	 * @param node
	 * @return
	 */
	public PagePermission pagePermission(SitemapNode node) {
		return new PagePermission(navigationState(node));
	}

	/**
	 * Returns a redirect for sourceNode if there is one, null if there is not. Allows for multiple levels of redirect
	 * 
	 * @return
	 */
	public SitemapNode getRedirectNodeFor(SitemapNode sourceNode) {
		String sourceUri = uri(sourceNode);

		String redirectPageFor = getRedirectPageFor(sourceUri);
		return nodeFor(redirectPageFor);
	}

}
