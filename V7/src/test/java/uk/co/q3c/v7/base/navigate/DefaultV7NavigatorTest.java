package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.shiro.ShiroIntegrationTestBase;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.Page;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultV7NavigatorTest extends ShiroIntegrationTestBase {
	DefaultV7Navigator navigator;

	@Mock
	Provider<ErrorView> errorViewPro;

	StrictURIFragmentHandler uriHandler;

	@Mock
	Map<String, Provider<V7View>> viewProMap;

	@Mock
	Provider<V7View> loginViewPro;

	@Mock
	Provider<V7View> logoutViewPro;

	@Mock
	ScopedUI scopedUI;

	@Mock
	LoginView loginView;

	@Mock
	LogoutView logoutView;

	@Mock
	V7View previousView;

	@Mock
	Provider<V7View> view1Pro;

	@Mock
	Provider<V7View> view2Pro;

	@Mock
	V7View view1;

	@Mock
	V7View view2;

	@Mock
	V7View privateHomeView;

	@Inject
	Injector injector;

	@Mock
	Page page;

	@Mock
	ErrorView errorView;

	@Mock
	V7ViewChangeListener listener1;

	@Mock
	V7ViewChangeListener listener2;

	@Mock
	V7ViewChangeListener listener3;

	V7Ini ini;

	@Mock
	Provider<V7View> privateHomePro;

	@Inject
	Provider<V7Ini> iniPro;

	@Mock
	Sitemap sitemap;

	@Override
	@Before
	public void setupShiro() {
		super.setupShiro();
		ini = iniPro.get();
		ini.validate();

		// sitemap = new TextReaderSitemapProvider(new StandardPageBuilder()).get();

		uriHandler = new StrictURIFragmentHandler();

		when(scopedUI.getPage()).thenReturn(page);
		when(errorViewPro.get()).thenReturn(errorView);

		navigator = new DefaultV7Navigator(errorViewPro, uriHandler, sitemap, viewProMap, getSecurityManager());
		CurrentInstance.set(UI.class, scopedUI);
	}

	@Test
	public void logout() {

		// given
		String page = "public/logout";
		when(sitemap.standardPageURI(StandardPageKey.Logout)).thenReturn(page);
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(logoutViewPro);
		when(logoutViewPro.get()).thenReturn(logoutView);
		// when
		navigator.navigateTo(StandardPageKey.Logout);
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(LogoutView.class);
		verify(scopedUI).changeView(null, logoutView);
	}

	@Test
	public void login() {
		// given
		String page = "public/login";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(loginViewPro);
		when(loginViewPro.get()).thenReturn(loginView);
		// when
		navigator.navigateTo(page);
		// then

		assertThat(navigator.getCurrentView()).isInstanceOf(LoginView.class);
		verify(scopedUI).changeView(null, loginView);

	}

	@Test
	public void loginSuccessFul_toPreviousView() {

		// given
		String page = "public/view2";
		String page2 = "public/login";

		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemap.getRedirectFor(page2)).thenReturn(page2);

		when(viewProMap.get(page)).thenReturn(view2Pro);
		when(viewProMap.get(page2)).thenReturn(loginViewPro);

		when(view2Pro.get()).thenReturn(view2);
		when(loginViewPro.get()).thenReturn(loginView);

		when(sitemap.standardPageURI(StandardPageKey.Login)).thenReturn(page2);

		navigator.navigateTo(page);
		navigator.navigateTo(StandardPageKey.Login);
		// when
		navigator.loginSuccessful();
		// then
		assertThat(navigator.getNavigationState()).isEqualTo(page);
	}

	@Test
	public void loginSuccessFul_noPreviousView() {

		// given
		String page = "private";
		when(sitemap.standardPageURI(StandardPageKey.Private_Home)).thenReturn(page);
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(privateHomePro);
		when(privateHomePro.get()).thenReturn(privateHomeView);

		navigator.setCurrentView(loginView, "xx", "yy");
		navigator.setPreviousView(null);
		// when
		navigator.loginSuccessful();
		// then
		verify(scopedUI).changeView(loginView, privateHomeView);

	}

	@Test
	public void navigateTo() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(view2Pro);
		when(view2Pro.get()).thenReturn(view2);

		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);

	}

	@Test
	public void navigateToEmptyPageWithParams() {

		// given
		String page1 = "";
		String fragment1 = page1 + "/id=2/age=5";
		when(sitemap.getRedirectFor(page1)).thenReturn("public");
		when(viewProMap.get(page1)).thenReturn(view1Pro);
		when(view1Pro.get()).thenReturn(view1);
		// when
		navigator.navigateTo(fragment1);
		// then
		assertThat(navigator.getNavigationState()).isEqualTo("public/id=2/age=5");

	}

	@Test
	public void navigateTo_invalidURI() {

		// given
		// given
		String page = "public/view3";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(null);
		when(view2Pro.get()).thenReturn(view2);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentView()).isEqualTo(errorView);

	}

	@Test
	public void getNavigationState() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(view2Pro);
		when(view2Pro.get()).thenReturn(view2);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getNavigationState()).isEqualTo(page);

	}

	@Test
	public void getNavigationParams() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(view2Pro);
		when(view2Pro.get()).thenReturn(view2);
		// when
		navigator.navigateTo("public/view2/id=1/age=2");
		// then
		assertThat(navigator.getNavigationParams()).containsOnly("id=1", "age=2");

	}

	@Test
	public void navigateToNode() {

		// given
		String page = "public/view2";
		SitemapNode node = new SitemapNode();
		when(sitemap.uri(node)).thenReturn(page);
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(view2Pro);
		when(view2Pro.get()).thenReturn(view2);

		// when
		navigator.navigateTo(node);
		// then
		assertThat(navigator.getNavigationState()).isEqualTo(page);

	}

	@Test
	public void currentAndPreviousViews_andClearHistory() {

		// given
		String page1 = "view1";
		String fragment1 = page1 + "/id=1";
		when(sitemap.getRedirectFor(page1)).thenReturn(page1);
		when(viewProMap.get(page1)).thenReturn(view1Pro);
		when(view1Pro.get()).thenReturn(view1);

		String page2 = "view2";
		String fragment2 = page2 + "/id=2";
		when(sitemap.getRedirectFor(page2)).thenReturn(page2);
		when(viewProMap.get(page2)).thenReturn(view2Pro);
		when(view2Pro.get()).thenReturn(view2);

		// when

		// then
		// start position
		assertThat(navigator.getCurrentView()).isNull();
		assertThat(navigator.getCurrentViewName()).isNull();
		assertThat(navigator.getNavigationState()).isNull();

		assertThat(navigator.getPreviousView()).isNull();
		assertThat(navigator.getPreviousViewName()).isNull();
		assertThat(navigator.getPreviousFragment()).isNull();

		// when
		navigator.navigateTo(fragment1);

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view1);
		assertThat(navigator.getCurrentViewName()).isEqualTo(page1);
		assertThat(navigator.getNavigationState()).isEqualTo(fragment1);

		assertThat(navigator.getPreviousView()).isNull();
		assertThat(navigator.getPreviousViewName()).isNull();
		assertThat(navigator.getPreviousFragment()).isNull();

		// when
		navigator.navigateTo(fragment2);

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);
		assertThat(navigator.getCurrentViewName()).isEqualTo(page2);
		assertThat(navigator.getNavigationState()).isEqualTo(fragment2);

		assertThat(navigator.getPreviousView()).isEqualTo(view1);
		assertThat(navigator.getPreviousViewName()).isEqualTo(page1);
		assertThat(navigator.getPreviousFragment()).isEqualTo(fragment1);

		// when
		navigator.clearHistory();

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);
		assertThat(navigator.getCurrentViewName()).isEqualTo(page2);
		assertThat(navigator.getNavigationState()).isEqualTo(fragment2);

		assertThat(navigator.getPreviousView()).isNull();
		assertThat(navigator.getPreviousViewName()).isNull();
		assertThat(navigator.getPreviousFragment()).isNull();
	}

	/**
	 * Checks add and remove listeners
	 */
	@Test
	public void listeners_allRespond() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(view2Pro);
		when(view2Pro.get()).thenReturn(view2);
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
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(viewProMap.get(page)).thenReturn(view2Pro);
		when(view2Pro.get()).thenReturn(view2);
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

		when(sitemap.getRedirectFor(page)).thenReturn(page2);
		when(viewProMap.get(page2)).thenReturn(view2Pro);
		when(view2Pro.get()).thenReturn(view2);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getNavigationState()).isEqualTo(page2);
	}

}
