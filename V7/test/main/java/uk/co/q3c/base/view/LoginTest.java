package uk.co.q3c.base.view;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.ui.V7UIModule;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, V7UIModule.class, DemoViewModule.class,
		V7ShiroVaadinModule.class })
public class LoginTest extends UITestBase {

	String username = "wiggly";
	String password = "password";
	String badpassword = "passwrd";

	@Test
	public void loginSuccess() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void loginFailed() {

		// given
		LoginView loginView = (LoginView) ui.getView();
		loginView.setUsername(username);
		loginView.setPassword(badpassword);
		// when
		loginView.getSubmitButton().click();

		// then
		assertThat(loginView.getStatusMessage()).isEqualTo("Invalid username or password");

	}

	@Test
	public void exceedLogins() {

		// given
		navigatorPro.get().login();
		LoginView loginView = (LoginView) ui.getView();
		loginView.setUsername(username);
		loginView.setPassword(badpassword);
		// when
		loginView.getSubmitButton().click();
		loginView.getSubmitButton().click();
		loginView.getSubmitButton().click();

		// then
		assertThat(false).isEqualTo(true);

	}

}
