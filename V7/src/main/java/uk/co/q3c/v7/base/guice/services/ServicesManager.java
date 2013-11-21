package uk.co.q3c.v7.base.guice.services;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.WeakHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.services.Service.Status;

@Singleton
public class ServicesManager {

	private static final Logger log = LoggerFactory.getLogger(ServicesManager.class);

	private Status status = Status.INITIAL;

	private final Map<Service, ServiceStatus> services;

	@Inject
	public ServicesManager() {
		this.services = new WeakHashMap<>();
	}

	/**
	 * Register the service to be managed by the ServiceManager and immediately start it if this manager already has a
	 * status of STARTED
	 * 
	 * @param service
	 */
	public void registerService(Service service) {
		log.info("Manually registering service {}", service);
		ServiceStatus status = new ServiceStatus();
		services.put(service, status);
		if (getStatus() == Status.STARTED) {
			startService(service);
		}
	}

	/**
	 * Start all registered services and those that will be added later
	 */
	public void start() {
		if (!(status == Status.STARTED)) {
			log.debug("starting Services Manager and all registered services");

			for (Service service : services.keySet()) {
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

			for (Service service : services.keySet()) {
				stopService(service);
			}

			status = Status.STOPPED;
		}
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
		return status;
	}

	/**
	 * Removes all services - use with care, this does NOT stop the services
	 */
	public void clear() {
		services.clear();

	}

	/**
	 * Get the status of {@code service}
	 * 
	 * @param service
	 * @return
	 */
	public Status getStatus(Service service) {
		ServiceStatus serviceStatus = services.get(service);
		if (serviceStatus == null) {
			return null;
		}
		return serviceStatus.getStatus();
	}

	public void setStatus(Service service, Status status) {
		ServiceStatus serviceStatus = services.get(service);
		checkNotNull(serviceStatus);
		serviceStatus.setStatus(status);

	}

}