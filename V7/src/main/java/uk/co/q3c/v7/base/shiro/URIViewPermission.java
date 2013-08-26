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

import org.apache.shiro.authz.permission.WildcardPermission;

import uk.co.q3c.v7.base.navigate.URIFragmentHandler;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class URIViewPermission extends WildcardPermission {

	@AssistedInject
	public URIViewPermission(URIFragmentHandler uriHandler, @Assisted String uri) {
		super();
		construct(uriHandler, uri, false);
	}

	@AssistedInject
	public URIViewPermission(URIFragmentHandler uriHandler, @Assisted String uri, @Assisted boolean appendWildcard) {
		super();
		construct(uriHandler, uri, appendWildcard);
	}

	protected void construct(URIFragmentHandler uriHandler, String uri, boolean appendWildcard) {
		uriHandler.setFragment(uri);
		String prefix = "uri:view:";
		String pagePerm = uriHandler.virtualPage().replace("/", ":");

		String permissionString = appendWildcard ? prefix + pagePerm + ":*" : prefix + pagePerm;
		setParts(permissionString);
	}
}
