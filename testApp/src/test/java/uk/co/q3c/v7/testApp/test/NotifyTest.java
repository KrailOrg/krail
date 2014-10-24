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
package uk.co.q3c.v7.testApp.test;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;
import uk.co.q3c.v7.testbench.page.object.MessageBarPageObject;

import static org.assertj.core.api.Assertions.assertThat;

public class NotifyTest extends V7TestBenchTestCase {

    private final String testPage = "notifications";
    @Rule
    public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);
    private MessageBarPageObject messageBar = new MessageBarPageObject(this);
    private NotificationsViewPageObject notificationsView = new NotificationsViewPageObject(this);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void defaultMessage() {

        // given
        navigateTo(testPage);
        // when

        // then
        assertThat(messageBar.message()).isEqualTo("Message Bar");
    }

    @Test
    public void notifyError() {

        // given
        navigateTo(testPage);

        // when
        notificationsView.errorButton()
                         .click();
        // then

        assertThat(messageBar.message()).isEqualTo("ERROR: You cannot use service Fake Service until it has been " +
                "started");
        assertThat(notification()).isNotNull();
        assertThat(notification().getText()).isEqualTo("You cannot use service Fake Service until it has been started");
        assertThat(notification().getAttribute("class")).isEqualTo("v-Notification error v-Notification-error");
        closeNotification();

    }

    @Test
    public void notifyWarning() {
        // given
        navigateTo(testPage);
        // when
        notificationsView.warningButton()
                         .click();
        // then
        assertThat(messageBar.message()).isEqualTo("Warning: You cannot use service Fake Service until it has been " +
                "" + "started");
        assertThat(notification()).isNotNull();
        assertThat(notification().getText()).isEqualTo("You cannot use service Fake Service until it has been started");
        assertThat(notification().getAttribute("class")).isEqualTo("v-Notification warning v-Notification-warning");
        closeNotification();
    }

    @Test
    public void notifyInformation() {
        // given
        navigateTo(testPage);

        // when
        notificationsView.informationButton()
                         .click();
        // then
        assertThat(messageBar.message()).isEqualTo("You cannot use service Fake Service until it has been started");
        assertThat(notification()).isNotNull();
        assertThat(notification().getText()).isEqualTo("You cannot use service Fake Service until it has been started");
        assertThat(notification().getAttribute("class")).isEqualTo("v-Notification humanized v-Notification-humanized");
        closeNotification();
    }
}
