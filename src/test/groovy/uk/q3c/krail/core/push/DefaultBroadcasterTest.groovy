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

package uk.q3c.krail.core.push

import com.vaadin.ui.Component
import spock.lang.Specification
import uk.q3c.krail.config.ApplicationConfiguration
import uk.q3c.krail.core.guice.uiscope.UIKey
import uk.q3c.krail.core.ui.ScopedUI
import uk.q3c.util.testutil.LogMonitor

import static org.assertj.core.api.Assertions.*

/**
 * Created by David Sowerby on 19 Jan 2016
 */
class DefaultBroadcasterTest extends Specification {

    DefaultBroadcaster broadcaster
    ApplicationConfiguration applicationConfiguration = Mock()
    Broadcaster.BroadcastListener listener1 = Mock()
    Broadcaster.BroadcastListener listener2 = Mock()
    LogMonitor logMonitor

    def setup() {
        broadcaster = new DefaultBroadcaster(applicationConfiguration)
        logMonitor = new LogMonitor()
        logMonitor.addClassFilter(DefaultBroadcaster)
    }

    def cleanup() {
        logMonitor.close()
    }

    def "register, unregister and getGroupListener return valid results for ALL_MESSAGES, used and unused groups"() {

        when:
        broadcaster.register("a", listener1)
        broadcaster.register(Broadcaster.ALL_MESSAGES, listener2)

        then:
        assertThat(broadcaster.getListenerGroup("a")).containsOnly(listener1)
        assertThat(broadcaster.getListenerGroup(Broadcaster.ALL_MESSAGES)).containsOnly(listener2)
        broadcaster.getListenerGroup("b").isEmpty()

        when:
        broadcaster.unregister("b", listener1)
        broadcaster.unregister("a", listener2)

        //nothing changed, calls would have been ignored
        then:
        assertThat(broadcaster.getListenerGroup("a")).containsOnly(listener1)
        assertThat(broadcaster.getListenerGroup(Broadcaster.ALL_MESSAGES)).containsOnly(listener2)
        broadcaster.getListenerGroup("b").isEmpty()

        when:
        broadcaster.unregister("a", listener1)

        then:
        broadcaster.getListenerGroup("a").isEmpty()

        when:
        broadcaster.unregister(Broadcaster.ALL_MESSAGES, listener1)

        then:
        assertThat(broadcaster.getListenerGroup(Broadcaster.ALL_MESSAGES)).containsOnly(listener2)

        when:
        broadcaster.unregister(Broadcaster.ALL_MESSAGES, listener2)

        then:
        broadcaster.getListenerGroup(Broadcaster.ALL_MESSAGES).isEmpty()

    }

    def "broadcast, push enabled, messages received, using UiKey for sender"() {
        given:
        UIKey uiKey = new UIKey()
        broadcaster.register("a", listener1)
        broadcaster.register(Broadcaster.ALL_MESSAGES, listener2)

        when:
        broadcaster.broadcast("a", "msg", uiKey)

        then:
        1 * applicationConfiguration.getPropertyValue(PushModuleKt.SERVER_PUSH_ENABLED, true) >> true
        1 * listener1.receiveBroadcast("a", "msg", uiKey, 1)
        1 * listener2.receiveBroadcast("a", "msg", uiKey, 1)
        logMonitor.debugLogs().contains("broadcasting message: 1 from: " + uiKey)

    }

    def "broadcast, push not enabled, messages not received, using UiKey for sender"() {
        given:
        UIKey uiKey = new UIKey(UUID.randomUUID())
        broadcaster.register("a", listener1)
        broadcaster.register(Broadcaster.ALL_MESSAGES, listener2)

        when:
        broadcaster.broadcast("a", "msg", uiKey)

        then:
        1 * applicationConfiguration.getPropertyValue(PushModuleKt.SERVER_PUSH_ENABLED, true) >> false
        0 * listener1.receiveBroadcast(_, _, _, _)
        0 * listener2.receiveBroadcast(_, _, _, _)
        !logMonitor.debugLogs().contains('broadcasting message: 1 from: UIKey:55')
        logMonitor.debugLogs().contains('server push is disabled, message not broadcast')
    }

    def "broadcast, push enabled, messages received, using component for sender"() {
        given:
        UIKey uiKey = new UIKey()
        ScopedUI ui = Mock()
        ui.getInstanceKey() >> uiKey
        Component component = Mock()
        component.getUI() >> ui
        broadcaster.register("a", listener1)
        broadcaster.register(Broadcaster.ALL_MESSAGES, listener2)

        when:
        broadcaster.broadcast("a", "msg", component)

        then:
        1 * applicationConfiguration.getPropertyValue(PushModuleKt.SERVER_PUSH_ENABLED, true) >> true
        1 * listener1.receiveBroadcast("a", "msg", uiKey, 1)
        1 * listener2.receiveBroadcast("a", "msg", uiKey, 1)
        logMonitor.debugLogs().contains("broadcasting message: 1 from: " + uiKey)

    }

    def "broadcast, push not enabled, messages not received, using component for sender"() {
        given:
        UIKey uiKey = new UIKey()
        ScopedUI ui = Mock()
        ui.getInstanceKey() >> uiKey
        Component component = Mock()
        component.getUI() >> ui
        broadcaster.register("a", listener1)
        broadcaster.register(Broadcaster.ALL_MESSAGES, listener2)

        when:
        broadcaster.broadcast("a", "msg", uiKey)

        then:
        1 * applicationConfiguration.getPropertyValue(PushModuleKt.SERVER_PUSH_ENABLED, true) >> false
        0 * listener1.receiveBroadcast(_, _, _, _)
        0 * listener2.receiveBroadcast(_, _, _, _)
        !logMonitor.debugLogs().contains('broadcasting message: 1 from: UIKey:55')
        logMonitor.debugLogs().contains('server push is disabled, message not broadcast')
    }

}
