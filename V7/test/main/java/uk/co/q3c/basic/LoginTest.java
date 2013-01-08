package uk.co.q3c.basic;

import static org.fest.assertions.Assertions.*;

import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroModule;
import uk.co.q3c.v7.base.ui.UIModule;
import uk.co.q3c.v7.base.view.DemoLogoutView;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.demo.view.DemoViewModule;
import uk.co.q3c.v7.demo.view.View2;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, UIModule.class, TestModule.class, TestShiroModule.class,
		DemoViewModule.class, V7ShiroModule.class })
public class LoginTest extends UITestBase {

	String username = "wiggly";
	String password = "password";

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
		loginView.setUsername(username);
		loginView.setPassword(password);
		// when user logs in
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
		assertThat(ui.getView()).isInstanceOf(DemoLogoutView.class);
		// username goes to guest
		assertThat(headerBar.getUserLabel().getValue()).isEqualTo("guest");
		// button caption to login
		assertThat(headerBar.getLoginBtn().getCaption()).isEqualTo("log in");
	}
}
