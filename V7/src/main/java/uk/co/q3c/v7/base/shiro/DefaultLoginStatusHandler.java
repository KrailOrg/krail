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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLoginStatusHandler.class);
	private final Set<LoginStatusListener> listeners =  Collections.newSetFromMap(new WeakHashMap<LoginStatusListener, Boolean>());

	@Inject
	protected DefaultLoginStatusHandler() {
		super();
	}

	@Override
	public void addListener(LoginStatusListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(LoginStatusListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void fireStatusChange(LoginStatusEvent event) {
		fireListeners(event);
	}
	
	private void fireListeners(LoginStatusEvent event) {
		LOGGER.debug("firing login status listeners");
		for (LoginStatusListener listener : listeners) {
			listener.loginStatusChange(event);
		}
	}
}
