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

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.GlobalBus;
import uk.q3c.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

/**
 * Provides bindings and AOP in support of {@link Service}s
 * <p>
 * <p>
 * Acknowledgement: developed originally from code contributed by https://github.com/lelmarir
 *
 * @author David Sowerby
 */
public class ServicesModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(ServicesModule.class);
    //has no contents in this module, but prevents Guice from complaining that there is no Set<DependencyDefinition>.  An empty set is legitimate, and other modules won't declare unless needed
    private Multibinder<DependencyDefinition> dependencies;


    @Override
    protected void configure() {
        dependencies = newSetBinder(binder(), DependencyDefinition.class);
        bindServicesController();
        bindServicesGraph();
        bindServiceDependencyScanner();

        // global bus provider and servicesGraph provider needed for the service.init in the injection listener
        TypeLiteral<PubSubSupport<BusMessage>> eventBusLiteral = new TypeLiteral<PubSubSupport<BusMessage>>() {
        };
        Key<PubSubSupport<BusMessage>> globalBusKey = Key.get(eventBusLiteral, GlobalBus.class);
        final Provider<PubSubSupport<BusMessage>> globalBusProvider = this.getProvider(globalBusKey);


//        final Provider<ServicesMonitor> servicesMonitorProvider = this.getProvider(ServicesMonitor.class);
        final Provider<ServicesGraph> servicesGraphProvider = this.getProvider(ServicesGraph.class);

        bindListener(new ServiceInterfaceMatcher(), new ServicesListener(servicesGraphProvider, globalBusProvider));
        bindInterceptor(Matchers.subclassesOf(Service.class), new FinalizeMethodMatcher(), new FinalizeMethodInterceptor());

        final Provider<ServiceDependencyScanner> scannerProvider = this.getProvider(ServiceDependencyScanner.class);
        bindListener(new ServiceUsingDependencyAnnotationMatcher(), new ServicesUsingDependencyListener(scannerProvider));
        bindInterceptor(Matchers.subclassesOf(Service.class), new FinalizeMethodMatcher(), new FinalizeMethodInterceptor());

    }

    protected void bindServicesController() {
        bind(ServicesController.class).to(DefaultServicesController.class);
    }

    protected void bindServicesGraph() {
        bind(ServicesGraph.class).to(DefaultServicesGraph.class);
    }

    protected void bindServiceDependencyScanner() {
        bind(ServiceDependencyScanner.class).to(DefaultServiceDependencyScanner.class
        );
    }

    /**
     * This listener is matched using the {@link Service} interface.  It registers the service with ServicesGraph, and
     * passes the global event bus to the service through the init() method
     *
     * @author David Sowerby
     */
    private static class ServicesListener implements TypeListener {
        private Provider<PubSubSupport<BusMessage>> globalBusProvider;
        private Provider<ServicesGraph> servicesGraphProvider;

        public ServicesListener(Provider<ServicesGraph> servicesGraphProvider, Provider<PubSubSupport<BusMessage>> globalBusProvider) {
            this.servicesGraphProvider = servicesGraphProvider;
            this.globalBusProvider = globalBusProvider;
        }

        @Override
        public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
            InjectionListener<Object> listener = new InjectionListener<Object>() {
                @Override
                public void afterInjection(Object injectee) {
                    // cast is safe - if not, the matcher is wrong
                    Service service = (Service) injectee;
                    ServicesGraph servicesGraph = servicesGraphProvider.get();
                    servicesGraph.registerService(service);
//                    use init because it avoids having to pass the eventBus through constructor injection for every Service, which also risks getting the wrong bus passed in.
                    service.init(globalBusProvider.get());

                }
            };
            encounter.register(listener);
        }

    }

    private static class FinalizeMethodMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method method) {
            return method.getName()
                    .equals("finalize");
        }
    }

    /**
     * Calls {@link Service#stop} before passing on the finalize() call
     */
    private static class FinalizeMethodInterceptor implements MethodInterceptor {

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
    private static class ServiceInterfaceMatcher extends AbstractMatcher<TypeLiteral<?>> {
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

    /**
     * Scans the intercepted {@link ServiceUsingDependencyAnnotation} with {@link ServiceDependencyScanner} to update
     * the {@link ServicesGraph}
     */
    private static class ServicesUsingDependencyListener implements TypeListener {

        private Provider<ServiceDependencyScanner> scannerProvider;

        public ServicesUsingDependencyListener(Provider<ServiceDependencyScanner> scannerProvider) {
            this.scannerProvider = scannerProvider;
        }

        @Override
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            InjectionListener<Object> listener = new InjectionListener<Object>() {
                @Override
                public void afterInjection(Object injectee) {
                    // cast is safe - if not, the matcher is wrong
                    scannerProvider.get().scan((Service) injectee);
                }
            };
            encounter.register(listener);
        }
    }

    private static class ServiceUsingDependencyAnnotationMatcher extends AbstractMatcher<TypeLiteral<?>> {
        @Override
        public boolean matches(TypeLiteral<?> t) {
            Class<?> rawType = t.getRawType();
            Set<Class<?>> interfaces = ReflectionUtils.allInterfaces(rawType);

            for (Class<?> intf : interfaces) {
                if (intf.equals(ServiceUsingDependencyAnnotation.class)) {
                    return true;
                }
            }
            return false;
        }
    }

}