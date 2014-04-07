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
package uk.co.q3c.v7.base.shiro;

import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

/**
 * Delegate for user access control when relating specifically to pages.
 * 
 * @author David Sowerby
 * 
 */
public class PageAccessController {

	private static Logger log = LoggerFactory.getLogger(PageAccessController.class);
	private final Sitemap sitemap;

	@Inject
	protected PageAccessController(Sitemap sitemap) {
		super();
		this.sitemap = sitemap;
	}

	public boolean isAuthorised(Subject subject, SitemapNode node) {
		Preconditions.checkNotNull(node, "node");
		Preconditions.checkNotNull(subject, "subject");
		String virtualPage = sitemap.navigationState(node).getVirtualPage();
		Preconditions.checkNotNull(virtualPage, "virtualPage");
		Preconditions.checkNotNull(node.getPageAccessControl(), "node.getPageAccessControl(), " + node.getUriSegment());
		switch (node.getPageAccessControl()) {
		case AUTHENTICATION:
			return subject.isAuthenticated();
		case GUEST:
			return (!subject.isAuthenticated()) && (!subject.isRemembered());
		case PERMISSION:
			return subject.isPermitted(new PagePermission(virtualPage));
		case PUBLIC:
			return true;
		case ROLES:
			return subject.hasAllRoles(node.getRoles());
		case USER:
			return (subject.isAuthenticated()) || (subject.isRemembered());
		}
		return false;
	}
}
