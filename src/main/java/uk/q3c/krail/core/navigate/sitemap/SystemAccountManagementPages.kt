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
import uk.q3c.krail.core.shiro.PageAccessControl.PUBLIC
import uk.q3c.krail.core.view.RequestSystemAccountCreateView
import uk.q3c.krail.core.view.RequestSystemAccountEnableView
import uk.q3c.krail.core.view.RequestSystemAccountRefreshView
import uk.q3c.krail.core.view.RequestSystemAccountResetView
import uk.q3c.krail.core.view.RequestSystemAccountUnlockView

/**
 * EXAMPLE ONLY.  A set of pages to support user account management.
 *
 * @author David Sowerby
 */
class SystemAccountManagementPages : DirectSitemapModule() {
    init {
        rootURI = "system-account"
    }

    /**
     * @see uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule.define
     */
    override fun define() {
        addEntry("", LabelKey.System_Account, PUBLIC, EmptyView::class.java)
        addEntry("request-account", LabelKey.Request_Account, PUBLIC, RequestSystemAccountCreateView::class.java)
        addEntry("enable-account", LabelKey.Enable_Account, PUBLIC, RequestSystemAccountEnableView::class.java)
        addEntry("refresh-account", LabelKey.Refresh_Account, PUBLIC, RequestSystemAccountRefreshView::class.java)
        addEntry("reset-account", LabelKey.Reset_Account, PUBLIC, RequestSystemAccountResetView::class.java)
        addEntry("unlock-account", LabelKey.Unlock_Account, PUBLIC, RequestSystemAccountUnlockView::class.java)
    }
}
