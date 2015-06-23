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

package uk.q3c.krail.core.validation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.Validator;
import org.apache.bval.guice.ValidationModule;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.data.TestEntity2;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.ui.form.BeanFieldGroup;
import uk.q3c.krail.core.ui.form.TestBeanFieldGroup2;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.DefaultI18NProcessor;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.krail.testutil.TestPersistenceModule;
import uk.q3c.krail.testutil.TestUIScopeModule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An integration test across the various pieces of the validation framework.  Uses a {@link BeanFieldGroup} to
 * generate
 * the validation failures
 * <p>
 * Created by David Sowerby on 05/02/15.
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({KrailValidationModule.class, ValidationModule.class, TestOptionModule.class, TestPersistenceModule.class, TestI18NModule.class, EventBusModule
        .class, TestUIScopeModule
        .class, VaadinSessionScopeModule.class})
public class ValidationTest {

    @Inject
    DefaultI18NProcessor i18NProcessor;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    Provider<BeanValidator> beanValidatorProvider;

    TestBeanFieldGroup2 fieldSet;
    TestEntity2 te1, te2;
    private Injector injector;

    @Before
    public void setup() {

    }

    @Test
    public void javaxDefault() {
        //given
        te1 = new TestEntity2();
        BeanValidator validator = beanValidatorProvider.get();
        te1.setAge(4);
        //when
        validator.init(TestEntity2.class, "age", false);

        try {
            validator.validate(4);
            assertThat(true).overridingErrorMessage("should have thrown a validation exception")
                            .isFalse();
        } catch (Validator.InvalidValueException ive) {
            String msg = getMessage(ive);
            //then
            assertThat(msg).isEqualTo("must be greater than or equal to 5");
        }
    }

    private String getMessage(Validator.InvalidValueException ive) {
        Validator.InvalidValueException[] list = ive.getCauses();
        for (Throwable throwable : list) {
            if (StringUtils.isNotEmpty(throwable.getMessage())) {
                return throwable.getMessage();
            }
        }
        return "Message not found";
    }

    @Test
    public void customMessage() {
        //given
        te1 = new TestEntity2();
        BeanValidator validator = beanValidatorProvider.get();
        //when
        validator.init(TestEntity2.class, "height", true);

        try {
            validator.validate(4);
            assertThat(true).overridingErrorMessage("should have thrown a validation exception")
                            .isFalse();
        } catch (Validator.InvalidValueException ive) {
            String msg = getMessage(ive);
            //then
            assertThat(msg).isEqualTo("a custom message");
        }
    }

    @Test
    public void string_krail_key() {
        //given
        te1 = new TestEntity2();
        BeanValidator validator = beanValidatorProvider.get();
        //when
        validator.init(TestEntity2.class, "weight", false);

        try {
            validator.validate(10);
            assertThat(true).overridingErrorMessage("should have thrown a validation exception")
                            .isFalse();
        } catch (Validator.InvalidValueException ive) {
            String msg = getMessage(ive);
            //then
            assertThat(msg).isEqualTo("is far too big, it should be less than 5");
        }

        //when
        validator.init(TestEntity2.class, "weight", true);
        try {
            validator.validate(10);
            assertThat(true).overridingErrorMessage("should have thrown a validation exception")
                            .isFalse();
        } catch (Validator.InvalidValueException ive) {
            String msg = getMessage(ive);
            //then
            assertThat(msg).isEqualTo("weight is far too big, it should be less than 5");
        }

    }

    @Test
    public void krail_key_from_Annotation() {
        //given
        te1 = new TestEntity2();
        BeanValidator validator = beanValidatorProvider.get();
        //when
        validator.init(TestEntity2.class, "speed", true);

        try {
            validator.validate(10);
            assertThat(true).overridingErrorMessage("should have thrown a validation exception")
                            .isFalse();
        } catch (Validator.InvalidValueException ive) {
            String msg = getMessage(ive);
            //then
            assertThat(msg).isEqualTo("speed is far too big, it should be less than 1");
        }

        //when
        validator.init(TestEntity2.class, "speed", false);
        try {
            validator.validate(10);
            assertThat(true).overridingErrorMessage("should have thrown a validation exception")
                            .isFalse();
        } catch (Validator.InvalidValueException ive) {
            String msg = getMessage(ive);
            //then
            assertThat(msg).isEqualTo("is far too big, it should be less than 1");
        }
    }

    @Test
    public void using_bval_annotation() {
        //given
        te1 = new TestEntity2();
        BeanValidator validator = beanValidatorProvider.get();
        //when
        validator.init(TestEntity2.class, "email", true);

        //then
        try {
            validator.validate("wiggly");
            assertThat(true).overridingErrorMessage("should have thrown a validation exception")
                            .isFalse();
        } catch (Validator.InvalidValueException ive) {
            String msg = getMessage(ive);
            //then
            assertThat(msg).isEqualTo("not a well-formed email address");
        }


    }
}
