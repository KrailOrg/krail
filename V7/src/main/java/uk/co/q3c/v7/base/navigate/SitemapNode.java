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

import java.text.CollationKey;
import java.text.Collator;
import java.util.Locale;

import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKey;

/**
 * Represents a node in the site map (equivalent to a web site 'page'). It contains a URI segment (this is just one part
 * of the URI, so the node for the page at /private/account/open would contain just 'open'). To obtain the full URI, use
 * {@link Sitemap#uri(SitemapNode)}.
 * <p>
 * {@link #viewClass} is the class of {@link V7View} to be used in displaying the page, and the {@link #getLabelKey()}
 * is an {@link I18NKey} key to a localised label for the page
 * <p>
 * The {@link #id} is required because the URI segment alone may not be unique, and the view class and labelKey are
 * optional. For the node to be used in a graph, it needs a unique identifier. The id is provided by
 * {@link Sitemap#addChild(SitemapNode, SitemapNode)} and {@link Sitemap#addNode(SitemapNode)}. This field has an
 * additional purpose in providing a record of insertion order, so that nodes can be sorted by insertion order if
 * required.
 * <p>
 * To enable locale sensitive sorting of nodes - for example within a UserNavigationTree - a collation key from
 * {@link Collator} is added by the {@link #setLabelKey(I18NKey, Locale, Collator)} method. This means the collation
 * key generally created only once, is available for sorting as often as needed, and will only need to be updated when
 * if locale or labelKey changes. This approach also takes advantage of the improved performance of the collation key
 * sorting (http://docs.oracle.com/javase/tutorial/i18n/text/perform.html)
 * <p>
 * Sorting by insertion order or collation key order is provided by
 * 
 * @author David Sowerby 6 May 2013
 * 
 */
public class SitemapNode {

	private int id;
	private String uriSegment;
	private Class<? extends V7View> viewClass;
	private I18NKey<?> labelKey;
	private String label;
	private CollationKey collationKey;

	public SitemapNode(String uriSegment, Class<? extends V7View> viewClass, I18NKey<?> labelKey, Locale locale,
			Collator collator) {
		super();
		this.uriSegment = uriSegment;
		this.viewClass = viewClass;
		setLabelKey(labelKey, locale, collator);
	}

	public SitemapNode() {

	}

	public String getUriSegment() {
		return uriSegment;
	}

	public void setUriSegment(String uriSegment) {
		this.uriSegment = uriSegment;
	}

	public I18NKey<?> getLabelKey() {
		return labelKey;
	}

	/**
	 * Sets the label key, but also requires the locale to enable translation for the label, and Collator for the
	 * collation key
	 * 
	 * @param labelKey
	 * @param locale
	 */
	public void setLabelKey(I18NKey<?> labelKey, Locale locale, Collator collator) {
		this.labelKey = labelKey;
		label = labelKey.getValue(locale);
		collationKey = collator.getCollationKey(label);
	}

	public Class<? extends V7View> getViewClass() {
		return viewClass;
	}

	public void setViewClass(Class<? extends V7View> viewClass) {
		this.viewClass = viewClass;
	}

	public String toStringAsMapEntry() {
		StringBuilder buf = new StringBuilder();
		buf.append((uriSegment == null) ? "no segment given" : uriSegment);
		buf.append((viewClass == null) ? "" : "\t\t:  " + viewClass.getSimpleName());
		buf.append((labelKey == null) ? "" : "\t~  " + ((Enum<?>) labelKey).name());
		return buf.toString();

	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("segment=");
		buf.append((uriSegment == null) ? "null" : uriSegment);
		buf.append(", viewClass=");
		buf.append((viewClass == null) ? "null" : viewClass.getName());
		buf.append(", labelKey=");
		buf.append((labelKey == null) ? "null" : ((Enum<?>) labelKey).name());
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

	public String getLabel() {
		return label;
	}

	public CollationKey getCollationKey() {
		return collationKey;
	}

}
