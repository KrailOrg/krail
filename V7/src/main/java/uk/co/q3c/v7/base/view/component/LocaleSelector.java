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
package uk.co.q3c.v7.base.view.component;

import java.util.Locale;

import uk.co.q3c.v7.i18n.LocaleChangeListener;

import com.vaadin.ui.Component;

public interface LocaleSelector extends LocaleChangeListener {
	/**
	 * returns the visual component used to display the current locale
	 *
	 * @return
	 */
	Component getComponent();

	Locale selectedLocale();
}
