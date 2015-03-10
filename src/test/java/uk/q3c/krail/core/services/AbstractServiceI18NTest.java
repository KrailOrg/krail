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
package uk.q3c.krail.core.services;


import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.TestLabelKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.testutil.TestOptionModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class, TestOptionModule.class, EventBusModule.class, UIScopeModule.class})
public class AbstractServiceI18NTest {

    @Inject
    CurrentLocale currentLocale;
    @Inject
    TestService service;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        currentLocale.setLocale(Locale.UK);
    }

    @Test
    public void name() {

        // given

        // when
        service.setNameKey(TestLabelKey.Home);
        // then
        assertThat(service.getName()).isEqualTo("home");
        // when
        service.setDescriptionKey(TestLabelKey.Private);
        // then
        assertThat(service.getDescription()).isEqualTo("Private");
    }

    @Test
    public void notNamed() {
        // given

        // when

        // then
        assertThat(service.getName()).isEqualTo("Unnamed");
    }



    static class TestService extends AbstractServiceI18N {
        @Inject
        protected TestService(Translate translate) {
            super(translate);
        }

        @Override
        public void doStart() {

        }

        @Override
        public void doStop() {

        }

    }

}
