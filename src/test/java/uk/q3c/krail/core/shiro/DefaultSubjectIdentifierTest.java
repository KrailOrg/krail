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
package uk.q3c.krail.core.shiro;

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
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.i18n.test.TestI18NModule;
import uk.q3c.krail.option.test.TestOptionModule;
import uk.q3c.krail.testutil.persist.TestPersistenceModuleVaadin;
import uk.q3c.util.UtilModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestOptionModule.class, TestPersistenceModuleVaadin.class, VaadinSessionScopeModule.class, EventBusModule.class, UIScopeModule
        .class, UtilModule.class})
public class DefaultSubjectIdentifierTest {


    @Mock
    SubjectProvider subjectPro;

    @Mock
    Subject subject;

    TestPrincipal principal;

    private DefaultSubjectIdentifier subjectIdentifier;

    @Inject
    private Translate translate;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        subjectIdentifier = new DefaultSubjectIdentifier(subjectPro, translate);
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
        assertThat(subjectIdentifier.subjectName()).isEqualTo("Guest");
        assertThat(subjectIdentifier.subjectIdentifier()).isNull();
    }

    @Test
    public void remembered() {

        // given
        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(true);
        when(subject.getPrincipal()).thenReturn(principal);
        // when

        // then
        assertThat(subjectIdentifier.subjectName()).isEqualTo("wiggly?");
        assertThat(subjectIdentifier.subjectIdentifier()).isNotNull();
        assertThat(subjectIdentifier.subjectIdentifier()).isEqualTo(principal);
    }

    @Test
    public void authenticated() {

        // given
        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        when(subject.getPrincipal()).thenReturn(principal);
        // when

        // then
        assertThat(subjectIdentifier.subjectName()).isEqualTo("wiggly");
        assertThat(subjectIdentifier.subjectIdentifier()).isNotNull();
        assertThat(subjectIdentifier.subjectIdentifier()).isEqualTo(principal);

    }

    @Test
    public void emptyUserName() {
        //given
        principal.name = null;
        when(subject.getPrincipal()).thenReturn(principal);
        //when

        //then
        assertThat(subjectIdentifier.userId()).isEqualTo("?");
    }

    @Test
    public void nullPrincipal() {
        //given
        when(subject.getPrincipal()).thenReturn(null);
        //when

        //then
        assertThat(subjectIdentifier.userId()).isEqualTo("?");
    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
            }

        };
    }

    private class TestPrincipal {
        private String name = "wiggly";

        @Override
        public String toString() {
            return name;
        }
    }

}
