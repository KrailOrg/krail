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
import uk.q3c.krail.i18n.LabelKey;

/**
 * A set of pages to support user account management.
 *
 * @author David Sowerby
 */
public class SystemAccountManagementPages extends DirectSitemapModule {

    /**
     * @see uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule#define()
     */
    @Override
    protected void define() {
        addEntry("system-account", SystemAccountView.class, LabelKey.System_Account, PageAccessControl.PUBLIC);
        addEntry("system-account/request-account", RequestSystemAccountCreateView.class, LabelKey.Request_Account,
                PageAccessControl.PUBLIC);
        addEntry("system-account/enable-account", RequestSystemAccountEnableView.class, LabelKey.Enable_Account,
                PageAccessControl.PUBLIC);
        addEntry("system-account/refresh-account", RequestSystemAccountRefreshView.class, LabelKey.Refresh_Account,
                PageAccessControl.PUBLIC);
        addEntry("system-account/reset-account", RequestSystemAccountResetView.class, LabelKey.Reset_Account,
                PageAccessControl.PUBLIC);
        addEntry("system-account/unlock-account", RequestSystemAccountUnlockView.class, LabelKey.Unlock_Account,
                PageAccessControl.PUBLIC);

    }
}
