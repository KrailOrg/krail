/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.ui;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({DataTypeModule.class})
public class DefaultDataTypeToUITest {

    @Inject
    DataTypeToUI dataTypeToUI;

    @Test
    public void componentForClass() {
        //given

        //when

        //then
        assertThat(dataTypeToUI.componentFor(Date.class)).isInstanceOf(DateField.class);
        assertThat(dataTypeToUI.componentFor(Integer.class)).isInstanceOf(TextField.class);
        assertThat(dataTypeToUI.componentFor(int.class)).isInstanceOf(TextField.class);
        assertThat(dataTypeToUI.componentFor(Boolean.class)).isInstanceOf(CheckBox.class);
        assertThat(dataTypeToUI.componentFor(boolean.class)).isInstanceOf(CheckBox.class);
        assertThat(dataTypeToUI.componentFor(String.class)).isInstanceOf(TextField.class);
    }

    @Test
    public void componentForObject() {
        //given

        //when

        //then
        assertThat(dataTypeToUI.componentFor(new Date())).isInstanceOf(DateField.class);
        assertThat(dataTypeToUI.componentFor(new Integer(5))).isInstanceOf(TextField.class);
        assertThat(dataTypeToUI.componentFor(5)).isInstanceOf(TextField.class);
        assertThat(dataTypeToUI.componentFor(new Boolean(true))).isInstanceOf(CheckBox.class);
        assertThat(dataTypeToUI.componentFor(false)).isInstanceOf(CheckBox.class);
        assertThat(dataTypeToUI.componentFor("string")).isInstanceOf(TextField.class);
    }

    @Test
    public void zeroValue() throws Exception {
        assertThat(dataTypeToUI.zeroValue(Integer.class)).isEqualTo(0);
        assertThat(dataTypeToUI.zeroValue(Boolean.class)).isFalse();
        assertThat(dataTypeToUI.zeroValue(Date.class)).isCloseTo(new Date(), 100);


    }
}