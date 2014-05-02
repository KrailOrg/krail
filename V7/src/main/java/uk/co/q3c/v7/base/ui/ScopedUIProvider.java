/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.base.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIKeyProvider;
import uk.co.q3c.v7.base.guice.uiscope.UIScope;
import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * A Vaadin UI provider which supports the use of Guice scoped UI (see {@link UIScoped}). If you do not need UIScope,
 * then just extend from UIProvider directly
 *
 * Subclasses should implement getUIClass(UIClassSelectionEvent event) to provide logic for selecting the UI class.
 *
 * @author David Sowerby, Will Temperley
 *
 */
public abstract class ScopedUIProvider extends UIProvider implements Provider<ScopedUI> {
	private static Logger log = LoggerFactory.getLogger(ScopedUIProvider.class);
	protected UIKeyProvider uiKeyProvider;
	protected Injector injector;

	@Inject
	protected void init(Injector injector, UIKeyProvider uiKeyProvider) {
		this.injector = injector;
		this.uiKeyProvider = uiKeyProvider;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		Class<? extends UI> uiClass = event.getUIClass();
		UIKey uiKey = uiKeyProvider.get();
		// hold the key while UI is created
		CurrentInstance.set(UIKey.class, uiKey);
		// and set up the scope
		UIScope scope = UIScope.getCurrent();
		scope.startScope(uiKey);
		// create the UI
		ScopedUI ui = (ScopedUI) injector.getInstance(uiClass);
		ui.setInstanceKey(uiKey);
		ui.setScope(scope);

		log.debug("Returning instance of {} with key {}", uiClass.getName(), uiKey);
		return ui;
	}

	@Override
	public ScopedUI get() {
		return (ScopedUI) UI.getCurrent();
	}
}
