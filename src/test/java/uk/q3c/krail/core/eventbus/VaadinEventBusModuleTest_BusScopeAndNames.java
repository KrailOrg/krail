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

package uk.q3c.krail.core.eventbus;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.bus.config.IBusConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.eventbus.mbassador.EventBusModule;
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule;
import uk.q3c.util.testutil.LogMonitor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinEventBusModule.class, VaadinSessionScopeModule.class, TestUIScopeModule.class, EventBusModule.class})
public class VaadinEventBusModuleTest_BusScopeAndNames {

    @Inject
    MessageBus messageBus;


    @Inject
    @SessionBus
    Provider<PubSubSupport<BusMessage>> sessionBusProvider;

    @Inject
    @UIBus
    Provider<PubSubSupport<BusMessage>> uiBusProvider;

    @Inject
    LogMonitor logMonitor;

    @Mock
    VaadinService vaadinService;

    @Before
    public void setup() {
        // we have to inject providers so that the log monitor can be set up first
        logMonitor.addClassFilter(VaadinEventBusModule.class);
        logMonitor.addClassFilter(EventBusModule.class);
        VaadinSession.setCurrent(new VaadinSession(vaadinService)); //stops pollution of tests with scope holding keys between tests
    }

    @Test
    public void busIds() {
        //given
        String uiBusId = null;
        String sessionBusId = null;
        String globalBusId = "Global Message Bus";
        //when
        PubSubSupport<BusMessage> sessionBus = sessionBusProvider.get();
        PubSubSupport<BusMessage> uiBus = uiBusProvider.get();

        final List<String> logs = logMonitor.debugLogs();
        for (String log : logs) {
            if (log.startsWith("instantiated a UI Bus with id ")) {
                uiBusId = log.replace("instantiated a UI Bus with id ", "");
            }
            if (log.startsWith("instantiated a Session Bus with id ")) {
                sessionBusId = log.replace("instantiated a Session Bus with id ", "");
            }
            if (log.startsWith("instantiated a Global Bus with id ")) {
                globalBusId = log.replace("instantiated a Global Bus with id ", "");
            }
        }

        //then
        assertThat(messageBus.busId()).isEqualTo(globalBusId);
        assertThat((String) sessionBus.getRuntime()
                .get(IBusConfiguration.Properties.BusId)).isEqualTo(sessionBusId);
        assertThat((String) uiBus.getRuntime()
                .get(IBusConfiguration.Properties.BusId)).isEqualTo(uiBusId);


    }

    public void teardown() {
        logMonitor.close();
    }
}