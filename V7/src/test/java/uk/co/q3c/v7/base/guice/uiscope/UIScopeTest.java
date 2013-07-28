package uk.co.q3c.v7.base.guice.uiscope;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.co.q3c.v7.base.data.V7DefaultConverterFactory;
import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.DefaultURIPermissionFactory;
import uk.co.q3c.v7.base.shiro.DefaultUnauthenticatedExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultUnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.shiro.URIPermissionFactory;
import uk.co.q3c.v7.base.shiro.UnauthenticatedExceptionHandler;
import uk.co.q3c.v7.base.shiro.UnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.shiro.V7ErrorHandler;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.ui.BasicUIProvider;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.view.DefaultErrorView;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.component.SubjectProvider;
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
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

public class UIScopeTest {

	static ErrorHandler mockedErrorHandler = mock(ErrorHandler.class);
	static ConverterFactory mockedConverterFactory = mock(ConverterFactory.class);

	private ScopedUI ui;
	private Injector injector;
	private static V7Navigator navigator;
	static Provider<UI> uiProvider;
	protected VaadinRequest mockedRequest = mock(VaadinRequest.class);
	protected VaadinSession mockedSession = mock(VaadinSession.class);
	public int connectCount;
	UIKeyProvider uiKeyProvider = new UIKeyProvider();
	static Map<String, Provider<UI>> uibinder;

	UIProvider provider;

	static class TestObject {

	}

	static class TestModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(UIProvider.class).to(BasicUIProvider.class);
			MapBinder<String, UI> uiProviders = MapBinder.newMapBinder(binder(), String.class, UI.class);
			uiProviders.addBinding(BasicUI.class.getName()).to(BasicUI.class);
			MapBinder<String, V7View> mapbinder = MapBinder.newMapBinder(binder(), String.class, V7View.class);
			uibinder = new HashMap<>();
			uibinder.put(BasicUI.class.getName(), uiProvider);
			bind(V7Navigator.class).to(DefaultV7Navigator.class);
			bind(TestObject.class).in(UIScoped.class);
			bind(ErrorView.class).to(DefaultErrorView.class);
			bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
			bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			bind(ErrorHandler.class).to(V7ErrorHandler.class);
			bind(ConverterFactory.class).to(V7DefaultConverterFactory.class);
			bind(UnauthenticatedExceptionHandler.class).to(DefaultUnauthenticatedExceptionHandler.class);
			bind(UnauthorizedExceptionHandler.class).to(DefaultUnauthorizedExceptionHandler.class);
			bind(Subject.class).toProvider(SubjectProvider.class);
			bind(URIPermissionFactory.class).to(DefaultURIPermissionFactory.class);
			bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
		}
	}

	public class ConnectorIdAnswer implements Answer<String> {

		@Override
		public String answer(InvocationOnMock invocation) throws Throwable {
			connectCount++;
			return Integer.toString(connectCount);
		}

	}

	//
	// @Test
	// public void uiScope() {
	// // given
	//
	// uia = getTestUI();
	// uib = getTestUI();
	// // when
	//
	// // then
	// // Just to make sure we are not looking at the same instance
	// Assert.assertNotEquals(uia, uib);
	//
	// // ui instances should have different panels
	// Assert.assertNotEquals(uia.getPanel1(), uib.getPanel1());
	// Assert.assertNotEquals(uia.getPanel2(), uib.getPanel2());
	// Assert.assertNotEquals(uia.getPanel2(), uib.getPanel1());
	// Assert.assertNotEquals(uia.getPanel2(), uib.getPanel2());
	//
	// // but both header bars should be the same within a ui instance
	// Assert.assertEquals(uia.getPanel1(), uia.getPanel2());
	// Assert.assertEquals(uib.getPanel1(), uib.getPanel2());
	//
	// // given
	// VaadinServletRequest mockedRequest = mock(VaadinServletRequest.class);
	// when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
	//
	// CurrentInstance.set(UI.class, null);
	// CurrentInstance.set(UIKey.class, null);
	// CurrentInstance.set(UI.class, uia);
	// when(mockedSession.createConnectorId(Matchers.any(UI.class))).thenAnswer(new ConnectorIdAnswer());
	// when(mockedSession.getLocale()).thenReturn(Locale.UK);
	// uia.setSession(mockedSession);
	// uia.doInit(mockedRequest, 1);
	//
	// LoginStatusPanel originalHeader = uia.getPanel1();
	//
	// // when
	// // simulates key being cleared by framework during navigation
	// CurrentInstance.set(UIKey.class, null);
	// uia.getV7Navigator().navigateTo("view2");
	//
	// // then
	// // this is not a good test do I need TestBench?
	// Assert.assertEquals(uia.getPanel1(), originalHeader);
	//
	// // // when ui is closed
	// // uia.detach();
	// //
	// // // then scope cache should have been cleared
	// // assertThat(scope.cacheHasEntryFor(uia)).isFalse();
	// // assertThat(scope.cacheHasEntryFor(uib)).isTrue();
	// }

	@Test
	public void uiScope2() {

		// given
		// when
		injector = Guice.createInjector(new TestModule(), new UIScopeModule());
		provider = injector.getInstance(UIProvider.class);
		when(mockedSession.hasLock()).thenReturn(true);
		createUI(BasicUI.class);
		navigator = injector.getInstance(V7Navigator.class);
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
		ui.doInit(mockedRequest, 23);
		return ui;
	}

	// /**
	// * Cannot use the inherited {@link #createTestUI()}, because that sets up the CurrentInstance. For this test we
	// need
	// * more than one CurrentInstance
	// *
	// * @return
	// */
	// private TestUI getTestUI() {
	// CurrentInstance.set(UI.class, null);
	// CurrentInstance.set(UIKey.class, null);
	// return (TestUI) provider.createInstance(TestUI.class);
	//
	// }
	//
	// @ModuleProvider
	// private ApplicationViewModule applicationViewModuleProvider() {
	// return TestHelper.applicationViewModuleUsingSitemap();
	// }
	//
	protected ScopedUIProvider getUIProvider() {
		// return new TestUIProvider(injector, uibinder, uiKeyProvider);
		return (ScopedUIProvider) provider;
	}

}
