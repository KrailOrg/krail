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
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.Button;
import net.engio.mbassy.bus.MBassador;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import testutil.MockCurrentLocale;
import testutil.TestI18NModule;
import testutil.TestOptionModule;
import testutil.TestPersistenceModule;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.eventbus.SessionBusProvider;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.CurrentLocale;
import uk.q3c.krail.core.i18n.LocaleChangeBusMessage;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.shiro.DefaultSubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(MycilaJunitRunner.class)
@GuiceContext({UIScopeModule.class, TestI18NModule.class, TestOptionModule.class, TestPersistenceModule.class, VaadinSessionScopeModule.class, EventBusModule
        .class})
public class DefaultUserStatusPanelTest {

    DefaultUserStatusPanel panel;

    @Mock
    Subject subject;

    @Mock
    Navigator navigator;

    Button loginoutBtn;

    @Mock
    SubjectProvider subjectProvider;



    MBassador<BusMessage> eventBus;

    @Mock
    SessionBusProvider sessionBusProvider;

    SubjectIdentifier subjectIdentifier;

    @Inject
    Translate translate;

    @Inject
    CurrentLocale currentLocale = new MockCurrentLocale();

    @Before
    public void setup() {
        eventBus = new MBassador<>();
        Locale.setDefault(Locale.UK);
        currentLocale.setLocale(Locale.UK);
        when(subjectProvider.get()).thenReturn(subject);
        subjectIdentifier = new DefaultSubjectIdentifier(subjectProvider, translate);
        when(sessionBusProvider.get()).thenReturn(eventBus);
        panel = new DefaultUserStatusPanel(navigator, subjectProvider, translate, subjectIdentifier, sessionBusProvider, currentLocale);

        loginoutBtn = panel.getLogin_logout_Button();
    }

    @Test
    public void unknown() {

        // given
        when(subject.isRemembered()).thenReturn(false);
        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.getPrincipal()).thenReturn(null);

        // when
        panel.build();
        // then
        assertThat(panel.getActionLabel()).isEqualTo("log in");
        assertThat(panel.getUserId()).isEqualTo("Guest");

        // when
        loginoutBtn.click();
        // then
        verify(navigator).navigateTo(StandardPageKey.Log_In);
    }

    @Test
    public void remembered() {

        // given
        when(subject.isRemembered()).thenReturn(true);
        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.getPrincipal()).thenReturn("userId");
        // when
        panel.build();
        // then
        assertThat(panel.getActionLabel()).isEqualTo("log in");
        assertThat(panel.getUserId()).isEqualTo("userId?");
        // when
        loginoutBtn.click();
        // then
        verify(navigator).navigateTo(StandardPageKey.Log_In);
    }


    @Test
    public void localeChange() {

        // given
        when(subject.isRemembered()).thenReturn(false);
        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.getPrincipal()).thenReturn(null);
        panel.build();

        // when
        currentLocale.setLocale(Locale.GERMANY);
        panel.localeChanged(new LocaleChangeBusMessage(this, Locale.GERMANY)); //simulated as we are using MockCurrentLocale
        // then
        assertThat(panel.getActionLabel()).isEqualTo("einloggen");
        assertThat(panel.getUserId()).isEqualTo("Gast");
    }

    @Test
    public void authenticated() {

        // given
        when(subject.isRemembered()).thenReturn(false);
        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.getPrincipal()).thenReturn("userId");
        // when
        panel.build();
        // then
        assertThat(panel.getActionLabel()).isEqualTo("log out");
        assertThat(panel.getUserId()).isEqualTo("userId");
    }



}
