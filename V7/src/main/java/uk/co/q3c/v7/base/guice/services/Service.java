package uk.co.q3c.v7.base.guice.services;

import java.util.Set;

/**
 * Implement this interface to provide a service you want to manage through the {@link ServicesManager}. There is no
 * need to explicitly register the service with ServicesManager - this will happen automatically during system
 * initialisation (although services can also be added after the system starts). This interface is supported by AOP code
 * in the {@link ServicesManagerModule}. There is no need to retain status within the implementation, it can be accessed
 * by calling {@link ServicesManager#getStatus(Service)}
 * <p>
 * Although normally services are registered automatically, if you wish to prevent that, annotate the Service
 * implementation class with {@link NoAutoRegister}
 * 
 * 
 * @author David Sowerby
 * 
 */
public interface Service {

	public enum Status {
		INITIAL, STARTED, STOPPED, FAILED_TO_START, FAILED_TO_STOP, PARTIAL
	}

	/**
	 * Implement this to start your service. There is no need to interact with the {@link ServicesManager} - that is
	 * taken care of by AOP provided by the {@link ServicesManagerModule}. The implementation should return a status
	 * appropriate to the service being represented, and the AOP code will retain that status in the
	 * {@link ServicesManager}. The AOP code will also catch any exceptions thrown and set the status to
	 * {@link Status#FAILED_TO_START}
	 */
	Status start();

	/**
	 * Implement this to stop your service. There is no need to interact with the {@link ServicesManager} - that is
	 * taken care of by AOP provided by the {@link ServicesManagerModule}. In some cases, there may not be any need to
	 * explicitly stop a service, in which case the implementation should simply return {@link Status#STOPPED}. The AOP
	 * code will still be applied and update the {@link ServicesManager}. The AOP code will also catch any exceptions
	 * thrown and set status to {@link Status#FAILED_TO_STOP}.
	 */
	Status stop();

	String getName();

	/**
	 * Identifies other service which need to be running in order for this service to work. These can also be defined in
	 * the {@link DependsOnServices} annotation, but this method is provided in case logic is needed to determine which
	 * services are required. Use whichever suits you best, you can even declare dependencies in both and the results
	 * will be merged.
	 * 
	 * @return
	 */
	Set<Class<? extends Service>> getDependencies();

	/**
	 * Provides an identifier for the ServiceInstance, unique to the application
	 * 
	 * @return application unique identifier
	 */
	String serviceId();
}
