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
package uk.co.q3c.v7.demo.bench;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import uk.co.q3c.v7.base.view.component.DefaultMessageBar;
import uk.co.q3c.v7.demo.view.DemoPublicHomeView;
import uk.co.q3c.v7.test.bench.V7TestBenchTestCase;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class NotifyTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		baseUrl = "http://localhost:8080/V7demo";
		driver.get(concatUrl(baseUrl, "/?restartApplication#"));
	}

	@Test
	public void defaultMessage() {

		// given
		WebElement messageBar = element(DefaultMessageBar.class, Label.class);
		// when

		// then
		assertThat(messageBar.getText()).isEqualTo("Message bar");
	}

	@Test
	public void notifyError() {

		// given
		WebElement messageBar = element(DefaultMessageBar.class, Label.class);
		// when
		WebElement errorBtn = element("error", DemoPublicHomeView.class, Button.class);
		// then
		errorBtn.click();
		assertThat(messageBar.getText()).isEqualTo(
				"ERROR: You cannot use service Fake Service until it has been started");
		assertThat(notification()).isNotNull();
		assertThat(notification().getText()).isEqualTo("You cannot use service Fake Service until it has been started");
		closeNotification();

	}

	@Test
	public void notifyWarning() {
		// given
		WebElement warningBtn = element("warning", DemoPublicHomeView.class, Button.class);
		WebElement messageBar = element(DefaultMessageBar.class, Label.class);
		// when
		warningBtn.click();
		// then
		assertThat(messageBar.getText()).isEqualTo(
				"Warning: You cannot use service Fake Service until it has been started");
		assertThat(notification()).isNotNull();
		assertThat(notification().getText()).isEqualTo("You cannot use service Fake Service until it has been started");
		closeNotification();
	}

	@Test
	public void notifyInformation() {
		// given
		WebElement informationBtn = element("information", DemoPublicHomeView.class, Button.class);
		WebElement messageBar = element(DefaultMessageBar.class, Label.class);
		// when
		informationBtn.click();
		// then
		assertThat(messageBar.getText()).isEqualTo("You cannot use service Fake Service until it has been started");
		assertThat(notification()).isNotNull();
		assertThat(notification().getText()).isEqualTo("You cannot use service Fake Service until it has been started");
		closeNotification();
	}
}
