/*
 * Copyright (C) 2013 David Sowerby
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
package uk.q3c.krail.core.services;

import com.google.inject.*;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.engio.mbassy.bus.MBassador;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.eventbus.GlobalBus;
import uk.q3c.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Acknowledgement: developed from code contributed by https://github.com/lelmarir
 *
 * @author David Sowerby
 */
public class ServiceModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(ServiceModule.class);


    @Override
    protected void configure() {

        // global bus provider needed for the service.init in the injection listener
        TypeLiteral<MBassador<BusMessage>> eventBusLiteral = new TypeLiteral<MBassador<BusMessage>>() {
        };
        Key<MBassador<BusMessage>> globalBusKey = Key.get(eventBusLiteral, GlobalBus.class);
        final Provider<MBassador<BusMessage>> globalBusProvider = this.getProvider(globalBusKey);


        final Provider<ServicesMonitor> servicesMonitorProvider = this.getProvider(ServicesMonitor.class);

        bindListener(new ServiceInterfaceMatcher(), new ServicesListener(servicesMonitorProvider, globalBusProvider));
        bindInterceptor(Matchers.subclassesOf(Service.class), new FinalizeMethodMatcher(), new FinalizeMethodInterceptor());

    }

    /**
     * We have to explicitly subscribe the monitor to the global bus here - becuase it is constructed with new() it will not be intercepted by the
     * InjectionListener in {@link EventBusModule}
     *
     * @param globalBus
     *         the global event bus
     *
     * @return a singleton instance of ServicesMonitor
     */
    @Provides
    @Singleton
    public ServicesMonitor getServicesMonitor(@GlobalBus MBassador<BusMessage> globalBus) {
        ServicesMonitor monitor = new ServicesMonitor();
        globalBus.subscribe(monitor);
        return monitor;
    }

    /**
     * This listener is constructed using the {@link Service} interface to identify service implementation instances..
     * All instances of {@link Service} implementations are registered with the {@link ServicesMonitor}
     *
     * @author David Sowerby
     */
    public class ServicesListener implements TypeListener {
        private final Provider<ServicesMonitor> servicesMonitorProvider;
        private Provider<MBassador<BusMessage>> globalBusProvider;

        public ServicesListener(Provider<ServicesMonitor> servicesMonitorProvider, Provider<MBassador<BusMessage>> globalBusProvider) {
            this.servicesMonitorProvider = servicesMonitorProvider;
            this.globalBusProvider = globalBusProvider;
        }

        @Override
        public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
            InjectionListener<Object> listener = new InjectionListener<Object>() {
                @Override
                public void afterInjection(Object injectee) {
                    //this appears not to do anything but it forces construction  of the monitor
                    //before service.init publishes a bus message
                    final ServicesMonitor servicesMonitor = servicesMonitorProvider.get();
                    // cast is safe - if not, the matcher is wrong
                    Service service = (Service) injectee;
                    service.init(globalBusProvider.get());

                }
            };
            encounter.register(listener);
        }

    }

    private class FinalizeMethodMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method method) {
            return method.getName()
                         .equals("finalize");
        }
    }

    /**
     * Calls {@link Service#stop} before passing on the finalize() call
     */
    private class FinalizeMethodInterceptor implements MethodInterceptor {

        public FinalizeMethodInterceptor() {
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Service service = (Service) invocation.getThis();
            service.stop();
            return invocation.proceed();
        }
    }

    /**
     * Matches classes implementing {@link Service}
     */
    private class ServiceInterfaceMatcher extends AbstractMatcher<TypeLiteral<?>> {
        @Override
        public boolean matches(TypeLiteral<?> t) {
            Class<?> rawType = t.getRawType();
            Set<Class<?>> interfaces = ReflectionUtils.allInterfaces(rawType);

            for (Class<?> intf : interfaces) {
                if (intf.equals(Service.class)) {
                    return true;
                }
            }
            return false;
        }
    }

}