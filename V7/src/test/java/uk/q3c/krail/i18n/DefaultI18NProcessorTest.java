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

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.base.guice.vsscope.VaadinSessionScopeModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class})
public class DefaultI18NProcessorTest {

    I18NTestClass testObject;
    I18NTestClass2 testObject2;
    I18NTestClass3 testObject3;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    DefaultI18NProcessor processor;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        testObject = new I18NTestClass();
        testObject2 = new I18NTestClass2();
        testObject3 = new I18NTestClass3();
        currentLocale.removeAllListeners();
        currentLocale.setLocale(Locale.UK);

    }

    @Test
    public void interpret() {

        // given
        // when
        processor.translate(testObject);
        // then
        assertThat(testObject.getButtonWithAnnotation()
                             .getCaption()).isEqualTo("Ok");
        assertThat(testObject.getButtonWithAnnotation()
                             .getDescription()).isEqualTo("Confirm this Value is Ok");
        assertThat(testObject.getButtonWithAnnotation()
                             .getLocale()).isEqualTo(Locale.UK);

        assertThat(testObject.getLabel()
                             .getCaption()).isEqualTo("Ok");
        assertThat(testObject.getLabel()
                             .getDescription()).isEqualTo("Confirm this Value is Ok");
        // assertThat(testObject.getLabel().getValue()).isEqualTo("Ok");
        assertThat(testObject.getLabel()
                             .getLocale()).isEqualTo(Locale.UK);

        assertThat(testObject.getTable()
                             .getCaption()).isEqualTo("Ok");
        assertThat(testObject.getTable()
                             .getDescription()).isEqualTo("Confirm this Value is Ok");
        assertThat(testObject.getTable()
                             .getLocale()).isEqualTo(Locale.UK);

        Object[] columns = testObject.getTable()
                                     .getVisibleColumns();
        assertThat(columns.length).isEqualTo(3);

        String[] headers = testObject.getTable()
                                     .getColumnHeaders();
        assertThat(headers).isEqualTo(new String[]{"Small", "Cancel", "not i18N"});

        // class annotation overruled by field annotation
        TestCompositeComponent ccs = testObject.getCcs();
        assertThat(ccs.getCaption()).isEqualTo("Field");
        assertThat(ccs.getLabel()).isNotNull();
        Label label = ccs.getLabel();
        // assertThat(label.getValue()).isEqualTo("Ok");
        assertThat(label.getDescription()).isEqualTo("Confirm this Value is Ok");

        // class annotation
        TestCompositeComponent ccc = testObject.getCcc();
        assertThat(ccc.getCaption()).isEqualTo("Class");
        assertThat(ccc.getLabel()).isNotNull();
        label = ccc.getLabel();
        // assertThat(label.getValue()).isEqualTo("Ok");
        assertThat(label.getDescription()).isEqualTo("Confirm this Value is Ok");

        // composite but not a component
        TestCompositeNonComponent cnc = testObject.getCnc();
        // assertThat(cnc.getLabel().getValue()).isEqualTo("Cancel");

        // nested component
        TestCompositeComponentNested ccn = testObject.getCcn();
        assertThat(ccn.getCaption()).isEqualTo("Field");
        TestCompositeComponent ccs2 = ccn.getCcs();
        assertThat(ccs2.getCaption()).isEqualTo("Nested");

        // specific locale
        Button specificLocale = testObject.getSpecificLocale();
        assertThat(specificLocale.getCaption()).isEqualTo("Ja");

        // flex (note that key map changes the case of these)
        Button flex = testObject.getFlex();
        assertThat(flex.getCaption()).isEqualTo("transfers");
        assertThat(flex.getDescription()).isEqualTo("home");

        // value
        Label value = testObject.getValue();
        assertThat(value.getValue()).isEqualTo("Guest");
        // flexValue
        Label flexValue = testObject.getFlexValue();
        assertThat(flexValue.getValue()).isEqualTo("Private");
        // valueLocale
        Label valueLocale = testObject.getValueLocale();
        assertThat(valueLocale.getValue()).isEqualTo("Ja");
        // flexValueLocale
        Label flexValueLocale = testObject.getFlexValueLocale();
        assertThat(flexValueLocale.getValue()).isEqualTo("Ja");
    }

    @Test(expected = I18NException.class)
    public void fieldNotConstructed() {
        // given

        // when
        processor.translate(testObject2);
        // then
    }

    @Test(expected = I18NException.class)
    public void nestedFieldNotConstructed() {
        // given

        // when
        processor.translate(testObject3);
        // then
    }

    @Test
    public void interpret_de() {

        String confirmValueOk = "Bestätigen Sie, dass dieser Wert in Ordnung ist";

        // given
        currentLocale.setLocale(Locale.GERMAN);
        // when
        processor.translate(testObject);
        // then
        assertThat(testObject.getButtonWithAnnotation()
                             .getCaption()).isEqualTo("OK");
        assertThat(testObject.getButtonWithAnnotation()
                             .getDescription()).isEqualTo(confirmValueOk);
        assertThat(testObject.getButtonWithAnnotation()
                             .getLocale()).isEqualTo(Locale.GERMAN);

        assertThat(testObject.getLabel()
                             .getCaption()).isEqualTo("OK");
        assertThat(testObject.getLabel()
                             .getDescription()).isEqualTo(confirmValueOk);
        // assertThat(testObject.getLabel().getValue()).isEqualTo("Ok");
        assertThat(testObject.getButtonWithAnnotation()
                             .getLocale()).isEqualTo(Locale.GERMAN);

        assertThat(testObject.getTable()
                             .getCaption()).isEqualTo("OK");
        assertThat(testObject.getTable()
                             .getDescription()).isEqualTo(confirmValueOk);
        assertThat(testObject.getButtonWithAnnotation()
                             .getLocale()).isEqualTo(Locale.GERMAN);

        Object[] columns = testObject.getTable()
                                     .getVisibleColumns();
        assertThat(columns.length).isEqualTo(3);

        String[] headers = testObject.getTable()
                                     .getColumnHeaders();
        assertThat(headers).isEqualTo(new String[]{"Klein", "Stornieren", "not i18N"});

    }

}
