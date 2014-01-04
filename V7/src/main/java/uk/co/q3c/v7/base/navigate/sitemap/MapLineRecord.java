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

import uk.co.q3c.v7.base.shiro.PageAccessControl;

public class MapLineRecord {

	private int indentLevel;
	private String segment;
	private String viewName;
	private String keyName;
	private String permission;
	private PageAccessControl pageAccessControl;
	private boolean requiresPageURI = false;

	public int getIndentLevel() {
		return indentLevel;
	}

	public void setIndentLevel(int indentLevel) {
		this.indentLevel = indentLevel;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public PageAccessControl getPageAccessControl() {
		return pageAccessControl;
	}

	public void setPageAccessControl(PageAccessControl pageAccessControl) {
		this.pageAccessControl = pageAccessControl;
	}

	public boolean isRequiresPageURI() {
		return requiresPageURI;
	}

	public void setRequiresPageURI(boolean requiresPageURI) {
		this.requiresPageURI = requiresPageURI;
	}

}
