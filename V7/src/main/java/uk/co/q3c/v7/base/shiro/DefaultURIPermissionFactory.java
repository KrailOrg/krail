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

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.URIFragmentHandler;

public class DefaultURIPermissionFactory implements URIPermissionFactory {

	private final URIFragmentHandler uriHandler;

	@Inject
	protected DefaultURIPermissionFactory(URIFragmentHandler uriHandler) {
		super();
		this.uriHandler = uriHandler;
	}

	@Override
	public URIViewPermission createViewPermission(String uri) {
		return new URIViewPermission(uriHandler, uri);
	}

	@Override
	public URIViewPermission createViewPermission(String uri, boolean appendWildcard) {
		return new URIViewPermission(uriHandler, uri, appendWildcard);
	}

}
