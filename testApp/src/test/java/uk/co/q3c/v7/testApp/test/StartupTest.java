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

package uk.co.q3c.v7.testApp.test;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import uk.co.q3c.v7.base.view.component.DefaultMessageBar;
import uk.co.q3c.v7.testbench.NavMenuPageObject;
import uk.co.q3c.v7.testbench.NavTreePageObject;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 07/07/14.  Checks for correct conditions at application start up
 */


public class StartupTest extends V7TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);
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
        String comboValue = localeSelectorValue();
        assertThat(comboValue).isEqualTo("English (United Kingdom)");

        WebElement messageBar = element(DefaultMessageBar.class, Label.class);
        assertThat(messageBar.getText()).isEqualTo("Message bar");
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