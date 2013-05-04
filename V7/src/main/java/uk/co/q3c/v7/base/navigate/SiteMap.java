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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.co.q3c.util.BasicForest;

public class SiteMap extends BasicForest<SiteMapNode> {

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

}
