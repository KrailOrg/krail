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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ID;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class V7TestBenchTestCase extends TestBenchTestCase {
	private static Logger log = LoggerFactory.getLogger(V7TestBenchTestCase.class);
	protected final String rootIdStem = "ROOT::PID_S";
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

	protected String navTreeSelection() {

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

	protected WebElement element(String qualifier, Class<?>... classes) {
		String s = id(qualifier, classes);
		WebElement findElement = driver.findElement(By.vaadin(s));
		return findElement;
	}

	protected WebElement element(Class<?>... classes) {
		return element(null, classes);
	}

	protected String id(Class<?>... components) {
		return "ROOT::PID_S" + ID.getIdc(components);
	}

	protected String id(String qualifier, Class<?>... components) {
		return "ROOT::PID_S" + ID.getIdc(qualifier, components);
	}

	protected void pause(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (Exception e) {
			log.error("Sleep was interrupted");
		}
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	protected void navigateForward() {
		driver.navigate().forward();
	}

	protected void navigateBack() {
		driver.navigate().back();
	}
}
