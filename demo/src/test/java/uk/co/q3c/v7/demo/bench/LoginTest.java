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

package uk.co.q3c.v7.demo.bench;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import uk.co.q3c.v7.testbench.LoginFormPageObject;
import uk.co.q3c.v7.testbench.NavTreePageObject;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends V7TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);
    private LoginFormPageObject loginForm = new LoginFormPageObject(this);
    private NavTreePageObject navTree = new NavTreePageObject(this);

    @Before
    public void setUp() throws Exception {
        appContext = "V7demo";
        driver.get(rootUrl());
    }

    @Test
    public void testLogin() {
        // given
        pause(1000);
        navTree.expand(0);
        String startFragment = "system-account";
        navigateTo(startFragment);

        pause(1000);

        // when
        // then initial state
        assertThat(loginStatus.loginButton()
                              .getText()).isEqualTo("log in");
        assertThat(loginStatusLabelText()).isEqualTo("Guest");

        // when LoginStatusPanel button clicked
        loginStatus.clickButton();
        // then
        verifyUrl("login");

        // when username and password entered
        loginForm.login("ds", "password");
        // then correct url and status panel updated
        verifyUrl(startFragment);
        assertThat(loginStatus.loginButton()
                              .getText()).isEqualTo("log out");
        assertThat(loginStatusLabelText()).isEqualTo("ds");
    }


    public void init(WebDriver driver, String baseUrl) {
        this.setDriver(driver);
        this.setBaseUrl(baseUrl);
    }

}