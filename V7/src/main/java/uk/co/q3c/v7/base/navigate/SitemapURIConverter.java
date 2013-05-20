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

	private Sitemap sitemap;
	private URIFragmentHandler uriHandler;
	private V7Navigator navigator;

	/**
	 * returns a list of {@link SitemapNode} matching the virtual page of the {@code navigationState} provided. If
	 * {@code navigationState} is invalid (does not match the site map) then nodes are matched as far as possible along
	 * the chain, and that result returned
	 * 
	 * @param navigationState
	 * @return
	 */
	public List<SitemapNode> nodeChainForUri(String navigationState) {
		String navState = navigator.getNavigationState();
		uriHandler.setFragment(navState);
		List<String> segments = uriHandler.getPathSegments();
		List<SitemapNode> nodeChain = sitemap.nodeChainForSegments(segments);
		return nodeChain;
	}

	public boolean hasNodeForUri(String navigationState) {
		return nodeForUri(navigationState) != null;
	}

	/**
	 * Returns the last node in the chain of {@link SitemapNode} representing {@code navigationState}. The chain is
	 * provided by {@link #nodeChainForUri(String)}, and this method just returns the last node in that chain.
	 * 
	 * @param navigationState
	 * @return
	 */
	public SitemapNode nodeForUri(String navigationState) {
		List<SitemapNode> nodeChain = nodeChainForUri(navigationState);
		return nodeChain.get(nodeChain.size() - 1);
	}

}
