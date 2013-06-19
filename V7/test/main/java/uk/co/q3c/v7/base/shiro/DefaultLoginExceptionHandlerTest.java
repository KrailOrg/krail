package uk.co.q3c.v7.base.shiro;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.config.BaseIni;
import uk.co.q3c.v7.base.navigate.StandardPageKeys;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.LoginView;

import com.mycila.testing.junit.MycilaJunitRunner;

@RunWith(MycilaJunitRunner.class)
public class DefaultLoginExceptionHandlerTest {

	// @Inject
	DefaultLoginExceptionHandler handler;

	@Mock
	LoginView loginView;

	@Mock
	V7Navigator navigator;

	@Inject
	BaseIni ini;

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
		handler.accountLocked(loginView, token, null); //right?
		// then
		verify(navigator).navigateTo(StandardPageKeys.unlockAccount);

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
		verify(navigator).navigateTo(StandardPageKeys.enableAccount);
	}

	@Test
	public void excessiveAttempts() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.excessiveAttempts(loginView, token, null);
		// then
		verify(navigator).navigateTo(StandardPageKeys.resetAccount);
	}

	@Test
	public void expiredCredentials() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.expiredCredentials(loginView, token, null);
		// then
		verify(navigator).navigateTo(StandardPageKeys.refreshAccount);
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
