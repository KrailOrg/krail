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
package uk.q3c.krail.core.push;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.UIBusProvider;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.eventbus.BusMessage;

import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultPushMessageRouterTest {

    DefaultPushMessageRouter pmr;
    @Mock
    UIBusProvider uiBusProvider;
    @Mock
    private PubSubSupport<BusMessage> uiBus;

    @Before
    public void setup() {
        when(uiBusProvider.get()).thenReturn(uiBus);
        pmr = new DefaultPushMessageRouter(uiBusProvider);
    }


    @Test
    public void messageIn_simple_with_identifiers() {
        // given
        UIKey uiKey = new UIKey(UUID.randomUUID());
        // when
        pmr.messageIn("a", "a1", uiKey, 5);
        // then
        verify(uiBus).publish(any(PushMessage.class));
    }


}
