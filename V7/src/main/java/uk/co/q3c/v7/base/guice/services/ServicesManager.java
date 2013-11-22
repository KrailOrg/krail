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
package uk.co.q3c.v7.base.guice.services;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.CycleDetectedException;
import uk.co.q3c.util.DynamicDAG;
import uk.co.q3c.v7.base.guice.services.Service.Status;

/**
 * Manages instances of {@link Service} implementations. Note that only one instance per service class can be managed.
 * Services are registered either automatically though AOP code contained {@link ServicesManagerModule}, or manually via
 * the {@link #registerService(Service)} method.
 * <p>
 * Services define their dependencies, and these are assembled into a dependency graph to ensure that services are
 * started up in the correct order.
 * <p>
 * Although it may not be strictly necessary, services are also stopped in reverse dependency order.
 * 
 * Acknowledgement: developed from code contributed by https://github.com/lelmarir
 */
@Singleton
public class ServicesManager {

	private static final Logger log = LoggerFactory.getLogger(ServicesManager.class);

	private Status dependencyStatus = Status.INITIAL;

	// private final Map<Service, ServiceStatus> services;
	private final Map<Class<? extends Service>, ServiceStatus> serviceClasses;

	private final DynamicDAG<Class<? extends Service>> dependencyGraph;

	@Inject
	public ServicesManager() {
		// this.services = new WeakHashMap<>();
		this.serviceClasses = new WeakHashMap<>();
		dependencyGraph = new DynamicDAG<>();
	}

	/**
	 * Register the service to be managed by the ServiceManager and immediately start it if this manager already has a
	 * status of STARTED. Will throw a {@link CycleDetectedException} if the service being registered specifies a
	 * dependency which would cause a loop in the dependency graph. Only one instance per Service class can be managed -
	 * if an attempt is made to register a second instance, a {@link ServiceRegistrationException} is thrown
	 * 
	 * @exception ServiceRegistrationException
	 *                if an instance of the same class as service has already been registered
	 * @exception CycleDetectedException
	 *                if adding this service would cause a dependency loop
	 * @param service
	 */
	public void registerService(Service service) {
		log.info("registering service {}", service);
		// even without dependencies, needs to be in the graph
		dependencyGraph.addNode(service.getClass());

		// get dependencies and apply to graph
		Set<Class<? extends Service>> dependencies = ServiceUtils.extractDependencies(service);
		for (Class<? extends Service> dependency : dependencies) {
			dependencyGraph.addChild(dependency, service.getClass());
		}

		// keep the service maps as well as the graph
		Class<? extends Service> serviceClass = service.getClass();
		if (serviceClasses.containsKey(serviceClass)) {
			throw new ServiceRegistrationException("An instance of " + serviceClass.getName()
					+ "has already been registered");
		} else {
			ServiceStatus status = new ServiceStatus();
			status.setService(service);
			serviceClasses.put(serviceClass, status);
		}

		// services.put(service, status);

		// if this manager has already been started, start the service immediately
		if (getStatus() == Status.STARTED) {
			startService(service);
		}
	}

	/**
	 * Returns the combined annotation and method based dependencies specified by {@code Service}, provided
	 * {@code Service} is registered with this {@link ServicesManager}. You can achieve the same by using
	 * {@link ServiceUtils#extractDependencies(Service)}, which would not require the {@code Service} to be registered.
	 * 
	 * @param service
	 * @return
	 */
	public Collection<Class<? extends Service>> getDependencies(Service service) {
		Class<? extends Service> serviceNode = dependencyGraph.getNode(service.getClass());
		if (serviceNode == null) {
			return null;
		}
		return dependencyGraph.getGraph().getPredecessors(serviceNode);
	}

	/**
	 * Start all registered services in the order specified by {@link #dependencyGraph}. Returns a list of Service
	 * classes in the order in which they were started. (This would return an empty list if an attempt was made to start
	 * when already started)
	 * 
	 * @return
	 */
	public List<Class<? extends Service>> start() {
		List<Class<? extends Service>> started = new ArrayList<>();
		if (!(dependencyStatus == Status.STARTED)) {
			log.debug("starting Services Manager and all registered services");

			Deque<Class<? extends Service>> waiting = new ArrayDeque<>(serviceClasses.keySet());
			List<Class<? extends Service>> failed = new ArrayList<>();

			// seed with root - it won't have any dependencies
			Class<? extends Service> candidate = dependencyGraph.getRoot();

			while (candidate != null) {

				// if candidate has all predecessors started, process it, otherwise put it to end of queue
				if (allPredecessorsStarted(candidate)) {
					boolean candidateStarted = startService(serviceClasses.get(candidate).getService());
					if (candidateStarted) {
						started.add(candidate);
					} else {
						failed.add(candidate);
					}
					waiting.remove(candidate);
				} else {
					waiting.addLast(candidate);
				}
				// take next candidate from the start of the queue
				candidate = waiting.pollFirst();
			}
		}
		// even if some or all services fail to start the status of this manager is STARTED
		dependencyStatus = Status.STARTED;
		return started;

	}

