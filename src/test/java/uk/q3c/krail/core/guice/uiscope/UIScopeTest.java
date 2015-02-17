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

package uk.q3c.krail.core.guice.uiscope;

import com.google.inject.*;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.*;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import fixture.TestI18NModule;
import fixture.TestUIModule;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.q3c.krail.core.config.ApplicationConfigurationModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.NavigationModule;
import uk.q3c.krail.core.navigate.sitemap.*;
import uk.q3c.krail.core.services.AbstractServiceI18N;
import uk.q3c.krail.core.services.ServicesMonitorModule;
import uk.q3c.krail.core.shiro.*;
import uk.q3c.krail.core.ui.BasicUI;
import uk.q3c.krail.core.ui.BasicUIProvider;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.ui.ScopedUIProvider;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.user.opt.OptionModule;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.StandardComponentModule;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.ResourceUtils;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIScopeTest {

    static ErrorHandler mockedErrorHandler = mock(ErrorHandler.class);
    static ConverterFactory mockedConverterFactory = mock(ConverterFactory.class);
    static Provider<UI> uiProvider;
    //    static Map<String, Provider<UI>> uibinder;
    static Subject subject = mock(Subject.class);
    static VaadinService vaadinService;
    public int connectCount;
    protected VaadinRequest mockedRequest = mock(VaadinRequest.class);
    protected VaadinSession mockedSession = mock(VaadinSession.class);
    UIKeyProvider uiKeyProvider = new UIKeyProvider();
    UIProvider provider;
    private Injector injector;
    private ScopedUI ui;

    @BeforeClass
    public static void setupClass() {
        vaadinService = mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(ResourceUtils.userTempDirectory());
        VaadinService.setCurrent(vaadinService);
    }

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
    }

    @Test
    public void uiScope2() {

        // given
        SecurityUtils.setSecurityManager(new KrailSecurityManager());
        when(subject.isPermitted(anyString())).thenReturn(true);
        when(subject.isPermitted(any(org.apache.shiro.authz.Permission.class))).thenReturn(true);
        when(mockedSession.hasLock()).thenReturn(true);

        // when

        injector = Guice.createInjector(new TestModule(), new ApplicationConfigurationModule(), new ViewModule(), new UIScopeModule(), new ServicesMonitorModule(), new OptionModule(), new UserModule(), new
                StandardComponentModule(), new TestI18NModule(), new StandardShiroModule(), new ShiroVaadinModule(), new VaadinSessionScopeModule(), new SitemapModule(), new TestUIModule(), new NavigationModule());
        provider = injector.getInstance(UIProvider.class);
        createUI(BasicUI.class);
        // navigator = injector.getInstance(Navigator.class);
        TestObject to1 = injector.getInstance(TestObject.class);
        createUI(BasicUI.class);
        TestObject to2 = injector.getInstance(TestObject.class);
        // then

        assertThat(to1).isNotNull();
        assertThat(to2).isNotNull();
        assertThat(to1).isNotEqualTo(to2);
    }

    @SuppressWarnings("deprecation")
    protected ScopedUI createUI(final Class<? extends UI> clazz) {
        String baseUri = "http://example.com";
        when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
        when(mockedSession.createConnectorId(Matchers.any(ClientConnector.class))).thenAnswer(new ConnectorIdAnswer());

        CurrentInstance.set(UI.class, null);
        CurrentInstance.set(UIKey.class, null);
        UICreateEvent event = mock(UICreateEvent.class);
        when(event.getSource()).thenReturn(vaadinService);

        Answer<Class<? extends UI>> answer = new Answer<Class<? extends UI>>() {

            @Override
            public Class<? extends UI> answer(InvocationOnMock invocation) throws Throwable {
                return clazz;
            }
        };
        when(event.getUIClass()).thenAnswer(answer);
        ui = (ScopedUI) getUIProvider().createInstance(event);
        ui.setLocale(Locale.UK);
        CurrentInstance.set(UI.class, ui);

        ui.setSession(mockedSession);

        return ui;
    }

    protected ScopedUIProvider getUIProvider() {
        return (ScopedUIProvider) provider;
    }

    static class MockSitemapService extends AbstractServiceI18N implements SitemapService {

        @Inject
        protected MockSitemapService(Translate translate) {
            super(translate);
            setNameKey(LabelKey.Sitemap_Service);
        }

        @Override
        public void doStart() throws Exception {

        }

        @Override
        public void doStop() {

        }

        @Override
        public Sitemap<MasterSitemapNode> getSitemap() {

            return mock(MasterSitemap.class);
        }

    }

    static class TestObject {

    }

    static class MockSubjectProvider implements Provider<Subject> {

        @Override
        public Subject get() {
            return subject;
        }

    }

    static class TestModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(UIProvider.class).to(BasicUIProvider.class);

            bind(TestObject.class).in(UIScoped.class);
            bind(VaadinSessionProvider.class).to(DefaultVaadinSessionProvider.class);
        }
    }

    public class ConnectorIdAnswer implements Answer<String> {

        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
            connectCount++;
            return Integer.toString(connectCount);
        }

    }

}
