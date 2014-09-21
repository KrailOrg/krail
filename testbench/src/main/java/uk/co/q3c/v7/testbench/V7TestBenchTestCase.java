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

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.junit.After;
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
import uk.co.q3c.v7.base.view.component.*;

import static org.assertj.core.api.Assertions.assertThat;

public class V7TestBenchTestCase extends TestBenchTestCase {
    private static Logger log = LoggerFactory.getLogger(V7TestBenchTestCase.class);
    protected final StringBuffer verificationErrors = new StringBuffer();
    protected String baseUrl = "http://localhost:8080/";
    protected String context = "testapp";

    @Before
    public void defaultSetup() {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        getDriver().manage()
                   .window()
                   .setPosition(new Point(0, 0));
        getDriver().manage()
                   .window()
                   .setSize(new Dimension(1024, 768));
    }

    @After
    public void tearDown() {
        driver.close();
    }

    /**
     * @see com.vaadin.testbench.TestBenchTestCase#concatUrl(java.lang.String, java.lang.String)
     * @deprecated Use buildUrl() instead
     */
    @Override
    @Deprecated
    protected String concatUrl(String baseUrl, String uri) {
        return super.concatUrl(baseUrl, uri);
    }

    protected void verifyUrl(String fragment) {
        String expected = rootUrl() + "/#" + fragment;
        String actual = driver.getCurrentUrl();
        assertThat(actual).isEqualTo(expected);
    }

