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
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.apache.bval.Validate;
import org.apache.bval.guice.ValidationModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.krail.testutil.TestPersistenceModule;

import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestOptionModule.class, TestPersistenceModule.class,  EventBusModule
        .class, UIScopeModule.class,
        VaadinSessionScopeModule.class})
public class KrailInterpolatorTest {

    @Mock
    SimpleContext context;
    @Mock
    ConstraintDescriptor constraintDescriptor;
    @Inject
    private KrailInterpolator interpolator;

    @Before
    public void setup() {
        when(context.getConstraintDescriptor()).thenReturn(constraintDescriptor);
    }

    @Test
    public void findI18NKey() {
        //given

        //when

        I18NKey actual = interpolator.findI18NKey(I18NKey.fullName(LabelKey.Cancel));
        //then
        assertThat(actual).isEqualTo(LabelKey.Cancel);

    }

    @Test
    public void isPattern() {
        //given

        //when

        //then
        assertThat(interpolator.isPattern("{com.anything}")).isFalse();
        assertThat(interpolator.isPattern("{com.anything")).isTrue();
    }

    @Test
    public void translateKey() {
        //given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("value", 5);
        when(context.isUseFieldNamesInMessages()).thenReturn(true);
        when(context.getPropertyName()).thenReturn("testValue");
        when(constraintDescriptor.getAttributes()).thenReturn(attributes);
        //when
        String actual = interpolator.translateKey(TestValidationKey.Too_Big, context, Locale.UK);
        //then
        assertThat(actual).isEqualTo("testValue is far too big, it should be less than 5");
    }


    @ModuleProvider
    protected Module validationModule(){
        return Modules.override(new ValidationModule()).with(new KrailValidationModule()        );
    }




}