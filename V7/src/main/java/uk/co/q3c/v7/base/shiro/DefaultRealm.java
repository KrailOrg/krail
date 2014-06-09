package uk.co.q3c.v7.base.shiro;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.subject.PrincipalCollection;

import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemap;

import com.google.inject.Inject;

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

	/**
	 * This Realm implementation is not expected to be used in a real system, not least because anyone can log in as
	 * long as they have a password of 'password'! <br>
	 * <br>
	 * It does however demonstrate the use of {@link LoginAttemptLog} to track login attempts Authorises all users to
	 * access the private pages of the {@link MasterSitemap}
	 *
	 * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
	 */

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

	/**
	 * This Realm implementation is not expected to be used in a real system, not least because anyone can log in as
	 * long as they have a password of 'password'! <br>
	 * <br>
	 * This method would normally retrieve user permissions and /or roles from an underlying datastore of some form.
	 * There are various implementations already provided by Shiro, including {@link ActiveDirectoryRealm},
	 * {@link JdbcRealm} and {@link JndiLdapRealm}<br>
	 * <br>
	 * You can provide your own Realm implementation by overriding {@link StandardShiroModule#bindRealms()}<br>
	 * <br>
	 * Authorises all users to access the private pages of the {@link MasterSitemap} (that is, all the pages in the
	 * 'private' branch)
	 *
	 * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
	 */

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		// this very simplistic example gives permission for all pages which do get submitted for authorisation
		// and are in the 'private' branch
		String privatePermission = "page:view:private:*";
		info.addStringPermission(privatePermission);
		return info;
	}

	@Override
	public String getName() {
		return "V7 Default Realm";
	}

	/**
	 * This has been made public to enable testing
	 *
	 * @see org.apache.shiro.realm.AuthorizingRealm#getAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
	 */
	@Override
	public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
		return super.getAuthorizationInfo(principals);
	}
}
