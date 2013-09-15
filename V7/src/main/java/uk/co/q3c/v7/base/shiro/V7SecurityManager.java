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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;

public class V7SecurityManager extends DefaultWebSecurityManager {
	private static Logger log = LoggerFactory.getLogger(V7SecurityManager.class);
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
		setSubject(subject);
		fireListeners();
	}

	@Override
	public void logout(Subject subject) {
		super.logout(subject);
		fireListeners();
	}

	public void addListener(LoginStatusListener listener) {
		listeners.add(listener);
	}

	public void removeListener(LoginStatusListener listener) {
		listeners.remove(listener);
	}

	private void fireListeners() {
		for (LoginStatusListener listener : listeners) {
			listener.updateStatus();
		}
	}

	private void setSubject(Subject subject) {
		VaadinSession session = getVaadinSession();
		log.debug("storing Subject instance in VaadinSession");
		session.setAttribute(Subject.class, subject);
	}

	private VaadinSession getVaadinSession() {
		VaadinSession session = VaadinSession.getCurrent();

		// This should never happen, but just in case we'll check.
		if (session == null) {
			log.debug("session is null");
			throw new IllegalStateException("Unable to locate VaadinSession to store Shiro Subject.");
		}

		return session;
	}

}
