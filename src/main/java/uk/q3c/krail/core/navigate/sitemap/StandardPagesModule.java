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
package uk.q3c.krail.core.navigate.sitemap;

import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.view.*;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;

public class StandardPagesModule extends DirectSitemapModule {

    // private MapBinder<String, StandardPageSitemapEntry> mapBinder;
    // private MapBinder<String, RedirectEntry> redirectBinder;

    /**
     * Override this method to define different {@link MasterSitemap} entries for Standard Pages. All of the views
     * specified
     * here are interfaces, so if you only want to change the View implementation you can change the binding in
     * {@link ViewModule}
     *
     * @see #addEntry(String, Class, I18NKey, boolean, String)
     */
    @Override
    protected void define() {
        addEntry("home", PublicHomeView.class, StandardPageKey.Public_Home, PageAccessControl.PUBLIC, null);
        addEntry("login", LoginView.class, StandardPageKey.Log_In, PageAccessControl.PUBLIC, null);
        addEntry("logout", LogoutView.class, StandardPageKey.Log_Out, PageAccessControl.PUBLIC, null);
        addEntry("private", null, LabelKey.Private, PageAccessControl.PERMISSION, null);
        addEntry("private/home", PrivateHomeView.class, StandardPageKey.Private_Home, PageAccessControl.PERMISSION,
                null);
        addRedirect("private", "private/home");
    }

    ;

}
