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

import java.text.CollationKey;
import java.text.Collator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.common.collect.ImmutableList;

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
 * {@link Collator} is added by the {@link #setLabelKey(I18NKey, Locale, Collator)} method. This means the collation key
 * is generally created only once, but is available for sorting as often as needed. The collation key will only need to
 * be updated if locale or labelKey changes. This approach also takes advantage of the improved performance of the
 * collation key sorting (http://docs.oracle.com/javase/tutorial/i18n/text/perform.html)
 * <p>
 * The type of user access control applied to the page is determined by {@link #pageAccessControl}. Note that these are
 * mutually exclusive, so a page cannot require both roles and permissions. This control is applied by the
 * {@link V7Navigator} during page changes, thereby disallowing access to an authorised page.
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
	private Translate translate;
	private PageAccessControl pageAccessControl;
	/**
	 * Contains roles required to access this page, but is not used unless {@link #pageAccessControl} is
	 * {@link PageAccessControl#ROLES}
	 */
	private final Set<String> roles = new HashSet<>();

	public SitemapNode(String uriSegment, Class<? extends V7View> viewClass, I18NKey<?> labelKey, Locale locale,
			Collator collator, Translate translate) {
		super();
		this.uriSegment = uriSegment;
		this.viewClass = viewClass;
		this.translate = translate;
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
	 * Sets {@link LabelKey} and {@link #label}, but also requires the locale to enable translation for the label (using
	 * {@link #translate} field, and Collator for the collation key
	 * 
	 * @param labelKey
	 * @param locale
	 */
	public void setLabelKey(I18NKey<?> labelKey, Locale locale, Collator collator) {
		this.labelKey = labelKey;
		label = translate.from(labelKey, locale);
		collationKey = collator.getCollationKey(label);
	}

	/**
	 * Sets {@link LabelKey} and {@link #label}, using supplied {@code translate} for translation and Collator for the
	 * collation key. CurrentLocale is assumed.
	 * 
	 * @param labelKey
	 * @param locale
	 */
	public void setLabelKey(I18NKey<?> labelKey, Translate translate, Collator collator) {
		this.labelKey = labelKey;
		label = translate.from(labelKey);
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
		buf.append(", permissions=");
		if (roles.isEmpty()) {
			buf.append("none");
		} else {
			boolean first = true;
			for (String permission : roles) {
				if (!first) {
					buf.append(';');
				}
				buf.append('[');
				buf.append(permission);
				buf.append(']');
				first = false;
			}
		}
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

	public Translate getTranslate() {
		return translate;
	}

	public void setTranslate(Translate translate) {
		this.translate = translate;
	}

	public boolean isPublicPage() {
		return pageAccessControl == PageAccessControl.PUBLIC;
	}

	/**
	 * Adds a role. Only relevant if {@link #pageAccessControl} is {@link PageAccessControl#ROLES}
	 */
	public void addRole(String role) {
		if (StringUtils.isNotEmpty(role)) {
			roles.add(role);
		}
	}

	public boolean hasPermissions() {
		return !roles.isEmpty();
	}

	public PageAccessControl getPageAccessControl() {
		return pageAccessControl;
	}

	public void setPageAccessControl(PageAccessControl pageAccessControl) {
		this.pageAccessControl = pageAccessControl;

	}

	public List<String> getRoles() {
		return ImmutableList.copyOf(roles);
	}

}
