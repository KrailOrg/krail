package uk.co.q3c.base.view;

import static org.fest.assertions.Assertions.*;

import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.ui.V7UIModule;
import uk.co.q3c.v7.base.view.DefaultLogoutView;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.demo.view.DemoViewModule;
import uk.co.q3c.v7.demo.view.View2;

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
	public void loginLogout() {

		// given
		navigator.navigateTo(view2);
		// when

		// then
		assertThat(headerBar.getLoginBtn().getCaption()).isEqualTo("log in");
		assertThat(headerBar.getUserLabel().getValue()).isEqualTo("guest");

		// when
		headerBar.getLoginBtn().click();
		// then
		assertThat(ui.getView()).isInstanceOf(LoginView.class);

		// given
		LoginView loginView = (LoginView) ui.getView();

		// when

		// then
		assertThat(loginView.getStatusMessage()).isEqualTo("Please enter your username and password");

		// when user logs in
		loginView.setUsername(username);
		loginView.setPassword(password);
		loginView.getSubmitButton().click();
		// then
		assertThat(SecurityUtils.getSubject().isAuthenticated()).isTrue();
		// loginView reverts to view2
		assertThat(ui.getView()).isInstanceOf(View2.class);
		// name appears in header
		assertThat(headerBar.getUserLabel().getValue()).isEqualTo(username);
		// login button caption changes
		assertThat(headerBar.getLoginBtn().getCaption()).isEqualTo("log out");

		// when user logs out
		headerBar.getLoginBtn().click();
		// then
		// view goes to logout view
		assertThat(ui.getView()).isInstanceOf(DefaultLogoutView.class);
		// username goes to guest
		assertThat(headerBar.getUserLabel().getValue()).isEqualTo("guest");
		// button caption to login
		assertThat(headerBar.getLoginBtn().getCaption()).isEqualTo("log in");
	}

	@Test
	public void loginFailed() {

		// given
		headerBar.getLoginBtn().click();
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
		headerBar.getLoginBtn().click();
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
