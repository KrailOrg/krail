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

package uk.q3c.krail.i18n;

import com.vaadin.ui.HorizontalLayout;

/**
 * Inherits from 5
 * <p>
 * Created by David Sowerby on 11/05/15.
 */
public class I18NTestClass5a extends I18NTestClass5 {

    @Caption(caption = LabelKey.Authentication)
    I18NMonitoredButton tbb;

    @I18N
    HorizontalLayout layout2Drilled;

    public I18NTestClass5a() {
        super();
        tbb = new I18NMonitoredButton();
        layout2Drilled = new HorizontalLayout(tbb); // try to catch it out

    }
}
