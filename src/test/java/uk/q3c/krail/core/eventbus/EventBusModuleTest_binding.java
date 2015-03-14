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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinSession;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.SyncMessageBus;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIScope;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScope;
import uk.q3c.krail.testutil.TestUIScopeModule;
import uk.q3c.krail.testutil.TestVaadinSessionScopeModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({EventBusModule.class, TestUIScopeModule.class, TestVaadinSessionScopeModule.class})
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

    @Inject
    @UIBus
    PubSubSupport<BusMessage> uiBus2;

    @Inject
    @SessionBus
    PubSubSupport<BusMessage> sessionBus2;

    @Inject
    @GlobalBus
    PubSubSupport<BusMessage> globalBus2;



    @Test
    public void name() {
        //given

        //when

        //then
        assertThat(uiBus).isInstanceOf(SyncMessageBus.class);
        assertThat(sessionBus).isInstanceOf(SyncMessageBus.class);
        assertThat(globalBus).isInstanceOf(MBassador.class);
    }

    @Test
    public void distinct() {
        //given

        //when

        //then
        assertThat((String) globalBus.getRuntime()
                                     .get(Properties.Common.Id)).isNotEqualTo(uiBus.getRuntime()
                                                                                   .get(Properties.Common.Id));
        assertThat((String) globalBus.getRuntime()
                                     .get(Properties.Common.Id)).isNotEqualTo(sessionBus.getRuntime()
                                                                                        .get(Properties.Common.Id));
        assertThat((String) sessionBus.getRuntime()
                                      .get(Properties.Common.Id)).isNotEqualTo(uiBus.getRuntime()
                                                                                    .get(Properties.Common.Id));

    }

    @Test
    public void instances() {
        //given

        //when

        //then
        assertThat(uiBus).isEqualTo(uiBus2);
        assertThat(sessionBus).isEqualTo(sessionBus2);
        assertThat(globalBus).isEqualTo(globalBus2);

    }

    @Test
    public void inUIScope() {
        //given

        //when
        UIScope scope = UIScope.getCurrent();
        ImmutableList<UIKey> keys = scope.scopeKeys();
        boolean uiInScope = scope.containsInstance(keys.get(0), uiBus);
        boolean sessionInScope = scope.containsInstance(keys.get(0), sessionBus);
        boolean globalInScope = scope.containsInstance(keys.get(0), globalBus);

        //then
        assertThat(uiInScope).isTrue();
        assertThat(sessionInScope).isFalse();
        assertThat(globalInScope).isFalse();
    }

    @Test
    public void inVaadinSessionScope() {
        //given

        //when
        VaadinSessionScope scope = VaadinSessionScope.getCurrent();
        ImmutableList<VaadinSession> keys = scope.scopeKeys();
        boolean uiInScope = scope.containsInstance(keys.get(0), uiBus);
        boolean sessionInScope = scope.containsInstance(keys.get(0), sessionBus);
        boolean globalInScope = scope.containsInstance(keys.get(0), globalBus);

        //then
        assertThat(uiInScope).isFalse();
        assertThat(sessionInScope).isTrue();
        assertThat(globalInScope).isFalse();
    }
}