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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.servicetag.UnauthorizedAccessException;

import uk.co.q3c.util.BasicForest;
import uk.co.q3c.v7.base.view.V7View;

/**
 * Encapsulates the site layout. Individual "virtual pages" are represented by
 * {@link SitemapNode} instances. This map is usually built by an implementation
 * of {@link SitemapProvider}, and is one of the fundamental building blocks of
 * the application, as it maps out pages, URIs and Views.
 * <p>
 * <p>
 * Because of it use as such a fundamental building block, an instance of this
 * class has to be created early in the application start up process. It is
 * better therefore not to introduce dependencies into this class, otherwise the
 * design, and ordering of construction, of Guice modules starts to get
 * complicated.
 * <p>
 * <p>
 * A potential solution for dependencies can be seen in
 * {@link SitemapURIConverter}, which acts as an intermediary between this class
 * and {@link URIFragmentHandler} implementations, thus avoiding the creation of
 * dependencies here.
 * <p>
 * <p>
 * Uses LinkedHashMap to hold the site map itself, to retain insertion order<br>
 * 
 * @author David Sowerby 19 May 2013
 * 
 */
@Singleton
public class Sitemap extends BasicForest<SitemapNode> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Sitemap.class);

	public static final String PATH_SEPARATOR = "/";

	public static String uri(SitemapNode parent, String segment) {
		if (parent != null) {
			return uri(parent.getUri(), segment);
		} else {
			return uri((String) null, segment);
		}
	}

	public static String uri(String parentUri, String segment) {
		if (parentUri != null) {
			return parentUri + PATH_SEPARATOR + segment;
		} else {
			return segment;
		}
	}

	private int errors = 0;
	private final Map<PageKey, SitemapNode> nodeKeys = new HashMap<>();
	private String report;
	// Uses LinkedHashMap to retain insertion order
	private final Map<String, String> uriRedirects = new LinkedHashMap<>();

	public SitemapNode addNode(String uri) {
		return addNode(null, uri);
	}

	/**
	 * creates a SiteMapNode and appends it to the map according to the
	 * {@code uri} given, then returns it. If a node already exists at that
	 * location it is returned. If there are gaps in the structure, nodes are
	 * created to fill them (the same idea as forcing directory creation on a
	 * file path). An empty (not null) URI is allowed. This represents the site
	 * base URI without any further qualification.
	 * 
	 * @param uri
	 * @return
	 */
	public SitemapNode addNode(PageKey key, String uri) {
		SitemapNode node = findNodeByUri(uri, true);
		if (key != null) {
			nodeKeys.put(key, node);
		}
		return node;
	}

	private SitemapNode findNodeByUri(String uri, boolean createIfAbsent) {
		if (uri == null) {
			assert uri != null;
		}

		String[] segments = StringUtils.split(uri, PATH_SEPARATOR);

		// bugfix: "".split("/") should return {""} but returns {}
		if (segments.length == 0) {
			segments = new String[] { "" };
		}

		int i = 0;

		SitemapNode parent = null;
		while (i < segments.length) {
			parent = findChildNode(parent, segments[i], createIfAbsent);
			if (parent == null) {
				assert createIfAbsent == false;
				return null;
			}
			i++;
		}
		return parent;
	}

	/**
	 * 
	 * @param parent
	 * @param segment
	 *            the full qualified uri
	 * @param createIfAbsent
	 * @return
	 */
	private SitemapNode findChildNode(SitemapNode parent, String segment,
			boolean createIfAbsent) {
		String searchedNode = uri(parent, segment);

		SitemapNode foundNode = null;
		List<SitemapNode> childrens = parent != null ? getChildren(parent)
				: getRoots();
		for (SitemapNode node : childrens) {
			if (node.getUri().equals(searchedNode)) {
				foundNode = node;
				break;
			}
		}
		if (foundNode == null) {
			if (createIfAbsent == true) {
				foundNode = new SitemapNode(this, searchedNode);
				addChild(parent, foundNode);
				return foundNode;
			} else {
				return null;
			}
		} else {
			return foundNode;
		}
	}

	public String pageUri(PageKey pageKey) {
		return nodeKeys.get(pageKey).getUri();
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
	 * If the {@code uri} has been redirected, return the uri it has been
	 * redirected to, otherwise, just return {@code uri}
	 * 
	 * @param uri
	 * @return
	 */
	public String getRedirectFor(String uri) {
		String p = uriRedirects.get(uri);
		return (p == null) ? uri : p;
	}

	public Map<String, String> getRedirects() {
		return uriRedirects;
	}

	public void addRedirect(String source, String target) {
		getRedirects().put(source, target);
	}

	/**
	 * Returns true if the sitemap contains {@code uri}.
	 * 
	 * @param uri
	 * @return
	 */
	public boolean hasUri(String uri) {
		return findNodeByUri(uri, false) != null;
	}

	public void setErrors(int errorSum) {
		errors = errorSum;
	}

	public void checkPermissions(String uri, Subject subject)
			throws NoSuchElementException, UnauthenticatedException,
			UnauthorizedException {
		SitemapNode node = findNodeByUri(uri, false);
		if (node != null) {
			node.checkPermissions(subject);
		} else {
			throw new NoSuchElementException(uri);
		}
	}

	public boolean hasPermissions(Subject subject, SitemapNode node) {
		try {
			checkPermissions(node.getUri(), subject);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a list of {@link SitemapNode} matching the {@code segments}
	 * provided. If there is an incomplete match (a segment cannot be found)
	 * then:
	 * <ol>
	 * <li>if {@code allowPartialPath} is true a list of nodes is returned
	 * correct to the longest path possible.
	 * <li>if {@code allowPartialPath} is false an empty list is returned
	 * 
	 * @param segments
	 * @return
	 */
	public List<SitemapNode> nodeChainForSegments(List<String> segments,
			boolean allowPartialPath) {
		List<SitemapNode> nodeChain = new ArrayList<>();
		int i = 0;
		String currentSegment = null;
		SitemapNode parent = null;
		boolean segmentNotFound = false;
		SitemapNode node = null;
		while ((i < segments.size()) && (!segmentNotFound)) {
			currentSegment = segments.get(i);
			node = findChildNode(parent, currentSegment, false);
			if (node != null) {
				nodeChain.add(node);
				parent = node;
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
}
