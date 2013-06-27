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
package uk.co.q3c.v7.base.navigate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import uk.co.q3c.util.BasicForest;

/**
 * Encapsulates the site layout. Individual "virtual pages" are represented by {@link SitemapNode} instances. This map
 * is usually built by an implementation of {@link SitemapProvider}, and is one of the fundamental building blocks of
 * the application, as it maps out pages, URIs and Views.
 * <p>
 * <p>
 * Because of it use as such a fundamental building block, an instance of this class has to be created early in the
 * application start up process. It is better therefore not to introduce dependencies into this class, otherwise the
 * design, and ordering of construction, of Guice modules starts to get complicated.
 * <p>
 * <p>
 * A potential solution for dependencies can be seen in {@link SitemapURIConverter}, which acts as an intermediary
 * between this class and {@link URIFragmentHandler} implementations, thus avoiding the creation of dependencies here.
 * <p>
 * <p>
 * Uses LinkedHashMap to hold the site map itself, to retain insertion order<br>
 * 
 * @author David Sowerby 19 May 2013
 * 
 */
public class Sitemap extends BasicForest<SitemapNode> {

	private int nextNodeId = 0;
	private int errors = 0;
	private final Map<StandardPageKey, String> standardPages = new HashMap<>();
	private String report;
	// Uses LinkedHashMap to retain insertion order
	private final Map<String, String> redirects = new LinkedHashMap<>();

	public String uri(SitemapNode node) {
		StringBuilder buf = new StringBuilder(node.getUriSegment());
		prependParent(node, buf);
		return buf.toString();
	}

	private void prependParent(SitemapNode node, StringBuilder buf) {
		SitemapNode parentNode = getParent(node);
		if (parentNode != null) {
			buf.insert(0, "/");
			buf.insert(0, parentNode.getUriSegment());
			prependParent(parentNode, buf);
		}
	}

	/**
	 * creates a SiteMapNode and appends it to the map according to the {@code uri} given, then returns it. If a node
	 * already exists at that location it is returned. If there are gaps in the structure, nodes are created to fill
	 * them (the same idea as forcing directory creation on a file path). An empty (not null) URI is allowed. This
	 * represents the site base URI without any further qualification.
	 * 
	 * @param uri
	 * @return
	 */
	public SitemapNode append(String uri) {

		if (uri.equals("")) {
			SitemapNode node = new SitemapNode();
			node.setUriSegment(uri);
			addNode(node);
			return node;
		}
		SitemapNode node = null;
		String[] segments = StringUtils.split(uri, "/");
		List<SitemapNode> nodes = getRoots();
		SitemapNode parentNode = null;
		for (int i = 0; i < segments.length; i++) {
			node = findNodeBySegment(nodes, segments[i], true);
			addChild(parentNode, node);
			nodes = getChildren(node);
			parentNode = node;
		}

		return node;
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

	@Override
	public void addNode(SitemapNode node) {
		if (node.getId() == 0) {
			node.setId(nextNodeId());
		}
		super.addNode(node);
	}

	@Override
	public void addChild(SitemapNode parentNode, SitemapNode childNode) {
		// super allows null parent
		if (parentNode != null) {
			if (parentNode.getId() == 0) {
				parentNode.setId(nextNodeId());
			}
		}
		if (childNode.getId() == 0) {
			childNode.setId(nextNodeId());
		}
		super.addChild(parentNode, childNode);
	}

	public String standardPageURI(StandardPageKey pageKey) {
		return standardPages.get(pageKey);
	}

	private int nextNodeId() {
		nextNodeId++;
		return nextNodeId;
	}

	public Map<StandardPageKey, String> getStandardPages() {
		return standardPages;
	}

	public boolean hasErrors() {
		return errors > 0;
	}

	public int getErrors() {
		return errors;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getReport() {
		return report;
	}

	/**
	 * If the {@code page} has been redirected, return the page it has been redirected to, otherwise, just return
	 * {@code page}
	 * 
	 * @param page
	 * @return
	 */
	public String getRedirectFor(String page) {
		String p = redirects.get(page);
		if (p == null) {
			return page;
		}
		return p;
	}

	public Map<String, String> getRedirects() {
		return redirects;
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
		List<SitemapNode> nodes = getRoots();
		boolean segmentNotFound = false;
		SitemapNode node = null;
		while ((i < segments.size()) && (!segmentNotFound)) {
			currentSegment = segments.get(i);
			node = findNodeBySegment(nodes, currentSegment, false);
			if (node != null) {
				nodeChain.add(node);
				nodes = getChildren(node);
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
	 * Returns a list of all the URIs contained in the sitemap. This is a fairly expensive call, as each URI has to be
	 * built from the node structure.
	 * 
	 * @return
	 */
	public List<String> uris() {
		List<String> list = new ArrayList<>();
		for (SitemapNode node : getAllNodes()) {
			list.add(uri(node));
		}
		return list;
	}

	/**
	 * Returns true if the sitemap contains {@code uri}. This is a fairly expensive call, as each URI has to be built
	 * from the node structure, before this method can be evaluated
	 * 
	 * @param uri
	 * @return
	 */
	public boolean hasUri(String uri) {
		List<String> list = uris();
		return list.contains(uri);
	}

	public void setErrors(int errorSum) {
		errors = errorSum;

	}
}
