package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.config.V7Ini.StandardPageKey;
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
	V7View secureHomeView;

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
	Provider<V7View> secureHomePro;

	@Inject
	Provider<V7Ini> iniPro;

	@Override
	@Before
	public void setup() {
		super.setup();
		ini = iniPro.get();
		ini.validate();

		uriHandler = mock(StrictURIFragmentHandler.class);

		when(loginViewPro.get()).thenReturn(loginView);
		when(logoutViewPro.get()).thenReturn(logoutView);
		when(viewProMap.get("view1")).thenReturn(view1Pro);
		when(viewProMap.get("view2")).thenReturn(view2Pro);
		when(viewProMap.get("login")).thenReturn(loginViewPro);
		when(viewProMap.get("public/logout")).thenReturn(logoutViewPro);
		when(viewProMap.get("secure/home")).thenReturn(secureHomePro);

		when(view1Pro.get()).thenReturn(view1);
		when(view2Pro.get()).thenReturn(view2);
		when(loginViewPro.get()).thenReturn(loginView);
		when(logoutViewPro.get()).thenReturn(logoutView);
		when(secureHomePro.get()).thenReturn(secureHomeView);

		when(scopedUI.getPage()).thenReturn(page);
		when(errorViewPro.get()).thenReturn(errorView);

		// when (ini.get(StandardPageKey.secureHome)).thenReturn

		navigator = new DefaultV7Navigator(errorViewPro, uriHandler, ini, viewProMap, getSecurityManager());
		CurrentInstance.set(UI.class, scopedUI);
	}

	@Test
	public void logout() {

		// given
		when(uriHandler.setFragment(anyString())).thenReturn(uriHandler);
		when(uriHandler.virtualPage()).thenReturn(ini.standardPageURI(StandardPageKey.logout));

		// when
		navigator.navigateTo(StandardPageKey.logout);
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(LogoutView.class);
		verify(scopedUI).changeView(null, logoutView);
	}

	@Test
	public void login() {
		// given
		when(uriHandler.setFragment("login")).thenReturn(uriHandler);
		when(uriHandler.virtualPage()).thenReturn("login");
		// when
		navigator.navigateTo("login");
		// then

		assertThat(navigator.getCurrentView()).isInstanceOf(LoginView.class);
		verify(scopedUI).changeView(null, loginView);

	}

	@Test
	public void loginSuccessFul() {

		// given
		navigator.setCurrentView(loginView, "xx", "yy");
		navigator.setPreviousView(previousView);
		// when
		navigator.loginSuccessful();
		// then
		verify(scopedUI).changeView(loginView, previousView);

	}

	@Test
	public void loginSuccessFul_noPreviousView() {

		// given
		// need to construct this test slightly differently
		uriHandler = new StrictURIFragmentHandler();
		navigator = new DefaultV7Navigator(errorViewPro, uriHandler, ini, viewProMap, getSecurityManager());
		navigator.setCurrentView(loginView, "xx", "yy");
		navigator.setPreviousView(null);
		// when
		navigator.loginSuccessful();
		// then
		verify(scopedUI).changeView(loginView, secureHomeView);

	}

	@Test
	public void navigateTo() {

		// given
		when(uriHandler.setFragment("view2")).thenReturn(uriHandler);
		when(uriHandler.virtualPage()).thenReturn("view2");
		// when
		navigator.navigateTo("view2");
		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);

	}

	@Test
	public void navigateTo_invalidURI() {

		// given
		when(uriHandler.setFragment("view3")).thenReturn(uriHandler);
		when(uriHandler.virtualPage()).thenReturn("view3");
		// when
		navigator.navigateTo("view3");
		// then
		assertThat(navigator.getCurrentView()).isEqualTo(errorView);

	}

	@Test
	public void getNavigationState() {

		// given
		String fragment = "public/view1";
		when(uriHandler.fragment()).thenReturn(fragment);
		// when

		// then
		assertThat(navigator.getNavigationState()).isEqualTo(fragment);

	}

	@Test
	public void getNavigationParams() {

		// given
		List<String> params = new ArrayList<>();
		params.add("id=1");
		params.add("age=2");

		// when
		when(uriHandler.parameterList()).thenReturn(params);
		// then
		assertThat(navigator.geNavigationParams()).isEqualTo(params);

	}

	@Test
	public void currentAndPreviousViews_andClearHistory() {

		// given
		// need to construct this test slightly differently
		uriHandler = new StrictURIFragmentHandler();
		navigator = new DefaultV7Navigator(errorViewPro, uriHandler, ini, viewProMap, getSecurityManager());
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
		navigator.navigateTo("view1/id=1");

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view1);
		assertThat(navigator.getCurrentViewName()).isEqualTo("view1");
		assertThat(navigator.getNavigationState()).isEqualTo("view1/id=1");

		assertThat(navigator.getPreviousView()).isNull();
		assertThat(navigator.getPreviousViewName()).isNull();
		assertThat(navigator.getPreviousFragment()).isNull();

		// when
		navigator.navigateTo("view2/id=2");

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);
		assertThat(navigator.getCurrentViewName()).isEqualTo("view2");
		assertThat(navigator.getNavigationState()).isEqualTo("view2/id=2");

		assertThat(navigator.getPreviousView()).isEqualTo(view1);
		assertThat(navigator.getPreviousViewName()).isEqualTo("view1");
		assertThat(navigator.getPreviousFragment()).isEqualTo("view1/id=1");

		// when
		navigator.clearHistory();

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);
		assertThat(navigator.getCurrentViewName()).isEqualTo("view2");
		assertThat(navigator.getNavigationState()).isEqualTo("view2/id=2");

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
		when(uriHandler.setFragment("view2")).thenReturn(uriHandler);
		when(uriHandler.virtualPage()).thenReturn("view2");
		// need to return true, or first listener will block the second
		when(listener1.beforeViewChange(any(V7ViewChangeEvent.class))).thenReturn(true);
		navigator.addViewChangeListener(listener1);
		navigator.addViewChangeListener(listener2);
		navigator.addViewChangeListener(listener3);
		// when
		navigator.removeViewChangeListener(listener3);
		navigator.navigateTo("view2");
		// then
		verify(listener1, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener2, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener3, never()).beforeViewChange(any(V7ViewChangeEvent.class));
	}

	@Test
	public void listener_blocked() {

		// given
		when(uriHandler.setFragment("view2")).thenReturn(uriHandler);
		when(uriHandler.virtualPage()).thenReturn("view2");
		// to block second and subsequent
		when(listener1.beforeViewChange(any(V7ViewChangeEvent.class))).thenReturn(false);
		navigator.addViewChangeListener(listener1);
		navigator.addViewChangeListener(listener2);
		navigator.addViewChangeListener(listener3);
		// when
		navigator.navigateTo("view2");
		// then
		verify(listener1, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener2, never()).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener3, never()).beforeViewChange(any(V7ViewChangeEvent.class));
	}

}
