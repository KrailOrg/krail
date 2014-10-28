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
package uk.co.q3c.v7.i18n;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.WebBrowser;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.ui.BrowserProvider;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class})
public class DescriptionKeyTest {


    @Mock
    CurrentLocale currentLocale;
    Translate translate;
    @Mock
    private WebBrowser browser;
    @Mock
    private BrowserProvider browserProvider;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        translate = new MapTranslate(currentLocale);
    }


    @Test
    public void locale_en() {
        // given
        when(currentLocale.getLocale()).thenReturn(Locale.UK);

        // when

        // then
        assertThat(translate.from(DescriptionKey.Last_Name)).isEqualTo("the last name or family name");

    }

    @Test
    public void locale_de() {
        // given
        when(currentLocale.getLocale()).thenReturn(Locale.GERMANY);
        // when

        // then
        assertThat(translate.from(DescriptionKey.Last_Name)).isEqualTo("Der Nachname oder der Familienname");

    }

    @Test
    public void locale_it() {
        // given
        when(currentLocale.getLocale()).thenReturn(Locale.ITALY);
        // when

        // then
        assertThat(translate.from(DescriptionKey.Last_Name)).isEqualTo("il cognome o il nome di famiglia");
    }
}
