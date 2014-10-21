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
import org.vaadin.risto.stepper.IntStepper;
import uk.co.q3c.v7.testapp.view.WidgetsetView;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;
import uk.co.q3c.v7.testbench.page.element.IntStepperElement;
import uk.co.q3c.v7.testbench.page.object.PageObject;

/**
 * Created by David Sowerby on 19/10/14.
 */
public class WidgetSetViewPageObject extends PageObject {

    public WidgetSetViewPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    public IntStepperElement stepper() {
        return element(IntStepperElement.class, Optional.absent(), WidgetsetView.class, IntStepper.class);
    }
}
