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

package uk.co.q3c.v7.testApp.push;

import com.google.common.base.Optional;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import uk.co.q3c.v7.base.view.component.BroadcastMessageLog;
import uk.co.q3c.v7.testapp.view.PushView;
import uk.co.q3c.v7.testbench.*;

/**
 * PageObject representing {@link PushView} used to assist the {@link Push_Functional} test
 * <p/>
 * Created by David Sowerby on 18/10/14.
 */
public class PushViewPageObject extends PageObject {

    public PushViewPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    public TextFieldPageElement groupBox() {
        return new TextFieldPageElement(parentCase, Optional.of("group"), PushView.class, TextField.class);
    }

    public TextFieldPageElement messageBox() {
        return new TextFieldPageElement(parentCase, Optional.of("message"), PushView.class, TextField.class);
    }

    public ButtonPageElement sendButton() {
        return new ButtonPageElement(parentCase, Optional.of("send"), PushView.class, Button.class);
    }

    public TextAreaPageElement messageLog() {
        return new TextAreaPageElement(parentCase, Optional.absent(), PushView.class, BroadcastMessageLog.class);
    }

    public CheckboxPageElement checkbox() {
        return new CheckboxPageElement(parentCase, Optional.absent(), PushView.class, CheckBox.class);
    }

}
