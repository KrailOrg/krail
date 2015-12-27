/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.services;

import uk.q3c.krail.i18n.I18NKey;

import java.util.EnumSet;

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
 *
 * @author David Sowerby
 */
public interface Service {

    enum State {
        INITIAL, STARTING, STARTED, FAILED, STOPPING, STOPPED, FAILED_TO_START, FAILED_TO_STOP, DEPENDENCY_STOPPED, DEPENDENCY_FAILED
    }

    EnumSet<State> stoppedStates = EnumSet.complementOf(EnumSet.of(State.INITIAL, State.STARTING, State.STARTED));
    EnumSet<State> stopReasons = EnumSet.of(State.STOPPED, State.DEPENDENCY_FAILED, State.DEPENDENCY_STOPPED, State.FAILED);


    /**
     * You will only need to implement this if you are not using a sub-class of
     * {@link AbstractService}. When you do sub-class {@link AbstractService}, override {@link AbstractService#doStart()}. Exceptions should be caught and
     * handled within the implementation of this method - generally this will cause the state to be set to {@link State#FAILED_TO_START}
     *
     */
    ServiceStatus start();


    /**
     * Equivalent to calling
     * {@link #stop(State)} with a value of {@link State#STOPPED}.  Implementations must handle all exceptions and set the state to {@link State#FAILED_TO_STOP}
     */
    ServiceStatus stop();

    /**
     * Attempts to stop the Service, and sets the state to
     * {@link State#FAILED}.   Implementations must handle all exceptions and set the state to {@link State#FAILED_TO_STOP}
     *
     * @return state after the call
     */
    ServiceStatus fail();

    /**
     * You will only need to implement this if you are not using a sub-class of {@link AbstractService}. When you do sub-class {@link AbstractService},
     * override {@link AbstractService#doStop()}.  Implementations must handle all exceptions and set the state to {@link State#FAILED_TO_STOP}
     *
     * @param reasonForStop
     *         the caller uses this to indicate the reason this method has been called.  Only the states contained in {@link #stopReasons} are valid. These
     *         mean:<ol><li>STOPPED: the user elected to stop this Service directly</li><li>DEPENDENCY_STOPPED: A dependency which this Service needs in order
     *         to run, has stopped</li><li>DEPENDENCY_FAILED: A dependency which this Service needs in order to run, has failed</li></ol>:
     *
     *
     */
    ServiceStatus stop(State reasonForStop);

    /**
     * Returns the translated name for this service. The implementation may wish to include an instance identifier if it is not of
     * Singleton scope, but this is not essential; the name is not used for anything except as a label.
     *
     * @return The translated name for this service.
     */
    default String getName() {
        return this.getClass()
                   .getName();
    }

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
     * Returns true if and only if status == Service.Status.STARTED)
     *
     * @return true if and only if status == Service.Status.STARTED)
     */
    boolean isStarted();

    /**
     * Returns true if the service is in a stopped state as defined by {@link #stoppedStates}
     *
     * @return true if the service is in a stopped state as defined by {@link #stoppedStates}
     */
    boolean isStopped();


    I18NKey getDescriptionKey();

    void setDescriptionKey(I18NKey descriptionKey);

    default ServiceKey getServiceKey() {
        return new ServiceKey(getNameKey());
    }

    /**
     * Implementations (even sub-classes of {@link AbstractService} must define a key which when combined with {@link #getInstance()}, provides a unique
     * identity for this Service.  It is an I18NKey because it is expected that this name will be presented to end users (even if only to application sys
     * admins)
     *
     * @return a key which when combined with {@link #getInstance()}, provides a unique identity for this Service
     */
    I18NKey getNameKey();


    int getInstance();

    void setInstance(int instance);

    /**
     * Resets a service from a failed or stopped state to INITIAL.  Does nothing if the service is STARTED or STARTING
     *
     * @return a ServiceStatus of INITIAL
     */
    ServiceStatus reset();
}
