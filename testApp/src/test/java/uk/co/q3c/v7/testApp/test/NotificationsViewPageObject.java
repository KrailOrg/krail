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

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.ui.Button;
import uk.co.q3c.v7.testapp.view.NotificationsView;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;
import uk.co.q3c.v7.testbench.page.object.PageObject;

/**
 * Created by David Sowerby on 19/10/14.
 */
public class NotificationsViewPageObject extends PageObject {

    public NotificationsViewPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }


    public ButtonElement errorButton() {
        return element(ButtonElement.class, Optional.of("error"), NotificationsView.class, Button.class);
    }

    public ButtonElement warningButton() {
        return element(ButtonElement.class, Optional.of("warning"), NotificationsView.class, Button.class);
    }

    public ButtonElement informationButton() {
        return element(ButtonElement.class, Optional.of("information"), NotificationsView.class, Button.class);
    }
}
