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

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;

import java.lang.annotation.Annotation;

/**
 * Created by David Sowerby on 08/03/15.
 */
public class EventBusModule extends AbstractModule {
    private MapBinder<Class<? extends Annotation>, IBusConfiguration> configs;

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        TypeLiteral<Class<? extends Annotation>> annotationTypeLiteral = new TypeLiteral<Class<? extends Annotation>>() {
        };
        TypeLiteral<IBusConfiguration> configLiteral = new TypeLiteral<IBusConfiguration>() {
        };

        configs = MapBinder.newMapBinder(binder(), annotationTypeLiteral, configLiteral);
        define();
        bindings();
    }

    /**
     * Binds {@link EventBusProvider} using annotations to distinguish between Event Bus instances with different scopes.  You do not have to have multiple
     * declarations here ... you can override this method and just leave the singleton declaration in place, although unless you inject the {@link
     * EventBusProvider} with a {@link SessionBus} annotation, no session scoped bus will be created anyway.
     */
    protected void bindings() {

        //        bind(EventBusProvider.class).annotatedWith(SessionBus.class).to(EventBusProvider.class).in(VaadinSessionScoped.class);
        //        bind(EventBusProvider.class).annotatedWith(GlobalBus.class).to(EventBusProvider.class).in(Singleton.class);

    }


    /**
     * Define the configuration(s) to be used when constructing the MBassador instance.  The code here achieves exactly the same as the defaults created when
     * invoking new MBassador(), and is therefore here primarily as an example of how to configure the event bus.  Refer to the MBassador documentation at
     * https://github.com/bennidi/mbassador/wiki/Configuration for more information about the configuration itself.
     * <p>
     * If no configuration is defined here, then the {@link EventBusProvider} will use the default configuration to create an instance.  To modify
     * configurations, override this method, and call {@link #setConfiguration(Class, IBusConfiguration)} for each configuration you define.
     */
    protected void define() {
        IBusConfiguration globalConfig = new BusConfiguration().addFeature(Feature.SyncPubSub.Default())
                                                               .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                                               .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                                               .setProperty("Bus Name", "Global Bus");
        setConfiguration(GlobalBus.class, globalConfig);

        IBusConfiguration sessionConfig = new BusConfiguration().addFeature(Feature.SyncPubSub.Default())
                                                                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                                                .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                                                .setProperty("Bus Name", "Session Bus");
        setConfiguration(SessionBus.class, globalConfig);


    }

    protected void setConfiguration(Class<? extends Annotation> annotationClass, IBusConfiguration configuration) {
        bind(IBusConfiguration.class).annotatedWith(annotationClass)
                                     .toInstance(configuration);
    }

    @Provides
    @SessionBus
    @VaadinSessionScoped
    protected MBassador<BusMessage> providesSessionBus(@SessionBus IBusConfiguration config) {
        return new MBassador<BusMessage>(config);
    }

    @Provides
    @GlobalBus
    @VaadinSessionScoped
    protected MBassador<BusMessage> providesGlobalBus(@GlobalBus IBusConfiguration config) {
        return new MBassador<BusMessage>(config);
    }
}
