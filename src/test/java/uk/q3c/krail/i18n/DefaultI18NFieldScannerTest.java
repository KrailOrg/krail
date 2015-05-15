/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.AbstractComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.testutil.MockOption;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.util.TestByteEnhancementModule;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, EventBusModule.class, UIScopeModule.class, TestOptionModule.class, VaadinSessionScopeModule.class,
        TestByteEnhancementModule.class})
public class DefaultI18NFieldScannerTest {

    @Inject
    I18NTestClass testObject;
    @Inject
    I18NTestClass2 testObject2;
    @Inject
    I18NTestClass3 testObject3;
    @Inject
    I18NTestClass4 testObject4;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    MockOption option;

    DefaultI18NFieldScanner scanner;

    @Inject
    I18NHostClassIdentifier i18NHostClassIdentifier;

    @Before
    public void setup() {

    }

    @Test
    public void class1() {
        //given
        scanner = new DefaultI18NFieldScanner(i18NHostClassIdentifier);
        //when
        scanner.scan(new I18NTestClass());
        //then
        assertThat(scanner.annotatedComponents()).hasSize(17);

        for (AbstractComponent abstractComponent : scanner.annotatedComponents()
                                  .keySet()) {
            AnnotationInfo annotationInfo = scanner.annotatedComponents()
                                                   .get(abstractComponent);
            List<Annotation> annotations = annotationInfo.getAnnotations();

            switch (annotationInfo.getField()
                                  .getName()) {
                case "buttonWithAnnotation":
                    assertThat(annotations).hasSize(1);
                    Caption caption = (Caption) annotations.get(0);
                    assertThat(caption.caption()).isEqualTo(LabelKey.Ok);
                    assertThat(caption.description()).isEqualTo(DescriptionKey.Confirm_Ok);
                    break;
                case "newButton":
                    assertThat(annotations).hasSize(2);
                    caption = (Caption) annotations.get(0);
                    Description description = (Description) annotations.get(1);
                    assertThat(caption.caption()).isEqualTo(LabelKey.Authentication);
                    assertThat(caption.description()).isEqualTo(DescriptionKey.Please_log_in);
                    assertThat(description.description()).isEqualTo(DescriptionKey.Account_Already_In_Use);
            }
        }

        assertThat(fieldNames(scanner.annotatedComponents())).containsOnly("table", "ccs", "labelInsideTcc", "label", "ccn", "grid", "specificLocale",
                "newButton", "labelInsideTcc", "value", "ccs", "label", "demoLabel", "ccc", "valueLocale", "labelInsideTcc", "buttonWithAnnotation");
    }

    private List<String> fieldNames(Map<AbstractComponent, AnnotationInfo> abstractComponentAnnotationInfoMap) {
        List<String> fieldNames = new ArrayList<>();
        for (AbstractComponent component : abstractComponentAnnotationInfoMap.keySet()) {
            final AnnotationInfo annotationInfo = abstractComponentAnnotationInfoMap.get(component);
            fieldNames.add(annotationInfo.getField()
                                         .getName());
        }
        return fieldNames;
    }


    @Test
    public void class2() {
        //given
        scanner = new DefaultI18NFieldScanner(i18NHostClassIdentifier);
        //when
        scanner.scan(new I18NTestClass2());
        //when

        //then
        assertThat(scanner.annotatedComponents()).hasSize(0);
    }


    @Test
    public void class3() {
        //given
        scanner = new DefaultI18NFieldScanner(i18NHostClassIdentifier);
        //when
        scanner.scan(new I18NTestClass3());
        //when

        //then
        assertThat(scanner.annotatedComponents()).hasSize(1);
    }



    @Test
    public void inheritance() {
        I18NTestClass5a tObject = new I18NTestClass5a();
        scanner = new DefaultI18NFieldScanner(i18NHostClassIdentifier);
        //when
        scanner.scan(tObject);

        //then
        assertThat(scanner.annotatedComponents()).hasSize(2);
        assertThat(scanner.processedDrillDowns()).hasSize(4);
        //includes the initial target
        assertThat(scanner.processedDrillDowns()).containsOnly(tObject, tObject.layout2Drilled, tObject.layoutDrilled, tObject.panelDrilled);
    }




}
