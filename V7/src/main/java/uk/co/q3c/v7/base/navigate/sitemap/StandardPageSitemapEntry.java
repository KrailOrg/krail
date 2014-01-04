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

import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.V7View;

public class StandardPageSitemapEntry {
	private PageAccessControl pageAccessControl;
	private Class<? extends V7View> viewClass;
	private final StandardPageKey pageKey;
	private String permission;

	protected StandardPageSitemapEntry(Class<? extends V7View> viewClass, StandardPageKey pageKey,
			PageAccessControl pageAccessControl, String permission) {
		super();
		this.pageAccessControl = pageAccessControl;
		this.viewClass = viewClass;
		this.pageKey = pageKey;
		this.permission = permission;
	}

	public Class<? extends V7View> getViewClass() {
		return viewClass;
	}

	public void setViewClass(Class<? extends V7View> viewClass) {
		this.viewClass = viewClass;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public StandardPageKey getPageKey() {
		return pageKey;
	}

	public PageAccessControl getPageAccessControl() {
		return pageAccessControl;
	}

	public void setPageAccessControl(PageAccessControl pageAccessControl) {
		this.pageAccessControl = pageAccessControl;
	}

}
