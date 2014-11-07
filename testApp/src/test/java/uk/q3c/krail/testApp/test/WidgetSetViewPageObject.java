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

package uk.q3c.krail.testApp.test;

import com.google.common.base.Optional;
import org.vaadin.risto.stepper.IntStepper;
import uk.q3c.krail.testapp.view.WidgetsetView;
import uk.q3c.krail.testbench.KrailTestBenchTestCase;
import uk.q3c.krail.testbench.page.element.IntStepperElement;
import uk.q3c.krail.testbench.page.object.PageObject;

/**
 * Created by David Sowerby on 19/10/14.
 */
public class WidgetSetViewPageObject extends PageObject {

    public WidgetSetViewPageObject(KrailTestBenchTestCase parentCase) {
        super(parentCase);
    }

    public IntStepperElement stepper() {
        return element(IntStepperElement.class, Optional.absent(), WidgetsetView.class, IntStepper.class);
    }
}
