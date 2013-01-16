package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.components.HeaderBar;

import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultV7NavigatorTest {
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
	HeaderBar headerBar;

	@Mock
	ScopedUI scopedUI;

	@Before
	public void setup() {
		navigator = new DefaultV7Navigator(errorViewPro, uriHandler, viewProMap, loginViewPro, logoutViewPro, headerBar);
		CurrentInstance.set(UI.class, scopedUI);
	}

	@Test
	public void navigateToLogout() {
		// given

		// when
		navigator.navigateToLogout();
		// then

		assertThat(navigator.getCurrentView()).isInstanceOf(LogoutView.class);

	}

}
