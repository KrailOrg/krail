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

public enum DescriptionKey implements I18NKey<Descriptions> {
	_nullkey_,
	Account_Already_In_Use,
	Account_is_Disabled,
	Account_Expired,
	Account_Locked,
	Application_Configuration_Service,
	Confirm_Ok,
	Enter_your_user_name,
	Invalid_Login,
	Last_Name,
	No_Permission,
	Please_log_in,
	Sitemap_Service,
	Too_Many_Login_Attempts,
	Unknown_Account,
	You_have_not_logged_in,

	// Small_Font;
	;
	@Override
	public Descriptions getBundle(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(Descriptions.class.getName(), locale);
		return (Descriptions) bundle;
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
