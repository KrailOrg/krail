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
package uk.co.q3c.v7.testbench;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.view.DefaultLoginView;
import uk.co.q3c.v7.base.view.component.DefaultBreadcrumb;
import uk.co.q3c.v7.base.view.component.DefaultUserStatusPanel;
import uk.co.q3c.v7.base.view.component.DefaultSubpagePanel;
import uk.co.q3c.v7.base.view.component.NavigationButton;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class V7TestBenchTestCase extends TestBenchTestCase {
	private static Logger log = LoggerFactory.getLogger(V7TestBenchTestCase.class);
	protected String baseUrl = "http://localhost:8080/";
	protected String context = "testapp";
	protected final StringBuffer verificationErrors = new StringBuffer();

	@Before
	public void defaultSetup() {
		setDriver(TestBench.createDriver(new FirefoxDriver()));
		getDriver().manage().window().setPosition(new Point(0, 0));
		getDriver().manage().window().setSize(new Dimension(1024, 768));
	}

	protected String url(String fragment) {
		return rootUrl() + "/#" + fragment;
	}

	protected String rootUrl() {
		String rootUrl = buildUrl(baseUrl, context);
		return rootUrl;
	}

	/**
	 * @deprecated Use buildUrl() instead
	 * 
	 * @see com.vaadin.testbench.TestBenchTestCase#concatUrl(java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	protected String concatUrl(String baseUrl, String uri) {
		return super.concatUrl(baseUrl, uri);
	}

	protected String buildUrl(String... segments) {
		StringBuilder buf = new StringBuilder();
		boolean firstSegment = true;
		for (String segment : segments) {
			if (!firstSegment) {
				buf.append("/");
			} else {
				firstSegment = false;
			}
			buf.append(segment.replace("/", ""));
		}
		String result = buf.toString();
		// slashes will have been removed
		result = result.replace("http:", "http://");
		result = result.replace("https:", "https://");
		return result;
	}

	protected void verifyUrl(String fragment) {
		String expected = rootUrl() + "/#" + fragment;
		String actual = driver.getCurrentUrl();
		assertThat(actual).isEqualTo(expected);
	}

	protected void verifyNotUrl(String fragment) {
		String expected = rootUrl() + fragment;
		String actual = driver.getCurrentUrl();
		assertThat(actual).isNotEqualTo(expected);
	}

	protected UITree navTree() {
		return treeLocator().id("DefaultUserNavigationTree");
	}

	/**
	 * Returns the ElementLocator for the breadcrumb button at index
	 * 
	 * @param index
	 * @return
	 */
	protected ElementLocator breadcrumb(int index) {
		String idIndex = ID.getIdcIndex(index, DefaultBreadcrumb.class, NavigationButton.class);
		return locator().id(idIndex);
	}

	protected ElementLocator subpagepanel(int index) {
		String idIndex = ID.getIdcIndex(index, DefaultSubpagePanel.class, NavigationButton.class);
		return locator().id(idIndex);
	}

	protected String navTreeSelection() {
		try {
			String selectedNodeText = getDriver().findElement(
					By.xpath("id('DefaultUserNavigationTree')//div[contains(@class, 'v-tree-node-selected')]"))
					.getText();
			return selectedNodeText;
		} catch (Exception e) {
			return null;
		}
	}

	protected UITree treeLocator() {
		return new UITree(driver, context);
	}

	protected ElementLocator locator() {
		return new ElementLocator(driver, context);
	}

	protected void navigateTo(String fragment) {
		navigateTo(driver, fragment);
	}

	protected void navigateTo(WebDriver driver, String fragment) {
		String url = url(fragment);
		driver.get(url);
		pause(500);
	}

	protected WebElement element(String qualifier, Class<?>... classes) {
		return element(driver, qualifier, classes);
	}

	protected WebElement element(WebDriver driver, String qualifier, Class<?>... classes) {
		if (classes == null || classes.length == 0) {
			throw new RuntimeException("Id will fail with only a qualifier supplied.  Always use classes to define Id");
		}
		String s = id(qualifier, classes);
		WebElement findElement = driver.findElement(By.vaadin(s));
		return findElement;
	}

	protected WebElement element(Class<?>... classes) {
		return element(driver, classes);
	}

	protected WebElement element(WebDriver driver, Class<?>... classes) {
		return element(driver, null, classes);
	}

	protected String id(Class<?>... components) {
		ElementPath elementPath = new ElementPath(context);
		ElementPath id = elementPath.id(ID.getIdc(components));
		return id.get();
	}

	protected String id(String qualifier, Class<?>... components) {
		ElementPath elementPath = new ElementPath(context);
		ElementPath id = elementPath.id(ID.getIdc(qualifier, components));
		return id.get();
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
		pause(500);
	}

	protected void navigateBack() {
		driver.navigate().back();
		pause(500);
	}

	protected WebElement loginButton() {
		return element(DefaultUserStatusPanel.class, Button.class);
	}

	protected WebElement notification() {
		WebElement notification = getDriver().findElement(By.className("v-Notification"));
		return notification;
	}

	protected void closeNotification() {
		((TestBenchElementCommands) notification()).closeNotification();
	}

	public void fillLoginForm() {
		fillLoginForm("ds", "password");
	}

	public void fillLoginForm(String username, String password) {
		usernameBox().clear();
		usernameBox().sendKeys(username);
		passwordBox().clear();
		passwordBox().sendKeys(password);
		clickButton(null, DefaultLoginView.class, Button.class);
	}

	public void login() {
		loginButton().click();
		pause(100);
		fillLoginForm();
	}

	protected WebElement loginLabel() {
		return element(DefaultUserStatusPanel.class, Label.class);
	}

	protected WebElement usernameBox() {
		return element("username", DefaultLoginView.class, TextField.class);
	}

	protected WebElement passwordBox() {
		return element("password", DefaultLoginView.class, PasswordField.class);
	}

	protected WebElement submitButton() {
		return element(DefaultLoginView.class, Button.class);
	}

	/**
	 * This does assume that you are already logged in!
	 */
	protected void logout() {
		loginButton().click();
	}

	protected void clickButton(WebDriver driver, String qualifier, Class<?>... classes) {
		String id = id(qualifier, classes);
		WebElement element = driver.findElement(By.vaadin(id));
		pause(1000);
		element.click();
	}

	protected void clickButton(String qualifier, Class<?>... classes) {
		clickButton(driver, qualifier, classes);
	}

	protected String readTextArea(WebDriver driver, String qualifier, Class<?>... classes) {
		WebElement webElement = element(driver, qualifier, classes);
		return webElement.getAttribute("value");
	}

	protected String readTextArea(String qualifier, Class<?>... classes) {
		return readTextArea(driver, qualifier, classes);
	}

	protected String readTextArea(WebDriver driver, Class<?>... classes) {
		return readTextArea(driver, null, classes);
	}

	protected String readTextArea(Class<?>... classes) {
		return readTextArea(driver, null, classes);
	}

	protected String readLabel(WebDriver driver, String qualifier, Class<?>... classes) {
		WebElement webElement = element(driver, qualifier, classes);
		return webElement.getText();
	}

	protected String readLabel(String qualifier, Class<?>... classes) {
		return readLabel(driver, qualifier, classes);
	}

	protected String readLabel(WebDriver driver, Class<?>... classes) {
		return readLabel(driver, null, classes);
	}

	protected String readLabel(Class<?>... classes) {
		return readLabel(driver, null, classes);
	}

	protected void setTextField(String value, WebDriver driver, String qualifier, Class<?>... classes) {
		WebElement webElement = element(driver, qualifier, classes);
		webElement.sendKeys(value);
	}

	protected void setTextField(String value, String qualifier, Class<?>... classes) {
		setTextField(value, driver, qualifier, classes);
	}

	protected void setTextField(String value, Class<?>... classes) {
		setTextField(value, driver, null, classes);
	}

	protected void clickCheckBox(WebDriver driver, String qualifier, Class<?>... classes) {
		String id = id(qualifier, classes) + "/domChild[0]";
		WebElement element = driver.findElement(By.vaadin(id));
		element.click();
	}

	protected void clickCheckBox(String qualifier, Class<?>... classes) {
		clickCheckBox(driver, qualifier, classes);
	}

	protected void clickCheckBox(Class<?>... classes) {
		clickCheckBox(driver, null, classes);
	}

	protected String currentSelectionNavTree() {
		String sysaccount = "system-account";
		WebElement tree = navTree().getLocator().get();
		WebElement e5 = tree.findElement(By.partialLinkText(sysaccount));
		return e5.getText();
	}
}
