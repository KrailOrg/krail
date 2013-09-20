/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE_2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.demo.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import uk.co.q3c.v7.i18n.I18NKey;

public enum DemoLabelKey implements I18NKey<DemoLabels> {

	_nullkey_, Yes, No, View1, View2, Home, Private, Public, Reset_Account, Logout, Unlock_Account, Enable_Account, Login, Refresh_Account, Request_Account;

	@Override
	public DemoLabels getBundle(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(
				DemoLabels.class.getName(), locale);
		return (DemoLabels) bundle;
	}

	@Override
	public String getValue(Locale locale) {
		return getBundle(locale).getValue(this);
	}

	@Override
	public boolean isNullKey() {
		return this.equals(_nullkey_);
	}
}
