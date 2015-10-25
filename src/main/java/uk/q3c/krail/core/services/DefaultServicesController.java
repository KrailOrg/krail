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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.i18n.Translate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static uk.q3c.krail.core.services.Service.State.*;

/**
 * Default implementation for ServicesController
 * <p>
 * Created by David Sowerby on 24/10/15.
 */
@Singleton
public class DefaultServicesController implements ServicesController {
    private static Logger log = LoggerFactory.getLogger(DefaultServicesController.class);

    private final ServicesGraph servicesGraph;
    private Translate translate;

    @Inject
    protected DefaultServicesController(ServicesGraph servicesGraph, Translate translate) {
        this.servicesGraph = servicesGraph;
        this.translate = translate;
    }


    @Override
    public boolean startDependenciesFor(Service dependant) {
        checkNotNull(dependant);

        //collect all the optional & required dependencies (they will be started in parallel, so order is irrelevant)
        final List<ServiceKey> serviceKeys = servicesGraph.findOptionalDependencies(dependant.getServiceKey());

        // we need dependencies which are always required, and those only required at start
        List<ServiceKey> requiredServiceKeys = servicesGraph.findDependenciesOnlyRequiredAtStartFor(dependant.getServiceKey());
        requiredServiceKeys.addAll(servicesGraph.findDependenciesAlwaysRequiredFor(dependant.getServiceKey()));
        serviceKeys.addAll(requiredServiceKeys);

        List<Service> services = lookupServiceKeys(serviceKeys);
        ExecutorService executor = createStartExecutor(services.size());
        List<Future<ServiceStatus>> futures = new ArrayList<>();

        //Submit each to executor, and hold the Future for it
        for (Service dependency : services) {
            Callable<ServiceStatus> task = dependency::start;
            Future<ServiceStatus> future = executor.submit(task);
            futures.add(future);
        }

        //Get result from each Future (get() will block until result returned)
        //Optional results are ignored, otherwise only start dependant if all dependencies started successfully
        boolean allRequiredDependenciesStarted = true;
        for (Future<ServiceStatus> future : futures) {
            try {
                ServiceStatus result = future.get();
                if (requiredServiceKeys.contains(result.getServiceKey())) {
                    if (result.getState() != STARTED) {
                        allRequiredDependenciesStarted = false;
                    }
                }
            } catch (InterruptedException e) {
                log.error("ServicesController thread interrupted while waiting for dependency to start");

            } catch (ExecutionException e) {
                log.error("Error occurred in thread spawned to start a service", e);
            }
        }


        closeExecutor(executor, "startDependenciesFor", dependant);


        return allRequiredDependenciesStarted;
    }

    /**
     * Stops the @{code executor} with appropriate timeouts and logging
     *
     * @param executor
     *         the Executor to be shut down.
     * @param caller
     *         the method making the call, purely for logging
     * @param instigator
     *         the Service which caused the call to be made, purley for logging
     */
    protected void closeExecutor(ExecutorService executor, String caller, Service instigator) {
        try {
            log.info("Closing Executor call via {}, initiated by {}", caller, translate.from(instigator.getNameKey(), Locale.UK));
            log.debug("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while shutting down Executor");
        } finally {
            if (!executor.isTerminated()) {
                log.error("forcing shutdown");
            }
            executor.shutdownNow();
            log.info("Services Executor shutdown finished");
        }
    }

    /**
     * Provides a ExecutorService for use with {@link #startDependenciesFor(Service)}.  If you need to use a different thread pool configuration override
     * this method in a sub-class, and bind that sub-class to {@link ServicesController} in {@link ServicesModule}.
     *
     * @param servicesToStart
     *         the number of services which will be started using this ThreadPool
     *
     * @return ExecutorService with ThreadPool set
     */
    @Nonnull
    protected ExecutorService createStartExecutor(int servicesToStart) {
        return Executors.newWorkStealingPool();
    }

    @Nonnull
    private List<Service> lookupServiceKeys(List<ServiceKey> serviceKeys) {
        return servicesGraph.servicesForKeys(serviceKeys);
    }

    @Override
    public void stopDependantsOf(@Nonnull Service dependency, boolean dependencyFailed) {
        checkNotNull(dependency);
        final List<ServiceKey> serviceKeys = servicesGraph.findDependantsAlwaysRequiringDependency(dependency.getServiceKey());
        List<Service> services = lookupServiceKeys(serviceKeys);
        ExecutorService executor = createStopExecutor(services.size());
        List<Future<ServiceStatus>> futures = new ArrayList<>();

        //Submit each to executor, and hold the Future for it
        for (Service dependant : services) {
            Callable<ServiceStatus> task = () -> {
                if (dependencyFailed) {
                    return dependant.stop(DEPENDENCY_FAILED);
                } else {
                    return dependant.stop(DEPENDENCY_STOPPED);
                }
            };
            Future<ServiceStatus> future = executor.submit(task);
            futures.add(future);
        }

        //wait until all threads complete
        for (Future<ServiceStatus> future : futures) {
            try {
                // just block until task complete
                future.get();
            } catch (InterruptedException e) {
                log.error("ServicesController thread interrupted while waiting for dependency to start");

            } catch (ExecutionException e) {
                log.error("Error occurred in thread spawned to start a service", e);
            }
        }
        closeExecutor(executor, "StopDependantsOf", dependency);
    }

    /**
     * Provides a ExecutorService for use with {@link #stopDependantsOf}.  If you need to use a different thread pool configuration override this method in a
     * sub-class, and bind that sub-class to {@link ServicesController} in {@link ServicesModule}.
     *
     * @param servicesToStart
     *         the number of services which will be started using this ThreadPool
     *
     * @return ExecutorService with ThreadPool set
     */
    @Nonnull
    protected ExecutorService createStopExecutor(int servicesToStart) {
        return Executors.newWorkStealingPool();
    }

    @Override
    public void stopAllServices() {
        log.info("Stopping all services");
        servicesGraph.registeredServices()
                     .forEach(Service::stop);
    }
}
