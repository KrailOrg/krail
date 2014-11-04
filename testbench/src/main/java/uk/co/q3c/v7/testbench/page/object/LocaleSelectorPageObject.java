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

package uk.co.q3c.v7.testbench.page.object;

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.ui.ComboBox;
import uk.co.q3c.v7.base.view.component.DefaultLocaleSelector;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import java.util.List;
import java.util.Locale;

/**
 * Created by David Sowerby on 19/10/14.
 */
public class LocaleSelectorPageObject extends PageObject {


    public LocaleSelectorPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    /**
     * Selecting the locale in the combo box is done by text string - that string will change depending on the local in
     * force at the time of the call.  To make this method work on platforms with different platform settings we need
     * to
     * specify the Locale which should be used to return the display name ({@code inLocale}
     *
     * @param locale
     * @param inLocale
     */
    public void selectLocale(Locale locale, Locale inLocale) {
        combo().selectByText(locale.getDisplayName(inLocale));
    }

    public ComboBoxElement combo() {
        return element(ComboBoxElement.class, Optional.absent(), DefaultLocaleSelector.class, ComboBox.class);
    }

    public String getValue() {
        return combo().getValue();
    }

    public List<String> getPopupSuggestions() {
        return combo().getPopupSuggestions();
    }
}
