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

package uk.q3c.krail.core.eventbus;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.SyncMessageBus;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.testutil.TestUIScopeModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({EventBusModule.class, TestUIScopeModule.class, VaadinSessionScopeModule.class})
public class EventBusModuleTest_binding {

    @Inject
    @UIBus
    PubSubSupport<BusMessage> uiBus;

    @Inject
    @SessionBus
    PubSubSupport<BusMessage> sessionBus;

    @Inject
    @GlobalBus
    PubSubSupport<BusMessage> globalBus;

    @Test
    public void name() {
        //given

        //when

        //then
        assertThat(uiBus).isInstanceOf(SyncMessageBus.class);
        assertThat(sessionBus).isInstanceOf(SyncMessageBus.class);
        assertThat(globalBus).isInstanceOf(MBassador.class);
    }
}