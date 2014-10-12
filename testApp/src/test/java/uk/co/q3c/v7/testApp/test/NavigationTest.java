/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.co.q3c.v7.testApp.test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.co.q3c.v7.testbench.BreadcrumbPageObject;
import uk.co.q3c.v7.testbench.NavTreePageObject;
import uk.co.q3c.v7.testbench.SubpagePanelPageObject;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class NavigationTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);
    private BreadcrumbPageObject breadcrumb = new BreadcrumbPageObject(this);
    private NavTreePageObject navTree = new NavTreePageObject(this);
    private SubpagePanelPageObject subPagePanel = new SubpagePanelPageObject(this);

    @Before
	public void setUp() throws Exception {
	}

	@Test
	public void navigateFromTree() throws InterruptedException {

		// given
		driver.get(rootUrl());
		// when

		// then
		verifyUrl("home");
		// // when
		navTree().select(4);
		// // then
		verifyUrl("system-account");
        assertThat(navTree.currentSelection()).isEqualTo("System Account");
        // // when
		navTree().select(3);
		// // then
		verifyUrl("home");
		// // when
		navTree().select(0);
		// // then
		verifyUrl("login");

		// when
		navTree().expand(4);
		navTree().select(4, 0);
		// then
		verifyUrl("system-account/enable-account");

	}

	/**
	 * Originally this would have reported an unauthorised action. The introduction of the UserSitemap means that an
	 * authorised page will apparently not exist, so an "invalid page" will be reported instead. In some ways that is
	 * actually better as even the existence of the page is masked.
	 */
	@Test
	public void navigateToUnauthorisedPage() {

		// given
		driver.get(rootUrl());
		pause(1000);
		// when

		navigateTo("private/home");
		pause(500);

		// then
		assertThat(notification()).isNotNull();
        assertThat(notification().getText()).isEqualTo("home is not a valid page");
        assertThat(notification().getAttribute("class")).isEqualTo("v-Notification humanized v-Notification-humanized");
        closeNotification();

		verifyNotUrl("private/home"); // not a valid test, but maybe it should be
		navigateTo("system-account");
		pause(1500);
		// when
		login();

		// then
		verifyUrl("system-account");
		// when
		navigateTo("private/home");
		// then
		verifyUrl("private/home");

	}

	@Test
	public void redirectFromPrivate() {

		// given
		driver.get(rootUrl());
		pause(1000);
		login();
		pause(1000);
		// when
		navigateTo("widgetset");
		pause(1000);
		navigateTo("private");
		pause(1000);
		// then
		verifyUrl("private/home");
        assertThat(navTree.currentSelection()).isEqualTo("Private Home");

    }

	@Test
	public void browserBackForward() {

		// given
		driver.get(rootUrl());
		pause(1000);
		// when
		navTree().select(4);
		// then
		verifyUrl("system-account");
        assertThat(navTree.currentSelection()).isEqualTo("System Account");

        // when
		navigateTo("notifications");
		// then
		verifyUrl("notifications");
        assertThat(navTree.currentSelection()).isEqualTo("Notifications");
        // when
		navigateTo("system-account/enable-account");
		// then
		verifyUrl("system-account/enable-account");
        assertThat(navTree.currentSelection()).isEqualTo("Enable Account");
        // when
		navigateBack();
		// then
		verifyUrl("notifications");
        assertThat(navTree.currentSelection()).isEqualTo("Notifications");
        // when
		navigateForward();
		verifyUrl("system-account/enable-account");
        assertThat(navTree.currentSelection()).isEqualTo("Enable Account");
    }

	@Test
	public void breadcrumb_navigate() {

		// given
		pause(1000);
		// when
		navigateTo("system-account/reset-account");
        assertThat(breadcrumb.button(0)).isNotNull();
        breadcrumb.button(0)
                  .click();
        // then
		verifyUrl("system-account");
	}

	@Test
	public void subpage_navigate() {

		// given
		pause(1000);
		// when
		navigateTo("system-account");
        //then
        assertThat(subPagePanel.buttonLabels()).containsExactly("Enable Account", "Refresh Account", "Request Account", "Reset Account", "Unlock Account");

        subPagePanel.button(0)
                    .click();
        pause(500);
		// then
		verifyUrl("system-account/enable-account");
        assertThat(subPagePanel.buttonLabels()).containsExactly("");

    }

	@Test
	public void menuNavigate() {

		// given
		driver.get(rootUrl());
		pause(1000);
		// when

        testBenchElement(driver.findElement(By.vaadin("testapp::PID_SDefaultUserNavigationMenu#item4"))).click(43, 6);
        testBenchElement(driver.findElement(By.vaadin("testapp::Root/VOverlay[0]/VMenuBar[0]#item0"))).click(44, 8);
		pause(500);
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
