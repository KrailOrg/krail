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
package uk.co.q3c.v7.testApp.push;

import com.google.common.base.Optional;
import com.vaadin.testbench.TestBench;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import uk.co.q3c.v7.base.view.component.BroadcastMessageLog;
import uk.co.q3c.v7.base.view.component.DefaultMessageBar;
import uk.co.q3c.v7.testapp.view.PushView;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class Push_Functional extends V7TestBenchTestCase {

    private WebDriver driver2;

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
        setTextFieldValue("a", Optional.of("group"), PushView.class, TextField.class);
        setTextFieldValue("a1", Optional.of("message"), PushView.class, TextField.class);
        clickButton(Optional.of("send"), PushView.class, Button.class);
        // then
        assertThat(textAreaValue(Optional.absent(), PushView.class, BroadcastMessageLog.class)).isEqualTo("a:a1\n");
        assertThat(labelText(Optional.absent(), DefaultMessageBar.class, Label.class)).isEqualTo("a:a1");
        selectDriver(2);
        assertThat(textAreaValue(Optional.absent(), PushView.class, BroadcastMessageLog.class)).isEqualTo("a:a1\n");


        // when
        setTextFieldValue("b", Optional.of("group"), PushView.class, TextField.class);
        setTextFieldValue("b1", Optional.of("message"), PushView.class, TextField.class);
        clickButton(Optional.of("send"), PushView.class, Button.class);

        // then
        assertThat(textAreaValue(Optional.absent(), PushView.class, BroadcastMessageLog.class)).isEqualTo
                ("b:b1\na:a1\n");

        selectDriver(1);
        assertThat(textAreaValue(Optional.absent(), PushView.class, BroadcastMessageLog.class)).isEqualTo
                ("b:b1\na:a1\n");
        assertThat(labelText(Optional.absent(), DefaultMessageBar.class, Label.class)).isEqualTo("b:b1");

    }

    @Test
    public void disabled() {
        // given
        navigateTo("notifications/push");
        selectDriver(2);
        navigateTo("notifications/push");
        selectDriver(1);
        clickCheckBox(Optional.absent(), PushView.class, CheckBox.class);
        pause(1000);
        // when

        setTextFieldValue("x", Optional.of("group"), PushView.class, TextField.class);
        setTextFieldValue("x1", Optional.of("message"), PushView.class, TextField.class);
        clickButton(Optional.of("send"), PushView.class, Button.class);

        // then message not processed
        assertThat(labelText(Optional.absent(), DefaultMessageBar.class, Label.class)).isEqualTo("Message bar");
        // when
    }

    @After
    public void teardown() {
        driver.close();
        driver2.close();
    }

}
