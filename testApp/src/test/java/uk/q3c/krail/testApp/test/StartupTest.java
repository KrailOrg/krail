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
import uk.q3c.krail.testbench.page.object.MessageBarPageObject;
import uk.q3c.krail.testbench.page.object.NavMenuPageObject;
import uk.q3c.krail.testbench.page.object.NavTreePageObject;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 07/07/14.  Checks for correct conditions at application start up
 */


public class StartupTest extends KrailTestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);
    private LocaleSelectorPageObject localeSelector = new LocaleSelectorPageObject(this);
    private MessageBarPageObject messageBar = new MessageBarPageObject(this);
    private NavMenuPageObject navMenu = new NavMenuPageObject(this);
    private NavTreePageObject navTree = new NavTreePageObject(this);

    @Before
    public void setUp() throws Exception {
        driver.get(rootUrl());
    }

    @Test
    public void componentSettings() {

        // given

        // when

        // then
        // localSelector is UK
        String comboValue = localeSelector.getValue();
        assertThat(comboValue).isEqualTo("English (United Kingdom)");

        assertThat(messageBar.message()).isEqualTo("Message Bar");
    }

    @Test
    public void navTreeOrder() {

        // given

        // when

        // then
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(navTree.itemCaption(i));
        }
        assertThat(items).containsExactly("Log In", "Message Box", "Notifications", "Public Home", "System Account");
    }

    @Test
    public void navMenuOrder() {

        // given

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String s = navMenu.item(i);
            items.add(s);
        }
        assertThat(items).containsExactly("Log In", "Message Box", "Notifications", "Public Home", "System Account");
    }


}