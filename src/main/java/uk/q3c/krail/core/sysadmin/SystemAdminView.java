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

import com.google.inject.Inject;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.view.Grid3x3ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Caption;
import uk.q3c.krail.i18n.DescriptionKey;
import uk.q3c.krail.i18n.LabelKey;

/**
 * Created by David Sowerby on 24/05/15.
 */
public class SystemAdminView extends Grid3x3ViewBase {
    @Caption(caption = LabelKey.Sitemap_Build_Report, description = DescriptionKey.Report_generated_by_the_Sitemap_build_process)
    private Button buildReportBtn;
    private Navigator navigator;

    @Inject
    public SystemAdminView(Navigator navigator) {
        this.navigator = navigator;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        buildReportBtn = new Button();
        buildReportBtn.addClickListener(c -> navigator.navigateTo("system-admin/sitemap-build-report"));
        setCentreCell(new VerticalLayout(buildReportBtn));
    }
}
