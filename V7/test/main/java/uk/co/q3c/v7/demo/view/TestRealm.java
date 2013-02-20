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
package uk.co.q3c.v7.demo.view;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ConcurrentAccessException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class TestRealm extends AuthorizingRealm {
	public enum Response {
		valid,
		unknownAccount,
		incorrectCredentials,
		expiredCredentials,
		lockedAccount,
		excessiveAttempts,
		disabledAccount,
		concurrentAccess,
		authenticationFailed // the catch all
	}

	private Response response = Response.authenticationFailed;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		throw new RuntimeException("not yet implemented");
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		switch (response) {
		case valid:
			return new SimpleAuthenticationInfo("username", "password", this.getName());
		case unknownAccount:
			throw new UnknownAccountException();
		case incorrectCredentials:
			throw new IncorrectCredentialsException();
		case expiredCredentials:
			throw new ExpiredCredentialsException();
		case lockedAccount:
			throw new LockedAccountException();
		case excessiveAttempts:
			throw new ExcessiveAttemptsException();
		case disabledAccount:
			throw new DisabledAccountException();
		case concurrentAccess:
			throw new ConcurrentAccessException();
		case authenticationFailed:
			throw new AuthenticationException();
		}
		return null;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
