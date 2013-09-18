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
package uk.co.q3c.v7.test.bench;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class V7TestBenchTestCase extends TestBenchTestCase {
	protected String baseUrl;
	protected final StringBuffer verificationErrors = new StringBuffer();

	@Before
	public void defaultSetup() {
		setDriver(TestBench.createDriver(new FirefoxDriver()));
		getDriver().manage().window().setPosition(new Point(0, 0));
		getDriver().manage().window().setSize(new Dimension(1024, 768));
	}

	protected String url(String fragment) {
		return rootUrl() + fragment;
	}

	private String rootUrl() {
		String rootUrl = concatUrl(baseUrl, "/?restartApplication#");
		return rootUrl;
	}

	protected void verifyUrl(String fragment) {
		String expected = rootUrl() + fragment;
		String actual = driver.getCurrentUrl();
		assertThat("expected fragment to be " + expected + " but was " + actual, actual, is(expected));
	}

	protected ElementLocator navTree() {
		return locator().id("DefaultUserNavigationTree");
	}

	protected String navTreeSelection() throws InterruptedException {
		Thread.sleep(1000);
		String selectedNodeText = getDriver().findElement(
				By.xpath("id('DefaultUserNavigationTree')//div[contains(@class, 'v-tree-node-selected')]")).getText();
		return selectedNodeText;
	}

	protected ElementLocator locator() {
		return new ElementLocator(driver);
	}

	protected void navigateTo(String fragment) {
		String url = url(fragment);
		driver.get(url);
	}
}
