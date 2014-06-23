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

import java.util.ArrayList;
import java.util.List;

import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Encapsulates the site layout. Individual "virtual pages" are represented by {@link MasterSitemapNode} instances. This
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
 * @see SitemapURIConverter
 *
 * @author David Sowerby 19 May 2013
 *
 */
@Singleton
public class DefaultMasterSitemap extends DefaultSitemapBase<MasterSitemapNode> implements MasterSitemap {

	private int nextNodeId = 0;

	private String report;

	@Inject
	public DefaultMasterSitemap(URIFragmentHandler uriHandler) {
		super(uriHandler);

	}

	/**
	 * creates a {@link MasterSitemapNode} and appends it to the map according to the {@code navigationState} given,
	 * then returns it. If a node already exists at that location it is returned. If there are gaps in the structure,
	 * nodes are created to fill them (the same idea as forcing directory creation on a file path). An empty (not null)
	 * URI is allowed. This represents the site base URI without any further qualification.
	 *
	 *
	 * @param uri
	 * @return
	 */
	@Override
	public MasterSitemapNode append(NavigationState navigationState) {

		// if there is already a node for this navigation state, there is nothing to do, just return it
		if (hasUri(navigationState)) {
			return nodeFor(navigationState);
		}

		// take a copy to protect the parameter
		NavigationState navState = uriHandler.navigationState(navigationState.getFragment());

		// loop and remove the trailing segment each time until we find a matching node
		// or run out of segments
		List<String> segments = navState.getPathSegments();
		MasterSitemapNode node = null;
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

		MasterSitemapNode parentNode = null;

		if (startIndex != 0) {
			parentNode = node;
		}

		MasterSitemapNode childNode = null;
		for (int i = startIndex; i < segments.size(); i++) {
			String segment = segments.get(i);
			childNode = new MasterSitemapNode();
			childNode.setUriSegment(segment);
			addChild(parentNode, childNode);
			parentNode = childNode;
		}
		if (childNode.getLabelKey() instanceof StandardPageKey) {
			StandardPageKey spk = (StandardPageKey) childNode.getLabelKey();
			standardPages.put(spk, childNode);
		}
		return childNode;
	}

	@Override
	public MasterSitemapNode append(String uri) {
		return append(uriHandler.navigationState(uri));
	}

	@Override
	protected MasterSitemapNode createNode(String segment) {
		MasterSitemapNode newNode = new MasterSitemapNode();
		newNode.setUriSegment(segment);
		return newNode;
	}

	private int nextNodeId() {
		nextNodeId++;
		return nextNodeId;
	}

	@Override
	public void setReport(String report) {
		this.report = report;
	}

	@Override
	public String getReport() {
		return report;
	}

	@Override
	protected void setId(MasterSitemapNode node) {
		node.setId(nextNodeId());
	}

}
