/*
 * Copyright (C) 2014 David Sowerby
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
package uk.q3c.krail.testApp.push;

import com.vaadin.testbench.TestBench;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import uk.q3c.krail.testbench.KrailTestBenchTestCase;
import uk.q3c.krail.testbench.page.object.MessageBarPageObject;

import static org.assertj.core.api.Assertions.assertThat;

public class Push_Functional extends KrailTestBenchTestCase {

    private WebDriver driver2;
    private MessageBarPageObject messageBar = new MessageBarPageObject(this);
    private PushViewPageObject pushView = new PushViewPageObject(this);

    @Before
    public void setup() {
        selectDriver(1);
        driver2 = TestBench.createDriver(new FirefoxDriver());
        driver2.manage()
               .window()
               .setPosition(new Point(1026, 0));
        driver2.manage()
               .window()
               .setSize(new Dimension(1024, 768));
        addDriver(driver2);
    }

    /**
     * Passes messages between 2 browser instances, in both directions, then disables push
     */
    @Test
    public void enabled() {
        // given
        navigateTo("notifications/push");
        selectDriver(2);
        navigateTo("notifications/push");
        // when

        selectDriver(1);
        pushView.groupBox()
                .sendKeys("a");
        pushView.messageBox()
                .sendKeys("a1");
        pushView.sendButton()
                .click();
        // then

        assertThat(pushView.messageLog()
                           .getValue()).isEqualTo("a:a1\n");
        assertThat(messageBar.message()).isEqualTo("a:a1");
        selectDriver(2);
        assertThat(pushView.messageLog()
                           .getValue()).isEqualTo("a:a1\n");


        // when
        pushView.groupBox()
                .sendKeys("b");
        pushView.messageBox()
                .sendKeys("b1");
        pushView.sendButton()
                .click();

        // then
        assertThat(pushView.messageLog()
                           .getValue()).isEqualTo("b:b1\na:a1\n");

        selectDriver(1);
        assertThat(pushView.messageLog()
                           .getValue()).isEqualTo("b:b1\na:a1\n");
        assertThat(messageBar.message()).isEqualTo("b:b1");

    }

    @Test
    public void disabled() {
        // given
        navigateTo("notifications/push");
        selectDriver(2);
        navigateTo("notifications/push");
        selectDriver(1);
        pushView.checkbox()
                .click();
        pause(1000);
        // when

        pushView.groupBox()
                .sendKeys("x");
        pushView.messageBox()
                .sendKeys("x1");

        pushView.sendButton()
                .click();

        // then message not processed
        assertThat(messageBar.message()).isEqualTo("Message Bar");
    }


}
