/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.testApp.test;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.q3c.krail.testbench.KrailTestBenchTestCase;
import uk.q3c.krail.testbench.page.object.LocaleSelectorPageObject;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 07/07/14.  Checks for correct conditions at application start up
 */


public class StartupTest_UnsupportedBrowserLocale extends KrailTestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);
    private LocaleSelectorPageObject localeSelector = new LocaleSelectorPageObject(this);

    public StartupTest_UnsupportedBrowserLocale() {
        firefoxLocale = Locale.CHINA;
    }

    @Before
    public void setUp() throws Exception {
        driver.get(rootUrl());
    }

    @Test
    public void languageSelected() {

        // given

        // when

        // then
        // localSelector is UK
        String comboValue = localeSelector.getValue();
        assertThat(comboValue).isEqualTo("English (United Kingdom)");

    }


}