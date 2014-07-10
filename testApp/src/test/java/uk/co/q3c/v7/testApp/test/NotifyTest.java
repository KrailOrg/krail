/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.testApp.test;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import uk.co.q3c.v7.base.view.component.DefaultMessageBar;
import uk.co.q3c.v7.testapp.view.NotificationsView;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class NotifyTest extends V7TestBenchTestCase {

	private final String testPage = "notifications";

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void defaultMessage() {

		// given
		navigateTo(testPage);
		WebElement messageBar = element(DefaultMessageBar.class, Label.class);
		// when

		// then
		assertThat(messageBar.getText()).isEqualTo("Message bar");
	}

	@Test
	public void notifyError() {

		// given
		navigateTo(testPage);

		// when

		WebElement errorBtn = element("error", NotificationsView.class, Button.class);
		// then
		errorBtn.click();
		WebElement messageBar = element(DefaultMessageBar.class, Label.class);
		assertThat(messageBar.getText()).isEqualTo(
				"ERROR: You cannot use service Fake Service until it has been started");
		assertThat(notification()).isNotNull();
        assertThat(notification().getText()).isEqualTo("Error: You cannot use service Fake Service until it has been started - close with ESC-key");
        closeNotification();

	}

	@Test
	public void notifyWarning() {
		// given
		navigateTo(testPage);
		WebElement warningBtn = element("warning", NotificationsView.class, Button.class);
		WebElement messageBar = element(DefaultMessageBar.class, Label.class);
		// when
		warningBtn.click();
		// then
		assertThat(messageBar.getText()).isEqualTo(
				"Warning: You cannot use service Fake Service until it has been started");
		assertThat(notification()).isNotNull();
        assertThat(notification().getText()).isEqualTo("Warning: You cannot use service Fake Service until it has been started");
        closeNotification();
	}

	@Test
	public void notifyInformation() {
		// given
		navigateTo(testPage);
		WebElement informationBtn = element("information", NotificationsView.class, Button.class);
		WebElement messageBar = element(DefaultMessageBar.class, Label.class);
		// when
		informationBtn.click();
		// then
		assertThat(messageBar.getText()).isEqualTo("You cannot use service Fake Service until it has been started");
		assertThat(notification()).isNotNull();
        assertThat(notification().getText()).isEqualTo("Info: You cannot use service Fake Service until it has been started");
        closeNotification();
	}
}
