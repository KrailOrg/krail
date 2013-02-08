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

import javax.inject.Inject;
import javax.inject.Provider;

public class CurrentLocale {

	private Locale locale = Locale.UK;
	private final List<I18NListener> listeners = new ArrayList<>();
	private final Provider<I18NInterpreter> interpreterPro;

	@Inject
	protected CurrentLocale(Provider<I18NInterpreter> interpreterPro) {
		super();
		this.interpreterPro = interpreterPro;
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

	public void addListener(I18NListener listener) {
		listeners.add(listener);
	}

	public void removeListener(I18NListener listener) {
		listeners.remove(listener);
	}

	private void fireListeners(Locale locale) {
		for (I18NListener listener : listeners) {
			I18NInterpreter interpreter = interpreterPro.get();
			interpreter.setLocale(locale);
			listener.localeChange(interpreter);
		}
	}

}
