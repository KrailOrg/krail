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

import com.vaadin.server.VaadinSession
import org.apache.shiro.session.Session
import org.apache.shiro.session.mgt.SessionContext
import org.apache.shiro.session.mgt.SessionKey
import org.apache.shiro.session.mgt.SimpleSession
import spock.lang.Specification

/**
 * Created by David Sowerby on 10 Feb 2016
 */
class VaadinSessionManagerTest extends Specification {

    VaadinSessionManager manager
    VaadinSessionProvider vaadinSessionProvider = Mock()
    VaadinSession vaadinSession = Mock()

    def setup() {
        manager = new VaadinSessionManager(vaadinSessionProvider)
        vaadinSessionProvider.get() >> vaadinSession
    }

    def "start() provides Shiro Session"() {
        given:
        SessionContext sessionContext = Mock()


        when:
        Session shiroSession = manager.start(sessionContext)

        then:
        shiroSession.getId() != null
        //can't figure out how to make this bit more specific
        1 * vaadinSession.setAttribute(_ as String, _ as Session)

    }

    def "get session, with session held by Vaadin session and is valid, returns it"() {
        given:
        SessionKey sessionKey = Mock()
        sessionKey.getSessionId() >> 'xxx'
        String attributeName = VaadinSessionManager.SESSION_ATTRIBUTE_PREFIX + sessionKey.getSessionId();
        Session session = new SimpleSession()
        vaadinSession.getAttribute(attributeName) >> session

        expect:
        manager.getSession(sessionKey) == session
    }

    def "getSession, vaadinSession is null, return null"() {
        given:
        SessionKey sessionKey = Mock()
        sessionKey.getSessionId() >> 'xxx'
        vaadinSession = null

        expect:
        manager.getSession(sessionKey) == null
    }

    def "getSession, vaadinSession does not have session, return null"() {
        given:
        SessionKey sessionKey = Mock()
        sessionKey.getSessionId() >> 'xxx'
        vaadinSession = null

        expect:
        manager.getSession(sessionKey) == null
    }

    def "get session, with session held by VaadinSession but is not valid, remove entry from VaadinSession"() {
        given:
        SessionKey sessionKey = Mock()
        sessionKey.getSessionId() >> 'xxx'
        String attributeName = VaadinSessionManager.SESSION_ATTRIBUTE_PREFIX + sessionKey.getSessionId();
        Session session = new SimpleSession()
        session.setExpired(true)
        vaadinSession.getAttribute(attributeName) >> session

        when:
        manager.getSession(sessionKey) == null

        then:
        1 * vaadinSession.setAttribute(_ as String, null)
    }
}
