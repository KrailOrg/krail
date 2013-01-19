package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.base.shiro.ShiroIntegrationTestBase;
import uk.co.q3c.v7.base.shiro.LoginStatusMonitor;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.V7View;

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

	@Mock
	StrictURIFragmentHandler uriHandler;

	@Mock
	Map<String, Provider<V7View>> viewProMap;

	@Mock
	Provider<V7View> loginViewPro;

	@Mock
	Provider<V7View> logoutViewPro;

	@Mock
	LoginStatusMonitor loginMonitor;

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

	@Inject
	Injector injector;

	@Mock
	Page page;

	@Mock
	ErrorView errorView;

	@Override
	@Before
	public void setup() {
		super.setup();
		when(loginViewPro.get()).thenReturn(loginView);
		when(logoutViewPro.get()).thenReturn(logoutView);
		when(viewProMap.get("view1")).thenReturn(view1Pro);
		when(viewProMap.get("view2")).thenReturn(view2Pro);
		when(viewProMap.get("login")).thenReturn(loginViewPro);
		when(viewProMap.get("logout")).thenReturn(logoutViewPro);
		when(view1Pro.get()).thenReturn(view1);
		when(view2Pro.get()).thenReturn(view2);
		when(loginViewPro.get()).thenReturn(loginView);
		when(logoutViewPro.get()).thenReturn(logoutView);

		when(scopedUI.getPage()).thenReturn(page);
		when(errorViewPro.get()).thenReturn(errorView);

		navigator = new DefaultV7Navigator(errorViewPro, uriHandler, viewProMap, loginMonitor);
		CurrentInstance.set(UI.class, scopedUI);
	}

	/**
	 * A call to update login status is made as the UI is constructed
	 */
	@Test
	public void construction() {

		// given

		// when

		// then
		verify(loginMonitor).updateStatus(SecurityUtils.getSubject());

	}

	/**
	 * A call to update login status is made as the UI is constructed. Logout event should be called (by the
	 * V7SecurityManager), so there is still only one
	 * {@link LoginStatusMonitor#updateStatus(org.apache.shiro.subject.Subject)} call.
	 */
	@Test
	public void logout() {
		// given
		when(uriHandler.setFragment("logout")).thenReturn(uriHandler);
		when(uriHandler.virtualPage()).thenReturn("logout");
		UsernamePasswordToken upt = new UsernamePasswordToken("xxx", "password");
		SecurityUtils.getSubject().login(upt);
		assertThat(SecurityUtils.getSubject().isAuthenticated()).isTrue();
		// when
		navigator.navigateTo("logout");
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(LogoutView.class);
		verify(scopedUI).changeView(null, logoutView);
		verify(loginMonitor, times(1)).updateStatus(SecurityUtils.getSubject());
		assertThat(SecurityUtils.getSubject().isAuthenticated()).isFalse();
	}

	/**
	 * A call to update login status is made as the UI is constructed, but no call made during login(). That happens in
	 * {@link #returnAfterLogin()}
	 */
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
		verify(loginMonitor, times(1)).updateStatus(SecurityUtils.getSubject());

	}

	@Test
	public void returnAfterLogin() {

		// given
		navigator.setCurrentView(loginView);
		navigator.setPreviousView(previousView);
		// when
		navigator.returnAfterLogin();
		// then
		verify(scopedUI).changeView(loginView, previousView);
		verify(loginMonitor, times(2)).updateStatus(SecurityUtils.getSubject());

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
	public void listeners() {

		// given

		// when
		// add
		// remove
		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void requestAccountReset() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void requestAccountRefresh() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void requestAccountUnlock() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void requestAccountEnable() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

}
