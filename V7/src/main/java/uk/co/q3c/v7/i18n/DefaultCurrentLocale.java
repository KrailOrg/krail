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

import com.google.inject.Inject;

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

	@Inject
	protected DefaultCurrentLocale() {
		super();
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public void setLocale(Locale locale) {
		setLocale(locale, true);
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

	@Override
	public void setLocale(Locale locale, boolean fireListeners) {
		if (locale != this.locale) {
			this.locale = locale;
			log.debug("CurrentLocale set to {}", locale);
			if (fireListeners) {
				fireListeners(locale);
			}
		}

	}

}
