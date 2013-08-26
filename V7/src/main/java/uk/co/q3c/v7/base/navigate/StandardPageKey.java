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
package uk.co.q3c.v7.base.navigate;

import java.util.Locale;
import java.util.ResourceBundle;

import uk.co.q3c.v7.i18n.I18NKey;

public enum StandardPageKey implements I18NKey<StandardPageLabels> {

	Public_Home, // The home page for non-authenticated users
	Private_Home, // The home page for authenticated users
	Login, // the login page
	Logout, // the page to go to after logging out
	Reset_Account, // page for the user to request an account reset
	Unlock_Account, // the page to go to for the user to request their account be unlocked
	Refresh_Account, // the page to go to for the user to refresh their account after credentials have expired
	Request_Account, // the page to go to for the user to request an account (Equivalent to 'register')
	Enable_Account, // the page to go to for the user to request that their account is enabled
	System_Account // parent page for all above with Account in the name
	;

	@Override
	public StandardPageLabels getBundle(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(StandardPageLabels.class.getName(), locale);
		return (StandardPageLabels) bundle;
	}

	@Override
	public String getValue(Locale locale) {
		String mapValue = getBundle(locale).getValue(this);
		if (mapValue == null) {
			return this.name().replace("_", " ");
		} else {
			return mapValue;
		}
	}

	@Override
	public boolean isNullKey() {
		return false;
	}

}
