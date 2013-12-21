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

import uk.co.q3c.v7.base.view.V7View;

public class AnnotationSitemapEntry {
	private boolean publicPage;
	private String uriSegment;
	private Class<? extends V7View> viewClass;
	private String labelKeyName;

	public boolean isPublicPage() {
		return publicPage;
	}

	public void setPublicPage(boolean publicPage) {
		this.publicPage = publicPage;
	}

	public String getUriSegment() {
		return uriSegment;
	}

	public void setUriSegment(String uriSegment) {
		this.uriSegment = uriSegment;
	}

	public Class<? extends V7View> getViewClass() {
		return viewClass;
	}

	public void setViewClass(Class<? extends V7View> viewClass) {
		this.viewClass = viewClass;
	}

	public String getLabelKeyName() {
		return labelKeyName;
	}

	public void setLabelKeyName(String labelKeyName) {
		this.labelKeyName = labelKeyName;
	}
}
