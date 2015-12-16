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
 * Provides a model of dependencies between Services.  Dependencies, of types specified by {@link Dependency.Type}  are
 * defined at {@link Service} class level, but are also held at {@link Service} instance level.  This enables, for example, a dependant
 * to be stopped automatically if one of its dependencies stops. <br><br>
 * Dependencies can be declared either using @{@link Dependency} annotations or by Guice configuration. (
 * ServicesModule} or at runtime through the {@link ServicesGraphRuntimeUserInterface}<br> <br> Note that when a call is
 * made to any method, when a {@link ServiceKey} parameter does not exist in the graph, that {@link ServiceKey} is added
 * to the graph automatically.   For example, a call to {@link #findOptionalDependencies} with a ServiceKey which has
 * not yet been added to the graph, will add the key, and return an empty list.
 * <p>
 * Throws a {@link CycleDetectedException} If a dependency is created which causes a loop (Service A depends on B which
 * depends on A)
 * <p>
 * /** Implementation works in conjunction with {@link AbstractService} to ensure that dependencies between services are
 * managed appropriately.  The dependencies are defined by configuring {@link ServicesModel}.  {@link AbstractService}
 * delegates calls to start() and stop() to this interface, as should any Service implementation which continues to use
 * this interface
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
    void alwaysDependsOn(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency);

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
    void requiresOnlyAtStart(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency);

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
    void optionallyUses(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency);


    /**
     * Adds a ServiceKey. Returns true if {@code serviceKey} is added, false if not added (because {@code serviceKey} is already in the graph)
     *
     * @param serviceKey the ServiceKey to add
     */

    boolean addService(@Nonnull ServiceKey serviceKey);


    /**
     * Adds a service instance, and creates instances of dependencies using the class graph as a 'template'.  Returns true if {@code service} is added, false
     * if not added (because {@code service} is already in the graph).
     *
     * @param service the Service to add
     */
    boolean addService(@Nonnull Service service);

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


//    /**
//     * It is unlikely that a developer would need to call this method directly, as generally the find**** methods are
//     * more relevant.  This method returns 0 or more dependencies as Service instances for the {@code dependant}.
//     *
//     * @param dependant the Service for which dependencies are required.
//     * @return instances of services which {@code dependant} depends on
//     */
//    List<Service> getDependencyInstances(Service dependant);


    /**
     * Starts any dependencies which are required in order to start {@code service}.  These are a union of {@link
     * ServicesModel#alwaysDependsOn (ServiceKey, ServiceKey)} and {@link ServicesModel#requiresOnlyAtStart(ServiceKey,
     * ServiceKey)}.
     *
     * @param dependant the service to start the dependencies for
     * @return true if all required dependencies attain a state of {@link Service.State#STARTED}, false if any
     * dependency fails to do so
     */
    boolean startDependenciesFor(Service dependant);

    /**
     * Stops all dependants which have declared that {@code dependency} must be running in order for them to continue
     * running (see {@link ServicesModel#alwaysDependsOn(ServiceKey, ServiceKey)}).
     *
     * @param dependency       the dependency which requires its dependants to be stopped
     * @param dependencyFailed if true, the dependency has called this method because it failed, if false, the
     *                         dependency has been stopped
     */
    void stopDependantsOf(Service dependency, boolean dependencyFailed);

    /**
     * Stops all services.  Usually only used during shutdown
     */
    void stopAllServices();

    /**
     * Returns a list of {@link DependencyInstanceDefinition}s describing the dependencies and their relationship with {@code service}
     *
     * @param service the service for which to obtain dependencies
     * @return list of {@link DependencyInstanceDefinition}s describing the dependencies and their relationship with {@code service}
     */
    List<DependencyInstanceDefinition> findInstanceDependencies(@Nonnull Service service);

    ServicesInstanceGraph getInstanceGraph();

    ServicesClassGraph getClassGraph();

    Service getInstanceOf(ServiceKey fieldClass);

    ImmutableList<ServiceKey> getRegisteredServices();
}

