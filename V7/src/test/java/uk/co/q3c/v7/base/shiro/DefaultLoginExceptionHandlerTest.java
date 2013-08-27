package uk.co.q3c.v7.base.shiro;

import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.LoginView;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.TestIniModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestIniModule.class })
public class DefaultLoginExceptionHandlerTest {

	// @Inject
	DefaultLoginExceptionHandler handler;

	@Mock
	LoginView loginView;

	@Mock
	V7Navigator navigator;

	@Inject
	V7Ini ini;

	UsernamePasswordToken token;

	@Before
	public void setup() {
		handler = new DefaultLoginExceptionHandler(navigator);
	}

	@Test
	public void accountLocked() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.accountLocked(loginView, token, null);
		// then
		verify(navigator).navigateTo(StandardPageKey.Unlock_Account);

	}

	@Test
	public void unknownAccount() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.unknownAccount(loginView, token, null);
		// then
		verify(loginView).setStatusMessage(DefaultLoginExceptionHandler.INVALID_LOGIN);

	}

	@Test
	public void concurrentAccess() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.concurrentAccess(loginView, token, null);
		// then
		verify(loginView).setStatusMessage(DefaultLoginExceptionHandler.CONCURRENT);

	}

	@Test
	public void disabledAccount() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.disabledAccount(loginView, token, null);
		// then
		verify(navigator).navigateTo(StandardPageKey.Enable_Account);
	}

	@Test
	public void excessiveAttempts() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.excessiveAttempts(loginView, token, null);
		// then
		verify(navigator).navigateTo(StandardPageKey.Reset_Account);
	}

	@Test
	public void expiredCredentials() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.expiredCredentials(loginView, token, null);
		// then
		verify(navigator).navigateTo(StandardPageKey.Refresh_Account);
	}

	@Test
	public void incorrectCredentials() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.incorrectCredentials(loginView, token, null);
		// then
		verify(loginView).setStatusMessage(DefaultLoginExceptionHandler.INVALID_LOGIN);
	}
}
