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

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScoped;

import com.google.inject.Inject;

/**
 * Provides a reference to the currently selected Locale. {@link LocaleChangeListener}s can be added to listen for
 * locale changes. It is {@link VaadinSessionScoped} as the selection of locale is a choice usually available to an
 * individual user.
 * 
 * 
 * @author David Sowerby 3 Mar 2013
 * 
 */
@VaadinSessionScoped
public class CurrentLocale {

	private Locale locale = Locale.UK;
	private final List<LocaleChangeListener> listeners = new ArrayList<>();

	@Inject
	protected CurrentLocale() {
		super();
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		if (locale != this.locale) {
			this.locale = locale;
			fireListeners(locale);
		}
	}

	public void addListener(LocaleChangeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(LocaleChangeListener listener) {
		listeners.remove(listener);
	}

	private void fireListeners(Locale locale) {
		for (LocaleChangeListener listener : listeners) {
			listener.localeChanged(locale);
		}
	}

}
