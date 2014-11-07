/*
 * Copyright (C) 2014 David Sowerby
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
package uk.q3c.krail.base.navigate.sitemap;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.base.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.Translate;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class})
public class StandardPageKeyTest {

    @Inject
    CurrentLocale currentLocale;

    @Inject
    Translate translate;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
    }

    @Test
    public void locale_en() {
        // given
        // when
        currentLocale.setLocale(Locale.UK);
        // then
        assertThat(translate.from(StandardPageKey.Public_Home)).isEqualTo("Public Home");
    }

    @Test
    public void locale_de() {
        // given
        // when
        currentLocale.setLocale(Locale.GERMANY);
        // then
        assertThat(translate.from(StandardPageKey.Public_Home)).isEqualTo("Öffentliche Startseite");
    }

    @Test
    public void locale_it() {
        // given
        // when
        currentLocale.setLocale(Locale.ITALY);
        // then
        assertThat(translate.from(StandardPageKey.Public_Home)).isEqualTo("Public Pagina");
    }

}
