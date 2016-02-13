/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.shiro

import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.ExcessiveAttemptsException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authc.credential.CredentialsMatcher
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.cache.CacheManager
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection
import spock.lang.Specification

/**
 * Created by David Sowerby on 02 Jan 2016
 */
class DefaultRealmTest extends Specification {

    String onlyValidPassword = "password";

    DefaultRealm realm;

    LoginAttemptLog attemptLog = new DefaultLoginAttemptLog();

    CredentialsMatcher matcher = new AlwaysPasswordCredentialsMatcher();

    Optional<CacheManager> cacheManagerOpt = Optional.empty();

    CacheManager cacheManager = Mock(CacheManager);
    SubjectIdentifier subjectIdentifer = Mock(SubjectIdentifier);

    def setup() {
        realm = new DefaultRealm(attemptLog, matcher, subjectIdentifer, cacheManagerOpt);
    }

    def "realmName and default cache settings"() {
        expect:
        realm.getName().equals("Krail Default Realm")
        !realm.isCachingEnabled()
        realm.getCacheManager() == null
    }

    def "authenticate with valid password"() {
        when:
        AuthenticationInfo info = realm.getAuthenticationInfo(token("fred", onlyValidPassword));

        then:
        info != null

    }

    def "authenticate with invalid password"() {

        given:
        attemptLog.resetAttemptCount("fred");

        when:
        AuthenticationInfo info = realm.getAuthenticationInfo(token("fred", "rubbish"));

        then:
        info == null

    }

    def "exceeding max attempts at log in throws exception"() {
        given:
        attemptLog.setMaximumAttempts(3);

        when:

        realm.getAuthenticationInfo(token("fred", "rubbish"));
        realm.getAuthenticationInfo(token("fred", "rubbish"));
        realm.getAuthenticationInfo(token("fred", "rubbish"));

        then:
        thrown(ExcessiveAttemptsException)
    }

    def "null user name fails authentication"() {
        given:
        UsernamePasswordToken tk = token(null, "rubbish");

        when:
        AuthenticationInfo info = realm.getAuthenticationInfo(tk);

        then:
        info == null
    }

    def "authenticate user who has been given permission"() {
        given:
        subjectIdentifer.userId() >> "ds"
        PrincipalCollection pc = new SimplePrincipalCollection();

        when:
        AuthorizationInfo info = realm.getAuthorizationInfo(pc);

        then:
        info != null
        info.getStringPermissions().contains("page:view:private:*")
    }

    def "cache provided"() {
        given:
        cacheManagerOpt = Optional.of(cacheManager);

        when:
        realm = new DefaultRealm(attemptLog, matcher, subjectIdentifer, cacheManagerOpt);

        then:
        realm.isCachingEnabled()
        realm.getCacheManager().equals(cacheManager);
    }

    private UsernamePasswordToken token(String username, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        return token;
    }
}
