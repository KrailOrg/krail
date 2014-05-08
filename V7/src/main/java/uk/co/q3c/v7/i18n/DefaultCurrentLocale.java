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
package uk.co.q3c.v7.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScoped;
import uk.co.q3c.v7.base.ui.BrowserProvider;
import uk.co.q3c.v7.base.ui.ScopedUI;

import com.google.inject.Inject;
import com.vaadin.server.Page;

/**
 * When a CurrentLocale is instantiated it will take the browser locale as the default (at this point the user has not
 * logged in). When a user logs in, that user's preference for locale is used. On log out will current locale will
 * remain set to the (now former) user's preference.
 *
 * @author David Sowerby
 * @date 5 May 2014
 */
@VaadinSessionScoped
public class DefaultCurrentLocale implements CurrentLocale {
	private static Logger log = LoggerFactory.getLogger(DefaultCurrentLocale.class);
	private Locale locale = Locale.UK;
	private final List<LocaleChangeListener> listeners = new ArrayList<>();
	private final BrowserProvider browserProvider;

	@Inject
	protected DefaultCurrentLocale(BrowserProvider browserProvider) {
		super();
		this.browserProvider = browserProvider;
	}

	/**
	 * When UI is being constructed the Vaadin {@link Page} is not available - which also means that the browser
	 * information cannot be retrieved. This method is called by {@link ScopedUI} during initialisation, so that the
	 * user's browser can be used as a reference for the desired Locale. Does not fire change listeners, as this is part
	 * of the initialisation process.
	 */
	@Override
	public void init() {
		Locale browserLocale = browserProvider.get().getLocale();
		locale = browserLocale;
		log.debug("CurrentLocale initialised to browser locale of '{}'", browserLocale);
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public void setLocale(Locale locale) {
		if (locale != this.locale) {
			this.locale = locale;
			fireListeners(locale);
		}
	}

	@Override
	public void addListener(LocaleChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(LocaleChangeListener listener) {
		listeners.remove(listener);
	}

	private void fireListeners(Locale locale) {
		for (LocaleChangeListener listener : listeners) {
			listener.localeChanged(locale);
		}
	}

}
