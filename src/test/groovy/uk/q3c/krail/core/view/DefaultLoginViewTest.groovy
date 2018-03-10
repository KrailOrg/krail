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

package uk.q3c.krail.core.view

import com.vaadin.ui.Button
import net.engio.mbassy.bus.common.PubSubSupport
import org.apache.shiro.authc.*
import org.apache.shiro.subject.Subject
import uk.q3c.krail.core.eventbus.SessionBusProvider
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.shiro.SubjectProvider
import uk.q3c.krail.core.view.component.LoginFormException
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.eventbus.BusMessage
/**
 * Created by David Sowerby on 09 Feb 2016
 */
class DefaultLoginViewTest extends ViewTest {

    DefaultLoginView thisView
    SubjectProvider subjectProvider = Mock()
    SessionBusProvider eventBusProvider = Mock()
    PubSubSupport<BusMessage> eventBus = Mock()
    Subject subject = Mock()
    ViewChangeBusMessage busMessage = Mock()
    Button.ClickEvent event = Mock()

    def setup() {
        eventBusProvider.get() >> eventBus
        subjectProvider.get() >> subject
//        thisView = new DefaultLoginView( subjectProvider, translate, eventBusProvider)
        thisView = new DefaultLoginView(subjectProvider, translate)
        view = thisView
        fieldsWithoutCaptions = ['label', 'statusMsgLabel']
        fieldsWIthoutIds = ['centrePanel']
    }

    def "getters and setters"() {
        given:
        thisView.buildView(null)
        thisView.setPassword('a')
        thisView.setUsername('b')
        thisView.setStatusMessage(LabelKey.Active_Source)

        expect:
        thisView.getPassword().getValue().equals('a')
        thisView.getUsername().getValue().equals('b')
        thisView.getStatusMessage().equals('Active Source')
        thisView.getSubmit() != null


        when:
        thisView.setStatusMessage("x")

        then:
        thisView.getStatusMessage().equals('x')
    }

    def "username empty"() {
        given:
        thisView.buildView(busMessage)

        when:
        thisView.buttonClick(event)

        then:
        LoginFormException ex = thrown()
        ex.msgKey == LabelKey.Username_Cannot_be_Empty
    }


    def "password empty"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')


        when:
        thisView.buttonClick(event)

        then:
        LoginFormException ex = thrown()
        ex.msgKey == LabelKey.Password_Cannot_be_Empty
    }


    def "unknown account"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')
        subject.login(_) >> { throw new UnknownAccountException() }

        when:
        thisView.buttonClick(event)

        then:
        1 * loginExceptionHandler.unknownAccount(view, _)
    }

    def "incorrect credentials"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')
        subject.login(_) >> { throw new IncorrectCredentialsException() }

        when:
        thisView.buttonClick(event)

        then:
        1 * loginExceptionHandler.incorrectCredentials(view, _)
    }

    def "expired credentials"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')
        subject.login(_) >> { throw new ExpiredCredentialsException() }

        when:
        thisView.buttonClick(event)

        then:
        1 * loginExceptionHandler.expiredCredentials(view, _)
    }

    def "locked account"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')
        subject.login(_) >> { throw new LockedAccountException() }

        when:
        thisView.buttonClick(event)

        then:
        1 * loginExceptionHandler.accountLocked(view, _)
    }

    def "excessive attempts"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')
        subject.login(_) >> { throw new ExcessiveAttemptsException() }

        when:
        thisView.buttonClick(event)

        then:
        1 * loginExceptionHandler.excessiveAttempts(view, _)
    }

    def "disabled account"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')
        subject.login(_) >> { throw new DisabledAccountException() }

        when:
        thisView.buttonClick(event)

        then:
        1 * loginExceptionHandler.disabledAccount(view, _)
    }

    def "concurrent access"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')
        subject.login(_) >> { throw new ConcurrentAccessException() }

        when:
        thisView.buttonClick(event)

        then:
        1 * loginExceptionHandler.concurrentAccess(view, _)
    }

    def "authentication"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')
        subject.login(_) >> { throw new AuthenticationException() }

        when:
        thisView.buttonClick(event)

        then:
        1 * loginExceptionHandler.authentication(view, _)
    }

    def "successful login"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')

        when:
        thisView.buttonClick(event)

        then:
        1 * eventBus.publish(_)
    }

    def "submit button click calls click method"() {
        given:
        thisView.buildView(busMessage)
        thisView.setUsername('ds')
        thisView.setPassword('password')

        when:
        thisView.getSubmit().click()

        then:
        1 * eventBus.publish(_)
    }
}
