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

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class I18NTestClass {
    @Caption(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
    private final Button buttonWithAnnotation;
    @Caption(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
    private final Label label;
    @Caption(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
    private final Table table;
    @Caption(caption = LabelKey.Yes, description = DescriptionKey.Please_log_in, locale = "de-DE")
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
    private final Label value;
    @Value(value = LabelKey.Yes, locale = "de-DE")
    @Description(description = DescriptionKey.Account_Already_In_Use)
    private final Label valueLocale;
    private final Button buttonWithoutAnnotation;
    private final Integer integer;
    private double dubble;
    @Caption(caption = LabelKey.Broadcast_Messages, description = DescriptionKey.Field)
    private Grid grid;
    @Caption(caption = LabelKey.Authentication, description = DescriptionKey.Please_log_in)
    @Description(description = DescriptionKey.Account_Already_In_Use)
    private Button newButton;

    protected I18NTestClass() {
        super();
        buttonWithAnnotation = new Button();
        buttonWithoutAnnotation = new Button();
        newButton = new Button();
        label = new Label();
        demoLabel = new Label();
        integer = new Integer(5);
        table = new Table();
        setupTableColumns();

        cnc = new TestCompositeNonComponent();
        ccn = new TestCompositeComponentNested();
        ccs = new TestCompositeComponent();
        ccc = new TestCompositeComponent();
        specificLocale = new Button();
        value = new Label();
        valueLocale = new Label();
        grid = new Grid();
    }

    private void setupTableColumns() {
        table.addContainerProperty(LabelKey.Small, String.class, "numpty");
        table.addContainerProperty(LabelKey.Cancel, String.class, "numpty");
        table.addContainerProperty("not i18N", String.class, "numpty");
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

    public Table getTable() {
        return table;
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


    public Label getValue() {
        return value;
    }


    public Label getValueLocale() {
        return valueLocale;
    }


    public Button getNewButton() {
        return newButton;
    }

    public void setNewButton(Button newButton) {
        this.newButton = newButton;
    }

}
