package uk.q3c.krail.core.services;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import uk.q3c.krail.i18n.Translate;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.q3c.krail.core.services.Dependency.Type.REQUIRED_ONLY_AT_START;
import static uk.q3c.krail.core.services.Service.State.STARTED;
import static uk.q3c.krail.core.services.ServicesGraph.Selection.ALL;

/**
 * Created by David Sowerby on 16 Dec 2015
 */
@Singleton
public class DefaultServicesModel implements ServicesModel {


    private static Logger log = getLogger(DefaultServicesModel.class);
    private Map<ServiceKey, Provider<Service>> registeredServices;

    private Translate translate;
    private ServicesClassGraph classGraph;
    private ServicesInstanceGraph instanceGraph;

    @Inject
    public DefaultServicesModel(Set<DependencyDefinition> configuredDependencies, Translate
            translate, ServicesClassGraph classGraph, ServicesInstanceGraph instanceGraph, Map<ServiceKey, Provider<Service>> registeredServices) {
        this.translate = translate;
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
    public void alwaysDependsOn(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        classGraph.createDependency(dependant, dependency, Dependency.Type.ALWAYS_REQUIRED);
    }


    private void createDependency(DependencyDefinition dependencyDefinition) {
        classGraph.createDependency(dependencyDefinition.getDependant(), dependencyDefinition.getDependency(), dependencyDefinition.getType());
    }

    @Override
    public void requiresOnlyAtStart(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        classGraph.createDependency(dependant, dependency, REQUIRED_ONLY_AT_START);
    }

    @Override
    public void optionallyUses(@Nonnull ServiceKey dependant, @Nonnull ServiceKey dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        classGraph.createDependency(dependant, dependency, Dependency.Type.OPTIONAL);
    }

    @Override
    public boolean addService(@Nonnull ServiceKey serviceClass) {
        checkNotNull(serviceClass);
        return classGraph.addService(serviceClass);
    }

    @Override
    public boolean addService(@Nonnull Service service) {
        checkNotNull(service);

        //don't try and add it twice
        if (!instanceGraph.addService(service)) {
            return false;
        }
        //it's a new entry so we need to create dependencies as defined by class graph
        List<ServiceKey> classDependencies = classGraph.findDependencies(service.getServiceKey(), ALL);
        classDependencies.forEach(classDependency -> {
            Service instance = getInstanceOf(classDependency);
            instanceGraph.addService(instance);
            Optional<ServiceEdge> edge = classGraph.getEdge(service.getServiceKey(), classDependency);
            if (edge.isPresent()) {
                instanceGraph.createDependency(service, instance, edge.get()
                                                                      .getType());
            }
        });

        return true;
    }

    @Override
    public boolean contains(@Nonnull Service service) {
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
    public boolean startDependenciesFor(@Nonnull Service dependant) {
        checkNotNull(dependant);
        List<ServiceKey> dependencyServiceClasses = classGraph.findDependencies(dependant.getServiceKey(), ALL);


        //instantiate each dependency that is missing.  These should be lightweight and therefore quick to create
        //scope may vary

        List<Service> servicesToStart = new ArrayList<>();
        List<Service> existingDependencyInstances = instanceGraph.findDependencies(dependant, ALL);
        for (ServiceKey dependencyServiceKey : dependencyServiceClasses) {

            Service dependency = null;

            // if there is already an instance, re-use it
            for (Service existingInstance : existingDependencyInstances) {
                if (existingInstance.getServiceKey()
                                    .equals(dependencyServiceKey)) {
                    dependency = existingInstance;
                    break;
                }
            }

            // if there is no instance, get one
            if (dependency == null) {
                dependency = getInstanceOf(dependencyServiceKey);
            }
            //whether new or existing instance, add it to the list for starting
            //if it is already started, another call to start() will be ignored anyway
            servicesToStart.add(dependency);
        }


        //Call service.start() for all dependencies in parallel
        ExecutorService executor = createStartExecutor(servicesToStart.size());
        List<Future<ServiceStatus>> futures = startDependencies(executor, servicesToStart);


        //Get result from each Future (get() will block until result returned)
        //Optional results are ignored, otherwise only start dependant if all dependencies started successfully
        boolean allRequiredDependenciesStarted = true;
        for (Future<ServiceStatus> future : futures) {
            try {
                ServiceStatus result = future.get();
                if (!classGraph.isOptionalDependency(result.getService()
                                                           .getServiceKey(), dependant.getServiceKey())) {

                    if (result.getState() != STARTED) {
                        allRequiredDependenciesStarted = false;
                    }
                }
            } catch (InterruptedException e) {
                log.error("ServicesModel thread interrupted while waiting for dependency to start");

            } catch (ExecutionException e) {
                log.error("Error occurred in thread spawned to start a service", e);
            }
        }


        closeExecutor(executor, "startDependenciesFor", dependant);


        return allRequiredDependenciesStarted;
    }


    protected List<Future<ServiceStatus>> startDependencies(ExecutorService executor, List<Service> services) {

        List<Future<ServiceStatus>> futures = new ArrayList<>();

        //Submit each to executor, and hold the Future for it
        for (Service dependency : services) {
            Callable<ServiceStatus> task = dependency::start;
            Future<ServiceStatus> future = executor.submit(task);
            futures.add(future);
        }
        return futures;
    }


    /**
     * Stops the @{code executor} with appropriate timeouts and logging
     *
     * @param executor   the Executor to be shut down.
     * @param caller     the method making the call, purely for logging
     * @param instigator the Service which caused the call to be made, purely for logging
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
     * this method in a sub-class, and bind that sub-class to {@link ServicesModel} in {@link ServicesModule}.
     *
     * @param servicesToStart the number of services which will be started using this ThreadPool
     * @return ExecutorService with ThreadPool set
     */
    @SuppressWarnings("UnusedParameters")
    @Nonnull
    protected ExecutorService createStartExecutor(int servicesToStart) {
        return Executors.newWorkStealingPool();
    }

    /**
     * Uses the #instanceGraph to identify dependants of {@code dependency}, and stops or fails the dependants as appropriate
     *
     * @param dependency       the dependency which requires its dependants to be stopped
     * @param dependencyFailed if true, the dependency failed, otherwise the dependency stopped
     */
    @Override
    public void stopDependantsOf(@Nonnull Service dependency, boolean dependencyFailed) {
        checkNotNull(dependency);
        List<Service> dependants = instanceGraph.findDependants(dependency, ServicesGraph.Selection.ALWAYS_REQUIRED);
        ExecutorService executor = createStopExecutor(dependants.size());
        List<Future<ServiceStatus>> futures = new ArrayList<>();

        //Submit each to executor, and hold the Future for it
        for (Service dependant : dependants) {
            Callable<ServiceStatus> task = () -> {
                if (dependencyFailed) {
                    return dependant.stop(Service.State.DEPENDENCY_FAILED);
                } else {
                    return dependant.stop(Service.State.DEPENDENCY_STOPPED);
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
                log.error("ServicesModel thread interrupted while waiting for dependency to start");

            } catch (ExecutionException e) {
                log.error("Error occurred in thread spawned to start a service", e);
            }
        }
        closeExecutor(executor, "StopDependantsOf", dependency);
    }

    /**
     * Provides a ExecutorService for use with {@link #stopDependantsOf}.  If you need to use a different thread pool configuration override this method in a
     * sub-class, and bind that sub-class to {@link ServicesModel} in {@link ServicesModule}.
     *
     * @param servicesToStop the number of services which will be started using this ThreadPool
     * @return ExecutorService with ThreadPool set
     */
    @SuppressWarnings("UnusedParameters")
    @Nonnull
    protected ExecutorService createStopExecutor(int servicesToStop) {
        return Executors.newWorkStealingPool();
    }

    @Override
    public void stopAllServices() {
        log.info("Stopping all services");
        registeredServiceInstances()
                .forEach(Service::stop);
    }

    @Override
    public List<DependencyInstanceDefinition> findInstanceDependencies(@Nonnull Service service) {
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

    @Override
    public Service getInstanceOf(@Nonnull ServiceKey serviceKey) {
        checkNotNull(serviceKey);
        Provider<? extends Service> provider = registeredServices.get(serviceKey);
        if (provider != null) {
            Service instance = provider.get();
            instanceGraph.addService(instance);
            log.debug("returning instance for {}", serviceKey);
            return instance;
        } else {
            throw new ServiceRegistrationException("no service has been registered for " + serviceKey);
        }
    }

    @Override
    public ImmutableList<ServiceKey> getRegisteredServices() {
        return classGraph.getServices();
    }


}


