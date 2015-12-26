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

package uk.q3c.krail.core.user.notify;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.UIBusProvider;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;

import static org.mockito.Mockito.*;
@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultUserNotifierTest {

    DefaultUserNotifier notifier;
    @Mock
    UIBusProvider messageBusProvider;
    @Mock
    private PubSubSupport<BusMessage> messageBus;
    @Mock
    private Translate translate;

    @Before
    public void setup() {
        when(messageBusProvider.getUIBus()).thenReturn(messageBus);
        notifier = new DefaultUserNotifier(messageBusProvider, translate);
    }
    @Test
    public void error() {
        //given
        when(translate.from(LabelKey.Edit, "x")).thenReturn("Wiggly");
        //when
        notifier.notifyError(LabelKey.Edit, "x");
        //then
        verify(messageBus).publish(any(ErrorNotificationMessage.class));
    }

    @Test
    public void warning() {
        //given
        when(translate.from(LabelKey.Edit, "x")).thenReturn("Wiggly");
        //when
        notifier.notifyError(LabelKey.Edit, "x");
        //then
        verify(messageBus).publish(any(WarningNotificationMessage.class));
    }

    @Test
    public void info() {
        //given
        when(translate.from(LabelKey.Edit, "x")).thenReturn("Wiggly");
        //when
        notifier.notifyError(LabelKey.Edit, "x");
        //then
        verify(messageBus).publish(any(InformationNotificationMessage.class));
    }
}