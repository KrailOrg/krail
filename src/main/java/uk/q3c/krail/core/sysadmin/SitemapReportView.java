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

package uk.q3c.krail.core.sysadmin;

import com.google.inject.Inject;
import com.vaadin.ui.TextArea;
import uk.q3c.krail.core.i18n.Caption;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.view.Grid3x3ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.util.Experimental;

/**
 * Created by David Sowerby on 24/05/15.
 */
@Experimental
public class SitemapReportView extends Grid3x3ViewBase {


    private MasterSitemap masterSitemap;
    @Caption(caption = LabelKey.Sitemap_Build_Report, description = DescriptionKey.Report_generated_by_the_Sitemap_build_process)
    private TextArea reportArea;

    @Inject
    protected SitemapReportView(MasterSitemap masterSitemap, Translate translate) {
        super(translate);
        this.masterSitemap = masterSitemap;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        reportArea = new TextArea();
        //        reportArea.setEnabled(false);
        reportArea.setSizeFull();
        reportArea.setValue(masterSitemap.getReport());
        setCentreCell(reportArea);
        setColumnWidths(1f, 4f, 1f);
        setRowHeights(1f, 10f, 1f);

    }
}
