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
package uk.co.q3c.v7.base.navigate.sitemap;

import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.PrivateHomeView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.LabelKey;

public class StandardPagesModule extends DirectSitemapModule {

	// private MapBinder<String, StandardPageSitemapEntry> mapBinder;
	// private MapBinder<String, RedirectEntry> redirectBinder;

	/**
	 * Override this method to define different {@link Sitemap} entries for Standard Pages. All of the views specified
	 * here are interfaces, so if you only want to change the View implementation you can change the binding in
	 * {@link StandardViewModule}
	 * 
	 * @see #addEntry(String, Class, I18NKey, boolean, String)
	 */
	@Override
	protected void define() {
		addEntry("home", PublicHomeView.class, StandardPageKey.Public_Home, PageAccessControl.PUBLIC, null);
		addEntry("login", LoginView.class, StandardPageKey.Login, PageAccessControl.PUBLIC, null);
		addEntry("logout", LogoutView.class, StandardPageKey.Logout, PageAccessControl.PUBLIC, null);
		addEntry("private", null, LabelKey.Private, PageAccessControl.PERMISSION, null);
		addEntry("private/home", PrivateHomeView.class, StandardPageKey.Private_Home, PageAccessControl.PERMISSION,
				null);
		addRedirect("private", "private/home");
	};

}
