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

package uk.q3c.krail.core.service

import com.google.inject.Inject
import net.engio.mbassy.bus.common.PubSubSupport
import spock.lang.Specification
import uk.q3c.krail.core.eventbus.GlobalBusProvider
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate

import static uk.q3c.krail.core.service.RelatedServicesExecutor.*
import static uk.q3c.krail.core.service.Service.*

/**
 * Created by David Sowerby on 08/11/15.
 *
 */
//@UseModules([])
class AbstractServiceTest extends Specification {

    def translate = Mock(Translate)

    TestService service

    def servicesModel = Mock(ServicesModel)
    GlobalBusProvider globalBusProvider = Mock(GlobalBusProvider)
    def eventBus = Mock(PubSubSupport)
    RelatedServicesExecutor servicesExecutor = Mock(RelatedServicesExecutor)

    def setup() {
        globalBusProvider.get() >> eventBus
        service = new TestService(translate, globalBusProvider, servicesExecutor)
        service.setThrowStartException(false)
        service.setThrowStopException(false)

    }

    def "name translated"() {
        given:
        translate.from(LabelKey.Authorisation) >> "Authorisation"
        expect:
        service.getNameKey().equals(LabelKey.Authorisation)
        service.getName().equals("Authorisation")
    }


    def "description key translated"() {
        given:
        translate.from(LabelKey.Authorisation) >> "Authorisation"
        service.setDescriptionKey(LabelKey.Authorisation)
        expect:
        service.getDescriptionKey().equals(LabelKey.Authorisation)
        service.getDescription().equals("Authorisation")
    }


    def "setDescriptionKey null is accepted"() {
        when:
        service.setDescriptionKey(null)

        then:

        service.getDescriptionKey().equals(null)
    }

    def "missing description key returns empty String"() {

        expect:
        service.getDescription().equals("")
    }

    def "start"(State initialState, Action action, boolean serviceFail, boolean allDepsOk, Cause callWithCause, State transientState, State finalState, Cause finalCause) {

        given:

        service.state = initialState
        service.throwStartException serviceFail

        when:

        ServiceStatus status = service.start(callWithCause)

        then:
        1 * eventBus.publish({ ServiceBusMessage m -> m.toState == transientState && m.cause == callWithCause })
        1 * servicesExecutor.execute(action, callWithCause) >> allDepsOk
        1 * eventBus.publish({ ServiceBusMessage m -> m.toState == finalState && m.cause == finalCause })
        service.getState() == finalState
        service.getCause() == finalCause
        status.state == finalState
        status.cause == finalCause
        status.service == service


        where:
        initialState  | action       | serviceFail | allDepsOk | callWithCause | transientState | finalState    | finalCause
//        State.INITIAL | Action.START | false       | true      | Cause.STARTED | State.STARTING | State.RUNNING | Cause.STARTED
//        State.STOPPED | Action.START | false       | true      | Cause.STARTED | State.STARTING | State.RUNNING | Cause.STARTED
        State.STOPPED | Action.START | true        | true      | Cause.STARTED | State.STARTING | State.FAILED  | Cause.FAILED_TO_START
//        State.INITIAL | Action.START | false       | false     | Cause.STARTED | State.STARTING | State.INITIAL | Cause.DEPENDENCY_FAILED
//        State.STOPPED | Action.START | false       | false     | Cause.STARTED | State.STARTING | State.STOPPED | Cause.DEPENDENCY_FAILED

    }

