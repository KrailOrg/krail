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

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.SyncMessageBus;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.ConfigurationErrorHandler;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.EventBusAutoSubscriber;
import uk.q3c.krail.eventbus.GlobalMessageBus;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.service.Service;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Configures Event bus implementations for, UIScope, VaadinSessionScope and Singleton scope.  All classes annotated
 * with {@link Listener} are subscribed to a bus, using logic provided by {@link VaadinEventBusAutoSubscriber}, but can
 * be changed by providing an alternative implementation in {@link VaadinEventBusModule.BusTypeListener}:
 * <p>
 * If there is also a {@link SubscribeTo} annotation, the values of that annotation are used to subscribe to one or more
 * buses.
 * <p>
 * If there is no {@link SubscribeTo} annotation, a {@link Singleton} scoped object will be subscribed to the {@link
 * GlobalMessageBus}, all other objects with a {@link Listener} annotation is subscribed to a {@link SessionBus}
 * <p>
 * <p>
 * Created by David Sowerby on 08/03/15.
 */
public class VaadinEventBusModule extends AbstractModule {
    public final static String BUS_SCOPE = "bus_id";
    public final static String BUS_INDEX = "bus_index";
    private static Logger log = LoggerFactory.getLogger(VaadinEventBusModule.class);
    private AtomicInteger globalBusIndex = new AtomicInteger(1);
    private AtomicInteger sessionBusIndex = new AtomicInteger(1);
    private AtomicInteger uiBusIndex = new AtomicInteger(1);

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        TypeLiteral<PubSubSupport<BusMessage>> eventBusLiteral = new TypeLiteral<PubSubSupport<BusMessage>>() {
        };

        Key<PubSubSupport<BusMessage>> uiBusKey = Key.get(eventBusLiteral, UIBus.class);
        final Provider<PubSubSupport<BusMessage>> uiBusProvider = this.getProvider(uiBusKey);

        Key<PubSubSupport<BusMessage>> sessionBusKey = Key.get(eventBusLiteral, SessionBus.class);
        final Provider<PubSubSupport<BusMessage>> sessionBusProvider = this.getProvider(sessionBusKey);

