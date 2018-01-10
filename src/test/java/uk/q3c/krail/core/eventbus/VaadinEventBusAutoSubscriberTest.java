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

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.listener.Listener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.i18n.I18N;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.EventBus;
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.eventbus.mbassador.MBassadorEventBusAutoSubscriber;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class VaadinEventBusAutoSubscriberTest {

    VaadinEventBusAutoSubscriber listener;
    @Mock
    private MessageBus messageBus;


    @Mock
    private Provider<EventBus> eventBusProvider;
    @Mock
    private EventBus eventBus;

    @Mock
    private Provider<MessageBus> messageBusProvider;

    @Mock
    private Provider<Provider<MessageBus>> messageBusProviderProvider;

    @Mock
    private Provider<Provider<EventBus>> eventBusProviderProvider;

    @Mock
    private PubSubSupport<BusMessage> sessionBus;
    @Mock
    private Provider<PubSubSupport<BusMessage>> sessionBusProvider;
    @Mock
    private PubSubSupport<BusMessage> uiBus;
    @Mock
    private Provider<PubSubSupport<BusMessage>> uiBusProvider;
    private MBassadorEventBusAutoSubscriber listener2;

    @Before
    public void setup() {
        when(uiBusProvider.get()).thenReturn(uiBus);
        when(sessionBusProvider.get()).thenReturn(sessionBus);
        when(eventBusProvider.get()).thenReturn(eventBus);
        when(messageBusProvider.get()).thenReturn(messageBus);
        when(eventBusProviderProvider.get()).thenReturn(eventBusProvider);
        when(messageBusProviderProvider.get()).thenReturn(messageBusProvider);
        listener = new VaadinEventBusAutoSubscriber(uiBusProvider, sessionBusProvider);
        listener2 = new MBassadorEventBusAutoSubscriber(messageBusProviderProvider, eventBusProviderProvider);
    }

    @Test
    public void no_subscribeTo_no_scope() {
        //given

        //when
        listener.afterInjection(new TestWithNoSubscribeTo());
        listener2.afterInjection(new TestWithNoSubscribeTo());
        //then
        verify(uiBus, times(0)).subscribe(anyObject());
        verify(sessionBus, times(0)).subscribe(anyObject());
        verify(messageBus, times(1)).subscribe(anyObject());
    }

    /**
     * Should not subscribe to anything
     */
    @Test
    public void subscribeTo_empty() {
        //given

        //when
        listener.afterInjection(new TestSubscribeTo_Empty_Annotation());
        listener2.afterInjection(new TestSubscribeTo_Empty_Annotation());
        //then
        verify(uiBus, times(0)).subscribe(anyObject());
        verify(sessionBus, times(0)).subscribe(anyObject());
        verify(messageBus, times(0)).subscribe(anyObject());
    }


    @Test
    public void no_subscribeTo_uiScope() {
        //given

        //when
        listener.afterInjection(new TestWithNoSubscribeTo_UIScope());
        listener2.afterInjection(new TestWithNoSubscribeTo_UIScope());
        //then
        verify(uiBus, times(0)).subscribe(anyObject());
        verify(sessionBus, times(0)).subscribe(anyObject());
        verify(messageBus, times(1)).subscribe(anyObject());
    }

    @Test
    public void no_subscribeTo_sessionScope() {
        //given

        //when
        listener.afterInjection(new TestWithNoSubscribeTo_SessionScope());
        listener2.afterInjection(new TestWithNoSubscribeTo_SessionScope());
        //then
        verify(uiBus, never()).subscribe(anyObject());
        verify(sessionBus, times(0)).subscribe(anyObject());
        verify(messageBus, times(1)).subscribe(anyObject());
    }

    @Test
    public void no_subscribeTo_singleton_scope() {
        //given

        //when
        listener.afterInjection(new TestWithNoSubscribeTo_SingletonScope());
        listener2.afterInjection(new TestWithNoSubscribeTo_SingletonScope());
        //then
        verify(uiBus, never()).subscribe(anyObject());
        verify(sessionBus, never()).subscribe(anyObject());
        verify(messageBus, times(1)).subscribe(anyObject());
    }

    @Test
    public void subscribeToOne() {
        //given

        //when
        listener.afterInjection(new TestSubscribeTo_One());
        listener2.afterInjection(new TestSubscribeTo_One());
        //then
        verify(uiBus, times(1)).subscribe(anyObject());
        verify(sessionBus, times(0)).subscribe(anyObject());
        verify(messageBus, times(0)).subscribe(anyObject());
    }

    @Test
    public void subscribeTo2() {
        //given

        //when
        listener.afterInjection(new TestSubscribeTo_Two());
        listener2.afterInjection(new TestSubscribeTo_Two());
        //then
        verify(uiBus, times(1)).subscribe(anyObject());
        verify(sessionBus, times(1)).subscribe(anyObject());
        verify(messageBus, times(0)).subscribe(anyObject());
    }

    @Test
    public void subscribeTo2_overrides_scope() {
        //given

        //when
        listener.afterInjection(new TestSubscribeTo_With_Singleton());
        listener2.afterInjection(new TestSubscribeTo_With_Singleton());
        //then
        verify(uiBus, times(1)).subscribe(anyObject());
        verify(sessionBus, times(1)).subscribe(anyObject());
        verify(messageBus, times(0)).subscribe(anyObject());
    }

    /**
     * Should just silently ignore
     */
    @Test
    public void subscribeTo_with_wrong_annotation_parameter() {
        //given

        //when
        listener.afterInjection(new TestSubscribeTo_With_Invalid_Annotation());
        listener2.afterInjection(new TestSubscribeTo_With_Invalid_Annotation());
        //then
        verify(uiBus, times(0)).subscribe(anyObject());
        verify(sessionBus, times(0)).subscribe(anyObject());
        verify(messageBus, times(0)).subscribe(anyObject());
    }


    @Test
    public void inherited_subscribeTo_override() {
        //given

        //when
        listener.afterInjection(new Child());
        listener2.afterInjection(new Child());
        //then
        verify(uiBus, times(0)).subscribe(anyObject());
        verify(sessionBus, times(1)).subscribe(anyObject());
        verify(messageBus, times(0)).subscribe(anyObject());
    }

    @Test
    public void inherited_subscribeTo() {
        //given

        //when
        listener.afterInjection(new OtherChild());
        listener2.afterInjection(new OtherChild());
        //then
        verify(uiBus, times(1)).subscribe(anyObject());
        verify(sessionBus, times(1)).subscribe(anyObject());
        verify(messageBus, times(0)).subscribe(anyObject());
    }

    @Listener
    private class TestWithNoSubscribeTo {
    }

    @Listener
    @UIScoped
    private class TestWithNoSubscribeTo_UIScope {
    }

    @Listener
    @VaadinSessionScoped
    private class TestWithNoSubscribeTo_SessionScope {
    }

    @Listener
    @Singleton
    private class TestWithNoSubscribeTo_SingletonScope {
    }

    @Listener
    @SubscribeTo(UIBus.class)
    private class TestSubscribeTo_One {
    }

    @Listener
    @SubscribeTo({UIBus.class, SessionBus.class})
    private class TestSubscribeTo_Two {
    }


    @Listener
    @Singleton
    @SubscribeTo({UIBus.class, SessionBus.class})
    private class TestSubscribeTo_With_Singleton {
    }

    @Listener
    @Singleton
    @SubscribeTo(I18N.class)
    private class TestSubscribeTo_With_Invalid_Annotation {
    }

    @Listener
    @Singleton
    @SubscribeTo()
    private class TestSubscribeTo_Empty_Annotation {
    }

    @Listener
    @Singleton
    private class Grandparent {

    }

    @SubscribeTo({UIBus.class, SessionBus.class})
    private class Parent extends Grandparent {

    }

    @Listener
    @SubscribeTo(SessionBus.class)
    private class Child extends Parent {

    }

    private class OtherChild extends Parent {

    }

}