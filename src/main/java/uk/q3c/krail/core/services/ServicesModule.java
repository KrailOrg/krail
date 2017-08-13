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
package uk.q3c.krail.core.services;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import uk.q3c.util.reflect.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Provides bindings and AOP in support of {@link Service}s.  Inherits from {@link AbstractServiceModule} to ensure there is always a map binding for
 * registered services, even if it is empty
 * <p>
 * <p>
 * Acknowledgement: developed originally from code contributed by https://github.com/lelmarir
 *
 * @author David Sowerby
 */
public class ServicesModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        super.configure();
        bindServicesModel();
        bindServiceDependencyScanner();
        bindServicesExecutor();

        final Provider<ServicesModel> servicesModelProvider = this.getProvider(ServicesModel.class);
        final Provider<ServiceDependencyScanner> scannerProvider = this.getProvider(ServiceDependencyScanner.class);

        bindListener(new ServiceInterfaceMatcher(), new ServicesListener(servicesModelProvider, scannerProvider));
        bindInterceptor(Matchers.subclassesOf(Service.class), new FinalizeMethodMatcher(), new FinalizeMethodInterceptor());

    }

    protected void bindServicesExecutor() {
        bind(RelatedServicesExecutor.class).to(DefaultRelatedServicesExecutor.class);
    }

    @Override
    protected void registerServices() {
        // There are none
    }

    @Override
    protected void defineDependencies() {
        // There are none
    }

    protected void bindServicesModel() {
        bind(ServicesModel.class).to(DefaultServicesModel.class);
        bind(ServicesClassGraph.class).to(DefaultServicesClassGraph.class);
        bind(ServicesInstanceGraph.class).to(DefaultServicesInstanceGraph.class);
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
        private Provider<ServiceDependencyScanner> scannerProvider;
        private Provider<ServicesModel> servicesModelProvider;

        public ServicesListener(Provider<ServicesModel> servicesModelProvider,
                                Provider<ServiceDependencyScanner> scannerProvider) {
            this.servicesModelProvider = servicesModelProvider;
            this.scannerProvider = scannerProvider;
        }


        @Override
        public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
            InjectionListener<Object> listener = new InjectionListener<Object>() {
                @Override
                public void afterInjection(Object injectee) {
                    // cast is safe - if not, the matcher is wrong
                    Service service = (Service) injectee;
                    //get dependencies from annotations
                    scannerProvider.get()
                                   .scan((Service) injectee);
                    ServicesModel servicesModel = servicesModelProvider.get();
                    servicesModel.addService(service);

                }
            };
            encounter.register(listener);
        }

    }

    private static class FinalizeMethodMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method method) {
            return "finalize".equals(method.getName());
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


}