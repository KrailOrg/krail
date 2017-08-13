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

package uk.q3c.krail.core.guice.uiscope;

import com.google.inject.*;
import com.vaadin.server.*;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.q3c.krail.core.config.ApplicationConfigurationModule;
import uk.q3c.krail.core.data.DataModule;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.eventbus.GlobalBusProvider;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.I18NKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.navigate.NavigationModule;
import uk.q3c.krail.core.navigate.sitemap.SitemapModule;
import uk.q3c.krail.core.navigate.sitemap.SitemapService;
import uk.q3c.krail.core.option.InMemory;
import uk.q3c.krail.core.option.OptionModule;
import uk.q3c.krail.core.persist.inmemory.common.InMemoryModule;
import uk.q3c.krail.core.push.PushModule;
import uk.q3c.krail.core.services.AbstractService;
import uk.q3c.krail.core.services.RelatedServicesExecutor;
import uk.q3c.krail.core.services.ServicesModule;
import uk.q3c.krail.core.shiro.*;
import uk.q3c.krail.core.ui.*;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.DefaultComponentModule;
import uk.q3c.krail.testutil.i18n.TestI18NModule;
import uk.q3c.krail.testutil.persist.TestPersistenceModule;
import uk.q3c.krail.testutil.ui.TestUIModule;
import uk.q3c.krail.util.DefaultResourceUtils;
import uk.q3c.krail.util.ResourceUtils;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class UIScopeTest {
    static Optional<CacheManager> cacheManagerOpt = Optional.empty();
    static Subject subject = mock(Subject.class);
    static VaadinService vaadinService;
    static ResourceUtils resourceUtils;
    public int connectCount;
    protected VaadinRequest mockedRequest = mock(VaadinRequest.class);
    protected VaadinSession mockedSession = mock(VaadinSession.class);
    UIProvider provider;
    VaadinSessionProvider vaadinSessionProvider;
    VaadinSession vaadinSession;
    private Injector injector;
    private ScopedUI ui;

    @BeforeClass
    public static void setupClass() {
        resourceUtils = new DefaultResourceUtils();
        vaadinService = mock(VaadinService.class);

        when(vaadinService.getBaseDirectory()).thenReturn(resourceUtils.userTempDirectory());
        VaadinService.setCurrent(vaadinService);
    }

    @Before
    public void setup() {
        vaadinSession = mock(VaadinSession.class);
        VaadinSession.setCurrent(vaadinSession);
        when(vaadinSession.getAttribute(Subject.class)).thenReturn(subject);
        Locale.setDefault(Locale.UK);
        vaadinSessionProvider = mock(VaadinSessionProvider.class);
        when(vaadinSessionProvider.get()).thenReturn(vaadinSession);
    }

    @Test
    public void uiScope2() {

        // given
        KrailSecurityManager securityManager = new KrailSecurityManager(cacheManagerOpt);
        //        securityManager.setVaadinSessionProvider(vaadinSessionProvider);

        SecurityUtils.setSecurityManager(securityManager);

        when(subject.isPermitted(anyString())).thenReturn(true);
        when(subject.isPermitted(any(org.apache.shiro.authz.Permission.class))).thenReturn(true);
        when(mockedSession.hasLock()).thenReturn(true);

        // when

        injector = Guice.createInjector(new PushModule(), new TestModule(), new ApplicationConfigurationModule(), new ViewModule(), new UIScopeModule(), new
                        ServicesModule(), new OptionModule().activeSource(InMemory.class), new UserModule(), new DefaultComponentModule(), new TestI18NModule
                (), new
                        DefaultShiroModule(), new ShiroVaadinModule(), new VaadinSessionScopeModule(), new SitemapModule(), new TestUIModule(), new
                        TestPersistenceModule(), new NavigationModule(), new EventBusModule(), new UtilModule(),
                new DataModule(), new DataTypeModule(), new UtilsModule(), new InMemoryModule().provideOptionDao());
        provider = injector.getInstance(UIProvider.class);
        createUI(BasicUI.class);
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

    static class MockSitemapService extends AbstractService implements SitemapService {

        @Inject
        protected MockSitemapService(Translate translate, GlobalBusProvider globalBusProvider, RelatedServicesExecutor
                servicesExecutor) {
            super(translate, globalBusProvider, servicesExecutor);
        }

        @Override
        public void doStart() throws Exception {

        }

        @Override
        public void doStop() {

        }

        @Override
        public I18NKey getNameKey() {
            return LabelKey.Sitemap_Service;
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
