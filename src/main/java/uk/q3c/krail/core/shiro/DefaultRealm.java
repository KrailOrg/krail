/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.shiro;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;

import javax.annotation.Nonnull;

public class DefaultRealm extends AuthorizingRealm {
    private static Logger log = LoggerFactory.getLogger(DefaultRealm.class);
    private final LoginAttemptLog loginAttemptLog;
    private SubjectIdentifier subjectIdentifier;

    @Inject
    protected DefaultRealm(LoginAttemptLog loginAttemptLog, CredentialsMatcher matcher, SubjectIdentifier subjectIdentifier) {
        super(matcher);
        this.loginAttemptLog = loginAttemptLog;
        this.subjectIdentifier = subjectIdentifier;
        setCachingEnabled(false);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     * This Realm implementation is not expected to be used in a real system, not least because anyone can log in as
     * long as they have a password of 'password'! AND it exposes user names and password in the debug log <br>
     * <br>
     * It does however demonstrate the use of {@link LoginAttemptLog} to track login attempts Authorises all users to
     * access the private pages of the {@link MasterSitemap}
     *
     * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(@Nonnull AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = String.copyValueOf(upToken.getPassword());
        log.debug("Username {}, password: {}", username, password);
        if (password.equals("password") && (!(username == null) && (!username.isEmpty()))) {
            log.debug("login succeeds");
            loginAttemptLog.recordSuccessfulAttempt(upToken);
            return new SimpleAuthenticationInfo(username, password, this.getName());
        } else {
            log.debug("login fails");
            if (username != null) {
                loginAttemptLog.recordFailedAttempt(upToken);
            }
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
     * This implementation authorises<ol>
     * <li>all authenticated users to access the pages of the 'private' or 'system-admin' branches of the {@link MasterSitemap}</li>
     * <li>users to edit their own options in the SimpleUserHierarchy</li>
     * </ol>
     *
     * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // used by PageController - authorisation to see pages in 'private' branch
        String privatePermission = "page:view:private:*";
        info.addStringPermission(privatePermission);
        // used by PageController - authorisation to see pages in 'system-admin' branch
        String sysAdminPermission = "page:view:system-admin:*";
        info.addStringPermission(sysAdminPermission);
        // used by Option to enable users to edit their own options in SimpleUserHierarchy
        String userId = subjectIdentifier.userId();
        String editOwnOptionsPermission = "option:edit:SimpleUserHierarchy:" + userId + ":0:*:*";
        info.addStringPermission(editOwnOptionsPermission);

        // admin can set any options in SimpleUserHierarchy
        if (userId.toLowerCase()
                  .equals("admin")) {
            String editAnyOption = "option:edit:SimpleUserHierarchy:*:*:*:*";
            info.addStringPermission(editAnyOption);
        }
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
