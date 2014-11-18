/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.guice.uiscope;

import com.google.inject.*;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.*;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.q3c.krail.core.config.ApplicationConfigurationService;
import uk.q3c.krail.core.config.DefaultApplicationConfigurationService;
import uk.q3c.krail.core.config.IniFileConfig;
import uk.q3c.krail.core.data.KrailDefaultConverterFactory;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScope;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.navigate.*;
import uk.q3c.krail.core.navigate.sitemap.*;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;
import uk.q3c.krail.core.services.AbstractServiceI18N;
import uk.q3c.krail.core.services.ServicesMonitorModule;
import uk.q3c.krail.core.shiro.*;
import uk.q3c.krail.core.ui.*;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.user.opt.DefaultUserOption;
import uk.q3c.krail.core.user.opt.DefaultUserOptionStore;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionStore;
import uk.q3c.krail.core.view.*;
import uk.q3c.krail.core.view.component.StandardComponentModule;
import uk.q3c.krail.i18n.*;
import uk.q3c.util.ResourceUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIScopeTest {

    static ErrorHandler mockedErrorHandler = mock(ErrorHandler.class);
    static ConverterFactory mockedConverterFactory = mock(ConverterFactory.class);
    static Provider<UI> uiProvider;
    static Map<String, Provider<UI>> uibinder;
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

        injector = Guice.createInjector(new TestModule(), new UIScopeModule(), new ServicesMonitorModule(),
                new UserModule(), new StandardComponentModule());
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

        // private final Scope vaadinSessionScope = mock(VaadinSessionScope.class);
        private final Scope vaadinSessionScope = new VaadinSessionScope();
        private MapBinder<Integer, PatternSource> patternSources;
        private Multibinder<String> registeredAnnotations;
        private Multibinder<String> registeredValueAnnotations;
        private Multibinder<Locale> supportedLocales;

        @SuppressWarnings("unused")
        @Override
        protected void configure() {
            registeredAnnotations = newSetBinder(binder(), String.class, I18N.class);
            registeredValueAnnotations = newSetBinder(binder(), String.class, I18NValue.class);
            patternSources = MapBinder.newMapBinder(binder(), Integer.class, PatternSource.class, PatternSources.class);

            bind(ApplicationTitle.class).toInstance(new ApplicationTitle(LabelKey.Krail));

            MapBinder<String, UI> uiProviders = MapBinder.newMapBinder(binder(), String.class, UI.class);
            bind(UIProvider.class).to(BasicUIProvider.class);
            uiProviders.addBinding(BasicUI.class.getName())
                       .to(BasicUI.class);
            uibinder = new HashMap<>();
            uibinder.put(BasicUI.class.getName(), uiProvider);

            bind(PublicHomeView.class).to(DefaultPublicHomeView.class);
            bind(Navigator.class).to(DefaultNavigator.class);
            bind(TestObject.class).in(UIScoped.class);
            bind(ErrorView.class).to(DefaultErrorView.class);
            bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
            bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
            bind(ErrorHandler.class).to(KrailErrorHandler.class);
            bind(ConverterFactory.class).to(KrailDefaultConverterFactory.class);
            bind(UnauthenticatedExceptionHandler.class).to(DefaultUnauthenticatedExceptionHandler.class);
            bind(UnauthorizedExceptionHandler.class).to(DefaultUnauthorizedExceptionHandler.class);
            bind(Subject.class).toProvider(MockSubjectProvider.class);
            bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
            bind(UserOption.class).to(DefaultUserOption.class);
            bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
            bind(InvalidURIExceptionHandler.class).to(DefaultInvalidURIExceptionHandler.class);
            bind(VaadinSessionProvider.class).to(DefaultVaadinSessionProvider.class);
            bind(SessionManager.class).to(VaadinSessionManager.class)
                                      .asEagerSingleton();
            bind(SubjectIdentifier.class).to(DefaultSubjectIdentifier.class);
            bind(SitemapService.class).to(MockSitemapService.class);
            bind(FileSitemapLoader.class).to(DefaultFileSitemapLoader.class);
            bind(ApplicationConfigurationService.class).to(DefaultApplicationConfigurationService.class);
            bind(CurrentLocale.class).to(DefaultCurrentLocale.class);
            MapBinder<Integer, IniFileConfig> iniFileConfigs = MapBinder.newMapBinder(binder(), Integer.class,
                    IniFileConfig.class);
            MapBinder<String, KrailView> viewMapping = MapBinder.newMapBinder(binder(), String.class, KrailView.class);
            bind(ScopedUIProvider.class).to(BasicUIProvider.class);
            bindScope(VaadinSessionScoped.class, vaadinSessionScope);
            bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
            bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
            bind(UserSitemap.class).to(DefaultUserSitemap.class);
            bind(UserOption.class).to(DefaultUserOption.class);
            bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
            bind(UserSitemapSorters.class).to(DefaultUserSitemapSorters.class);
            bind(Translate.class).to(DefaultTranslate.class);
            bind(Locale.class).annotatedWith(DefaultLocale.class)
                              .toInstance(Locale.UK);
            registeredAnnotations.addBinding()
                                 .toInstance(I18N.class.getName());
            registeredValueAnnotations.addBinding()
                                      .toInstance(I18NValue.class.getName());
            supportedLocales = newSetBinder(binder(), Locale.class, SupportedLocales.class);
            supportedLocales.addBinding()
                            .toInstance(Locale.UK);
            patternSources.addBinding(10)
                          .to(DefaultJavaMapPatternSource.class);

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
