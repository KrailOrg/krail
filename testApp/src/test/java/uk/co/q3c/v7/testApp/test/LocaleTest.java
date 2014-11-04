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

package uk.co.q3c.v7.testApp.test;

import org.junit.Before;
import org.junit.Test;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;
import uk.co.q3c.v7.testbench.page.object.LocaleSelectorPageObject;
import uk.co.q3c.v7.testbench.page.object.LoginFormPageObject;
import uk.co.q3c.v7.testbench.page.object.NavMenuPageObject;
import uk.co.q3c.v7.testbench.page.object.NavTreePageObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 09/07/14.
 */

public class LocaleTest extends V7TestBenchTestCase {

    private LocaleSelectorPageObject localeSelector = new LocaleSelectorPageObject(this);
    private LoginFormPageObject loginForm = new LoginFormPageObject(this);
    private NavMenuPageObject navMenu = new NavMenuPageObject(this);
    private NavTreePageObject navTree = new NavTreePageObject(this);

    @Before
    public void setUp() throws Exception {
        driver.get(rootUrl());
    }


    @Test
    public void defaultLocale() {

        // given

        // when

        // then
        String comboValue = localeSelector.getValue();
        assertThat(comboValue).isEqualTo("English (United Kingdom)");
    }

    @Test
    public void switchToGerman_UIComponents() {

        // given

        // when
        localeSelector.selectLocale(Locale.GERMANY);
        pause(500);

        // then
        String comboValue = localeSelector.getValue();
        assertThat(comboValue).isEqualTo(Locale.GERMANY.getDisplayName());
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(navTree.itemCaption(i));
        }
        assertThat(items).containsExactly("Benachrichtigungen", "Einloggen", "Nachrichtenfeld",
                "Öffentliche Startseite", "Systemkonto");

        items.clear();
        for (int i = 0; i < 5; i++) {
            String s = navMenu.item(i);
            items.add(s);
        }

        //this is in a different order to navtree. See https://github.com/davidsowerby/v7/issues/257
        assertThat(items).containsExactly("Benachrichtigungen", "Einloggen", "Nachrichtenfeld", "Systemkonto",
                "Öffentliche Startseite");

        assertThat(loginStatus.loginButton()
                              .getText()).isEqualTo("einloggen");
        assertThat(loginStatus.username()).isEqualTo("Gast");
    }

    @Test
    public void switchLanguages_View() {
        //given
        localeSelector.selectLocale(Locale.UK);
        pause(500);
        navigateTo("login");
        //when

        //then
        assertThat(loginForm.submitButton()
                            .getCaption()).isEqualTo("Submit");
        assertThat(loginForm.passwordBox()
                            .getCaption()).isEqualTo("Password");
        assertThat(loginForm.usernameBox()
                            .getCaption()).isEqualTo("User Name");

        //when
        localeSelector.selectLocale(Locale.GERMANY);
        pause(500);

        //then
        assertThat(loginForm.submitButton()
                            .getCaption()).isEqualTo("Absenden");
        assertThat(loginForm.passwordBox()
                            .getCaption()).isEqualTo("Passwort");
        assertThat(loginForm.usernameBox()
                            .getCaption()).isEqualTo("Benutzername");

    }

    /**
     * Have the languages from the I18NModule been loaded into the LocaleSelector?
     */
    @Test
    public void popup() {
        //given

        //when

        //then
        assertThat(localeSelector.getPopupSuggestions()).containsOnly("English (United Kingdom)", "German (Germany)",
                "Italian (Italy)");
    }


}