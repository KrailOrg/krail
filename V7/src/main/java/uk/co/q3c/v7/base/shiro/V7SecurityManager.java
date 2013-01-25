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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

public class V7SecurityManager extends DefaultWebSecurityManager {
	private final List<LoginStatusListener> listeners = new ArrayList<>();

	public V7SecurityManager() {
		super();
	}

	public V7SecurityManager(Collection<Realm> realms) {
		super(realms);
	}

	public V7SecurityManager(Realm singleRealm) {
		super(singleRealm);
	}

	@Override
	protected void onSuccessfulLogin(AuthenticationToken token, AuthenticationInfo info, Subject subject) {
		super.onSuccessfulLogin(token, info, subject);
		fireListeners(subject);
	}

	@Override
	public void logout(Subject subject) {
		super.logout(subject);
		fireListeners(subject);
	}

	public void addListener(LoginStatusListener listener) {
		listeners.add(listener);
	}

	public void removeListener(LoginStatusListener listener) {
		listeners.remove(listener);
	}

	private void fireListeners(Subject subject) {
		for (LoginStatusListener listener : listeners) {
			listener.updateStatus(subject);
		}
	}

}
