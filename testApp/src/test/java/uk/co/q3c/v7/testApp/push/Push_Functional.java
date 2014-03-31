/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.testApp.push;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import uk.co.q3c.v7.base.view.component.BroadcastMessageLog;
import uk.co.q3c.v7.base.view.component.DefaultMessageBar;
import uk.co.q3c.v7.testapp.view.PushView;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import com.vaadin.testbench.TestBench;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class Push_Functional extends V7TestBenchTestCase {

	private WebDriver driver2;

	@Before
	public void setup() {
		driver2 = TestBench.createDriver(new FirefoxDriver());
		driver2.manage().window().setPosition(new Point(1026, 0));
		driver2.manage().window().setSize(new Dimension(1024, 768));
	}

	/**
	 * Passes messages between 2 browser instances, in both directions, then disables push
	 */
	@Test
	public void enabled() {
		// given
		navigateTo("notifications/push");
		navigateTo(driver2, "notifications/push");
		// when

		setTextField("a", "group", PushView.class, TextField.class);
		setTextField("a1", "message", PushView.class, TextField.class);
		clickButton("send", PushView.class, Button.class);
		// then
		assertThat(readTextArea(PushView.class, BroadcastMessageLog.class)).isEqualTo("a:a1\n");
		assertThat(readTextArea(driver2, PushView.class, BroadcastMessageLog.class)).isEqualTo("a:a1\n");
		assertThat(readLabel(DefaultMessageBar.class, Label.class)).isEqualTo("a:a1");

		// when
		setTextField("b", driver2, "group", PushView.class, TextField.class);
		setTextField("b1", driver2, "message", PushView.class, TextField.class);
		clickButton(driver2, "send", PushView.class, Button.class);

		// then
		assertThat(readTextArea(PushView.class, BroadcastMessageLog.class)).isEqualTo("b:b1\na:a1\n");
		assertThat(readTextArea(driver2, PushView.class, BroadcastMessageLog.class)).isEqualTo("b:b1\na:a1\n");
		assertThat(readLabel(DefaultMessageBar.class, Label.class)).isEqualTo("b:b1");

	}

	@Test
	public void disabled() {
		// given
		navigateTo("notifications/push");
		navigateTo(driver2, "notifications/push");
		clickCheckBox(PushView.class, CheckBox.class);
		pause(1000);
		// when

		setTextField("x", "group", PushView.class, TextField.class);
		setTextField("x1", "message", PushView.class, TextField.class);
		clickButton("send", PushView.class, Button.class);

		// then message not processed
		assertThat(readLabel(DefaultMessageBar.class, Label.class)).isEqualTo("Message bar");
		// when
	}

	@After
	public void teardown() {
		driver.close();
		driver2.close();
	}

}
