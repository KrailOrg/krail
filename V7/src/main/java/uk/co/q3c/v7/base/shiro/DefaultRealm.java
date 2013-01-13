package uk.co.q3c.v7.base.shiro;

import java.util.Set;

import javax.inject.Inject;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.google.common.collect.ImmutableSet;

public class DefaultRealm extends AuthorizingRealm {

	private final LoginAttemptLog loginAttemptLog;

	@Inject
	protected DefaultRealm(LoginAttemptLog loginAttemptLog, CredentialsMatcher matcher) {
		super(matcher);
		this.loginAttemptLog = loginAttemptLog;
		setCachingEnabled(false);
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String username = upToken.getUsername();

		if (username == null) {
			throw new AccountException("user name cannot be null");
		}

		String password = String.copyValueOf(upToken.getPassword());

		if (password.equals("password")) {
			loginAttemptLog.recordSuccessfulAttempt(upToken);
			return new SimpleAuthenticationInfo(username, password, this.getName());
		} else {
			loginAttemptLog.recordFailedAttempt(upToken);
			return null;
		}

	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		String username = (String) principals.fromRealm(getName()).iterator().next();
		Set roleNames = ImmutableSet.of();
		if (username != null) {
			roleNames = ImmutableSet.of("foo", "goo");
		}
		return new SimpleAuthorizationInfo(roleNames);
	}

	@Override
	public String getName() {
		return "V7 Default Realm";
	}
}
