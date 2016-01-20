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
import org.apache.shiro.authc.HostAuthenticationToken
import org.apache.shiro.authc.UsernamePasswordToken
import spock.lang.Specification

/**
 * Created by David Sowerby on 19 Jan 2016
 */
class AlwaysPasswordCredentialsMatcherTest extends Specification {

    AlwaysPasswordCredentialsMatcher matcher
    UsernamePasswordToken token = Mock()
    AuthenticationInfo info = Mock()
    HostAuthenticationToken otherToken = Mock()

    def setup() {
        matcher = new AlwaysPasswordCredentialsMatcher()
    }

    def "password valid"() {

        when:
        boolean result = matcher.doCredentialsMatch(token, info)

        then:
        token.getPassword() >> "password"
        result
    }

    def "password not valid"() {
        when:
        boolean result = matcher.doCredentialsMatch(token, info)

        then:
        token.getPassword() >> "pasword"
        !result
    }

    def "not username password token"() {
        when:
        boolean result = matcher.doCredentialsMatch(otherToken, info)

        then:
        !result
    }
}
