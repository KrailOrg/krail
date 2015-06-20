/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.i18n;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * Test class with both Caption and I18N annotations
 */
@Caption(caption = LabelKey.Class, description = DescriptionKey.Locale_Flag_Size)
@I18N
public class CompositeComponent_with_Caption_and_I18N extends Panel {

    @Caption(caption = LabelKey.First_Name, description = DescriptionKey.Enter_your_first_name)
    @Value(LabelKey.Unnamed)
    private final Label labelInsideTcc = new Label();

    public Label getLabelInsideTcc() {
        return labelInsideTcc;
    }

}
