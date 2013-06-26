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

import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.RequestSystemAccountEnableView;
import uk.co.q3c.v7.base.view.RequestSystemAccountRefreshView;
import uk.co.q3c.v7.base.view.RequestSystemAccountResetView;
import uk.co.q3c.v7.base.view.RequestSystemAccountUnlockView;
import uk.co.q3c.v7.base.view.RequestSystemAccountView;
import uk.co.q3c.v7.base.view.SecureHomeView;
import uk.co.q3c.v7.base.view.SystemAccountView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKeys;

public enum StandardPageKey implements I18NKeys<StandardPageLabels> {

	Public_Home, // The home page for non-authenticated users
	Secure_Home, // The home page for authenticated users
	Login, // the login page
	Logout, // the page to go to after logging out
	Reset_Account, // page for the user to request an account reset
	Unlock_Account, // the page to go to for the user to request their account be unlocked
	Refresh_Account, // the page to go to for the user to refresh their account after credentials have expired
	Request_Account, // the page to go to for the user to request an account (Equivalent to 'register')
	Enable_Account, // the page to go to for the user to request that their account is enabled
	System_Account // parent page for all above with Account in the name
	;
	private static final String publicSegment = "public";
	private static final String secureSegment = "secure";

	public static String defaultUri(StandardPageKey key) {
		switch (key) {
		case Secure_Home:
			return secureSegment;
		case Public_Home:
			return publicSegment;
		case Login:
		case Logout:
			return publicSegment + "/" + defaultSegment(key);
		case Request_Account:
		case Refresh_Account:
		case Enable_Account:
		case Unlock_Account:
		case Reset_Account:
			return publicSegment + "/" + defaultSegment(System_Account) + "/" + defaultSegment(key);
		case System_Account:
			return publicSegment + "/" + defaultSegment(key);
		}
		return null;
	}

	public static String defaultSegment(StandardPageKey key) {
		switch (key) {
		case Public_Home:
			return publicSegment;
		case Secure_Home:
			return secureSegment;
		default:
			String s = key.name().toLowerCase().replace("_", "-");
			return s;
		}

	}

	public static Class<? extends V7View> defaultViewInterface(StandardPageKey key) {
		switch (key) {
		case Public_Home:
			return PublicHomeView.class;
		case Secure_Home:
			return SecureHomeView.class;
		case Login:
			return LoginView.class;
		case Logout:
			return LogoutView.class;
		case Reset_Account:
			return RequestSystemAccountResetView.class;
		case Unlock_Account:
			return RequestSystemAccountUnlockView.class;
		case Refresh_Account:
			return RequestSystemAccountRefreshView.class;
		case Request_Account:
			return RequestSystemAccountView.class;
		case Enable_Account:
			return RequestSystemAccountEnableView.class;
		case System_Account:
			return SystemAccountView.class;
		}
		return null;
	}

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
