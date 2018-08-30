/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate.sitemap

import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.user.LoginView
import uk.q3c.krail.core.view.LogoutView
import uk.q3c.krail.core.view.PrivateHomeView
import uk.q3c.krail.core.view.PublicHomeView
import uk.q3c.krail.core.view.ViewModule

class StandardPagesModule : DirectSitemapModule() {

    // private MapBinder<String, StandardPageSitemapEntry> mapBinder;
    // private MapBinder<String, RedirectEntry> redirectBinder;

    /**
     * Override this method to define different [MasterSitemap] entries for Standard Pages. All of the views
     * specified
     * here are interfaces, so if you only want to change the View implementation you can change the binding in
     * [ViewModule]
     *
     *
     */
    override fun define() {
        addEntry("home", StandardPageKey.Public_Home, PageAccessControl.PUBLIC, PublicHomeView::class.java)
        addEntry("login", StandardPageKey.Log_In, PageAccessControl.PUBLIC, LoginView::class.java)
        addEntry("logout", StandardPageKey.Log_Out, PageAccessControl.PUBLIC, LogoutView::class.java)
        addEntry("private", LabelKey.Private)
        addEntry("private/home", StandardPageKey.Private_Home, PageAccessControl.PERMISSION, PrivateHomeView::class.java)
        addRedirect("private", "private/home")
    }

}
