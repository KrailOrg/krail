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
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.ui.ComboBox;
import uk.q3c.krail.base.view.component.DefaultLocaleSelector;
import uk.q3c.krail.base.view.component.LocaleContainer;
import uk.q3c.krail.testbench.V7TestBenchTestCase;

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
     * Selects the locale using the same method as {@link LocaleContainer} (that is, translating the displayed string
     * into the language of the target selection (for example Locale.GERMANY is always "German (Germany)"
     *
     * @param locale
     */
    public void selectLocale(Locale locale) {
        combo().selectByText(locale.getDisplayName(locale));
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
