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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import net.engio.mbassy.bus.SyncMessageBus;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.bus.config.IBusConfiguration;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIScope;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScope;
import uk.q3c.krail.core.ui.BasicUI;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.eventbus.mbassador.EventBusModule;
import uk.q3c.krail.eventbus.mbassador.MBassadorMessageBus;
import uk.q3c.krail.testutil.guice.vsscope.TestVaadinSessionScopeModule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinEventBusModule.class, UIScopeModule.class, EventBusModule.class, TestVaadinSessionScopeModule.class})
public class VaadinEventBusModuleTest_binding {


    PubSubSupport<BusMessage> uiBus;
    PubSubSupport<BusMessage> uiBus2;

    @Inject
    @UIBus
    Provider<PubSubSupport<BusMessage>> uiBusProvider;


    @Inject
    @SessionBus
    PubSubSupport<BusMessage> sessionBus;

    @Inject
    MessageBus messageBus;


    @Inject
    @SessionBus
    PubSubSupport<BusMessage> sessionBus2;

    @Inject
    MessageBus messageBus2;


    @Before
    public void setup() {
        setUIScope(1);
        uiBus = uiBusProvider.get();
        uiBus2 = uiBusProvider.get();
    }

    @AfterClass
    public static void teardownClass() {
        VaadinSession.setCurrent(null);
    }

    private void setUIScope(int index) {
        UI.setCurrent(null); // overcomes the inheritable check in CurrentInstance.set
        //flush last test if there was one
        UIScope oldScope = UIScope.getCurrent();
        if (oldScope != null) {
            oldScope.flush();
        }
        UIKey uiKey = new UIKey(index);
        BasicUI ui = mock(BasicUI.class);
        when(ui.getInstanceKey()).thenReturn(uiKey);
        UI.setCurrent(ui);
        CurrentInstance.set(UIKey.class, uiKey);
    }

    @Test
    public void name() {
        //given

        //when

        //then
        assertThat(uiBus).isInstanceOf(SyncMessageBus.class);
        assertThat(sessionBus).isInstanceOf(SyncMessageBus.class);
        assertThat(messageBus).isInstanceOf(MBassadorMessageBus.class);
    }

    @Test
    public void distinct() {
        //given

        //when

        //then
        assertThat(messageBus.busId()).isNotEqualTo(uiBus.getRuntime().get(IBusConfiguration.Properties.BusId));
        assertThat(messageBus.busId()).isNotEqualTo(sessionBus.getRuntime().get(IBusConfiguration.Properties.BusId));
        assertThat((String) sessionBus.getRuntime()
                .get(IBusConfiguration.Properties.BusId)).isNotEqualTo(uiBus.getRuntime()
                .get(IBusConfiguration.Properties.BusId));

    }

    @Test
    public void instances() {
        //given

        //when

        //then
        assertThat(uiBus).isEqualTo(uiBus2);
        assertThat(sessionBus).isEqualTo(sessionBus2);
        assertThat(messageBus).isEqualTo(messageBus2);

    }

    @Test
    public void inUIScope() {
        //given

        //when
        UIScope scope = UIScope.getCurrent();
        ImmutableList<UIKey> keys = scope.scopeKeys();
        boolean uiInScope = scope.containsInstance(keys.get(0), uiBus);
        boolean sessionInScope = scope.containsInstance(keys.get(0), sessionBus);
        boolean globalInScope = scope.containsInstance(keys.get(0), messageBus);

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
        boolean globalInScope = scope.containsInstance(keys.get(0), messageBus);

        //then
        assertThat(uiInScope).isFalse();
        assertThat(sessionInScope).isTrue();
        assertThat(globalInScope).isFalse();
    }

    @Test
    public void appIds_UI() {
        //given

        //when
        Object busScope1 = uiBus.getRuntime()
                .get(VaadinEventBusModule.BUS_SCOPE);
        Object busScope2 = uiBus2.getRuntime()
                .get(VaadinEventBusModule.BUS_SCOPE);
        int busIndex1 = uiBus.getRuntime()
                .get(VaadinEventBusModule.BUS_INDEX);
        int busIndex2 = uiBus2.getRuntime()
                .get(VaadinEventBusModule.BUS_INDEX);
        //then

        assertThat(busScope1).isEqualTo("ui");
        assertThat(busScope2).isEqualTo("ui");
        assertThat(busIndex1).isEqualTo(1);
        assertThat(busIndex2).isEqualTo(1);

        //when new scope
        setUIScope(2);
        uiBus = uiBusProvider.get();
        uiBus2 = uiBusProvider.get();

        busScope1 = uiBus.getRuntime()
                .get(VaadinEventBusModule.BUS_SCOPE);
        busScope2 = uiBus2.getRuntime()
                .get(VaadinEventBusModule.BUS_SCOPE);
        busIndex1 = uiBus.getRuntime()
                .get(VaadinEventBusModule.BUS_INDEX);
        busIndex2 = uiBus2.getRuntime()
                .get(VaadinEventBusModule.BUS_INDEX);


        //then

        assertThat(busScope1).isEqualTo("ui");
        assertThat(busScope2).isEqualTo("ui");
        assertThat(busIndex1).isEqualTo(2);
        assertThat(busIndex2).isEqualTo(2);

    }

    @Test
    public void appIds_session() {
        //given

        //when
        Object busScope1 = sessionBus.getRuntime()
                .get(VaadinEventBusModule.BUS_SCOPE);
        Object busScope2 = sessionBus2.getRuntime()
                .get(VaadinEventBusModule.BUS_SCOPE);
        int busIndex1 = sessionBus.getRuntime()
                .get(VaadinEventBusModule.BUS_INDEX);
        int busIndex2 = sessionBus2.getRuntime()
                .get(VaadinEventBusModule.BUS_INDEX);
        //then

        assertThat(busScope1).isEqualTo("session");
        assertThat(busScope2).isEqualTo("session");
        assertThat(busIndex1).isEqualTo(1);
        assertThat(busIndex2).isEqualTo(1);

        //when new scope
    }

    @Test
    public void appIds_global() {
        //given

        //when
        Object busScope1 = messageBus.scope();
        Object busScope2 = messageBus2.scope();
        int busIndex1 = messageBus.index();
        int busIndex2 = messageBus2.index();
        //then

        assertThat(busScope1).isEqualTo("Singleton");
        assertThat(busScope2).isEqualTo("Singleton");
        assertThat(busIndex1).isEqualTo(1);
        assertThat(busIndex2).isEqualTo(1);


    }
}