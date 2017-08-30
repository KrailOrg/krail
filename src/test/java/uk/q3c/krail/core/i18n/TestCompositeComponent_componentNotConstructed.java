/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.i18n;

import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;

/**
 * Deliberately does not construct the contained component to test handling of null
 *
 * @author David Sowerby
 * @date 2 May 2014
 */
@Caption(caption = LabelKey.Class, description = DescriptionKey.Field)
public class TestCompositeComponent_componentNotConstructed extends Panel {

    @Description(description = DescriptionKey.Confirm_Ok)
    private Label label;

    public Label getLabel() {
        return label;
    }

}
