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

import uk.q3c.krail.core.i18n.NamedAndDescribed;

/**
 * Implement this interface to provide a Service. A Service is typically something which is wired up using Guice
 * modules, but requires logic to get fully up and running, or consumes external resources - database connections, web
 * services etc, and is based on the recommendations of the Guice team. (see
 * https://code.google.com/p/google-guice/wiki/ModulesShouldBeFastAndSideEffectFree).
 * <p>
 * A {@link Service} can however be used for anything you feel appropriate, which could benefit from having a two stage
 * creation cycle - the initial configuration through Guice modules, followed by a controlled start to activate /
 * consume resources.
 * <p>
 * The easiest way is to create an implementation is to sub-class either {@link AbstractService}.<br>
 * Dependencies between services shold not be coded directly but use the features described in {@link ServicesGraph}
 * <p>
 * Implementations (even sub-classes of {@link AbstractService} must define a key which when combined with {@link #getInstanceNumber()}, provides a unique
 * identity for this Service.  It is an I18NKey because it is expected that this name will be presented to end users (even if only to application sys
 * admins)
 * <p>
 * When an instance of a {@link Service} implementation is created through Guice, it is automatically registered with
 * the {@link DefaultServicesMonitor}. (This is done through a Guice listener in the {@link ServicesModule}).
 * <p>
 * The AOP code in the ServicesMonitorModule also intercepts the finalize() method, and calls the stop() method to
 * ensure a service is stopped before being finalized.
 * <p>
 * A service should have the following characteristics:
 * <ol>
 * <li>All Services must be instantiated through Guice
 * <li>Other {@link Service} instances which your Service depends on, must be injected through the constructor
 * <li>The constructor must be lightweight and must not require that its dependencies are already started at the time
 * of injection.
 * <li>If the dependency's constructor is lightweight as it should be, it should also be unnecessary to inject a Provider<Service>
 * </ol>
 * Details of the lifecycle can be found at http://krail.readthedocs.org/en/master/devguide-services/
 *
 * @author David Sowerby
 */
public interface Service extends NamedAndDescribed {

    enum State {
        INITIAL, STARTING, RUNNING, STOPPING, STOPPED, RESETTING, FAILED
    }

    enum Cause {
        FAILED, STOPPED, FAILED_TO_START, FAILED_TO_STOP, DEPENDENCY_STOPPED, STARTED, DEPENDENCY_FAILED, FAILED_TO_RESET, RESET
    }


    /**
     * You will only need to implement this if you are not using a sub-class of
     * {@link AbstractService}. When you do sub-class {@link AbstractService}, override {@link AbstractService#doStart()}. Exceptions should be caught and
     * handled within the implementation of this method - generally this will set the cause to {@link Cause#FAILED_TO_START}
     *
     * @throws ServiceStatusException if called when service is currently in a state which does not allow a start
     */
    ServiceStatus start();


    /**
     * Equivalent to calling
     * {@link #stop(Cause)} with a value of {@link State#STOPPED}.  Implementations must handle all exceptions and set the state to {@link Cause#FAILED_TO_STOP}
     */
    ServiceStatus stop();

    /**
     * Attempts to stop the Service, and sets the state to
     * {@link Cause#FAILED}.   Implementations must handle all exceptions and if appropriate set cause to {@link Cause#FAILED_TO_STOP}
     *
     * @return state after the call
     */
    ServiceStatus fail();

    /**
     * You will only need to implement this if you are not using a sub-class of {@link AbstractService}. When you do sub-class {@link AbstractService},
     * override {@link AbstractService#doStop()}.  Implementations must handle all exceptions and set the state to {@link State#STOPPED} and Cause appropriately
     *
     * @param cause the caller uses this to indicate the cause of the stop.
     *
     *                      valid. These
     *                      mean:<ol><li>STOPPED: the user elected to stop this Service directly</li><li>DEPENDENCY_STOPPED: A dependency which this Service
     *                      needs in order
     *                      to run, has stopped</li><li>DEPENDENCY_FAILED: A dependency which this Service needs in order to run, has failed</li></ol>:
     */
    ServiceStatus stop(Cause cause);

    /**
     * Returns the translated description for this service, or an empty String if no description as been set
     *
     * @return The translated description for this service, or an empty String if no description as been set
     */
    String getDescription();

    /**
     * returns the State value for this service instance
     *
     * @return the State value for this service instance
     */
    State getState();

    /**
     * Returns the cause of the last state change
     *
     * @return Returns the cause of the last state change
     */
    Cause getCause();

    /**
     * Returns true if and only if status == Service.Status.STARTED)
     *
     * @return true if and only if status == Service.Status.STARTED)
     */
    boolean isStarted();

    /**
     * Returns true if the service is in a stopped state
     *
     * @return true if the service is in a stopped state
     */
    boolean isStopped();

    /**
     * Notify this service that one of its required dependencies has failed
     *
     * @return the resultant {@link ServiceStatus}
     */
    ServiceStatus dependencyFail();

    /**
     * Notify this service that one of its required dependencies has stopped
     *
     * @return the resultant {@link ServiceStatus}
     */
    ServiceStatus dependencyStop();

    default ServiceKey getServiceKey() {
        return new ServiceKey(getNameKey());
    }

    /**
     * Not used by default, but can be used to identify a specific instance of a {@link Service}
     *
     * @return 0 by default
     */
    int getInstanceNumber();

    /**
     * /**
     * Not used by default, but can be used to identify a specific instance of a {@link Service}
     *
     * @param instance number to set
     */
    void setInstanceNumber(int instance);

    /**
     * Resets a service from a stopped or failed state to INITIAL.  Does nothing if the service is STARTED, STARTING or STOPPING
     *
     * @return a ServiceStatus of INITIAL
     */
    ServiceStatus reset();
}