        bindListener(new ListenerAnnotationMatcher(), new BusTypeListener(uiBusProvider, sessionBusProvider));
        bindConfigurationErrorHandlers();
        bindPublicationErrorHandlers();
        bindBusProviders();
    }

    /**
     * Use bus providers where you want to enforce the use of a particular bus by sub-classes.  This avoids an annotated
     * constructor parameter in a super-class being ignored / overridden in a sub-class
     */
    protected void bindBusProviders() {
        bind(SessionBusProvider.class).to(DefaultSessionBusProvider.class);
        bind(UIBusProvider.class).to(DefaultUIBusProvider.class);
    }


    /**
     * All buses use the default error handler by default.  Override this method to provide alternative bindings.
     */
    protected void bindConfigurationErrorHandlers() {
        bind(ConfigurationErrorHandler.class).annotatedWith(UIBus.class)
                                             .to(DefaultEventBusConfigurationErrorHandler.class);
        bind(ConfigurationErrorHandler.class).annotatedWith(SessionBus.class)
                                             .to(DefaultEventBusConfigurationErrorHandler.class);
    }

    /**
     * All buses use the default error handler by default.  Override this method to provide alternative bindings.
     */
    protected void bindPublicationErrorHandlers() {
        bind((IPublicationErrorHandler.class)).annotatedWith(UIBus.class)
                                              .to(DefaultEventBusErrorHandler.class);
        bind((IPublicationErrorHandler.class)).annotatedWith(SessionBus.class)
                                              .to(DefaultEventBusErrorHandler.class);
    }


    @Provides
    protected EventBusAutoSubscriber autoSubscriber(@UIBus Provider<PubSubSupport<BusMessage>> uiBus, @SessionBus Provider<PubSubSupport<BusMessage>>
            sessionBus) {
        return new VaadinEventBusAutoSubscriber(uiBus, sessionBus);
    }

    /**
     * Refer to the MBassador documentation at https://github.com/bennidi/mbassador/wiki/Configuration for more
     * information about the configuration itself.
     *
     * @return configuration for the UIBus
     */
    @Provides
    @UIBus
    protected IBusConfiguration uiBusConfig() {
        return new BusConfiguration().addFeature(Feature.SyncPubSub.Default())
                                     .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                     .addFeature(Feature.AsynchronousMessageDispatch.Default());

    }

    /**
     * Refer to the MBassador documentation at https://github.com/bennidi/mbassador/wiki/Configuration for more
     * information about the configuration itself.
     *
     * @return configuration for the SessionBus
     */
    @Provides
    @SessionBus
    protected IBusConfiguration sessionBusConfig() {
        return new BusConfiguration().addFeature(Feature.SyncPubSub.Default())
                                     .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                     .addFeature(Feature.AsynchronousMessageDispatch.Default());

    }




    @Provides
    @UIBus
    @UIScoped
    protected PubSubSupport<BusMessage> providesUIBus(@UIBus IBusConfiguration config, @UIBus IPublicationErrorHandler publicationErrorHandler, @UIBus
    ConfigurationErrorHandler configurationErrorHandler) {
        PubSubSupport<BusMessage> bus = createBus(config, publicationErrorHandler, configurationErrorHandler, "UI", false);
        bus.getRuntime()
           .add(BUS_SCOPE, "ui")
           .add(BUS_INDEX, uiBusIndex.getAndIncrement());
        return bus;
    }

    private PubSubSupport<BusMessage> createBus(IBusConfiguration config, IPublicationErrorHandler publicationErrorHandler, ConfigurationErrorHandler
            configurationErrorHandler, String name, boolean useAsync) {
        config.addPublicationErrorHandler(publicationErrorHandler);
        PubSubSupport<BusMessage> eventBus;
        eventBus = (useAsync) ? new MBassador<>(config) : new SyncMessageBus<>(config);
        log.debug("instantiated a {} Bus with id {}", name, eventBus.getRuntime().get(IBusConfiguration.Properties.BusId));
        return eventBus;
    }

    @Provides
    @SessionBus
    @VaadinSessionScoped
    protected PubSubSupport<BusMessage> providesSessionBus(@SessionBus IBusConfiguration config, @SessionBus IPublicationErrorHandler
            publicationErrorHandler, @SessionBus ConfigurationErrorHandler configurationErrorHandler) {
        PubSubSupport<BusMessage> bus = createBus(config, publicationErrorHandler, configurationErrorHandler, "Session", false);
        bus.getRuntime()
           .add(BUS_SCOPE, "session")
           .add(BUS_INDEX, sessionBusIndex.getAndIncrement());
        return bus;
    }



    /**
     * Matches classes implementing {@link Service}
     */
    private static class ListenerAnnotationMatcher extends AbstractMatcher<TypeLiteral<?>> {
        @Override
        public boolean matches(TypeLiteral<?> t) {
            Class<?> rawType = t.getRawType();
            return rawType.isAnnotationPresent(Listener.class);
        }
    }

    private static class BusTypeListener implements TypeListener {
        private Provider<PubSubSupport<BusMessage>> sessionBusProvider;
        private Provider<PubSubSupport<BusMessage>> uiBusProvider;

        public BusTypeListener(Provider<PubSubSupport<BusMessage>> uiBusProvider, Provider<PubSubSupport<BusMessage>> sessionBusProvider) {
            this.uiBusProvider = uiBusProvider;
            this.sessionBusProvider = sessionBusProvider;
        }

        /**
         * The logic for auto subscribing can be changed by providing an alternative implementation of
         * EventBusAutoSubscriber, but it has to be created using 'new' here, because the Injector is not yet complete
         *
         * @param type
         * @param encounter
         * @param <I>
         */
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            encounter.register(new VaadinEventBusAutoSubscriber(uiBusProvider, sessionBusProvider));
        }
    }
}
