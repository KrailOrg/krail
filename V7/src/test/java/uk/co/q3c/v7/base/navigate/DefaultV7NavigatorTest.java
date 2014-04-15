package uk.co.q3c.v7.base.navigate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModule;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapService;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.shiro.PageAccessController;
import uk.co.q3c.v7.base.shiro.PagePermission;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class })
public class DefaultV7NavigatorTest {

	private static final String public_view1 = "public/view1";
	private static final String public_view2 = "public/view2";
	private static final String public_login = "public/login";

	class MockNodeAnswer implements Answer<SitemapNode> {

		@Override
		public SitemapNode answer(InvocationOnMock invocation) throws Throwable {
			Object[] arguments = invocation.getArguments();
			NavigationState navigationState = (NavigationState) arguments[0];
			switch (navigationState.getVirtualPage()) {
			case public_view1:
				return mockNode1;

			case public_view2:
				return mockNode2;

			case public_login:
				return loginNode;
			}
			return null;
		}

	}

	static class View2 implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

		@Override
		public String viewName() {
			return "view2";
		}

		@Override
		public void setIds() {
		}

	}

	static class View1 implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

		@Override
		public String viewName() {

			return "view1";
		}

		@Override
		public void setIds() {
		}

	}

	static class LogoutView implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

		@Override
		public String viewName() {

			return "logout";
		}

		@Override
		public void setIds() {
		}

	}

	static class LoginView implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

		@Override
		public String viewName() {

			return "login";
		}

		@Override
		public void setIds() {
		}

	}

	static class PrivateHomeView implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

		@Override
		public String viewName() {

			return "private home";
		}

		@Override
		public void setIds() {
		}

	}

	static class PublicHomeView implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

		@Override
		public String viewName() {

			return "public home";
		}

		@Override
		public void setIds() {
		}

	}

	DefaultV7Navigator navigator;

	@Mock
	Provider<ErrorView> errorViewProvider;

	StrictURIFragmentHandler uriHandler;

	@Inject
	Map<String, Provider<V7View>> viewMapping;

	@Mock
	ScopedUI scopedUI;

	@Mock
	V7View previousView;

	@Mock
	View1 view1;

	@Mock
	View2 view2;

	@Mock
	V7View privateHomeView;

	@Mock
	Injector injector;

	@Mock
	Page browserPage;

	@Mock
	ErrorView errorView;

	@Mock
	V7ViewChangeListener listener1;

	@Mock
	V7ViewChangeListener listener2;

	@Mock
	V7ViewChangeListener listener3;

	@Mock
	Sitemap sitemap;

	@Mock
	SitemapService sitemapService;

	@Mock
	SubjectProvider subjectProvider;

	@Mock
	Subject subject;

	@Mock
	Collator collator;

	@Mock
	Translate translate;

	@Inject
	PageAccessController pageAccessController;

	@Mock
	ScopedUIProvider uiProvider;

	// had some issues with mocking this - the getViewClass() method wouldn't play
	// so resorted to old fashioned mocking
	SitemapNode mockNode1;
	SitemapNode mockNode2;
	SitemapNode loginNode;

	@Before
	public void setup() {
		// ini = iniPro.get();
		// ini.validate();

		// sitemap = new TextReaderSitemapProvider(new StandardPageBuilder()).get();

		uriHandler = new StrictURIFragmentHandler();
		mockNode1 = new SitemapNode();
		mockNode2 = new SitemapNode();
		loginNode = new SitemapNode();

		mockNode1.setUriSegment("view1");
		mockNode2.setUriSegment("view2");
		loginNode.setUriSegment("login");
		loginNode.setPageAccessControl(PageAccessControl.PUBLIC);

		when(sitemapService.getSitemap()).thenReturn(sitemap);
		when(uiProvider.get()).thenReturn(scopedUI);
		when(scopedUI.getPage()).thenReturn(browserPage);
		when(errorViewProvider.get()).thenReturn(errorView);
		when(subjectProvider.get()).thenReturn(subject);
		when(injector.getInstance(View2.class)).thenReturn(view2);
		when(injector.getInstance(View1.class)).thenReturn(view1);
		when(sitemap.uri(mockNode1)).thenReturn(public_view1);

		navigator = new DefaultV7Navigator(uriHandler, viewMapping, errorViewProvider, sitemapService, subjectProvider,
				pageAccessController, uiProvider);

		CurrentInstance.set(UI.class, scopedUI);
	}

	@Test
	public void logout() {

		// given
		String page = "public/logout";
		when(sitemap.standardPageURI(StandardPageKey.Logout)).thenReturn(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setViewClass(LogoutView.class);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		// when
		navigator.navigateTo(StandardPageKey.Logout);
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(LogoutView.class);
		verify(scopedUI).changeView(any(LogoutView.class));
	}

	@Test
	public void login() {
		// given
		String page = "public/login";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		mockNode1.setViewClass(LoginView.class);
		// when
		navigator.navigateTo(page);
		// then

		assertThat(navigator.getCurrentView()).isInstanceOf(LoginView.class);
		verify(scopedUI).changeView(any(LoginView.class));

	}

	@Test
	public void loginSuccessFul_toPreviousView() {
		Sitemap sitemap = new Sitemap(uriHandler, translate);
		SitemapNode privateHomeNode = sitemap.append("private/home");
		SitemapNode node2 = sitemap.append("public/view2");
		SitemapNode loginNode = sitemap.append("public/login");
		sitemap.addStandardPage(StandardPageKey.Login, loginNode);
		sitemap.addStandardPage(StandardPageKey.Private_Home, privateHomeNode);

		when(sitemapService.getSitemap()).thenReturn(sitemap);

		node2.setPageAccessControl(PageAccessControl.PUBLIC);
		loginNode.setPageAccessControl(PageAccessControl.PUBLIC);

		privateHomeNode.setViewClass(View1.class);
		privateHomeNode.setPageAccessControl(PageAccessControl.PERMISSION);
		when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);

		node2.setViewClass(View2.class);
		loginNode.setViewClass(LoginView.class);
		navigator = new DefaultV7Navigator(uriHandler, viewMapping, errorViewProvider, sitemapService, subjectProvider,
				pageAccessController, uiProvider);

		CurrentInstance.set(UI.class, scopedUI);
		// when
		navigator.navigateTo("public/view2");
		navigator.navigateTo(StandardPageKey.Login);
		assertThat(navigator.getCurrentView()).isInstanceOf(LoginView.class);
		// // when
		navigator.loginSuccessful();
		// // then
		assertThat(navigator.getCurrentView()).isInstanceOf(View2.class);
	}

	@Test
	public void loginSuccessFul_noPreviousView() {

		// given

		Sitemap sitemap = new Sitemap(uriHandler, translate);
		SitemapNode privateHomeNode = sitemap.append("private/home");
		SitemapNode node2 = sitemap.append("public/home/view2");
		SitemapNode loginNode = sitemap.append("public/login");
		sitemap.addStandardPage(StandardPageKey.Login, loginNode);
		sitemap.addStandardPage(StandardPageKey.Private_Home, privateHomeNode);

		when(sitemapService.getSitemap()).thenReturn(sitemap);

		node2.setPageAccessControl(PageAccessControl.PUBLIC);
		loginNode.setPageAccessControl(PageAccessControl.PUBLIC);

		privateHomeNode.setViewClass(View1.class);
		privateHomeNode.setPageAccessControl(PageAccessControl.PERMISSION);
		when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
		node2.setViewClass(View2.class);
		loginNode.setViewClass(LoginView.class);
		navigator = new DefaultV7Navigator(uriHandler, viewMapping, errorViewProvider, sitemapService, subjectProvider,
				pageAccessController, uiProvider);

		CurrentInstance.set(UI.class, scopedUI);
		// when
		navigator.navigateTo(StandardPageKey.Login);
		assertThat(navigator.getCurrentView()).isInstanceOf(LoginView.class);
		verify(scopedUI).changeView(any(V7View.class));
		// // when
		navigator.loginSuccessful();
		// // then
		verify(scopedUI, times(2)).changeView(any(V7View.class));
		assertThat(navigator.getCurrentView()).isInstanceOf(PrivateHomeView.class);

	}

	@Test
	public void navigateTo() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		mockNode1.setViewClass(View2.class);

		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(View2.class);

	}

	@Test
	public void navigateToEmptyPageWithParams() {

		// given
		String page1 = "";
		String fragment1 = page1 + "/id=2/age=5";

		when(sitemap.standardPageURI(StandardPageKey.Public_Home)).thenReturn("public/home");
		when(sitemap.getRedirectPageFor("")).thenReturn("public/home");
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		mockNode1.setViewClass(View1.class);

		// when
		navigator.navigateTo(fragment1);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo("public/home/age=5/id=2");

	}

	@Test(expected = InvalidURIException.class)
	public void navigateTo_invalidURI() {

		// given
		// given
		String page = "public/view3";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);

		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentView()).isEqualTo(errorView);

	}

	@Test
	public void getNavigationState() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		mockNode1.setViewClass(View2.class);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(page);

	}

	@Test
	public void getNavigationParams() {

		// given
		String page = "public/view2";
		String pageWithParams = "public/view2/id=1/age=2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setViewClass(View2.class);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		// when
		navigator.navigateTo(pageWithParams);
		// then
		assertThat(navigator.getNavigationParams()).containsOnly("id=1", "age=2");

	}

	@Test
	public void navigateToNode() {

		// given
		String page = "public/view2";
		mockNode1 = new SitemapNode(page, view2.getClass(), LabelKey.Cancel, Locale.UK, collator, translate);
		when(sitemap.uri(mockNode1)).thenReturn(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		when(browserPage.getUriFragment()).thenReturn("wiggly");
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		mockNode1.setViewClass(View2.class);

		// when
		navigator.navigateTo(mockNode1);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(page);

	}

	@Test
	public void currentAndPreviousViews_andClearHistory() {

		// given
		String page1 = "public/view1";
		String fragment1 = page1 + "/id=1";
		when(sitemap.getRedirectPageFor(page1)).thenReturn(page1);
		when(sitemap.nodeFor(any(NavigationState.class))).thenAnswer(new MockNodeAnswer());
		mockNode1.setViewClass(View1.class);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);

		String page2 = "public/view2";
		String fragment2 = page2 + "/id=2";
		when(sitemap.getRedirectPageFor(page2)).thenReturn(page2);
		when(sitemap.nodeFor(fragment2)).thenReturn(mockNode2);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.PUBLIC);

		// when

		// then
		// start position
		assertThat(navigator.getCurrentView()).isNull();
		assertThat(navigator.getCurrentNavigationState()).isNull();

		assertThat(navigator.getPreviousNavigationState()).isNull();

		// when
		navigator.navigateTo(fragment1);

		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(View1.class);
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(fragment1);

		assertThat(navigator.getPreviousNavigationState()).isNull();

		// when
		navigator.navigateTo(fragment2);

		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(View2.class);
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(fragment2);

		assertThat(navigator.getPreviousNavigationState().getFragment()).isEqualTo(fragment1);

		// when
		navigator.clearHistory();

		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(View2.class);
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(fragment2);

		assertThat(navigator.getPreviousNavigationState()).isNull();
	}

	/**
	 * Checks add and remove listeners
	 */
	@Test
	public void listeners_allRespond() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		mockNode1.setViewClass(View2.class);

		// need to return true, or first listener will block the second
		when(listener1.beforeViewChange(any(V7ViewChangeEvent.class))).thenReturn(true);
		navigator.addViewChangeListener(listener1);
		navigator.addViewChangeListener(listener2);
		navigator.addViewChangeListener(listener3);
		// when
		navigator.removeViewChangeListener(listener3);
		navigator.navigateTo(page);
		// then
		verify(listener1, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener2, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener3, never()).beforeViewChange(any(V7ViewChangeEvent.class));
	}

	@Test
	public void listener_blocked() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		mockNode1.setViewClass(View2.class);
		// to block second and subsequent
		when(listener1.beforeViewChange(any(V7ViewChangeEvent.class))).thenReturn(false);
		navigator.addViewChangeListener(listener1);
		navigator.addViewChangeListener(listener2);
		navigator.addViewChangeListener(listener3);
		// when
		navigator.navigateTo(page);
		// then
		verify(listener1, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener2, never()).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener3, never()).beforeViewChange(any(V7ViewChangeEvent.class));
	}

	@Test
	public void redirection() {

		// given
		String page = "wiggly";
		String page2 = "private/transfers";

		when(sitemap.getRedirectPageFor(page)).thenReturn(page2);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode1);
		mockNode1.setPageAccessControl(PageAccessControl.PUBLIC);
		mockNode1.setViewClass(View2.class);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(page2);
	}

	@Test
	public void navigateToNavState() {

		// given
		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.PUBLIC);

		// when
		navigator.navigateTo(navigationState);
		// then
		assertThat(navigator.getCurrentNavigationState()).isEqualTo(navigationState);
		assertThat(navigator.getCurrentView()).isInstanceOf(View2.class);
	}

	@Test
	public void error() {

		// given

		// when
		navigator.error();
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(ErrorView.class);

	}

	@Test
	public void UAC_Public() {

		// given
		// given
		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.PUBLIC);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());
	}

	@Test
	public void UAC_User() {

		// given authenticated
		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.USER);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());

		// given remembered
		page = "public/view2";
		navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(true);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.USER);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());
	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_User_fail() {

		// given authenticated
		String page = "public/view2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(false);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.USER);
		// when
		navigator.navigateTo(page);
		// then
	}

	@Test
	public void UAC_Guest() {

		// given authenticated
		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(false);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.GUEST);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());

	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_Guest_Fail_remembered() {

		// given authenticated
		String page = "public/view2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(true);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.GUEST);
		// when
		navigator.navigateTo(page);
		// then
	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_Guest_Fail_authenticated() {

		// given authenticated
		String page = "public/view2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.GUEST);
		// when
		navigator.navigateTo(page);
		// then
	}

	@Test
	public void UAC_Authenticate() {

		// given authenticated
		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.AUTHENTICATION);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());

	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_Authenticate_Fail() {

		// given authenticated
		String page = "public/view2";
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(true);
		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.AUTHENTICATION);
		// when
		navigator.navigateTo(page);
		// then
	}

	@Test
	public void UAC_Permission() {

		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);

		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.PERMISSION);
		when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());
	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_Permission_Failed() {

		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);

		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.PERMISSION);
		when(subject.isPermitted(any(PagePermission.class))).thenReturn(false);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());
	}

	@Test
	public void UAC_roles() {

		// given
		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);

		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.ROLES);
		mockNode2.addRole("admin");
		mockNode2.addRole("beast");
		List<String> permissions = mockNode2.getRoles();
		when(subject.hasAllRoles(permissions)).thenReturn(true);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());

	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_roles_failed() {

		// given
		String page = "public/view2";
		NavigationState navigationState = uriHandler.navigationState(page);
		when(sitemap.getRedirectPageFor(page)).thenReturn(page);
		when(sitemap.nodeFor(any(NavigationState.class))).thenReturn(mockNode2);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);

		mockNode2.setViewClass(View2.class);
		mockNode2.setPageAccessControl(PageAccessControl.ROLES);
		mockNode2.addRole("admin");
		mockNode2.addRole("beast");
		List<String> permissions = mockNode2.getRoles();
		when(subject.hasAllRoles(permissions)).thenReturn(false);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(navigationState.getFragment());

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

	@ModuleProvider
	protected AbstractModule moduleProvider2() {
		return new DirectSitemapModule() {

			@Override
			protected void define() {
				addEntry("public/view1", View1.class, LabelKey.Yes, PageAccessControl.PUBLIC);
				addEntry("public/view2", View2.class, LabelKey.Yes, PageAccessControl.PUBLIC);
				addEntry("public/logout", LogoutView.class, LabelKey.Yes, PageAccessControl.PUBLIC);
				addEntry("public/login", LoginView.class, LabelKey.Yes, PageAccessControl.PUBLIC);
				addEntry("private/home", PrivateHomeView.class, LabelKey.Yes, PageAccessControl.PUBLIC);
				addEntry("public/home", PublicHomeView.class, LabelKey.Yes, PageAccessControl.PUBLIC);
				addEntry("private/transfers", View1.class, LabelKey.Yes, PageAccessControl.PUBLIC);

			}
		};

	}
}
