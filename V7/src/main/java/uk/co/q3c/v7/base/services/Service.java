package uk.co.q3c.v7.base.services;

import com.google.inject.Provider;

/**
 * Implement this interface to provide a Service. A Service is typically something which is wired up using Guice
 * modules, but requires logic to get fully up and running, or consumes external resources - database connections, web
 * services etc, and is based on the recommendations of the Guice team. (see
 * https://code.google.com/p/google-guice/wiki/ModulesShouldBeFastAndSideEffectFree).
 * <p>
 * A {@link Service} can however be used for anything you feel appropriate, which could benefit from having a two stage
 * creation cycle - the initial construction of a class followed by the call of the {@link #start()} method.
 * <p>
 * The easiest way is to create an implementation is to sub-class either {@link AbstractService} or
 * {@link AbstractServiceI18N}.
 * <p>
 * When an instance of a {@link Service} implementation is created through Guice, it is automatically registered with
 * the {@link ServicesMonitor}. (This is done through a Guice listener in the {@link ServicesMonitorModule}).
 * <p>
 * A service should have the following characteristics:
 * <ol>
 * <li>All Services must be instantiated through Guice
 * <li>Other {@link Service} instances which your Service depends on, must be injected through the constructor
 * <li>The constructor must be lightweight and must not require that its dependencies are started.
 * <li>There are some limitations with injecting {@link Provider}s of services - see the {@link #start()} method javadoc
 * - but if the dependency's constructor is lightweight as it should be, it should also be unnecessary to inject a
 * Provider
 * </ol>
 * <p>
 * Note that when a Service implementation is instantiated through Guice it is automatically registered with the
 * {@link ServicesMonitor}
 * <p>
 * 
 * @author David Sowerby
 * 
 */
public interface Service extends ServiceStatusChangeListener {

	public enum Status {
		INITIAL, STARTED, STOPPED, FAILED_TO_START, FAILED_TO_STOP, DEPENDENCY_FAILED
	}

	/**
	 * Implement this to start your service. When this method is called, AOP code in the {@link ServicesMonitorModule}
	 * intercepts (before the implementations's code is executed) and checks for any {@link Service} fields annotated
	 * with {@link AutoStart}, and if it finds them, calls their start methods.
	 * <p>
	 * If any {@link AutoStart} dependencies fail to start up successfully, default behaviour is to attempt to start all
	 * other fields annotated with {@link AutoStart}, set a status of {@link Status#DEPENDENCY_FAILED}. Control is then
	 * passed to the implementation's start() method, and the implementation must then decide how to respond. This means
	 * that if you are using {@link AutoStart}, your code should first check the status, as presumably a different
	 * action will be needed if a dependency has failed to start.
	 * <p>
	 * Once a Service is in a Started state, any subsequent calls to this method are ignored, so a call to this method
	 * may be made without needing to check first whether it is already started
	 * <p>
	 * If an exception is thrown during this method, it is caught by the AOP Code, and the status set to
	 * {@link Status#FAILED_TO_START}
	 */
	Status start();

	/**
	 * Stops the service. No other action is taken automatically apart form the {@link ServicesMonitor} recording the
	 * change of status. If other Services are dependent and need to respond to this change of status, then the
	 * dependent services should respond to the
	 * {@link ServiceStatusChangeListener#serviceStatusChange(Service, Status, Status)}. Listeners are added in the AOP
	 * code, so that Services dependent on another Service are notified of a change of status in their dependencies.
	 * 
	 * @return
	 */
	Status stop();

	/**
	 * The name for this service. The implementation may wish to include an instance identifier if it is not of
	 * Singleton scope, but this is not essential; the name is not used for anything except as a label. You may also
	 * choose to implement by sub-classing {@link AbstractServiceI18N}, which will handle I18N keys and translation
	 * 
	 * @return
	 */
	String getName();

	/**
	 * The name description for this service. You may also choose to implement by sub-classing
	 * {@link AbstractServiceI18N}, which will handle I18N keys and translation
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * returns the Status value for this service instance
	 * 
	 * @return
	 */
	Status getStatus();

	/**
	 * Returns true if and only if status == Service.Status.STARTED)
	 * 
	 * @return
	 */
	boolean isStarted();

	void setStatus(Status result);

	void addListener(ServiceStatusChangeListener listener);

	void removeListener(ServiceStatusChangeListener listener);

}
