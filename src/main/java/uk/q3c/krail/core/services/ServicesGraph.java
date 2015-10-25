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

package uk.q3c.krail.core.services;

import com.google.common.collect.ImmutableList;
import uk.q3c.util.CycleDetectedException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implementation provides a graph for modelling dependencies between instances of Service.   This graph is configured
 * using {@link Dependency} annotations, through the {@link ServicesModule} or at runtime through the {@link ServicesGraphRuntimeUserInterface}<br>
 * <br>
 * Note that when a call is made to any method, when a {@link ServiceKey} parameter does not exist in the graph, that {@link ServiceKey} is added to the graph
 * automatically.   For example, a call to {@link #findOptionalDependencies(ServiceKey)} with a ServiceKey which has not yet been added to the graph, willadd
 * the key, and return an empty list.
 * <p>
 * Throws a {@link CycleDetectedException} If a dependency is created which causes a loop (Service A depends on B which depends on A)
 * <p>
 * Created by David Sowerby on 24/10/15.
 */

public interface ServicesGraph {


    /**
     * Add a {@link Service} to the graph.  Duplicate calls will be ignored
     *
     * @param service
     *         the {@link Service} to add
     */
    void addService(@Nonnull ServiceKey service);

    /**
     * The {@code dependant} service always depends on {@code dependency}.  Thus:<ol><li>if {@code dependency} does not
     * start,{@code dependant} cannot start</li><li>if {@code dependency} stops or fails after {@code dependant} has
     * started,{@code dependency} stops or fails </li></ol>
     *
     * @param dependant
     *         the Service which depends on {@code dependency}.  Will be added to the graph if not already added.
     * @param dependency
     *         the Service on which {@code dependant} depends.  Will be added to the graph if not already added.
     *
     * @throws CycleDetectedException
     *         if a loop is created by forming this dependency
     */
    void alwaysDependsOn(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency);

    /**
     * The {@code dependant} service depends on {@code dependency}, but only in order to start - for example, {@code
     * dependency} may just provide some configuration data in order for {@code dependant} to start.  Thus:<ol><li>if
     * {@code dependency} does not start,{@code dependant} cannot start</li><li>if {@code dependency} stops or fails
     * after {@code dependant} has started,{@code dependency} will continue</li></ol>
     *
     * @param dependant
     *         the Service which depends on {@code dependency}.  Will be added to the graph if not already added.
     * @param dependency
     *         the Service on which {@code dependant} depends.  Will be added to the graph if not already added.
     *
     * @throws CycleDetectedException
     *         if a loop is created by forming this dependency
     */
    void requiresOnlyAtStart(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency);

    /**
     * The {@code dependant} service will attempt to use {@code dependency} if it is available, but will start and
     * continue to run without it.  Thus:<ol><li>if {@code dependency} does not start,{@code dependant} will still
     * start.  Note, however, that {@code dependant} will wait until {@code dependency} has either started or failed
     * before commencing its own start process.</li><li>if {@code dependency} stops or fails after {@code dependant} has
     * started,{@code dependency} will continue</li></ol>
     *
     * @param dependant
     *         the Service which depends on {@code dependency}. Will be added to the graph if not already added.
     * @param dependency
     *         the Service on which {@code dependant} depends.  Will be added to the graph if not already added.
     *
     * @throws CycleDetectedException
     *         if a loop is created by forming this dependency
     */
    void optionallyUses(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency);

    /**
     * Equivalent to {@link #optionallyUses(ServiceKey, ServiceKey)}, {@link #requiresOnlyAtStart(ServiceKey,
     * ServiceKey)} or {@link #alwaysDependsOn(ServiceKey, ServiceKey)} depending on the value of {@code type}
     *
     * @param dependant
     *         the Service which depends on {@code dependency}. Will be added to the graph if not already
     *         added.
     * @param dependency
     *         the Service on which {@code dependant} depends.  Will be added to the graph if not already
     *         added.
     * @param type
     *         the type of {@link Dependency)
     *
     * @throws CycleDetectedException
     *         if a loop is created by forming this dependency
     */
    void addDependency(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency, Dependency.Type type);


    /**
     * Returns a list of all services which are required to be started only to start the {@code dependant} - once the dependant has started, they are no longer
     * required.  To obtain a complete set of dependencies needed to start a dependant, combine the results of this method and {@link
     * #findDependenciesAlwaysRequiredFor}
     *
     * @param dependant
     *         the Service to identify the dependencies for
     *
     * @return a list of all service which must be started before the {@code dependant} can start
     */
    @Nonnull
    List<ServiceKey> findDependenciesOnlyRequiredAtStartFor(@Nonnull ServiceKey dependant);

    /**
     * Returns a list of all services which are optionally started before the {@code dependant} starts.  Note that the {@code dependant} still wiats for
     * optional dependencies to either start or fail, before starting itself.  This is to enable the {@code dependant} to use the state of optional services in
     * its start up logic.
     *
     * @param dependant
     *         the Service to identify the dependencies for
     *
     * @return a list of all services which are required to be started only to start the {@code dependant}
     */
    @Nonnull
    List<ServiceKey> findOptionalDependencies(@Nonnull ServiceKey dependant);


    /**
     * Returns a list of all services which either must be must always be running for {@code dependant} to continue
     * running.
     *
     * @param dependant
     *         the Service to identify the dependencies for
     *
     * @return a list of all services which either must be must always be running for {@code dependant} to continue
     * running.
     */
    @Nonnull
    List<ServiceKey> findDependenciesAlwaysRequiredFor(@Nonnull ServiceKey dependant);

    /**
     * Returns a list of all services which have declared that {@code dependency} must be running in order for them to continue running.
     */
    @Nonnull
    List<ServiceKey> findDependantsAlwaysRequiringDependency(@Nonnull ServiceKey dependency);

    /**
     * Returns a list of all services which have declared that they use {@code dependency} as an optional dependency
     */
    @Nonnull
    List<ServiceKey> findDependantsOptionallyUsingDependency(@Nonnull ServiceKey dependency);

    /**
     * Returns a list of all dependants which have declared that {@code dependency} is only required in order to start the dependant.  The result therefore
     * does not include those returned by {@link #findDependantsAlwaysRequiringDependency(ServiceKey)}
     */
    @Nonnull
    List<ServiceKey> findDependantsRequiringDependencyOnlyToStart(@Nonnull ServiceKey dependency);

    /**
     * Returns a list of {@link Service} instances corresponding to the provided list of {@link ServiceKey}s.  Throws a {@link ServiceKeyException} if there is
     * no
     * Service mapped to any {@link ServiceKey}, but always returns all the mappings that are available.
     *
     * @param serviceKeys
     *         the keys to look up
     *
     * @return Returns a list of {@link Service} of all instances which correspond to an entry in the provided list of {@link ServiceKey}s.
     *
     * @throws ServiceKeyException
     *         if there is no Service mapped to any {@link ServiceKey}
     */
    @Nonnull
    List<Service> servicesForKeys(@Nonnull List<ServiceKey> serviceKeys);

    /**
     * Registers a service, usually as it is constructed, and maps it to its {@link ServiceKey}.  This enables the ServicesGraph implementation to map the
     * statically defined dependencies (through {@link ServiceKey}) to the runtime, instantiated Services
     *
     * @param service
     *         the Service to register
     */
    void registerService(@Nonnull Service service);

    /**
     * Returns true if {@code Service} is registered
     *
     * @param service
     *         the service to check for
     */
    boolean isRegistered(Service service);


    /**
     * Returns true if the {@code serviceKey} is registered.  There may however not be a Service associated with the key yet.
     *
     * @param serviceKey
     *         the ServiceKey to look for
     *
     * @return Returns true if the {@code serviceKey} is registered.  There may however not be a Service associated with the key yet.
     */
    boolean isRegistered(ServiceKey serviceKey);

    /**
     * returns an immutable list of currently registered services
     *
     * @return an immutable list of currently registered services
     */
    ImmutableList<Service> registeredServices();
}

