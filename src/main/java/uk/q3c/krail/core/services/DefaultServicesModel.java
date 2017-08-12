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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

import static com.google.common.base.Preconditions.*;
import static org.slf4j.LoggerFactory.*;
import static uk.q3c.krail.core.services.Dependency.Type.*;
import static uk.q3c.krail.core.services.ServicesGraph.Selection.*;

/**
 * Created by David Sowerby on 16 Dec 2015
 */
@Singleton
@ThreadSafe
public class DefaultServicesModel implements ServicesModel {


    private static Logger log = getLogger(DefaultServicesModel.class);
    private final ServicesClassGraph classGraph;
    private final ServicesInstanceGraph instanceGraph;
    private Map<ServiceKey, Provider<Service>> registeredServices;

    @Inject
    public DefaultServicesModel(Set<DependencyDefinition> configuredDependencies, ServicesClassGraph classGraph, ServicesInstanceGraph instanceGraph,
                                Map<ServiceKey, Provider<Service>> registeredServices) {
        this.classGraph = classGraph;
        this.instanceGraph = instanceGraph;
        this.registeredServices = registeredServices;
        processConfiguredDependencies(configuredDependencies);
    }

    /**
     * Places an entry for every registered service into the class graph.  Reads dependency definitions from set provided via Guice and creates dependencies
     *
     * @param configuredDependencies dependency definitions provided via Guice
     */
    private void processConfiguredDependencies(Set<DependencyDefinition> configuredDependencies) {
        configuredDependencies.forEach(this::createDependency);
    }

    @Override
    public void alwaysDependsOn(ServiceKey dependant, ServiceKey dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        classGraph.createDependency(dependant, dependency, Dependency.Type.ALWAYS_REQUIRED);
    }


    private void createDependency(DependencyDefinition dependencyDefinition) {
        classGraph.createDependency(dependencyDefinition.getDependant(), dependencyDefinition.getDependency(), dependencyDefinition.getType());
    }

    @Override
    public void requiresOnlyAtStart(ServiceKey dependant, ServiceKey dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        classGraph.createDependency(dependant, dependency, REQUIRED_ONLY_AT_START);
    }

    @Override
    public void optionallyUses(ServiceKey dependant, ServiceKey dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        classGraph.createDependency(dependant, dependency, Dependency.Type.OPTIONAL);
    }

    @Override
    public boolean addService(ServiceKey serviceClass) {
        checkNotNull(serviceClass);
        return classGraph.addService(serviceClass);
    }

    @Override
    public synchronized boolean addService(Service service) {
        checkNotNull(service);

        //don't try and add it twice
        if (!instanceGraph.addService(service)) {
            return false;
        }
        //it's a new entry so we need to create dependencies as defined by class graph
        createInstanceDependencies(service);

        return true;
    }

    private void createInstanceDependencies(Service service) {

        List<ServiceKey> classDependencies = classGraph.findDependencies(service.getServiceKey(), ALL);
        classDependencies.forEach(classDependency -> {
            Service instance = getInstanceOf(classDependency);
            Optional<ServiceEdge> edge = classGraph.getEdge(service.getServiceKey(), classDependency);
            if (edge.isPresent()) {
                //will also add the service if not already contained
                instanceGraph.createDependency(service, instance, edge.get()
                                                                      .getType());
            }
            createInstanceDependencies(instance);
        });
    }

    @Override
    public boolean contains(Service service) {
        checkNotNull(service);
        return instanceGraph.contains(service);
    }

    @Override
    public boolean contains(ServiceKey serviceClass) {
        return classGraph.contains(serviceClass);
    }


    @Override
    public ImmutableList<Service> registeredServiceInstances() {
        return instanceGraph.getServices();
    }




    @Override
    public synchronized List<DependencyInstanceDefinition> findInstanceDependencyDefinitions(Service service) {
        checkNotNull(service);
        List<Service> instanceDependencies = instanceGraph.findDependencies(service, ALL);
        List<DependencyInstanceDefinition> definitions = new ArrayList<>();
        for (Service instanceDependency : instanceDependencies) {
            Optional<ServiceEdge> edge = instanceGraph.getEdge(service, instanceDependency);
            if (edge.isPresent()) {
                definitions.add(new DependencyInstanceDefinition(instanceDependency, edge.get()
                                                                                         .getType()));
            } else {
                log.error("Very strange - edge must be present in order to have retrieved the dependencies.  This should be impossible");
            }
        }
        return definitions;
    }

    @Override
    public ServicesInstanceGraph getInstanceGraph() {
        return instanceGraph;
    }

    @Override
    public ServicesClassGraph getClassGraph() {
        return classGraph;
    }

    private Service getInstanceOf(ServiceKey serviceKey) {
        checkNotNull(serviceKey);
        Provider<? extends Service> provider = registeredServices.get(serviceKey);
        if (provider != null) {
            Service instance = provider.get();
            instanceGraph.addService(instance);
            log.debug("returning instance for {}", serviceKey);
            return instance;
        } else {
            throw new ServiceRegistrationException("no service has been registered for " + serviceKey + ", have you forgotten to register a service in a " +
                    "Guice " +
                    "module, or forgotten to add a Guice module to the BindingManager?" +
                    ' ');
        }
    }

    @Override
    public ImmutableList<ServiceKey> registeredServices() {
        return classGraph.getServices();
    }

    @Override
    public List<Service> findInstanceDependencies(Service service) {
        return instanceGraph.findDependencies(service, ALL);
    }

    @Override
    public List<Service> findInstanceDependencies(Service service, ServicesGraph.Selection selection) {
        return instanceGraph.findDependencies(service, selection);
    }

    @Override
    public synchronized void stopAllServices() {
        log.info("Stopping all services");
        registeredServiceInstances()
                .forEach(Service::stop);

    }


}


