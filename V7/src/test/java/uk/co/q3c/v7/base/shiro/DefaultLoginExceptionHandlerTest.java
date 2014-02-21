package uk.co.q3c.v7.base.shiro;

import static org.mockito.Mockito.*;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.i18n.DescriptionKey;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultLoginExceptionHandlerTest {

	// @Inject
	DefaultLoginExceptionHandler handler;

	@Mock
	LoginView loginView;

	@Mock
	V7Navigator navigator;

	UsernamePasswordToken token;

	@Before
	public void setup() {
		handler = new DefaultLoginExceptionHandler();
	}

	@Test
	public void accountLocked() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.accountLocked(loginView, token);
		// then
		verify(loginView).setStatusMessage(DescriptionKey.Account_Locked);

	}

	@Test
	public void unknownAccount() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.unknownAccount(loginView, token);
		// then
		verify(loginView).setStatusMessage(DescriptionKey.Unknown_Account);

	}

	@Test
	public void concurrentAccess() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.concurrentAccess(loginView, token);
		// then
		verify(loginView).setStatusMessage(DescriptionKey.Account_Already_In_Use);

	}

	@Test
	public void disabledAccount() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.disabledAccount(loginView, token);
		// then
		verify(loginView).setStatusMessage(DescriptionKey.Account_is_Disabled);
	}

	@Test
	public void excessiveAttempts() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.excessiveAttempts(loginView, token);
		// then
		verify(loginView).setStatusMessage(DescriptionKey.Too_Many_Login_Attempts);
	}

	@Test
	public void expiredCredentials() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.expiredCredentials(loginView, token);
		// then
		verify(loginView).setStatusMessage(DescriptionKey.Account_Expired);
	}

	@Test
	public void incorrectCredentials() {
		// given
		token = new UsernamePasswordToken("fred", "password");
		// when
		handler.incorrectCredentials(loginView, token);
		// then
		verify(loginView).setStatusMessage(DescriptionKey.Invalid_Login);
	}
}
