/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.shiro;

import com.google.inject.Inject;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.subject.PrincipalCollection;
import uk.q3c.krail.base.navigate.sitemap.MasterSitemap;

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

    @Override
    public String getName() {
        return "Krail Default Realm";
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
