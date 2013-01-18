package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.inject.Inject;

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
	LoginView mockLoginView;

	@Mock
	LogoutView mockLogoutView;

	@Inject
	Injector injector;

	@Override
	@Before
	public void setup() {
		when(loginViewPro.get()).thenReturn(mockLoginView);
		when(logoutViewPro.get()).thenReturn(mockLogoutView);
		navigator = new DefaultV7Navigator(errorViewPro, uriHandler, viewProMap, loginViewPro, logoutViewPro,
				loginMonitor);
		CurrentInstance.set(UI.class, scopedUI);
	}

	@Test
	public void logout() {
		// given

		// when
		navigator.logout();
		// then

		assertThat(navigator.getCurrentView()).isInstanceOf(LogoutView.class);

	}

	@Test
	public void login() {
		// given

		// when
		navigator.login();
		// then

		assertThat(navigator.getCurrentView()).isInstanceOf(LoginView.class);

	}

}
