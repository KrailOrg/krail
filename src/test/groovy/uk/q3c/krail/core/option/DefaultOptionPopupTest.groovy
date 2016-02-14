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

package uk.q3c.krail.core.option

import com.vaadin.data.Property
import com.vaadin.ui.*
import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.Translate
import uk.q3c.krail.core.ui.DataTypeToUI
import uk.q3c.krail.core.ui.DefaultDataTypeToUI
import uk.q3c.krail.testutil.i18n.TestLabelKey
import uk.q3c.krail.testutil.option.MockOption

import javax.annotation.Nonnull

/**
 * Created by David Sowerby on 07 Feb 2016
 */
class DefaultOptionPopupTest extends Specification {

    DefaultOptionPopup popup
    MockContext context1
    MockContext2 context2
    DataTypeToUI dataTypetoUi = new DefaultDataTypeToUI() {}
    UI ui = Mock()

    Translate translate = Mock()
    OptionContext context0

    def setup() {
        translate.from(LabelKey.Authorisation) >> "Authorisation"
        translate.from(LabelKey.No_Options_to_Show) >> "No options to show"
        translate.from(TestLabelKey.key1) >> 'key1'
        translate.from(TestLabelKey.key2) >> 'key2'
        translate.from(TestLabelKey.Private_Static) >> 'Private Static'
        translate.from(TestLabelKey.Static) >> 'Static'
        popup = new DefaultOptionPopup(dataTypetoUi, translate, new OptionKeyLocator())
        context0 = new MockContext0()
        context1 = new MockContext()
        context2 = new MockContext2()
        UI.setCurrent(ui)

    }

    def "optionKeys"() {
        when:
        Map<OptionKey, Class<?>> actual = popup.contextKeys(context2)

        then:
        actual.size() == 4
    }

    def "optionKeys_empty"() {
        when:
        Map<OptionKey, Class<?>> actual = popup.contextKeys(context1)

        then:
        actual.size() == 0
    }


    def "popup"() {
        when:
        popup.popup(context2, LabelKey.Authorisation);

        then:
        popup.getWindow().getCaption().equals("Authorisation")
        popup.getWindow().getId() != null
        popup.getWindow().isClosable()
        popup.getWindow().getContent() instanceof Panel
        Panel windowContent = (Panel) popup.getWindow().getContent()
        windowContent.getContent() instanceof GridLayout
        GridLayout gridLayout = windowContent.getContent()
        gridLayout.getRows() == 4
    }


    def "popup with empty context displays label"() {
        when:
        popup.popup(context0, LabelKey.Authorisation);

        then:
        GridLayout content = getWindowContent(popup)
        Component cellContent = content.getComponent(0, 0)
        cellContent instanceof Label
        ((Label) cellContent).getValue().equals("No options to show")
    }

    def "component and button presented for an OptionKey, component contains option value"() {
        when:
        popup.popup(context2, LabelKey.Authorisation);

        then:
        GridLayout content = getWindowContent(popup)
        Component layout0 = content.getComponent(0, 0)
        Component layout1 = content.getComponent(1, 0)
        layout0 instanceof FormLayout
        layout1 instanceof FormLayout
        ((FormLayout) layout0).getComponent(0) instanceof TextField
        ((FormLayout) layout1).getComponent(0) instanceof Button
        TextField field = (TextField) ((FormLayout) layout0).getComponent(0)
        field.getValue().equals("126")
        field.getCaption().equals("Private Static")
        field.getId().equals("DefaultOptionPopup-TextField-Private_Static")
    }

    def "user changes value in UI component, option is updated"() {
        when:
        popup.popup(context2, LabelKey.Authorisation);

        then:
        AbstractField field = getField(popup, 0)
        field.setValue("333")
        context2.getOption().get(MockContext2.key4).equals('333') //MockOption does not convert data type correctly
    }

    def "reset to default button resets the option value"() {
        when:
        popup.popup(context2, LabelKey.Authorisation);

        then:
        AbstractField field = getField(popup, 0)
        field.setValue("333")
        getResetButton(popup, 0).click()
        context2.getOption().get(MockContext2.key4).equals('126') //MockOption does not convert data type correctly
    }

    def "reset to default button resets the option value, using a converted value"() {
        when:
        popup.popup(context2, LabelKey.Authorisation);

        then:
        AbstractField field = getField(popup, 3)
        field.setValue(true)
        getResetButton(popup, 3).click()
        context2.getOption().get(MockContext2.key3).equals(false) //MockOption does not convert data type correctly
    }

    def "loading second time closed first window"() {
        given:
        popup.popup(context2, LabelKey.Authorisation);
        Window firstWindow = popup.getWindow()
        firstWindow.setParent(ui)

        when:
        popup.popup(context2, LabelKey.Authorisation);
        Window secondWindow = popup.getWindow()

        then:
        firstWindow != secondWindow
        1 * ui.removeWindow(firstWindow)
    }

    private GridLayout getWindowContent(DefaultOptionPopup popup) {
        Panel panel = popup.getWindow().getContent()
        return panel.getContent()
    }

    private AbstractField getField(DefaultOptionPopup popup, int row) {
        GridLayout layout = getWindowContent(popup)
        FormLayout formLayout = layout.getComponent(0, row)
        return (AbstractField) formLayout.getComponent(0)
    }

    private Button getResetButton(DefaultOptionPopup popup, int row) {
        GridLayout layout = getWindowContent(popup)
        FormLayout formLayout = layout.getComponent(1, row)
        return (Button) formLayout.getComponent(0)
    }

    static class MockContext implements OptionContext {

        Option option = new MockOption()

        @Nonnull
        @Override
        public Option getOption() {
            return option;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }
    }

    static class MockContext2 implements OptionContext {
        Option option = new MockOption()

        public static
        final OptionKey<Boolean> key3 = new OptionKey<>(false, MockContext2.class, TestLabelKey.Static, TestLabelKey.Large);
        private static
        final OptionKey<Integer> key4 = new OptionKey<>(126, MockContext2.class, TestLabelKey.Private_Static, TestLabelKey.Large);
        public final OptionKey<Integer> key2 = new OptionKey<>(124, this, TestLabelKey.key2, TestLabelKey.Blank);
        private final OptionKey<Integer> key1 = new OptionKey<Integer>(123, this, TestLabelKey.key1);

        @Nonnull
        @Override
        public Option getOption() {
            return option;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }
    }

    static class MockContext0 implements OptionContext {
        Option option = new MockOption()


        @Nonnull
        @Override
        public Option getOption() {
            return option;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }
    }
}
