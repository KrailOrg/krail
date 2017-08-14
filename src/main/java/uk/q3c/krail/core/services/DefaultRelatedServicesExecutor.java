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

import com.google.inject.Inject;
import org.slf4j.Logger;
import uk.q3c.krail.i18n.Translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.*;
import static org.slf4j.LoggerFactory.*;
import static uk.q3c.krail.core.services.RelatedServicesExecutor.Action.*;
import static uk.q3c.krail.core.services.Service.State.*;

/**
 * Default implementation for {@link RelatedServicesExecutor}
 * <p>
 * Created by David Sowerby on 11 Jan 2016
 */
public class DefaultRelatedServicesExecutor implements RelatedServicesExecutor {

    private static Logger log = getLogger(DefaultRelatedServicesExecutor.class);
    private ServicesModel servicesModel;
    private Translate translate;
    private Service service;

    @Inject
    protected DefaultRelatedServicesExecutor(ServicesModel servicesModel, Translate translate) {
        this.servicesModel = servicesModel;
        this.translate = translate;
    }

    public Service getService() {
        return service;
    }

    @Override
    public void setService(Service service) {
        checkNotNull(service);
        this.service = service;
    }


    protected List<Service> dependencies() {
        return servicesModel.findInstanceDependencies(service);
    }

    protected List<Service> dependants() {
        return servicesModel.getInstanceGraph()
                            .findDependants(service, ServicesGraph.Selection.ALWAYS_REQUIRED);
    }


    @Override
    public boolean execute(Action action, Service.Cause cause) {
        checkNotNull(action);
        checkNotNull(cause);
        List<Service> servicesToExecute = (action == START) ? dependencies() : dependants();

        //Execute in parallel
        ExecutorService executor = createStartExecutor(servicesToExecute.size(), action);
        List<Future<ServiceStatus>> futures = execute(executor, servicesToExecute, action, cause);


        //Get result from each Future (get() will block until result returned)


        boolean allActionedSuccessfully = true;
        for (Future<ServiceStatus> future : futures) {
            try {
                ServiceStatus result = future.get();
                if (action == START) {
                    // if an optional dependency did not start we do not care
                    // considered loading dependency types, as isOptional() makes call to servicesModel - hoever, that call is only made if a dependency
                    // doesn't start, and getting all the depenency info takes a bit more work
                    if (result.getState() != RUNNING && !isOptional(result.getService())) {
                        allActionedSuccessfully = false;
                    }
                } else {

                    switch (cause) {
                        case DEPENDENCY_FAILED:
                        case FAILED:
                            if (result.getCause() != Service.Cause.DEPENDENCY_FAILED) {
                                allActionedSuccessfully = false;
                            }
                            break;
                        case DEPENDENCY_STOPPED:
                        case STOPPED:
                            if (result.getCause() != Service.Cause.DEPENDENCY_STOPPED) {
                                allActionedSuccessfully = false;
                            }
                            break;
                        case STARTED:
                        case FAILED_TO_START:
                            throw new ServiceStatusException("Service should not be in this state after a stop()");
                        case FAILED_TO_STOP:
                            allActionedSuccessfully = false;
                            break;
                        default:
                            throw new ServiceStatusException("Service should not be in this state after a stop()");
                    }
                }


            } catch (InterruptedException e) {
                log.error("ServicesModel thread interrupted while waiting for dependency to start");

            } catch (ExecutionException e) {
                log.error("Error occurred in thread spawned to start a service", e);
            }
        }


        closeExecutor(executor, action);
        return allActionedSuccessfully;
    }

    private boolean isOptional(Service dependency) {
        return servicesModel.getClassGraph()
                            .isOptionalDependency(dependency
                                    .getServiceKey(), service.getServiceKey());
    }


    protected List<Future<ServiceStatus>> execute(ExecutorService executor, List<Service> services, Action action, Service.Cause cause) {

        List<Future<ServiceStatus>> futures = new ArrayList<>();

        //Submit each to executor, and hold the Future for it
        for (Service dependency : services) {
            Callable<ServiceStatus> task;
            switch (action) {
                case START:
                    task = dependency::start;
                    break;
                case STOP:
                    Service.Cause causeForDependant;
                    switch (cause) {
                        case STOPPED:
                        case DEPENDENCY_STOPPED:
                            causeForDependant = Service.Cause.DEPENDENCY_STOPPED;
                            break;
                        case FAILED:
                        case DEPENDENCY_FAILED:
                            causeForDependant = Service.Cause.DEPENDENCY_FAILED;
                            break;
                        default:
                            throw new ServiceCauseException("Unexpected cause value of " + cause);
                    }
                    task = () -> dependency.stop(causeForDependant);
                    break;
                default:
                    throw new ServiceActionException("Unknown action to execute");
            }
            Future<ServiceStatus> future = executor.submit(task);
            futures.add(future);
        }
        return futures;
    }


    /**
     * Stops the @{code executor} with appropriate timeouts and logging
     *
     * @param executor the Executor to be shut down.
     * @param action   the action being taken which used the executor
     */
    protected void closeExecutor(ExecutorService executor, Action action) {
        try {
            log.info("Closing Executor after an action to {}, initiated by {}", action.name(), translate.from(service.getNameKey(), Locale.UK));
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
     * Provides a ExecutorService   If you need to use a different thread pool configuration override {@link RelatedServicesExecutor}
     * this method in a sub-class, and bind that sub-class to {@link ServicesModel} in {@link ServicesModule}.
     *
     * @param dependencies the number of services which will be started using this ExecutorService
     * @param action       the action being undertaken, in case a different ExecutorService configuration is required for different actions
     * @return ExecutorService with ThreadPool set
     */
    @SuppressWarnings("UnusedParameters")

    protected ExecutorService createStartExecutor(int dependencies, Action action) {
        return Executors.newWorkStealingPool();
    }


}
