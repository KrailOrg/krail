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

import com.google.common.base.Optional;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.*;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
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
import uk.co.q3c.v7.base.view.component.DefaultLocaleSelector;
import uk.co.q3c.v7.base.view.component.DefaultUserStatusPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class V7TestBenchTestCase extends TestBenchTestCase {
    private static Logger log = LoggerFactory.getLogger(V7TestBenchTestCase.class);
    protected final StringBuffer verificationErrors = new StringBuffer();
    protected String baseUrl = "http://localhost:8080/";
    protected LoginStatusPageObject loginStatus = new LoginStatusPageObject(this);
    protected LoginFormPageObject loginForm = new LoginFormPageObject(this);
    protected String appContext = "testapp";
    private int currentDriverIndex = 1;
    private List<WebDriver> drivers = new ArrayList<>();


    @Before
    public void baseSetup() {
        System.out.println("setting up base test bench case");
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        getDriver().manage()
                   .window()
                   .setPosition(new Point(0, 0));
        getDriver().manage()
                   .window()
                   .setSize(new Dimension(1024, 768));
        System.out.println("default driver added");
        addDriver(getDriver());
        currentDriverIndex = 1;
        System.out.println("current driver index set to " + currentDriverIndex);
    }

    protected void addDriver(WebDriver driver) {
        System.out.println("adding driver " + drivers.size());
        drivers.add(driver);
    }

    @After
    public void baseTearDown() {
        System.out.println("closing all drivers");
        for (WebDriver webDriver : drivers) {
            webDriver.close();
            System.out.println(webDriver.getTitle() + " closed");
        }
        drivers.clear();
    }

    public String getAppContext() {
        return appContext;
    }

    protected void verifyUrl(String fragment) {
        String expected = rootUrl() + "/#" + fragment;
        String actual = driver.getCurrentUrl();
        assertThat(actual).isEqualTo(expected);
    }

    protected String rootUrl() {
        String rootUrl = buildUrl(baseUrl, appContext);
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

    protected void navigateTo(String fragment) {
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
        notification().closeNotification();
    }

    protected NotificationElement notification() {
        NotificationElement notification = $(NotificationElement.class).get(0);
        return notification;
    }


    /**
     * shorthand method to click the login button, and fill in the login form using credentials in {@link #loginForm}
     */
    protected void login() {
        loginStatus.loginButton()
                   .click();
        loginForm.login();
    }

    protected WebElement loginLabel() {
        return label(Optional.absent(), DefaultUserStatusPanel.class, Label.class);
    }


    protected LabelElement label(Optional<?> qualifier, Class<?>... componentClasses) {
        String id = ID.getIdc(qualifier, componentClasses);
        return label(id);
    }

    protected LabelElement label(String id) {
        LabelElement label = $(LabelElement.class).id(id);
        return label;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Use rarely - you should be able to use the more specialised versions;  this is a catch all for classes which do
     * not have an implementation of AbstractElement
     *
     * @param driver
     * @param qualifier
     * @param classes
     *
     * @return
     */
    protected WebElement element(Optional<?> qualifier, Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            throw new RuntimeException("Id will fail with only a qualifier supplied.  Always use classes to define Id");
        }
        String s = ID.getIdc(qualifier, classes);
        WebElement findElement = getDriver().findElement(By.vaadin(s));
        return findElement;
    }

    protected String id(Optional<?> qualifier, Class<?>... components) {
        ElementPath elementPath = new ElementPath(appContext);
        ElementPath id = elementPath.id(ID.getIdc(qualifier, components));
        return id.get();
    }


    //--
    protected String intStepperValue(Optional<?> qualifier, Class<?>... componentClasses) {
        IntStepperElement element = intStepper(qualifier, componentClasses);
        return element.getText();
    }

    protected IntStepperElement intStepper(Optional<?> qualifier, Class<?>... componentClasses) {
        IntStepperElement element = $(IntStepperElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }

    protected void setIntStepperValue(String text, Optional<?> qualifier, Class<?>... componentClasses) {
        IntStepperElement element = intStepper(qualifier, componentClasses);
        element.sendKeys(text);
    }

    //--
    protected String passwordFieldValue(Optional<?> qualifier, Class<?>... componentClasses) {
        PasswordFieldElement element = passwordField(qualifier, componentClasses);
        return element.getText();
    }

    protected PasswordFieldElement passwordField(Optional<?> qualifier, Class<?>... componentClasses) {
        PasswordFieldElement element = $(PasswordFieldElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }

    protected void setPasswordFieldValue(String text, Optional<?> qualifier, Class<?>... componentClasses) {
        PasswordFieldElement element = passwordField(qualifier, componentClasses);
        element.sendKeys(text);
    }


    protected <E extends AbstractElement> E element(Class<E> elementClass, Optional<?> qualifier,
                                                    Class<?>... componentClasses) {

        return element(elementClass, ID.getIdc(qualifier, componentClasses));

    }

    public <E extends AbstractElement> E element(Class<E> elementClass, String id) {

        return $(elementClass).id(id);

    }


    public PanelElement panel(Optional<?> qualifier, Class<?>... componentClasses) {
        PanelElement element = $(PanelElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }

    public HorizontalLayoutElement horizontalLayout(Optional<?> qualifier, Class<?>... componentClasses) {
        HorizontalLayoutElement element = $(HorizontalLayoutElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }

    public VerticalLayoutElement verticalLayout(Optional<?> qualifier, Class<?>... componentClasses) {
        VerticalLayoutElement element = $(VerticalLayoutElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }


    protected String comboValue(Optional<?> qualifier, Class<?>... componentClasses) {
        ComboBoxElement element = combo(qualifier, componentClasses);
        return element.getValue();
    }

    protected ComboBoxElement combo(Optional<?> qualifier, Class<?>... componentClasses) {
        ComboBoxElement element = $(ComboBoxElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }

    protected void selectComboValue(String valueToSelect, Optional<?> qualifier, Class<?>... componentClasses) {
        ComboBoxElement element = combo(qualifier, componentClasses);
        element.selectByText(valueToSelect);
    }

    protected void clickMenuItem(String[] path, Optional<?> qualifier, Class<?>... componentClasses) {
        menu(qualifier, componentClasses).clickItem(path);
    }

    protected MenuBarElement menu(Optional<?> qualifier, Class<?>... componentClasses) {
        MenuBarElement element = $(MenuBarElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }

    protected boolean checkboxValue(Optional<?> qualifier, Class<?>... componentClasses) {
        return checkbox(qualifier, componentClasses).getValue()
                                                    .equals("checked");
    }

    protected CheckBoxElement checkbox(Optional<?> qualifier, Class<?>... componentClasses) {
        String id = ID.getIdc(qualifier, componentClasses);
        return $(CheckBoxElement.class).id(id);
    }

    protected void clickCheckBox(Optional<?> qualifier, Class<?>... componentClasses) {
        checkbox(qualifier, componentClasses).click();
    }

    protected String textAreaValue(Optional<?> qualifier, Class<?>... componentClasses) {
        return textArea(qualifier, componentClasses).getValue();
    }

    protected TextAreaElement textArea(Optional<?> qualifier, Class<?>... componentClasses) {
        TextAreaElement element = $(TextAreaElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }

    protected void setTextAreaValue(String text, Optional<?> qualifier, Class<?>... componentClasses) {
        textArea(qualifier, componentClasses).sendKeys(text);
    }

    protected String localeSelectorValue() {
        return localeSelector().getValue();
    }

    protected ComboBoxElement localeSelector() {
        return combo(Optional.absent(), DefaultLocaleSelector.class, ComboBox.class);
    }

    protected void selectLocale(Locale locale) {
        localeSelector().selectByText(locale.getDisplayName());
    }

    protected TreeElement tree(Optional<?> qualifier, Class<?>... componentClasses) {
        TreeElement element = $(TreeElement.class).id(ID.getIdc(qualifier, componentClasses));
        return element;
    }


    ;

    /**
     * Indexed from 1 (that is, the default driver is at index 1)
     *
     * @param index
     *
     * @return
     */
    public WebDriver selectDriver(int index) {
        try {
            WebDriver wd = drivers.get(index - 1);
            currentDriverIndex = index;
            setDriver(wd);
            System.out.println("Driver index " + index + " selected");
            return driver;
        } catch (Exception e) {
            throw new RuntimeException("Driver index of " + index + " is invalid");
        }
    }
}