    def "stop"(State initialState, Action action, boolean serviceFail, boolean allDepsOk, Cause callWithCause, State transientState, State finalState, Cause finalCause) {

        given:

        service.state = initialState
        service.throwStopException serviceFail

        when:

        ServiceStatus status = service.stop(callWithCause)

        then:
        1 * eventBus.publish({ ServiceBusMessage m -> m.toState == transientState && m.cause == callWithCause })
        1 * servicesExecutor.execute(action, callWithCause) >> true
        1 * eventBus.publish({ ServiceBusMessage m -> m.toState == finalState && m.cause == finalCause })
        service.getState() == finalState
        service.getCause() == finalCause
        status.state == finalState
        status.cause == finalCause
        status.service == service


        where:
        initialState  | action      | serviceFail | allDepsOk | callWithCause            | transientState | finalState    | finalCause
        State.RUNNING | Action.STOP | false       | true      | Cause.STOPPED            | State.STOPPING | State.STOPPED | Cause.STOPPED
        State.RUNNING | Action.STOP | false       | true      | Cause.FAILED             | State.STOPPING | State.FAILED  | Cause.FAILED
        State.RUNNING | Action.STOP | false       | true      | Cause.DEPENDENCY_STOPPED | State.STOPPING | State.STOPPED | Cause.DEPENDENCY_STOPPED
        State.RUNNING | Action.STOP | false       | true      | Cause.DEPENDENCY_FAILED  | State.STOPPING | State.STOPPED | Cause.DEPENDENCY_FAILED
        State.RUNNING | Action.STOP | true        | true      | Cause.STOPPED            | State.STOPPING | State.FAILED  | Cause.FAILED_TO_STOP
        State.RUNNING | Action.STOP | true        | true      | Cause.FAILED             | State.STOPPING | State.FAILED  | Cause.FAILED
        State.RUNNING | Action.STOP | true        | true      | Cause.DEPENDENCY_STOPPED | State.STOPPING | State.FAILED  | Cause.FAILED_TO_STOP
        State.RUNNING | Action.STOP | true        | true      | Cause.DEPENDENCY_FAILED  | State.STOPPING | State.FAILED  | Cause.FAILED_TO_STOP
    }


    def "ignored start calls"(State initialState, Action action) {

        given:

        service.state = initialState
        Cause initialCause = service.getCause()

        when:

        ServiceStatus status = service.start()

        then:
        0 * servicesExecutor.execute(action, Cause.STARTED)
        service.getState() == initialState
        service.getCause() == initialCause
        status.state == initialState
        status.cause == initialCause
        status.service == service


        where:
        initialState   | action
        State.STARTING | Action.START
        State.RUNNING  | Action.START
    }

    def "disallowed start calls throw exception"(State initialState, Action action) {

        given:

        service.state = initialState
        Cause initialCause = service.getCause()

        when:

        ServiceStatus status = service.start()

        then:
        thrown(ServiceStatusException)


        where:
        initialState   | action
        State.STOPPING | Action.START
        State.FAILED   | Action.START
    }


    def "ignored stop calls"(State initialState, Action action, Cause callWithCause) {

        given:

        service.state = initialState
        Cause initialCause = service.getCause()

        when:

        ServiceStatus status = service.stop(callWithCause)

        then:
        0 * servicesExecutor.execute(action, callWithCause)
        service.getState() == initialState
        service.getCause() == initialCause
        status.state == initialState
        status.cause == initialCause
        status.service == service


        where:
        initialState    | action      | callWithCause
        State.STOPPED   | Action.STOP | Cause.STOPPED
        State.STOPPED   | Action.STOP | Cause.FAILED
        State.STOPPED   | Action.STOP | Cause.DEPENDENCY_STOPPED
        State.STOPPED   | Action.STOP | Cause.DEPENDENCY_FAILED
        State.FAILED    | Action.STOP | Cause.STOPPED
        State.FAILED    | Action.STOP | Cause.FAILED
        State.FAILED    | Action.STOP | Cause.DEPENDENCY_STOPPED
        State.FAILED    | Action.STOP | Cause.DEPENDENCY_FAILED
        State.STOPPING  | Action.STOP | Cause.STOPPED
        State.STOPPING  | Action.STOP | Cause.FAILED
        State.STOPPING  | Action.STOP | Cause.DEPENDENCY_STOPPED
        State.STOPPING  | Action.STOP | Cause.DEPENDENCY_FAILED
        State.RESETTING | Action.STOP | Cause.STOPPED
        State.RESETTING | Action.STOP | Cause.FAILED
        State.RESETTING | Action.STOP | Cause.DEPENDENCY_STOPPED
        State.RESETTING | Action.STOP | Cause.DEPENDENCY_FAILED
    }

    def "disallowed stop calls throw exception"() {
        //there are none
        expect: true
    }


