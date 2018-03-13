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

package uk.q3c.krail.core.i18n;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Provides a byte-enhanced class using JPA @Transactional
 * <p>
 * Created by David Sowerby on 11/05/15.
 */
public class I18NTestClass5 {

    @Caption(caption = LabelKey.Authentication, description = DescriptionKey.Confirm_Ok)
    Button tba;

    @I18N(drillDown = false)
    Panel panelNoDrill;

    @I18N
    Panel panelDrilled;

    @I18N(drillDown = false)
    VerticalLayout layoutNoDrill;

    @I18N
    HorizontalLayout layoutDrilled;

    public I18NTestClass5() {
        tba = new Button();
        panelNoDrill = new Panel();
        panelDrilled = new Panel();
        layoutNoDrill = new VerticalLayout();
        layoutDrilled = new HorizontalLayout();
    }
}
