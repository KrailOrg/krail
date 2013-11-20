package uk.co.q3c.v7.base.guice.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.services.Service.Status;

@Singleton
public class ServicesManager {

	private static final Logger log = LoggerFactory.getLogger(ServicesManager.class);

	private Status status = Status.INITIAL;

	private final List<Service> services;

	@Inject
	public ServicesManager() {
		this.services = new ArrayList<>();
	}

	/**
	 * Register the service to be managed by the ServiceManager and immediately start it if this manager already has a
	 * status of STARTED
	 * 
	 * @param service
	 */
	public void registerService(Service service) {
		services.add(service);
		if (status == Status.STARTED) {
			startService(service);
		}
	}

	/**
	 * Start all registered services and those that will be added later
	 */
	public void start() {
		if (!(status == Status.STARTED)) {
			log.debug("starting Services Manager and all registered services");

			for (Service service : services) {
				startService(service);
			}
			// even if some services fail to start the overall status is STARTED
			status = Status.STARTED;
		}
	}

	/**
	 * Stop all registered services. Even if some services fail to stop correctly the overall status is STOPPED
	 */
	public void stop() {
		if (status == Status.STARTED) {
			log.debug("stopping all registered services and Services Manager");

			for (Service service : services) {
				stopService(service);
			}

			status = Status.STOPPED;
		}
	}

	/**
	 * Start a single service
	 * 
	 * @param service
	 * @return true if started, false if the start method throws an exception
	 */
	boolean startService(Service service) {

		log.info("Starting service {}", service.getName());
		try {
			service.start();
			return true;
		} catch (Exception e) {
			log.error("The start method of the service {} has thrown an exception:", service.getName(), e);
			return false;
		}

	}

	/**
	 * Stop a single service
	 * 
	 * @param service
	 * @return true if stopped, false if the stop method throws an exception
	 */
	boolean stopService(Service service) {
		log.info("Stopping service {}", service.getName());
		try {
			service.stop();
			return true;
		} catch (Exception e) {
			log.error("The stop method of the service {} has thrown an exception:", service.getName(), e);
			return false;
		}
	}

	public Status getStatus() {
		return status;
	}

	/**
	 * Removes all services - use with care, this does NOT stop the services
	 */
	public void clear() {
		services.clear();

	}

}