    protected String rootUrl() {
        String rootUrl = buildUrl(baseUrl, context);
        return rootUrl;
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

    protected void verifyNotUrl(String fragment) {
        String expected = rootUrl() + fragment;
        String actual = driver.getCurrentUrl();
        assertThat(actual).isNotEqualTo(expected);
    }

    /**
     * Returns the ElementLocator for the breadcrumb button at index
     *
     * @param index
     *
     * @return
     */
    protected ElementLocator breadcrumb(int index) {
        String idIndex = ID.getIdcIndex(index, DefaultBreadcrumb.class, NavigationButton.class);
        return locator().id(idIndex);
    }

    protected ElementLocator locator() {
        return new ElementLocator(driver, context);
    }

    protected ElementLocator subpagepanel(int index) {
        String idIndex = ID.getIdcIndex(index, DefaultSubpagePanel.class, NavigationButton.class);
        return locator().id(idIndex);
    }

    protected String navTreeSelection() {
        try {
            String selectedNodeText = getDriver().findElement(By.xpath("id('DefaultUserNavigationTree')//div[contains" +
                    "(@class, 'v-tree-node-selected')]"))
                                                 .getText();
            return selectedNodeText;
        } catch (Exception e) {
            return null;
        }
    }

    protected void navigateTo(String fragment) {
        navigateTo(driver, fragment);
    }

    protected void navigateTo(WebDriver driver, String fragment) {
        String url = url(fragment);
        driver.get(url);
        pause(500);
    }

    protected String url(String fragment) {
        return rootUrl() + "/#" + fragment;
    }

    protected void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            log.error("Sleep was interrupted");
        }
    }

    protected WebElement element(String qualifier, Class<?>... classes) {
        return element(driver, qualifier, classes);
    }

    protected String id(Class<?>... components) {
        ElementPath elementPath = new ElementPath(context);
        ElementPath id = elementPath.id(ID.getIdc(components));
        return id.get();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected void navigateForward() {
        driver.navigate()
              .forward();
        pause(500);
    }

    protected void navigateBack() {
        driver.navigate()
              .back();
        pause(500);
    }

    protected void closeNotification() {
        ((TestBenchElementCommands) notification()).closeNotification();
    }

    protected WebElement notification() {
        WebElement notification = getDriver().findElement(By.className("v-Notification"));
        return notification;
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

    protected WebElement element(Class<?>... classes) {
        return element(driver, classes);
    }

    protected WebElement element(WebDriver driver, Class<?>... classes) {
        return element(driver, null, classes);
    }

    protected WebElement element(WebDriver driver, String qualifier, Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            throw new RuntimeException("Id will fail with only a qualifier supplied.  Always use classes to define Id");
        }
        String s = id(qualifier, classes);
        WebElement findElement = driver.findElement(By.vaadin(s));
        return findElement;
    }

    protected String id(String qualifier, Class<?>... components) {
        ElementPath elementPath = new ElementPath(context);
        ElementPath id = elementPath.id(ID.getIdc(qualifier, components));
        return id.get();
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

    protected WebElement loginButton() {
        return element(DefaultUserStatusPanel.class, Button.class);
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

    protected String readTextArea(String qualifier, Class<?>... classes) {
        return readTextArea(driver, qualifier, classes);
    }

    protected String readTextArea(WebDriver driver, String qualifier, Class<?>... classes) {
        WebElement webElement = element(driver, qualifier, classes);
        return webElement.getAttribute("value");
    }

    protected String readTextArea(WebDriver driver, Class<?>... classes) {
        return readTextArea(driver, null, classes);
    }

    protected String readTextArea(Class<?>... classes) {
        return readTextArea(driver, null, classes);
    }

    protected String readLabel(String qualifier, Class<?>... classes) {
        return readLabel(driver, qualifier, classes);
    }

    protected String readLabel(WebDriver driver, String qualifier, Class<?>... classes) {
        WebElement webElement = element(driver, qualifier, classes);
        return webElement.getText();
    }

    protected String readLabel(WebDriver driver, Class<?>... classes) {
        return readLabel(driver, null, classes);
    }

    protected String readLabel(Class<?>... classes) {
        return readLabel(driver, null, classes);
    }

    protected void setTextField(String value, String qualifier, Class<?>... classes) {
        setTextField(value, driver, qualifier, classes);
    }

    protected void setTextField(String value, WebDriver driver, String qualifier, Class<?>... classes) {
        WebElement webElement = element(driver, qualifier, classes);
        webElement.sendKeys(value);
    }

    protected void setTextField(String value, Class<?>... classes) {
        setTextField(value, driver, null, classes);
    }

    protected void clickCheckBox(String qualifier, Class<?>... classes) {
        clickCheckBox(driver, qualifier, classes);
    }

    protected void clickCheckBox(WebDriver driver, String qualifier, Class<?>... classes) {
        String id = id(qualifier, classes) + "/domChild[0]";
        WebElement element = driver.findElement(By.vaadin(id));
        element.click();
    }

    protected void clickCheckBox(Class<?>... classes) {
        clickCheckBox(driver, null, classes);
    }

    protected String currentSelectionNavTree() {
        String sysaccount = "system-account";
        WebElement tree = navTree().getLocator()
                                   .get();
        WebElement e5 = tree.findElement(By.partialLinkText(sysaccount));
        return e5.getText();
    }

    protected UITree navTree() {
        return treeLocator().id("DefaultUserNavigationTree");
    }

    protected UITree treeLocator() {
        return new UITree(driver, context);
    }

    protected String comboValue(Class<?>... classes) {
        return comboValue(null, classes);
    }

    protected String comboValue(String qualifier, Class<?>... classes) {
        String id = id(qualifier, classes) + "#textbox";
        WebElement element = driver.findElement(By.vaadin(id));
        String value = element.getAttribute("value");
        return value;
    }

    /**
     * reads the text (caption) of the top level navigation menu item, as selected by {@code index}
     *
     * @param index
     *
     * @return
     */
    protected String navMenuItem(int index) {
        String id = id((String) null, DefaultUserNavigationMenu.class) + "#item" + index;
        WebElement element = driver.findElement(By.vaadin(id));
        return element.getText();
    }

    protected void navMenuClick(int index) {
        String id = id((String) null, DefaultUserNavigationMenu.class) + "#item" + index;
        WebElement element = driver.findElement(By.vaadin(id));
        TestBenchElementCommands telement = testBenchElement(element);
        telement.click(20, 20);
    }

    protected void localeSelect(int index) {
        testBenchElement(driver.findElement(By.vaadin("testapp::PID_SDefaultLocaleSelector-ComboBox#button"))).click
                (17, 15);
        testBenchElement(driver.findElement(By.xpath("//div[@id='VAADIN_COMBOBOX_OPTIONLIST']/div/div[2]/table/tbody/tr[" + index + "]/td"))).click(7, 7);
    }

    protected String navTreeItemCaption(int index) {
        return navTree().index(index)
                        .get()
                        .getText();
    }
}
