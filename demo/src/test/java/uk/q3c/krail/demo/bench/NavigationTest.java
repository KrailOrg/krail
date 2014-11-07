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

package uk.q3c.krail.demo.bench;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.q3c.krail.testbench.V7TestBenchTestCase;
import uk.q3c.krail.testbench.page.object.NavTreePageObject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class NavigationTest extends V7TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

    private NavTreePageObject navTree = new NavTreePageObject(this);

    @Before
    public void setUp() throws Exception {
        appContext = "V7demo";
    }

    @Test
    public void navigateFromTree() throws InterruptedException {

        // given
        driver.get(rootUrl());
        // when

        // then
        verifyUrl("home");
        // // when
        navTree.select("System Account");
        // // then
        verifyUrl("system-account");
        assertThat(navTree.currentSelection()).isEqualTo("System Account");
        // // when
        navTree.select("Public Home");
        // // then
        verifyUrl("home");
        // // when
        navTree.select("Log In");
        // // then
        verifyUrl("login");

        // when
        navTree.select("System Account/Enable Account");
        // then
        verifyUrl("system-account/enable-account");

    }

    @After
    public void tearDown2() throws Exception {
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

}