    def "reset"(State initialState, Action action, boolean serviceFail, State transientState, State finalState, Cause finalCause) {

        given:

        service.state = initialState
        service.throwResetException serviceFail

        when:

        ServiceStatus status = service.reset()

        then:
        1 * eventBus.publish({ ServiceBusMessage m -> m.toState == transientState && m.cause == Cause.RESET })
        1 * eventBus.publish({ ServiceBusMessage m -> m.toState == finalState && m.cause == finalCause })
        service.getState() == finalState
        service.getCause() == finalCause
        status.state == finalState
        status.cause == finalCause
        status.service == service


        where:
        initialState  | action      | serviceFail | transientState  | finalState    | finalCause
        State.STOPPED | Action.STOP | false       | State.RESETTING | State.INITIAL | Cause.RESET
        State.FAILED  | Action.STOP | false       | State.RESETTING | State.INITIAL | Cause.RESET
        State.STOPPED | Action.STOP | true        | State.RESETTING | State.FAILED  | Cause.FAILED_TO_RESET
        State.FAILED  | Action.STOP | true        | State.RESETTING | State.FAILED  | Cause.FAILED_TO_RESET

    }

    def "ignored reset calls"(State initialState) {

        given:

        service.state = initialState
        Cause initialCause = service.getCause()

        when:

        ServiceStatus status = service.reset()

        then:
        service.getState() == initialState
        service.getCause() == initialCause
        status.state == initialState
        status.cause == initialCause
        status.service == service


        where:
        initialState    | _
        State.INITIAL   | _
        State.RESETTING | _
    }

    def "disallowed reset calls throw exception"(State initialState) {

        given:

        service.state = initialState
        Cause initialCause = service.getCause()

        when:

        ServiceStatus status = service.reset()

        then:
        thrown(ServiceStatusException)


        where:
        initialState   | _
        State.STOPPING | _
        State.RUNNING  | _
        State.STARTING | _
    }

    def "fail short call"() {
        given:

        service.state = State.RUNNING

        when:

        service.fail()

        then:

        1 * servicesExecutor.execute(Action.STOP, Cause.FAILED) >> true
    }

    def "dependencyFail"() {

        given:

        service.state = State.RUNNING

        when:

        service.dependencyFail()

        then:

        1 * servicesExecutor.execute(Action.STOP, Cause.DEPENDENCY_FAILED) >> true
    }

    def "dependencyStop"() {

        given:

        service.state = State.RUNNING

        when:

        service.dependencyStop()

        then:

        1 * servicesExecutor.execute(Action.STOP, Cause.DEPENDENCY_STOPPED) >> true

    }

    def "start short call"() {


        when:

        service.start()

        then:

        1 * servicesExecutor.execute(Action.START, Cause.STARTED) >> true

    }

    def "stop"() {

        given:

        service.state = State.RUNNING

        when:

        service.stop()

        then:

        1 * servicesExecutor.execute(Action.STOP, Cause.STOPPED) >> true

    }


    static class TestService extends AbstractService implements Service {

        boolean throwStartException = false
        boolean throwStopException = false
        boolean throwResetException = false

        @Inject
        protected TestService(Translate translate, GlobalBusProvider globalBusProvider, RelatedServicesExecutor servicesExecutor) {
            super(translate, globalBusProvider, servicesExecutor)
        }

        @Override
        void doStart() {
            if (throwStartException) {
                throw new RuntimeException("Test Exception thrown")
            }
        }

        @Override
        void doStop() {
            if (throwStopException) {
                throw new RuntimeException("Test Exception thrown")
            }
        }

        @Override
        void doReset() {
            if (throwResetException) {
                throw new RuntimeException("Test Exception thrown")
            }
        }

        @Override
        I18NKey getNameKey() {
            return LabelKey.Authorisation
        }

        void throwStartException(boolean throwStartException) {
            this.throwStartException = throwStartException
        }

        void throwStopException(boolean throwStopException) {
            this.throwStopException = throwStopException
        }

        void throwResetException(boolean throwResetException) {
            this.throwResetException = throwResetException
        }
    }


}