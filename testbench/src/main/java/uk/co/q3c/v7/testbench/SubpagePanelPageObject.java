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

package uk.co.q3c.v7.testbench;

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.HorizontalLayoutElement;
import uk.co.q3c.v7.base.view.component.DefaultSubpagePanel;
import uk.co.q3c.v7.base.view.component.NavigationButton;

import java.util.Arrays;
import java.util.List;

/**
 * Created by david on 04/10/14.
 */
public class SubpagePanelPageObject extends PageObject {

    public SubpagePanelPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    public ButtonPageElement button(int index) {
        return new ButtonPageElement(parentCase, Optional.of(index), DefaultSubpagePanel.class, NavigationButton.class);
    }

    public List<String> buttonLabels() {
        HorizontalLayoutElement panelElement = parentCase.horizontalLayout(Optional.absent(),
                DefaultSubpagePanel.class);
        parentCase.pause(500);
        String[] labelSet = panelElement.getText()
                                        .split("\\\n");
        List<String> labels = Arrays.asList(labelSet);
        return labels;
    }
}
