/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.sysadmin;

import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.i18n.LabelKey;

/**
 * <b>Note:</b> This module and its associated pages is experimental and will probably change a lot
 * <p>
 * Created by David Sowerby on 24/05/15.
 */
public class SystemAdminPages extends DirectSitemapModule {

    public SystemAdminPages() {
        rootURI = "system-admin";
    }

    @Override
    protected void define() {
        addEntry("", SystemAdminView.class, LabelKey.System_Administration, PageAccessControl.PERMISSION);
        addEntry("sitemap-build-report", SitemapReportView.class, LabelKey.Sitemap_Build_Report, PageAccessControl.PERMISSION);
    }
}
