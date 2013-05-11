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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import uk.co.q3c.util.BasicForest;

public class Sitemap extends BasicForest<SiteMapNode> {

	private int nextNodeId = 0;
	private int errors = 0;
	private final Map<StandardPageKeys, String> standardPages = new HashMap<>();
	private String report;
	private final Map<String, String> redirects = new HashMap<>();

	public String url(SiteMapNode node) {
		StringBuilder buf = new StringBuilder(node.getUrlSegment());
		prependParent(node, buf);
		return buf.toString();
	}

	private void prependParent(SiteMapNode node, StringBuilder buf) {
		SiteMapNode parentNode = getParent(node);
		if (parentNode != null) {
			buf.insert(0, "/");
			buf.insert(0, parentNode.getUrlSegment());
			prependParent(parentNode, buf);
		}
	}

	/**
	 * creates a SiteMapNode and appends it to the map according to the {@code url} given, then returns it. If a node
	 * already exists at that location it is returned. If there are gaps in the structure, nodes are created to fill
	 * them (the same idea as forcing directory creation on a file path)
	 * 
	 * @param toUrl
	 * @return
	 */
	public SiteMapNode append(String url) {
		SiteMapNode node = null;
		String[] segments = StringUtils.split(url, "/");
		List<SiteMapNode> nodes = getRoots();
		SiteMapNode parentNode = null;
		for (int i = 0; i < segments.length; i++) {
			node = findNodeBySegment(nodes, segments[i], true);
			addChild(parentNode, node);
			nodes = getChildren(node);
			parentNode = node;
		}

		return node;
	}

	private SiteMapNode findNodeBySegment(List<SiteMapNode> nodes, String segment, boolean createIfAbsent) {
		SiteMapNode foundNode = null;
		for (SiteMapNode node : nodes) {
			if (node.getUrlSegment().equals(segment)) {
				foundNode = node;
				break;
			}
		}

		if ((foundNode == null) && (createIfAbsent)) {
			foundNode = new SiteMapNode();
			foundNode.setUrlSegment(segment);

		}
		return foundNode;
	}

	@Override
	public void addNode(SiteMapNode node) {
		if (node.getId() == 0) {
			node.setId(nextNodeId());
		}
		super.addNode(node);
	}

	@Override
	public void addChild(SiteMapNode parentNode, SiteMapNode childNode) {
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

	public String standardPageURI(StandardPageKeys pageKey) {
		return standardPages.get(pageKey);
	}

	private int nextNodeId() {
		nextNodeId++;
		return nextNodeId;
	}

	public Map<StandardPageKeys, String> getStandardPages() {
		return standardPages;
	}

	public boolean hasErrors() {
		return errors > 0;
	}

	public int getErrors() {
		return errors;
	}

	public void error() {
		errors++;
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
		if (redirects.containsKey(page)) {
			return redirects.get(page);
		} else {
			return page;
		}
	}

	public Map<String, String> getRedirects() {
		return redirects;
	}

}
