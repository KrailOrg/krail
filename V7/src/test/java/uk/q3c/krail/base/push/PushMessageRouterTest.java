/*
 * Copyright (C) 2014 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.base.push;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class PushMessageRouterTest {

    PushMessageRouter pmr;

    @Mock
    PushMessageListener listener1;

    @Mock
    PushMessageListener listener2;

    @Mock
    PushMessageListener listener3;

    @Mock
    PushMessageListener listener4;

    @Before
    public void setup() {
        pmr = new PushMessageRouter();
    }

    @Test
    public void messageIn_simple() {
        // given
        pmr.register(PushMessageRouter.ALL_MESSAGES, listener1);
        // when
        pmr.messageIn("a", "a1");
        // then
        verify(listener1).receiveMessage("a", "a1");
    }

    @Test
    public void messageIn_multi() {
        // given
        pmr.register(PushMessageRouter.ALL_MESSAGES, listener1);
        pmr.register("a", listener2);
        pmr.register("b", listener2);
        pmr.register("c", listener3);
        pmr.register("d", listener4);

        // when
        pmr.messageIn("a", "a1");
        pmr.messageIn("a", "a2");
        pmr.messageIn("b", "b1");
        pmr.messageIn("b", "b2");
        pmr.messageIn("c", "c1");

        // then
        verify(listener1).receiveMessage("a", "a1");
        verify(listener1).receiveMessage("b", "b1");
        verify(listener1).receiveMessage("a", "a2");
        verify(listener1).receiveMessage("b", "b2");
        verify(listener1).receiveMessage("c", "c1");

        verify(listener2).receiveMessage("a", "a1");
        verify(listener2).receiveMessage("b", "b1");
        verify(listener2).receiveMessage("a", "a2");
        verify(listener2).receiveMessage("b", "b2");

        verify(listener3).receiveMessage("c", "c1");

        verify(listener4, never()).receiveMessage(anyString(), anyString());

    }

}
