package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.base.shiro.ShiroIntegrationTestBase;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.user.LoginStatusMonitor;

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
	Provider<LoginView> loginViewPro;

	@Mock
	Provider<LogoutView> logoutViewPro;

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
	Provider<V7View> view1Provider;

	@Mock
	Provider<V7View> view2Provider;

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
		when(loginViewPro.get()).thenReturn(loginView);
		when(logoutViewPro.get()).thenReturn(logoutView);
		when(viewProMap.get("view1")).thenReturn(view1Provider);
		when(viewProMap.get("view2")).thenReturn(view2Provider);
		when(view1Provider.get()).thenReturn(view1);
		when(view2Provider.get()).thenReturn(view2);
		when(scopedUI.getPage()).thenReturn(page);
		when(errorViewPro.get()).thenReturn(errorView);

		navigator = new DefaultV7Navigator(errorViewPro, uriHandler, viewProMap, loginViewPro, logoutViewPro,
				loginMonitor);
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
	 * A call to update login status is made as the UI is constructed, so 2 calls to the monitor in total
	 */
	@Test
	public void logout() {
		// given

		// when
		navigator.logout();
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(LogoutView.class);
		verify(scopedUI).changeView(null, logoutView);
		verify(loginMonitor, times(2)).updateStatus(SecurityUtils.getSubject());
	}

	/**
	 * A call to update login status is made as the UI is constructed, but no call made during login(). That happens in
	 * {@link #returnAfterLogin()}
	 */
	@Test
	public void login() {
		// given

		// when
		navigator.login();
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
