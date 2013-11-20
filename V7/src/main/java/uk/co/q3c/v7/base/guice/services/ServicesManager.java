package uk.co.q3c.v7.base.guice.services;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.services.ServicesRegistry.Status;

@Singleton
public class ServicesManager {

	private static final Logger log = LoggerFactory.getLogger(ServicesManager.class);

	private Status status = Status.INITIAL;

	private final ServicesRegistry servicesRegistry;

	@Inject
	public ServicesManager() {
		this.servicesRegistry = new ServicesRegistry();
	}

	/**
	 * Register the service to be managed by the ServiceManager and immediately start it if {@link #start()} has been
	 * already called.
	 * 
	 * @param service
	 */
	public void registerService(Object service) {
		servicesRegistry.register(service);
		if (status == Status.STARTED) {
			startService(service);
		}
	}

	/**
	 * Start all registered services and those that will be added later
	 */
	public void start() {
		assert status != Status.STARTED;
		log.trace("start()");

		for (ServiceData data : servicesRegistry.getServices()) {
			startService(data.getService());
		}
		// even if some services fail to start the overall status is STARTED
		status = Status.STARTED;
	}

	/**
	 * Stop all registered services
	 */
	public void stop() {
		assert status != Status.STOPPED;
		for (ServiceData data : servicesRegistry.getServices()) {
			if (data.getStatus() == Status.STARTED) {
				stopService(data.getService());
			}
		}
		status = Status.STOPPED;
	}

	/**
	 * Start a single service
	 * 
	 * @param service
	 * @return true if started or there is no start method, false if the stop method throws an exception
	 */
	boolean startService(Object service) {
		log.info("Starting service {} using instance {}", service.getClass().getSimpleName(), service);
		ServiceData data = servicesRegistry.getServiceData(service);
		if (data.getStatus() != Status.STARTED) {
			Method start = data.getStartMethod();
			if (start != null) {
				try {
					assert data.getStatus() != Status.STARTED : data.getStatus();
					start.invoke(service, (Object[]) null);
					assert data.getStatus() == Status.STARTED : data.getStatus();
					return true;
				} catch (Exception e) {
					log.error("The start method of the service {} has trown an exception:", service, e);
					assert data.getStatus() == Status.FAILED : data.getStatus();
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Stop a single service
	 * 
	 * @param service
	 * @return true if started or there is no stop method, false if the stop method throws an exception
	 */
	boolean stopService(Object service) {
		log.info("Halting service {} on instance {}", service.getClass().getSimpleName(), service);

		ServiceData data = servicesRegistry.getServiceData(service);
		if (data == null) {
			throw new IllegalStateException("Unable to stop a service that has not been started by te ServiceManager");
		}
		if (data.getStatus() != Status.STARTED) {
			throw new RuntimeException("Unable to stop a service (" + service
					+ ") that is not in STARTED status (actual status: " + data.getStatus() + ")");
		}
		if (data.getStatus() == Status.STARTED) {
			Method stop = data.getStopMethod();
			if (stop != null) {
				try {
					assert data.getStatus() != Status.STOPPED : data.getStatus();
					stop.invoke(service, (Object[]) null);
					assert data.getStatus() == Status.STOPPED : data.getStatus();
					return true;
				} catch (Exception e) {
					log.error("The start method of the service {} has trown an exception:", service, e);
					assert data.getStatus() == Status.FAILED : data.getStatus();
					return false;
				}
			}
		}
		return true;
	}

	void markAs(Object service, Status status) {
		servicesRegistry.getServiceData(service).setStatus(status);
	}

	void finalize(Object service) {
		stopService(service);
	}

	public ServiceData getServiceData(Object service) {
		return servicesRegistry.getServiceData(service);
	}

	public Status getStatus() {
		return status;
	}

	public ServicesRegistry getServicesRegistry() {
		return servicesRegistry;
	}

}