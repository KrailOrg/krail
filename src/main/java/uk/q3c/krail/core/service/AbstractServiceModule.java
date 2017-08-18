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

package uk.q3c.krail.core.service;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import uk.q3c.krail.config.ConfigurationException;
import uk.q3c.krail.i18n.I18NKey;

import static com.google.common.base.Preconditions.*;
import static com.google.inject.multibindings.Multibinder.*;

/**
 * A useful base class which can be used where the developer wishes to declare dependencies between Service classes using Guice.
 * Remember to invoke super.configure from the sub-class configure()
 * <p>
 * Created by David Sowerby on 13/11/15.
 */
public abstract class AbstractServiceModule extends AbstractModule {
    TypeLiteral<Class<? extends Service>> serviceClassLiteral;
    private Multibinder<DependencyDefinition> dependencies;
    //other modules may add to this
    private MapBinder<ServiceKey, Service> registeredServices;
    private MapBinder<ServiceKey, Class<? extends Service>> serviceKeyMap;

    @Override
    protected void configure() {
        dependencies = newSetBinder(binder(), DependencyDefinition.class);
        //use TypeLiteral for one parameter have to use it for both
        serviceClassLiteral = new TypeLiteral<Class<? extends Service>>() {
        };
        TypeLiteral<ServiceKey> serviceKeyLiteral = new TypeLiteral<ServiceKey>() {
        };
        registeredServices = MapBinder.newMapBinder(binder(), ServiceKey.class, Service.class);
        serviceKeyMap = MapBinder.newMapBinder(binder(), serviceKeyLiteral, serviceClassLiteral);
        registerServices();
        defineDependencies();
    }


    protected abstract void registerServices();

    protected abstract void defineDependencies();


    /**
     * Define a dependency.  Dependencies added here are injected into {@link ServicesModel}
     */
    @SuppressWarnings("Duplicates")
    protected void addDependency(ServiceKey dependant, ServiceKey dependency, Dependency.Type type) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        checkNotNull(type);
        configCheck();
        dependencies.addBinding()
                    .toInstance(new DependencyDefinition(dependant, dependency, type));
    }

    private void configCheck() {
        if (registeredServices == null || dependencies == null || serviceKeyMap == null) {
            throw new ConfigurationException("MapBinder and MultiBinder fields cannot be null, have you sub-classed " + this.getClass()
                                                                                                                            .getSimpleName() + " but " +
                    "forgotten to call super.configure():");
        }
    }

    @SuppressWarnings("Duplicates")
    protected void addDependency(I18NKey dependant, I18NKey dependency, Dependency.Type type) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        checkNotNull(type);
        configCheck();
        dependencies.addBinding()
                    .toInstance(new DependencyDefinition(dependant, dependency, type));
    }


    /**
     * Each service must be registered so that Guice can instantiate it.  Call this method (or just provide your own bindings) to register a Service.
     *
     * @param serviceKey the class of Service to be registered
     */
    protected void registerService(ServiceKey serviceKey, Class<? extends Service> serviceClass) {
        checkNotNull(serviceKey);
        checkNotNull(serviceClass);
        configCheck();
        registeredServices.addBinding(serviceKey)
                          .to(serviceClass);
        serviceKeyMap.addBinding(serviceKey)
                     .toInstance(serviceClass);
    }

    protected void registerService(I18NKey key, Class<? extends Service> serviceClass) {
        checkNotNull(key);
        checkNotNull(serviceClass);
        configCheck();
        registerService(new ServiceKey(key), serviceClass);
    }
}
