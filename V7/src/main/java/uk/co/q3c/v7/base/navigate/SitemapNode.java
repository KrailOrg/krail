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

import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKeys;

/**
 * Represents a node in the site map (equivalent to a web site 'page'). It contains a url segment (this is just one part
 * of the url, so the node for the page at /secure/account/open would contain just 'open'). To obtain the full url, use
 * {@link Sitemap#url(SitemapNode)}.
 * <p>
 * {@link #viewClass} is the class of {@link V7View} to be used in displaying the page, and the {@link #getLabelKey()}
 * is an {@link I18NKeys} key to a localised label for the page
 * <p>
 * The {@link #id} is required because the url segment alone may not be unique, and the view class and labelKey are
 * optional. For the node to be used in a graph, it needs a unique identifier. The id is provided by
 * {@link Sitemap#addChild(SitemapNode, SitemapNode)} and {@link Sitemap#addNode(SitemapNode)}
 * 
 * @author David Sowerby 6 May 2013
 * 
 */
public class SitemapNode {

	private int id;
	private String urlSegment;
	private Class<? extends V7View> viewClass;
	private Enum<? extends I18NKeys<?>> labelKey;

	public SitemapNode(String urlSegment, Class<? extends V7View> viewClass, Enum<? extends I18NKeys<?>> labelKey) {
		super();
		this.urlSegment = urlSegment;
		this.viewClass = viewClass;
		this.labelKey = labelKey;
	}

	public SitemapNode() {

	}

	public String getUrlSegment() {
		return urlSegment;
	}

	public void setUrlSegment(String urlSegment) {
		this.urlSegment = urlSegment;
	}

	public Enum<? extends I18NKeys<?>> getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(Enum<? extends I18NKeys<?>> labelKey) {
		this.labelKey = labelKey;
	}

	public Class<? extends V7View> getViewClass() {
		return viewClass;
	}

	public void setViewClass(Class<? extends V7View> viewClass) {
		this.viewClass = viewClass;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append((urlSegment == null) ? "no segment given" : urlSegment);
		buf.append((viewClass == null) ? "" : "\t\t:  " + viewClass.getSimpleName());
		buf.append((labelKey == null) ? "" : "\t~  " + labelKey.name());
		return buf.toString();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SitemapNode other = (SitemapNode) obj;
		return id == other.id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
