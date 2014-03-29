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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @see Labels
 * @author David Sowerby 24 Mar 2013
 * 
 */
public enum LabelKey implements I18NKey<Labels> {
	_nullkey_,
	Application_Configuration_Service,
	Authentication,
	Authorisation,
	Broadcast_Messages,
	Cancel,
	Enable_Account,
	Error,
	First_Name,
	Guest,
	Home,
	Invalid_Page,
	Last_Name,
	Log_In,
	Log_Out,
	Message_Bar,
	Message_Box,
	No,
	Notifications,
	Ok,
	Private,
	Public,
	Pull,
	Push,
	Refresh_Account,
	Request_Account,
	Reset_Account,
	Small,
	Sitemap_Service,
	Splash,
	System_Account,
	Unlock_Account,
	Warning,
	Yes;

	@Override
	public Labels getBundle(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(Labels.class.getName(), locale);
		return (Labels) bundle;
	}

	@Override
	public String getValue(Locale locale) {
		String value = getBundle(locale).getValue(this);
		return value;
	}

	@Override
	public boolean isNullKey() {
		return this.equals(_nullkey_);
	}
}
