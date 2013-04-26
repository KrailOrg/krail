package uk.co.q3c.v7.demo.view;

import static org.fest.assertions.Assertions.*;

import org.apache.shiro.realm.Realm;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini.StandardPageKey;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.DefaultLoginExceptionHandler;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.demo.view.TestRealm.Response;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.TestUIModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, TestUIModule.class, StandardViewModule.class,
		DemoViewModule.class, V7ShiroVaadinModule.class, IniModule.class })
public class DefaultLoginViewTest extends UITestBase {

	private TestRealm testRealm;
	private LoginView loginView;

	@BeforeClass
	public static void setupClass() {
		uiClass = BasicUI.class;

	}

	@Override
	@Before
	public void setup() {
		super.setup();
		navigatorPro.get().navigateTo(StandardPageKey.login);
		loginView = (LoginView) ui.getView();
	}

	@Test
	public void authenticationFailed() {

		// given
		testRealm.setResponse(Response.authenticationFailed);

		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(navigatorPro.get().getNavigationState()).isEqualTo(
				ini.standardPageURI(StandardPageKey.enableAccount));

	}

	@Test
	public void excessiveAttempts() {

		// given
		testRealm.setResponse(Response.excessiveAttempts);

		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(navigatorPro.get().getNavigationState())
				.isEqualTo(ini.standardPageURI(StandardPageKey.resetAccount));

	}

	@Test
	public void lockedAccount() {

		// given
		testRealm.setResponse(Response.lockedAccount);

		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(navigatorPro.get().getNavigationState()).isEqualTo(
				ini.standardPageURI(StandardPageKey.unlockAccount));

	}

	@Test
	public void disabledAccount() {

		// given
		testRealm.setResponse(Response.disabledAccount);

		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(navigatorPro.get().getNavigationState()).isEqualTo(
				ini.standardPageURI(StandardPageKey.enableAccount));

	}

	@Test
	public void concurrentAccess() {

		// given
		testRealm.setResponse(Response.concurrentAccess);

		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(loginView.getStatusMessage()).isEqualTo(DefaultLoginExceptionHandler.concurrent);

	}

	@Test
	public void expiredCredentials() {

		// given
		testRealm.setResponse(Response.expiredCredentials);

		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(navigatorPro.get().getNavigationState()).isEqualTo(
				ini.standardPageURI(StandardPageKey.refreshAccount));

	}

	@Test
	public void unknownAccount() {

		// given
		testRealm.setResponse(Response.unknownAccount);

		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(loginView.getStatusMessage()).isEqualTo(DefaultLoginExceptionHandler.invalidLogin);

	}

	@Test
	public void incorrectCredentials() {

		// given
		testRealm.setResponse(Response.incorrectCredentials);

		// when
		loginView.getSubmitButton().click();
		// then
		assertThat(loginView.getStatusMessage()).isEqualTo(DefaultLoginExceptionHandler.invalidLogin);

	}

	@Override
	protected Realm getRealm() {
		this.testRealm = new TestRealm();
		return testRealm;
	}

}
