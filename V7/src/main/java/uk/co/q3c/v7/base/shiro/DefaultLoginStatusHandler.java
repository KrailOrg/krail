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

import javax.inject.Inject;

import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * See {@link LoginStatusHandler} for description.
 * <p>
 * There is generally no need to call {@link #removeListener(LoginStatusListener)} because instances of this class have
 * the same scope as the UI they belong to.
 * 
 * @author David Sowerby 16 Sep 2013
 * 
 */
@UIScoped
public class DefaultLoginStatusHandler implements LoginStatusHandler {
	private static Logger log = LoggerFactory.getLogger(DefaultLoginStatusHandler.class);
	private final List<LoginStatusListener> listeners = new ArrayList<>();
	private final VaadinSessionProvider sessionProvider;
	private final SubjectProvider subjectProvider;
	private final Translate translate;

	@Inject
	protected DefaultLoginStatusHandler(VaadinSessionProvider sessionProvider, SubjectProvider subjectProvider,
			Translate translate) {
		super();
		this.sessionProvider = sessionProvider;
		this.subjectProvider = subjectProvider;
		this.translate = translate;
	}

	@Override
	public void addListener(LoginStatusListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(LoginStatusListener listener) {
		listeners.remove(listener);
	}

	private void fireListeners(boolean authenticated, String name) {

		log.debug("firing login status listeners");
		for (LoginStatusListener listener : listeners) {
			listener.loginStatusChange(authenticated, name);
		}
	}

	@Override
	public void initiateStatusChange() {

		boolean authenticated = subjectIsAuthenticated();
		String name = subjectName();

		VaadinSession session = sessionProvider.get();
		Collection<UI> uIs = session.getUIs();

		for (UI ui : uIs) {
			ScopedUI sui = (ScopedUI) ui;
			sui.getLoginStatusHandler().respondToStatusChange(authenticated, name);
		}
	}

	@Override
	public void respondToStatusChange(boolean authenticated, String name) {
		fireListeners(authenticated, name);
	}

	@Override
	public boolean subjectIsAuthenticated() {
		Subject subject = subjectProvider.get();
		boolean authenticated = subject.isAuthenticated();
		return authenticated;
	}

	@Override
	public String subjectName() {
		Subject subject = subjectProvider.get();
		boolean authenticated = subject.isAuthenticated();
		boolean remembered = subject.isRemembered();
		String name = (authenticated) ? subject.getPrincipal().toString() : translate.from(LabelKey.Guest);
		name = (remembered) ? subject.getPrincipal().toString() + "?" : name;
		return name;
	}
}
