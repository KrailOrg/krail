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
import uk.q3c.util.dag.CycleDetectedException;

import java.util.List;

/**
 * Provides a model of Services and dependencies between them.  Dependencies types are specified by {@link Dependency.Type}.<br><br>
 * The model holds a classGraph and an instanceGraph.<br><br>
 * The developer defines dependencies at class level, using Guice (see {@link AbstractServiceModule}) or {@link Dependency} annotations.  When a Service
 * instance is created by Guice, it is also held by the model in the instance graph, and the model ensures that instances of required dependencies are also
 * available or created.

 * If a dependency is created which causes a loop (Service A depends on B which depends on A), a {@link CycleDetectedException} is thrown
 * <p>
 * Created by David Sowerby on 24/10/15.
 */

public interface ServicesModel {


    /**
     * The {@code dependant} service always depends on {@code dependency}.  Thus:<ol><li>if {@code dependency} does not
     * start,{@code dependant} cannot start</li><li>if {@code dependency} stops or fails after {@code dependant} has
     * started,{@code dependency} stops or fails </li></ol>
     *
     * @param dependant  the Service which depends on {@code dependency}.  Will be added to the graph if not already
     *                   added.
     * @param dependency the Service on which {@code dependant} depends.  Will be added to the graph if not already
     *                   added.
     * @throws CycleDetectedException if a loop is created by forming this dependency
     */
    void alwaysDependsOn(ServiceKey dependant, ServiceKey dependency);

    /**
     * The {@code dependant} service depends on {@code dependency}, but only in order to start - for example, {@code
     * dependency} may just provide some configuration data in order for {@code dependant} to start.  Thus:<ol><li>if
     * {@code dependency} does not start,{@code dependant} cannot start</li><li>if {@code dependency} stops or fails
     * after {@code dependant} has started,{@code dependency} will continue</li></ol>
     *
     * @param dependant  the Service which depends on {@code dependency}.  Will be added to the graph if not already
     *                   added.
     * @param dependency the Service on which {@code dependant} depends.  Will be added to the graph if not already
     *                   added.
     * @throws CycleDetectedException if a loop is created by forming this dependency
     */
    void requiresOnlyAtStart(ServiceKey dependant, ServiceKey dependency);

    /**
     * The {@code dependant} service will attempt to use {@code dependency} if it is available, but will start and
     * continue to run without it.  Thus:<ol><li>if {@code dependency} does not start,{@code dependant} will still
     * start.  Note, however, that {@code dependant} will wait until {@code dependency} has either started or failed
     * before commencing its own start process.</li><li>if {@code dependency} stops or fails after {@code dependant} has
     * started,{@code dependency} will continue</li></ol>
     *
     * @param dependant  the Service which depends on {@code dependency}. Will be added to the graph if not already
     *                   added.
     * @param dependency the Service on which {@code dependant} depends.  Will be added to the graph if not already
     *                   added.
     * @throws CycleDetectedException if a loop is created by forming this dependency
     */
    void optionallyUses(ServiceKey dependant, ServiceKey dependency);


    /**
     * Adds a ServiceKey. Returns true if {@code serviceKey} is added, false if not added (because {@code serviceKey} is already in the graph)
     *
     * @param serviceKey the ServiceKey to add
     */

    boolean addService(ServiceKey serviceKey);


    /**
     * Adds a service instance, and creates instances of dependencies using the class graph as a 'template'.  Returns true if {@code service} is added, false
     * if not added (because {@code service} is already in the graph).
     *
     * @param service the Service to add
     */
    boolean addService(Service service);

    /**
     * Returns true if {@code Service} is contained in the model
     *
     * @param service the service to check for
     */
    boolean contains(Service service);


    /**
     * Returns true if the {@code serviceKey} is contained within the model.  There may not yet be a Service instance associated with the class.
     *
     * @param serviceKey the ServiceKey to look for
     * @return Returns true if the {@code serviceKey} is registered.  There may however not be a Service associated with
     * the key yet.
     */
    boolean contains(ServiceKey serviceKey);

    /**
     * returns an immutable list of currently contained service instances
     *
     * @return an immutable list of currently contained service instances
     */
    ImmutableList<Service> registeredServiceInstances();

    /**
     * Stops all services.  Usually only used during shutdown
     */
    void stopAllServices();

    /**
     * Returns a list of {@link DependencyInstanceDefinition}s describing the dependencies and their relationship with {@code service}.  If you only want the
     * services (and not the dependency information), {@link #findInstanceDependencies} is a bit more efficient.
     *
     * @param service the service for which to obtain dependencies
     * @return list of {@link DependencyInstanceDefinition}s describing the dependencies and their relationship with {@code service}
     */
    List<DependencyInstanceDefinition> findInstanceDependencyDefinitions(Service service);

    ServicesInstanceGraph getInstanceGraph();

    ServicesClassGraph getClassGraph();

    ImmutableList<ServiceKey> registeredServices();

    /**
     * Returns a list of immediate dependencies for {@code service}.  If you also want the dependency type as well, use
     * {@link #findInstanceDependencyDefinitions}
     *
     * @param service the service for which the dependencies are required
     * @return a list of immediate dependencies for {@code service}
     */
    List<Service> findInstanceDependencies(Service service);

    /**
     * Returns a list of immediate dependencies for {@code service}, which are of the type specified by {@code selection}  If you also want the dependency type
     * as well, use {@link #findInstanceDependencyDefinitions}
     *
     * @param service the service for which the dependencies are required
     * @return a list of immediate dependencies for {@code service}
     */
    List<Service> findInstanceDependencies(Service service, ServicesGraph.Selection selection);
}

