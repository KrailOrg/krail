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

package uk.q3c.krail.core.ui.form;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.util.Modules;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.Validator;
import com.vaadin.server.VaadinSession;
import fixture.TestI18NModule;
import org.apache.bval.guice.ValidationModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.data.TestEntity;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.validation.BeanValidator;
import uk.q3c.krail.core.validation.KrailValidationModule;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.DefaultI18NProcessor;
import uk.q3c.krail.testutil.MockOption;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.krail.testutil.TestUIScopeModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(MycilaJunitRunner.class)
@GuiceContext({KrailValidationModule.class, ValidationModule.class, TestOptionModule.class, TestI18NModule.class, VaadinSessionScopeModule.class, EventBusModule.class, TestUIScopeModule.class,})
public class BeanFieldGroupBaseTest {

    @Inject
    DefaultI18NProcessor i18NProcessor;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    Provider<BeanValidator> beanValidatorProvider;

    @Inject
    MockOption option;

    TestBeanFieldGroup fieldSet;
    TestEntity te, te2;

    private Injector injector;

    @Before
    public void setup() {
        VaadinSession.setCurrent(null);
        injector = Guice.createInjector(new TestI18NModule(), new TestUIScopeModule(), new VaadinSessionScopeModule(), new EventBusModule(), new
                TestOptionModule(), Modules.override(new
                ValidationModule())
                                                                                                 .with(new
                                                                                                         KrailValidationModule()));


        i18NProcessor = injector.getInstance(DefaultI18NProcessor.class);
        currentLocale = injector.getInstance(CurrentLocale.class);
        beanValidatorProvider = injector.getProvider(BeanValidator.class);


        Locale.setDefault(Locale.UK);
        fieldSet = new TestBeanFieldGroup(i18NProcessor, beanValidatorProvider, option);
        te = new TestEntity();
        te.setFirstName("Mango");
        te.setLastName("Chutney");

        te2 = new TestEntity();
        te2.setFirstName("Pickled");
        te2.setLastName("Eggs");
    }

    @Test
    public void setBean() {

        // given

        // when
        fieldSet.setBean(te);
        // then
        assertThat(fieldSet.getFirstName()
                           .getValue()).isEqualTo("Mango");
        assertThat(fieldSet.getLastName()
                           .getValue()).isEqualTo("Chutney");
    }

    @Test
    public void setBeanTwice() {

        // given
        fieldSet.setBean(te);
        // when
        fieldSet.setBean(te2);
        // then
        assertThat(fieldSet.getFirstName()
                           .getValue()).isEqualTo("Pickled");
        assertThat(fieldSet.getLastName()
                           .getValue()).isEqualTo("Eggs");
    }

    @Test
    public void i18N_default() {

        // given
        currentLocale.setLocale(Locale.UK);
        // when
        fieldSet.setBean(te);
        i18NProcessor.translate(fieldSet);
        // then
        assertThat(fieldSet.getFirstName()
                           .getCaption()).isEqualTo("First Name");
        assertThat(fieldSet.getLastName()
                           .getCaption()).isEqualTo("Last Name");
        assertThat(fieldSet.getLastName()
                           .getDescription()).isEqualTo("the last name or family name");

    }

    @Test
    public void i18N_de() {

        // given
        fieldSet.setBean(te);
        // when
        currentLocale.setLocale(Locale.GERMANY);
        i18NProcessor.translate(fieldSet);
        // then
        assertThat(fieldSet.getFirstName()
                           .getCaption()).isEqualTo("Vorname");
        assertThat(fieldSet.getLastName()
                           .getCaption()).isEqualTo("Nachname");
        assertThat(fieldSet.getLastName()
                           .getDescription()).isEqualTo("Der Nachname oder der Familienname");

    }

    @Test(expected = Validator.InvalidValueException.class)
    public void validationFailure() {

        // given
        te.setFirstName("P");
        // when
        fieldSet.setBean(te);
        // then
        fieldSet.getFirstName()
                .validate();

    }


}