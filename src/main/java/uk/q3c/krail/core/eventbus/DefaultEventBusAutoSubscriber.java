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

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.spi.InjectionListener;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.EventBusAutoSubscriber;
import uk.q3c.krail.eventbus.GlobalBus;
import uk.q3c.krail.eventbus.SubscribeTo;

import java.lang.annotation.Annotation;

/**
 * Provides logic for automatically subscribing to event buses.  This is used as an {@link InjectionListener}, and cannot therefore use injection in its
 * constructor
 * <p>
 * Created by David Sowerby on 13/03/15.
 */
public class DefaultEventBusAutoSubscriber implements EventBusAutoSubscriber {


    private Provider<PubSubSupport<BusMessage>> globalBusProvider;
    private Provider<PubSubSupport<BusMessage>> sessionBusProvider;
    private Provider<PubSubSupport<BusMessage>> uiBusProvider;

    public DefaultEventBusAutoSubscriber(Provider<PubSubSupport<BusMessage>> uiBusProvider, Provider<PubSubSupport<BusMessage>> sessionBusProvider,
                                         Provider<PubSubSupport<BusMessage>> globalBusProvider) {
        this.uiBusProvider = uiBusProvider;
        this.sessionBusProvider = sessionBusProvider;
        this.globalBusProvider = globalBusProvider;
    }

    /**
     * Invoked by Guice after it injects the fields and methods of instance.  {@code injectee} must have a {@link Listener} annotation in order to get this
     * far (the matcher will only select those which have).
     * <p>
     * If there is a {@link SubscribeTo} annotation, the injectee is subscribed to the buses defined by the annotation.  If there is no {@link SubscribeTo}
     * annotation, the default behaviour is to subscribe singleton objects to the Global Bus, {@link VaadinSessionScoped} objects to the Session Bus, and
     * anything else to the UI Bus
     *
     * @param injectee
     *         instance that Guice injected dependencies into
     */
    @Override
    public void afterInjection(Object injectee) {
        Class<?> clazz = injectee.getClass();
        SubscribeTo subscribeTo = clazz.getAnnotation(SubscribeTo.class);
        if (subscribeTo == null) { //default behaviour
            if (clazz.isAnnotationPresent(Singleton.class)) {
                globalBusProvider.get()
                                 .subscribe(injectee);
                return;
            }

            sessionBusProvider.get()
                              .subscribe(injectee);
        } else { //defined by SubscribeTo
            Class<? extends Annotation>[] targets = subscribeTo.value();
            for (Class<? extends Annotation> target : targets) {
                if (target.equals(UIBus.class)) {
                    uiBusProvider.get()
                                 .subscribe(injectee);
                }
                if (target.equals(SessionBus.class)) {
                    sessionBusProvider.get()
                                      .subscribe(injectee);
                }
                if (target.equals(GlobalBus.class)) {
                    globalBusProvider.get()
                                     .subscribe(injectee);
                }


            }
        }

    }
}
