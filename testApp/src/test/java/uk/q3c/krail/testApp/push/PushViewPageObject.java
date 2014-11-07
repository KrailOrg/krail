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

package uk.q3c.krail.testApp.push;

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import uk.q3c.krail.base.view.component.BroadcastMessageLog;
import uk.q3c.krail.testapp.view.PushView;
import uk.q3c.krail.testbench.V7TestBenchTestCase;
import uk.q3c.krail.testbench.page.object.PageObject;

/**
 * PageObject representing {@link PushView} used to assist the {@link Push_Functional} test
 * <p/>
 * Created by David Sowerby on 18/10/14.
 */
public class PushViewPageObject extends PageObject {

    public PushViewPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    public TextFieldElement groupBox() {
        return element(TextFieldElement.class, Optional.of("group"), PushView.class, TextField.class);
    }

    public TextFieldElement messageBox() {
        return element(TextFieldElement.class, Optional.of("message"), PushView.class, TextField.class);
    }

    public ButtonElement sendButton() {
        return element(ButtonElement.class, Optional.of("send"), PushView.class, Button.class);
    }

    public TextAreaElement messageLog() {
        return element(TextAreaElement.class, Optional.absent(), PushView.class, BroadcastMessageLog.class);
    }

    public CheckBoxElement checkbox() {
        return element(CheckBoxElement.class, Optional.absent(), PushView.class, CheckBox.class);
    }

}
