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

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class I18NTestClass {
    @Caption(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
    private final Button buttonWithAnnotation;
    @Caption(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
    private final Label label;
    @Caption(caption = LabelKey.Yes, description = DescriptionKey.Field, locale = "de-DE")
    private final Label demoLabel;
    @Caption(caption = LabelKey.Field, description = DescriptionKey.Field)
    private final TestCompositeComponent ccs;
    // Class annotation
    private final TestCompositeComponent ccc;
    @I18N
    private final TestCompositeNonComponent cnc;
    @Caption(caption = LabelKey.Field, description = DescriptionKey.Field)
    @I18N
    private final TestCompositeComponentNested ccn;
    @Caption(caption = LabelKey.Yes, locale = "de-DE", description = DescriptionKey.Field)
    private final Button specificLocale;
    @Value(value = LabelKey.Guest)
    private final TextField value;
    @Value(value = LabelKey.Yes, locale = "de-DE")
    @Description(description = DescriptionKey.Enter_your_first_name)
    private final TextField valueLocale;
    private final Button buttonWithoutAnnotation;
    private final Integer integer;
    private double dubble;
    @Caption(caption = LabelKey.Broadcast_Messages, description = DescriptionKey.Field)
    private Grid grid;
    @Caption(caption = LabelKey.Authentication, description = DescriptionKey.Last_Name)
    @Description(description = DescriptionKey.Confirm_Ok)
    private Button newButton;

    protected I18NTestClass() {
        super();
        buttonWithAnnotation = new Button();
        buttonWithoutAnnotation = new Button();
        newButton = new Button();
        label = new Label();
        demoLabel = new Label();
        integer = new Integer(5);

        cnc = new TestCompositeNonComponent();
        ccn = new TestCompositeComponentNested();
        ccs = new TestCompositeComponent();
        ccc = new TestCompositeComponent();
        specificLocale = new Button();
        value = new TextField();
        valueLocale = new TextField();
        grid = new Grid();
    }


    public Button getButtonWithAnnotation() {
        return buttonWithAnnotation;
    }

    public Button getButtonWithoutAnnotation() {
        return buttonWithoutAnnotation;
    }

    public Integer getInteger() {
        return integer;
    }

    public double getDubble() {
        return dubble;
    }

    public Label getLabel() {
        return label;
    }

    public TestCompositeNonComponent getCnc() {
        return cnc;
    }

    public TestCompositeComponentNested getCcn() {
        return ccn;
    }

    public Label getDemoLabel() {
        return demoLabel;
    }

    public TestCompositeComponent getCcs() {
        return ccs;
    }

    public Button getSpecificLocale() {
        return specificLocale;
    }

    public TestCompositeComponent getCcc() {
        return ccc;
    }


    public TextField getValue() {
        return value;
    }


    public TextField getValueLocale() {
        return valueLocale;
    }


    public Button getNewButton() {
        return newButton;
    }

    public void setNewButton(Button newButton) {
        this.newButton = newButton;
    }

}
