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

package uk.co.q3c.v7.testbench.page.object;

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import uk.co.q3c.v7.base.view.DefaultLoginView;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;


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
     * Sets the credentials you want to use to log in.  If not set, the default is used when you call {@link #login()}.
     * Alternatively you can set the username and password independently and then click the submit button.
     *
     * @param credentials
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Log in using {@link #credentials}
     */
    public void login() {
        login(credentials.username, credentials.password);
    }

    public void login(String username, String password) {
        usernameBox().clear();
        usernameBox().sendKeys(username);
        passwordBox().clear();
        passwordBox().sendKeys(password);
        submitButton().click();

    }

    protected TextFieldElement usernameBox() {
        return element(TextFieldElement.class, Optional.of("username"), DefaultLoginView.class, TextField.class);
    }

    protected PasswordFieldElement passwordBox() {
        return element(PasswordFieldElement.class, Optional.of("password"), DefaultLoginView.class,
                PasswordField.class);
    }

    private ButtonElement submitButton() {
        return element(ButtonElement.class, Optional.absent(), DefaultLoginView.class, Button.class);
    }

    protected String submitButtonText() {
        return submitButton().getCaption();
    }

    public String message() {
        return messageLabel().getText();
    }

    public LabelElement messageLabel() {
        return element(LabelElement.class, Optional.of("status"), DefaultLoginView.class, Label.class);
    }

    public static class Credentials {
        private String password = "password";
        private String username = "ds";
    }


}
