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
package uk.q3c.krail.core.push;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.UIBusProvider;

import static org.mockito.Mockito.*;

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
        when(uiBusProvider.getUIBus()).thenReturn(uiBus);
        pmr = new DefaultPushMessageRouter(uiBusProvider);
    }

    @Test
    public void messageIn_simple() {
        // given
        // when
        pmr.messageIn("a", "a1");
        // then
        verify(uiBus).publish(any(PushMessage.class));
    }


}
