package functional;

import static org.fest.assertions.Assertions.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini.StandardPageKey;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.demo.view.DemoViewModule;
import uk.co.q3c.v7.demo.view.View1;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.TestUIModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, TestUIModule.class, DemoViewModule.class,
		V7ShiroVaadinModule.class, IniModule.class })
public class LoginTest extends UITestBase {

	String username = "wiggly";
	String password = "password";
	String badpassword = "passwrd";

	@BeforeClass
	public static void setupClass() {
		uiClass = BasicUI.class;
	}

	@Test
	public void loginSuccess_noPrevious() {

		// given
		navigatorPro.get().navigateTo(StandardPageKey.login);
		LoginView loginView = (LoginView) ui.getView();
		loginView.setUsername(username);
		loginView.setPassword(password);

		// simulate hitting straight from bookmark
		navigatorPro.get().clearHistory();
		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(navigatorPro.get().getNavigationState()).isEqualTo("secure/home");

	}

	@Test
	public void loginSuccess_withPreviousPublic() {

		// given
		navigatorPro.get().navigateTo(view1);
		navigatorPro.get().navigateTo(StandardPageKey.login);
		LoginView loginView = (LoginView) ui.getView();
		loginView.setUsername(username);
		loginView.setPassword(password);
		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(ui.getPage().getUriFragment()).isEqualTo(view1);
		assertThat(navigatorPro.get().getCurrentView()).isInstanceOf(View1.class);
		assertThat(navigatorPro.get().getNavigationState()).isEqualTo(view1);

	}

	@Test
	public void loginFailed() {

		// given

		// when
		navigatorPro.get().navigateTo(StandardPageKey.login);

		// then
		assertThat(ui.getView()).isInstanceOf(LoginView.class);

		// given
		LoginView loginView = (LoginView) ui.getView();
		loginView.setUsername(username);
		loginView.setPassword(badpassword);

		// when
		loginView.getSubmitButton().click();

		// then
		assertThat(loginView.getStatusMessage()).isEqualTo("That username or password was not recognised");

	}

	@Test
	public void exceedLogins() {

		// given
		navigatorPro.get().navigateTo(StandardPageKey.login);
		LoginView loginView = (LoginView) ui.getView();
		loginView.setUsername(username);
		loginView.setPassword(badpassword);
		// when
		loginView.getSubmitButton().click();
		loginView.getSubmitButton().click();
		loginView.getSubmitButton().click();

		// then
		assertThat(navigatorPro.get().getNavigationState()).isEqualTo("public/reset-account");

	}

}
