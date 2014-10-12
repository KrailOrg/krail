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

package uk.co.q3c.v7.testbench;

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.ui.Button;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.openqa.selenium.WebElement;
import uk.co.q3c.v7.base.view.DefaultLoginView;

/**
 * Created by david on 03/10/14.
 */
public class LoginFormPageObject extends PageObject {

    private Credentials credentials = new Credentials();

    /**
     * Initialises the PageObject with a reference to the parent test case, so that the PageObject can access a number
     * of variables from the parent, for example: drivers, baseUrl, application appContext.
     *
     * @param parentCase
     */
    public LoginFormPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the credentials you want to use to log in.  If not set, the default is used
     *
     * @param credentials
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Log in using the credentials in {@link #credentials}
     */
    public void login() {
        login(credentials.username, credentials.password);
    }

    public void login(String username, String password) {
        usernameBox().clear();
        usernameBox().sendKeys(username);
        passwordBox().clear();
        passwordBox().sendKeys(password);
        ButtonElement button = parentCase.button(Optional.absent(), DefaultLoginView.class, Button.class);
        button.click();

    }

    protected WebElement usernameBox() {
        return parentCase.element(Optional.of("username"), DefaultLoginView.class, TextField.class);
    }

    protected WebElement passwordBox() {
        return parentCase.element(Optional.of("password"), DefaultLoginView.class, PasswordField.class);
    }

    protected String submitButtonText() {
        return parentCase.button(Optional.absent(), DefaultLoginView.class, Button.class)
                         .getCaption();
    }

    public static class Credentials {
        private String password = "password";
        private String username = "ds";
    }

}
