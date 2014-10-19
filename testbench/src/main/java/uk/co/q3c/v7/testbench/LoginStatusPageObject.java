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
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import uk.co.q3c.v7.base.view.component.DefaultUserStatusPanel;

/**
 * Created by david on 03/10/14.
 */
public class LoginStatusPageObject extends PageObject {
    /**
     * Initialises the PageObject with a reference to the parent test case, so that the PageObject can access a number
     * of variables from the parent, for example: drivers, baseUrl,
     * application appContext.
     * <p/>
     * <p/>
     * Note that all calls requiring eventual access to a WebDriver should be made via the parentCase,
     * so that the correct driver is acted on.
     *
     * @param parentCase
     */
    public LoginStatusPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    /**
     * Clicks the login / out button - will only log you out if you are logged in
     */
    protected void logout() {
        loginButton().click();
    }

    public ButtonPageElement loginButton() {
        return new ButtonPageElement(parentCase, Optional.absent(), DefaultUserStatusPanel.class, Button.class);
    }


    /**
     * Should open the login form
     */
    public void clickButton() {
        loginButton().click();
    }

    public String username() {
        return usernameLabel().getText();
    }

    public LabelPageElement usernameLabel() {
        return new LabelPageElement(parentCase, Optional.absent(), DefaultUserStatusPanel.class, Label.class);
    }
}
