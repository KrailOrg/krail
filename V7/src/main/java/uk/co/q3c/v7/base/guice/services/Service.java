package uk.co.q3c.v7.base.guice.services;

/**
 * Implement this interface to provide a service you want to manage through the {@link ServicesManager}. There is no
 * need to explicitly register the service with ServicesManager - this will happen automatically during system
 * initialisation (although services can also be added after the system starts). This interface is supported by AOP code
 * in the {@link ServicesManagerModule}.
 * <p>
 * Only one instance of a {@link Service} implementation is supported
 * 
 * @author David Sowerby
 * 
 */
public interface Service {

	public enum Status {
		INITIAL, STARTED, STOPPED, FAILED
	}

	/**
	 * Implement this to start your service. There is no need to interact with the {@link ServicesManager} - that is
	 * taken care of by AOP provided by the {@link ServicesManagerModule}.
	 */
	void start();

	/**
	 * Implement this to stop your service. There is no need to interact with the {@link ServicesManager} - that is
	 * taken care of by AOP provided by the {@link ServicesManagerModule}. In some cases, there may not be any need to
	 * explicitly stop a service, in which case the implementation can be left as an empty method (AOP code will of
	 * course still be applied)
	 */
	void stop();

	Status getStatus();

	String getName();
}
