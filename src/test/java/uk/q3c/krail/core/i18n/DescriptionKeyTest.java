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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.env.ServletEnvironmentModule;
import uk.q3c.krail.core.eventbus.VaadinEventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.eventbus.mbassador.EventBusModule;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.i18n.test.TestI18NModule;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.mock.TestOptionModule;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.persist.inmemory.InMemoryOptionStore;
import uk.q3c.krail.persist.inmemory.store.DefaultInMemoryOptionStore;
import uk.q3c.util.UtilModule;
import uk.q3c.util.guice.SerializationSupportModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, EventBusModule.class, TestOptionModule.class, InMemoryModule.class, VaadinEventBusModule.class, UIScopeModule.class, VaadinSessionScopeModule
        .class, SerializationSupportModule.class, ServletEnvironmentModule.class, UtilModule.class})
public class DescriptionKeyTest {

    @Inject
    CurrentLocale currentLocale;

    @Inject
    Translate translate;

    @Inject
    Option option;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
    }


    @Test
    public void locale_en() {
        // given
        currentLocale.setLocale(Locale.UK);
        // when

        // then
        assertThat(translate.from(DescriptionKey.Last_Name)).isEqualTo("the last name or family name");

    }

    @Test
    public void locale_de() {
        // given
        currentLocale.setLocale(Locale.GERMANY);
        // when

        // then
        assertThat(translate.from(DescriptionKey.Last_Name)).isEqualTo("Der Nachname oder der Familienname");

    }

    @Test
    public void locale_it() {
        // give
        currentLocale.setLocale(Locale.ITALY);
        // when

        // then
        assertThat(translate.from(DescriptionKey.Last_Name)).isEqualTo("il cognome o il nome di famiglia");
    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(InMemoryOptionStore.class).to(DefaultInMemoryOptionStore.class);
            }

        };
    }
}
