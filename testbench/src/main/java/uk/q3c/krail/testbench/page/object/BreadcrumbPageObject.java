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

package uk.q3c.krail.testbench.page.object;

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.ButtonElement;
import uk.q3c.krail.base.view.component.DefaultBreadcrumb;
import uk.q3c.krail.base.view.component.NavigationButton;
import uk.q3c.krail.testbench.V7TestBenchTestCase;
import uk.q3c.krail.testbench.page.element.DefaultBreadcrumbElement;

/**
 * Created by david on 04/10/14.
 */
public class BreadcrumbPageObject extends PageObject {

    /**
     * Test object to represent a {@link DefaultBreadcrumb}
     *
     * @param parentCase
     */
    public BreadcrumbPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    public ButtonElement button(int index) {
        return element(ButtonElement.class, Optional.of(index), DefaultBreadcrumb.class, NavigationButton.class);
    }

    public DefaultBreadcrumbElement breadcrumb() {
        return element(DefaultBreadcrumbElement.class, Optional.absent(), DefaultBreadcrumb.class);
    }
}
