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

public class SiteMapNode {

	private String urlSegment;
	private Class<? extends V7View> viewClass;
	private Enum<? extends I18NKeys<?>> labelKey;

	public SiteMapNode(String urlSegment, Class<? extends V7View> viewClass, Enum<? extends I18NKeys<?>> labelKey) {
		super();
		this.urlSegment = urlSegment;
		this.viewClass = viewClass;
		this.labelKey = labelKey;
	}

	public SiteMapNode() {

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
		result = prime * result + ((labelKey == null) ? 0 : labelKey.hashCode());
		result = prime * result + ((urlSegment == null) ? 0 : urlSegment.hashCode());
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
		SiteMapNode other = (SiteMapNode) obj;
		if (labelKey == null) {
			if (other.labelKey != null)
				return false;
		} else if (!labelKey.equals(other.labelKey))
			return false;
		if (urlSegment == null) {
			if (other.urlSegment != null)
				return false;
		} else if (!urlSegment.equals(other.urlSegment))
			return false;

		if (viewClass == null) {
			return other.viewClass == null;
		}

		if (viewClass.getName() == null) {
			if (other.viewClass.getName() != null)
				return false;
		} else if (!viewClass.getName().equals(other.viewClass.getName()))
			return false;

		return true;
	}
}