	/**
	 * Returns true if all the predecessors (in dependency terms) are either STARTED, or there are no predecessors
	 * 
	 * @param candidate
	 * @return
	 */
	private boolean allPredecessorsStarted(Class<? extends Service> candidate) {
		Collection<Class<? extends Service>> predecessors = dependencyGraph.getGraph().getPredecessors(candidate);
		for (Class<? extends Service> predecessor : predecessors) {
			if (serviceClasses.get(predecessor).getStatus() != Status.STARTED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Stop all registered services. Even if some services fail to stop correctly the overall status is STOPPED
	 */
	public void stop() {
		List<Class<? extends Service>> stopped = new ArrayList<>();
		if (dependencyStatus == Status.STARTED) {
			log.debug("stopping all registered services and Services Manager");

			Deque<Class<? extends Service>> waiting = new ArrayDeque<>(serviceClasses.keySet());
			List<Class<? extends Service>> failed = new ArrayList<>();

			// seed with root - it won't have any dependencies
			Class<? extends Service> candidate = dependencyGraph.getRoot();

			while (candidate != null) {

				// if candidate has all predecessors started, process it, otherwise put it to end of queue
				if (allSuccessorsStoppedOrFailedToStop(candidate)) {
					boolean candidateStopped = stopService(serviceClasses.get(candidate).getService());
					if (candidateStopped) {
						stopped.add(candidate);
					} else {
						failed.add(candidate);
					}
					waiting.remove(candidate);
				} else {
					waiting.addLast(candidate);
				}
				// take next candidate from the start of the queue
				candidate = waiting.pollFirst();
			}

			dependencyStatus = Status.STOPPED;
		}
	}

	/**
	 * Returns true if all the successors (in dependency terms) are either STOPPED or FAILED_TO_STOP, or there are no
	 * successors
	 * 
	 * @param candidate
	 * @return
	 */
	private boolean allSuccessorsStoppedOrFailedToStop(Class<? extends Service> candidate) {
		Collection<Class<? extends Service>> successors = dependencyGraph.getGraph().getSuccessors(candidate);
		for (Class<? extends Service> successor : successors) {
			dependencyStatus = serviceClasses.get(successor).getStatus();
			if ((dependencyStatus != Status.STOPPED) && (dependencyStatus != Status.FAILED_TO_STOP)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Start a single service
	 * 
	 * @param service
	 * @return true if and only if the resulting status is {@link Status#STARTED}
	 */
	boolean startService(Service service) {
		checkNotNull(service);
		log.info("starting service {}", service.getName());
		service.start();
		return getStatus(service) == Status.STARTED;
	}

	/**
	 * Stop a single service
	 * 
	 * @param service
	 * @return true if and only if the resulting status is {@link Status#STOPPED}
	 */
	boolean stopService(Service service) {
		checkNotNull(service);
		log.info("stopping service {}", service.getName());
		service.stop();
		return getStatus(service) == Status.STOPPED;
	}

	/**
	 * Get the status if this {@link ServicesManager}
	 * 
	 * @return
	 */
	public Status getStatus() {
		return dependencyStatus;
	}

	/**
	 * Removes all services - use with care, this does NOT stop the services
	 */
	public void clear() {
		serviceClasses.clear();

	}

	/**
	 * Get the status of {@code service}
	 * 
	 * @param service
	 * @return
	 */
	public Status getStatus(Service service) {
		ServiceStatus serviceStatus = serviceClasses.get(service.getClass());
		if (serviceStatus == null) {
			return null;
		}
		return serviceStatus.getStatus();
	}

	public void setStatus(Service service, Status status) {
		ServiceStatus serviceStatus = serviceClasses.get(service.getClass());
		checkNotNull(serviceStatus);
		serviceStatus.setStatus(status);

	}

}