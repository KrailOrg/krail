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

import javax.inject.Inject;

import com.google.common.collect.Lists;

/**
 * Utility class to convert between URI fragments using {@link URIFragmentHandler} and {@link Sitemap}. This separate
 * class is used to
 * <p>
 * <ol>
 * <li>ensure there is no dependency on {@link Sitemap} within {@link URIFragmentHandler}
 * <li>reduce the complexity of Guice module creation order <br>
 * <br>
 * <p>
 * 
 * @author David Sowerby 19 May 2013
 * 
 */
public class SitemapURIConverter {

	private final Sitemap sitemap;
	private final URIFragmentHandler uriHandler;

	@Inject
	protected SitemapURIConverter(Sitemap sitemap, URIFragmentHandler uriHandler) {
		super();
		this.sitemap = sitemap;
		this.uriHandler = uriHandler;
	}

	/**
	 * returns a list of {@link SitemapNode} matching the virtual page of the {@code navigationState} provided. Uses the
	 * {@link URIFragmentHandler} to get URI path segments and {@link Sitemap} to obtain the node chain.
	 * {@code allowPartialPath} determines how a partial match is handled (see
	 * {@link Sitemap#nodeChainForSegments(List, boolean)} javadoc
	 * 
	 * @param navigationState
	 * @return
	 */
	public List<SitemapNode> nodeChainForUri(String navigationState, boolean allowPartialPath) {
		uriHandler.setFragment(navigationState);
		String[] segments = uriHandler.getPathSegments();
		List<SitemapNode> nodeChain = sitemap.nodeChainForSegments(Lists.newArrayList(segments), allowPartialPath);
		return nodeChain;
	}

	/**
	 * Returns true if a node is found for {@code navigationState}. If {@code allowPartialPath} is true, a node is
	 * considered found even if only a partial match for the {@code navigationState} is found. If
	 * {@code allowPartialPath} is false, then a full match must occur for true to be returned.
	 * 
	 * @param navigationState
	 * @param allowPartialPath
	 * @return
	 */
	public boolean hasNodeForUri(String navigationState, boolean allowPartialPath) {
		SitemapNode node = nodeForUri(navigationState, allowPartialPath);
		return node != null;
	}

	/**
	 * Returns the last node in the chain of {@link SitemapNode} representing {@code navigationState}. The chain is
	 * provided by {@link #nodeChainForUri(String)}, and this method just returns the last node in that chain. Returns
	 * null if no node found.
	 * <p>
	 * If {@code allowPartialPath} is true, a node is considered found even if only a partial match for the
	 * {@code navigationState} is found. In this case the last node in the match is returned. If
	 * {@code allowPartialPath} is false, then a full match must occur for a node to be returned.
	 * 
	 * @param navigationState
	 * @return
	 */
	public SitemapNode nodeForUri(String navigationState, boolean allowPartialPath) {
		List<SitemapNode> nodeChain = nodeChainForUri(navigationState, allowPartialPath);
		if (nodeChain.size() == 0) {
			return null;
		} else {
			return nodeChain.get(nodeChain.size() - 1);
		}
	}

	/**
	 * Returns true if the page within the supplied fragment is public (it can be viewed by unauthenticated users)
	 * 
	 * @param fragment
	 * @return
	 */
	public boolean pageIsPublic(String fragment) {

		return true;
	}

}
