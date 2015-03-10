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

import com.google.inject.*;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.services.Service;

import java.lang.annotation.Annotation;

/**
 * Configures Event bus implementations for, UIScope, VaadinSessionScope and Singleton scope.  All classes annotated with {@link Listener} are subscribed to a
 * bus:
 * <p>
 * If there is also a {@link SubscribeTo} annotation, the values of that annotation are used to subscribe to one or more buses.
 * <p>
 * If there is no {@link SubscribeTo} annotation, a {@link Singleton} scoped object will be subscribed to the {@link GlobalBus}, all other objects with a {@link
 * Listener} annotation is subscribed to a {@link SessionBus}
 *
 *
 * Created by David Sowerby on 08/03/15.
 */
public class EventBusModule extends AbstractModule {

    private IBusConfiguration globalConfig;
    private IBusConfiguration sessionConfig;
    private IBusConfiguration uiConfig;

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        TypeLiteral<MBassador<BusMessage>> eventBusLiteral = new TypeLiteral<MBassador<BusMessage>>() {
        };

        Key<MBassador<BusMessage>> uiBusKey = Key.get(eventBusLiteral, UIBus.class);
        final Provider<MBassador<BusMessage>> uiBusProvider = this.getProvider(uiBusKey);

        Key<MBassador<BusMessage>> sessionBusKey = Key.get(eventBusLiteral, SessionBus.class);
        final Provider<MBassador<BusMessage>> sessionBusProvider = this.getProvider(sessionBusKey);

        Key<MBassador<BusMessage>> globalBusKey = Key.get(eventBusLiteral, GlobalBus.class);
        final Provider<MBassador<BusMessage>> globalBusProvider = this.getProvider(globalBusKey);


        bindListener(new ListenerAnnotationMatcher(), new BusTypeListener(uiBusProvider, sessionBusProvider, globalBusProvider));
        defineBusConfigurations();
    }


    /**
     * Define the configuration(s) to be used when constructing the MBassador instance, using annotations to distinguish between Event Bus instances with
     * different scopes.  The code here achieves exactly the same as the defaults created when invoking new MBassador(). Refer to the MBassador documentation
     * at https://github.com/bennidi/mbassador/wiki/Configuration for more information about the configuration itself.
     * <p>
     */
    protected void defineBusConfigurations() {

        uiConfig = new BusConfiguration().addFeature(Feature.SyncPubSub.Default())
                                         .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                         .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                         .setProperty("Bus Name", "UI Bus");
        setConfiguration(UIBus.class, uiConfig);


        sessionConfig = new BusConfiguration().addFeature(Feature.SyncPubSub.Default())
                                              .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                              .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                              .setProperty("Bus Name", "Session Bus");
        setConfiguration(SessionBus.class, sessionConfig);


        globalConfig = new BusConfiguration().addFeature(Feature.SyncPubSub.Default())
                                             .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                             .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                             .setProperty("Bus Name", "Global Bus");
        setConfiguration(GlobalBus.class, globalConfig);


    }

    protected void setConfiguration(Class<? extends Annotation> annotationClass, IBusConfiguration configuration) {
        bind(IBusConfiguration.class).annotatedWith(annotationClass)
                                     .toInstance(configuration);
    }


    @Provides
    @UIBus
    @UIScoped
    protected MBassador<BusMessage> providesUIBus(@UIBus IBusConfiguration config) {
        return new MBassador<>(config);
    }

    @Provides
    @SessionBus
    @VaadinSessionScoped
    protected MBassador<BusMessage> providesSessionBus(@SessionBus IBusConfiguration config) {
        return new MBassador<>(config);
    }

    @Provides
    @GlobalBus
    @VaadinSessionScoped
    protected MBassador<BusMessage> providesGlobalBus(@GlobalBus IBusConfiguration config) {
        return new MBassador<>(config);
    }

    public static class EventBusListenerListener implements InjectionListener {


        private Provider<MBassador<BusMessage>> globalBusProvider;
        private Provider<MBassador<BusMessage>> sessionBusProvider;
        private Provider<MBassador<BusMessage>> uiBusProvider;

        public EventBusListenerListener(Provider<MBassador<BusMessage>> uiBusProvider, Provider<MBassador<BusMessage>> sessionBusProvider,
                                        Provider<MBassador<BusMessage>> globalBusProvider) {
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

    /**
     * Matches classes implementing {@link Service}
     */
    private class ListenerAnnotationMatcher extends AbstractMatcher<TypeLiteral<?>> {
        @Override
        public boolean matches(TypeLiteral<?> t) {
            Class<?> rawType = t.getRawType();
            return rawType.isAnnotationPresent(Listener.class);
        }
    }

    private class BusTypeListener implements TypeListener {
        private Provider<MBassador<BusMessage>> globalBusProvider;
        private Provider<MBassador<BusMessage>> sessionBusProvider;
        private Provider<MBassador<BusMessage>> uiBusProvider;

        public BusTypeListener(Provider<MBassador<BusMessage>> uiBusProvider, Provider<MBassador<BusMessage>> sessionBusProvider,
                               Provider<MBassador<BusMessage>> globalBusProvider) {
            this.uiBusProvider = uiBusProvider;
            this.sessionBusProvider = sessionBusProvider;
            this.globalBusProvider = globalBusProvider;
        }

        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            encounter.register(new EventBusListenerListener(uiBusProvider, sessionBusProvider, globalBusProvider));
        }
    }
}
