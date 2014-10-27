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
package uk.co.q3c.v7.base.shiro;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.MapTranslate;
import uk.co.q3c.v7.i18n.Translate;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultSubjectIdentifierTest {

    @Mock
    CurrentLocale currentLocale;
    @Mock
    SubjectProvider subjectPro;
    @Mock
    Subject subject;
    TestPrincipal principal;
    private DefaultSubjectIdentifier converter;
    @Inject
    private Translate translate;

    @Before
    public void setup() {
        when(currentLocale.getLocale()).thenReturn(Locale.UK);
        converter = new DefaultSubjectIdentifier(subjectPro, translate);
        when(subjectPro.get()).thenReturn(subject);
        principal = new TestPrincipal();
    }

    @Test
    public void notAuthenticatedNotRemembered() {

        // given
        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(false);
        when(subject.getPrincipal()).thenReturn(null);
        // when

        // then
        assertThat(converter.subjectName()).isEqualTo("Guest");
        assertThat(converter.subjectIdentifier()).isNull();
    }

    @Test
    public void remembered() {

        // given
        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(true);
        when(subject.getPrincipal()).thenReturn(principal);
        // when

        // then
        assertThat(converter.subjectName()).isEqualTo("wiggly?");
        assertThat(converter.subjectIdentifier()).isNotNull();
        assertThat(converter.subjectIdentifier()).isEqualTo(principal);
    }

    @Test
    public void authenticated() {

        // given
        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        when(subject.getPrincipal()).thenReturn(principal);
        // when

        // then
        assertThat(converter.subjectName()).isEqualTo("wiggly");
        assertThat(converter.subjectIdentifier()).isNotNull();
        assertThat(converter.subjectIdentifier()).isEqualTo(principal);

    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {

                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(Translate.class).to(MapTranslate.class);
                bind(CurrentLocale.class).toInstance(currentLocale);


            }

        };
    }

    private class TestPrincipal {
        private final String name = "wiggly";

        @Override
        public String toString() {
            return name;
        }
    }

}
