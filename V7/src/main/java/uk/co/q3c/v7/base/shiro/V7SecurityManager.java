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

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.LoginForm.LoginListener;

@Singleton
public class V7SecurityManager extends DefaultSecurityManager {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(V7SecurityManager.class);

	@Inject
	private VaadinSessionProvider sessionProvider;
	@Inject
	private Provider<SessionLoginStatusHandler> loginStatusHandlerProvider;

	private final Collection<LoginStatusListener> loginStatusListeners = new ArrayList<>(0);
	
	public V7SecurityManager() {
		super();
	}

	public V7SecurityManager(Collection<Realm> realms) {
		super(realms);
	}

	@Override
	protected void onSuccessfulLogin(AuthenticationToken token,
			AuthenticationInfo info, Subject subject) {
		super.onSuccessfulLogin(token, info, subject);
		setSubject(subject);
		
		LoginStatusEvent event = new LoginStatusEvent(subject);
		// notify global LoginListener
		fireGlobalLoginStatusListeners(event);
		// notify Session LoginListeners
		loginStatusHandlerProvider.get()
				.fireStatusChange(event);		
	}

	public Subject getSubject() {
		VaadinSession session = sessionProvider.get();

		if (session == null) {
			throw new IllegalStateException(
					"The vaadin session is not availible");
		}

		Subject subject = session.getAttribute(Subject.class);
		if (subject == null) {
			LOGGER.debug("VaadinSession is valid, but does not have a stored Subject, creating a new Subject");
			subject = new Subject.Builder().buildSubject();
			setSubject(subject);
		}
		return subject;
	}

	protected void setSubject(Subject subject) {
		VaadinSession session = sessionProvider.get();
		LOGGER.debug("storing Subject instance in VaadinSession");
		session.setAttribute(Subject.class, subject);
	}

	/**
	 * Method injection is needed because the constructor has to complete
	 * 
	 * @see org.apache.shiro.mgt.SessionsSecurityManager#setSessionManager(org.apache.shiro.session.mgt.SessionManager)
	 */
	@Inject
	@Override
	public void setSessionManager(SessionManager sessionManager) {
		super.setSessionManager(sessionManager);
	}
	
	/**
	 * Add a global {@link LoginStatusListener} that will be notified of the login/logout events of all sessions.
	 */
	public void addListener(LoginStatusListener listener) {
		this.loginStatusListeners.add(listener);
	}
	
	public boolean removeListener(LoginStatusListener listener) {
		return this.loginStatusListeners.remove(listener);		
	}
	
	private void fireGlobalLoginStatusListeners(LoginStatusEvent event) {
		for(LoginStatusListener l : this.loginStatusListeners) {
			l.loginStatusChange(event);
		}
	}
}
