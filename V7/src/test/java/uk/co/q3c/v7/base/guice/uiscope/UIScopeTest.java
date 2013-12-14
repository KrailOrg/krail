package uk.co.q3c.v7.base.guice.uiscope;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.co.q3c.util.ResourceUtils;
import uk.co.q3c.v7.base.config.ApplicationConfigurationService;
import uk.co.q3c.v7.base.config.DefaultApplicationConfigurationService;
import uk.co.q3c.v7.base.data.V7DefaultConverterFactory;
import uk.co.q3c.v7.base.navigate.DefaultInvalidURIExceptionHandler;
import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.InvalidURIExceptionHandler;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultFileSitemapLoader;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultSitemapService;
import uk.co.q3c.v7.base.navigate.sitemap.FileSitemapLoader;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapService;
import uk.co.q3c.v7.base.services.ServicesMonitorModule;
import uk.co.q3c.v7.base.shiro.DefaultLoginStatusHandler;
import uk.co.q3c.v7.base.shiro.DefaultSubjectIdentifier;
import uk.co.q3c.v7.base.shiro.DefaultURIPermissionFactory;
import uk.co.q3c.v7.base.shiro.DefaultUnauthenticatedExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultUnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultVaadinSessionProvider;
import uk.co.q3c.v7.base.shiro.LoginStatusHandler;
import uk.co.q3c.v7.base.shiro.SubjectIdentifier;
import uk.co.q3c.v7.base.shiro.URIPermissionFactory;
import uk.co.q3c.v7.base.shiro.UnauthenticatedExceptionHandler;
import uk.co.q3c.v7.base.shiro.UnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.shiro.V7ErrorHandler;
import uk.co.q3c.v7.base.shiro.V7SecurityManager;
import uk.co.q3c.v7.base.shiro.VaadinSessionManager;
import uk.co.q3c.v7.base.shiro.VaadinSessionProvider;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.ui.BasicUIProvider;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.useropt.DefaultUserOption;
import uk.co.q3c.v7.base.useropt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.useropt.UserOption;
import uk.co.q3c.v7.base.useropt.UserOptionStore;
import uk.co.q3c.v7.base.view.DefaultErrorView;
import uk.co.q3c.v7.base.view.DefaultPublicHomeView;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.I18NTranslator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

public class UIScopeTest {

	static ErrorHandler mockedErrorHandler = mock(ErrorHandler.class);
	static ConverterFactory mockedConverterFactory = mock(ConverterFactory.class);

	private ScopedUI ui;
	private Injector injector;
	// private static V7Navigator navigator;
	static Provider<UI> uiProvider;
	protected VaadinRequest mockedRequest = mock(VaadinRequest.class);
	protected VaadinSession mockedSession = mock(VaadinSession.class);
	public int connectCount;
	UIKeyProvider uiKeyProvider = new UIKeyProvider();
	static Map<String, Provider<UI>> uibinder;
	static Subject subject = mock(Subject.class);

	UIProvider provider;

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

			MapBinder<String, UI> uiProviders = MapBinder.newMapBinder(binder(), String.class, UI.class);
			bind(UIProvider.class).to(BasicUIProvider.class);
			uiProviders.addBinding(BasicUI.class.getName()).to(BasicUI.class);
			uibinder = new HashMap<>();
			uibinder.put(BasicUI.class.getName(), uiProvider);

			bind(PublicHomeView.class).to(DefaultPublicHomeView.class);
			bind(V7Navigator.class).to(DefaultV7Navigator.class);
			bind(TestObject.class).in(UIScoped.class);
			bind(ErrorView.class).to(DefaultErrorView.class);
			bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
			bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			bind(ErrorHandler.class).to(V7ErrorHandler.class);
			bind(ConverterFactory.class).to(V7DefaultConverterFactory.class);
			bind(UnauthenticatedExceptionHandler.class).to(DefaultUnauthenticatedExceptionHandler.class);
			bind(UnauthorizedExceptionHandler.class).to(DefaultUnauthorizedExceptionHandler.class);
			bind(Subject.class).toProvider(MockSubjectProvider.class);
			bind(URIPermissionFactory.class).to(DefaultURIPermissionFactory.class);
			bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			bind(UserOption.class).to(DefaultUserOption.class);
			bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
			bind(InvalidURIExceptionHandler.class).to(DefaultInvalidURIExceptionHandler.class);
			bind(VaadinSessionProvider.class).to(DefaultVaadinSessionProvider.class);
			bind(SessionManager.class).to(VaadinSessionManager.class).asEagerSingleton();
			bind(LoginStatusHandler.class).to(DefaultLoginStatusHandler.class);
			bind(SubjectIdentifier.class).to(DefaultSubjectIdentifier.class);
			bind(SitemapService.class).to(DefaultSitemapService.class);
			bind(FileSitemapLoader.class).to(DefaultFileSitemapLoader.class);
			bind(ApplicationConfigurationService.class).to(DefaultApplicationConfigurationService.class);

		}
	}

	public class ConnectorIdAnswer implements Answer<String> {

		@Override
		public String answer(InvocationOnMock invocation) throws Throwable {
			connectCount++;
			return Integer.toString(connectCount);
		}

	}

	static VaadinService vaadinService;

	@BeforeClass
	public static void setupClass() {
		vaadinService = mock(VaadinService.class);
		when(vaadinService.getBaseDirectory()).thenReturn(ResourceUtils.userTempDirectory());
		VaadinService.setCurrent(vaadinService);
	}

	@Test
	public void uiScope2() {

		// given
		SecurityUtils.setSecurityManager(new V7SecurityManager());
		when(subject.isPermitted(anyString())).thenReturn(true);
		when(subject.isPermitted(any(org.apache.shiro.authz.Permission.class))).thenReturn(true);
		when(mockedSession.hasLock()).thenReturn(true);

		// when

		injector = Guice.createInjector(new TestModule(), new UIScopeModule(), new ServicesMonitorModule());
		provider = injector.getInstance(UIProvider.class);
		createUI(BasicUI.class);
		// navigator = injector.getInstance(V7Navigator.class);
		TestObject to1 = injector.getInstance(TestObject.class);
		createUI(BasicUI.class);
		TestObject to2 = injector.getInstance(TestObject.class);
		// then

		assertThat(to1).isNotNull();
		assertThat(to2).isNotNull();
		assertThat(to1).isNotEqualTo(to2);
	}

	@SuppressWarnings("deprecation")
	protected ScopedUI createUI(Class<? extends ScopedUI> clazz) {

		String baseUri = "http://example.com";
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, null);

		ui = (ScopedUI) getUIProvider().createInstance(clazz);
		ui.setLocale(Locale.UK);
		CurrentInstance.set(UI.class, ui);
		when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
		when(mockedSession.createConnectorId(Matchers.any(ClientConnector.class))).thenAnswer(new ConnectorIdAnswer());
		ui.setSession(mockedSession);

		return ui;
	}

	protected ScopedUIProvider getUIProvider() {
		return (ScopedUIProvider) provider;
	}

}
