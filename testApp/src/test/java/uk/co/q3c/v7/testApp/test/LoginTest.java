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

import com.google.common.base.Optional;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import uk.co.q3c.v7.base.view.DefaultLoginView;
import uk.co.q3c.v7.testbench.NavTreePageObject;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends V7TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);
    private NavTreePageObject navTree = new NavTreePageObject(this);

    @Before
    public void setUp() throws Exception {
        driver.get(rootUrl());
    }

    @Test
    public void testLogin() {
        // given
        pause(1000);
        navTree.expand(0);
        // navTree().index(0).expand().get().click();
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

    @Test
    public void loginFromLogout() {

        // given
        login();
        pause(500);
        loginStatus.clickButton();
        pause(500);
        verifyUrl("logout");
        // when
        login();
        // then
        pause(500);
        verifyUrl("private/home");
    }

    /**
     * If previous tests have caused unsuccessful logins, or this test is run repeatedly, login will fail because of
     * excessive attempts instead of through invalid login. The only way to reset that through the UI at the moment is
     * to login successfully first (which resets the attempt count), logout, then use an invalid login.
     */
    @Test
    public void authenticationFailure() {

        // given
        login();
        //logout
        loginStatus.clickButton();
        // when
        loginStatus.clickButton();
        loginForm.login("ds", "rubbish");

        // // then
        verifyUrl("login"); // has not moved
        assertThat(navTree.currentSelection()).isEqualTo("Log In");
        pause(1000);
        WebElement label = label(Optional.of("status"), DefaultLoginView.class, Label.class);
        pause(1000);
        assertThat(label).isNotNull();
        String s = label.getText();

        assertThat(s).isEqualTo("That username or password was not recognised");
    }

    //	@After
    //    public void tearDown2() throws Exception {
    //        String verificationErrorString = verificationErrors.toString();
    //		System.out.println(verificationErrorString);
    //		if (!"".equals(verificationErrorString)) {
    //			fail(verificationErrorString);
    //		}
    //	}

    public void init(WebDriver driver, String baseUrl) {
        this.setDriver(driver);
        this.setBaseUrl(baseUrl);
    }

}