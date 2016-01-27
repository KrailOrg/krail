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

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.GlobalBus;
import uk.q3c.krail.core.eventbus.GlobalBusProvider;
import uk.q3c.krail.core.eventbus.SubscribeTo;
import uk.q3c.krail.core.i18n.I18NKey;
import uk.q3c.krail.core.i18n.Translate;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import static uk.q3c.krail.core.services.Service.State.*;

/**
 * The easiest way to provide a {@link Service} is to sub-class either this class or {@link AbstractService}.  For management of dependencies between services,
 * see {@link ServicesGraph}
 * <p>
 * Dedicated start and stop listeners can be used to respond to dependencies changing their state to started or stopped
 * respectively, and are used to respond to state changes in dependencies. service change listeners are fired every
 * time
 * there is a change of state (and is used by the {@link DefaultServicesMonitor})<br>
 * <p>
 * All service events are published on the GlobalBus, and all instances of {@link AbstractService} are subscribed to the GlobalBus; this enables the some of
 * the logic of service dependencies - for example, when a service needs to respond when a service it depends on stops.
 * <p>
 * This also means that it is not necessary to annotate a sub-class of AbstractService with a {@link Listener}, unless: <ol>
 * <li>you want to specify strong references, </li>
 * <li>you want to subscribe to another event bus as well the {@link GlobalBus}, in which case you will need both {@link Listener} and {@link SubscribeTo}
 * annotations</ol>
 * <p>
 *
 * @author David Sowerby
 */
@Listener
@ThreadSafe
public abstract class AbstractService implements Service {

    private static Logger log = LoggerFactory.getLogger(AbstractService.class);
    private final Translate translate;
    protected State state = INITIAL;
    private RelatedServicesExecutor servicesExecutor;
    private I18NKey descriptionKey;
    private I18NKey nameKey;
    private PubSubSupport<BusMessage> eventBus;
    private int instanceNumber = 0;
    private Cause cause;

    @Inject
    protected AbstractService(Translate translate, GlobalBusProvider globalBusProvider, RelatedServicesExecutor servicesExecutor) {
        super();
        this.translate = translate;
        this.servicesExecutor = servicesExecutor;
        servicesExecutor.setService(this);
        eventBus = globalBusProvider.get();
    }

    @Override
    public I18NKey getNameKey() {
        return nameKey;
    }

    protected void setNameKey(@Nonnull I18NKey nameKey) {
        this.nameKey = nameKey;
    }

    @Override
    public synchronized Cause getCause() {
        return cause;
    }

    @Override
    public synchronized boolean isStarted() {
        return state == RUNNING;
    }

    @Override
    public ServiceStatus stop() {
        return stop(Cause.STOPPED);
    }

    @Nonnull
    @Override
    public synchronized ServiceStatus stop(@Nonnull Cause cause) {
        if (state == STOPPED || state == STOPPING || state == FAILED || state == RESETTING) {
            log.debug("Attempting to stop service {}, but it is already stopped or resetting. No action taken", getName());
            return new ServiceStatus(this, this.state, this.cause);
        }
        if (state == INITIAL) {
            log.debug("Currently in INITIAL state, stop or fail ignored");
            return new ServiceStatus(this, this.state, this.cause);
        }
        log.info("Stopping service: {}", getName());
        setState(STOPPING, cause);
        //boolean dependantsRequiringThisAreStopped
        servicesExecutor.execute(RelatedServicesExecutor.Action.STOP, cause); // also stop / fail dependants which
        // always require this service
        try {
            doStop();
            setState(stopStateFromCause(cause), cause);
        } catch (Exception e) {
            log.error("Exception occurred while trying to stop {}.", getName());
            if (cause == Cause.FAILED) {
                //service has already failed, not just failed to stop
                setState(stopStateFromCause(Cause.FAILED), Cause.FAILED);
            } else {
                setState(stopStateFromCause(Cause.FAILED_TO_STOP), Cause.FAILED_TO_STOP);
            }
        }


        return new ServiceStatus(this, this.state, this.cause);
    }

    private State stopStateFromCause(Cause cause) {
        if (cause == Cause.FAILED || cause == Cause.FAILED_TO_STOP) {
            return FAILED;
        }
        return STOPPED;
    }

    protected abstract void doStop() throws Exception;

    @Override
    public synchronized String getName() {
        return translate.from(getNameKey());
    }

    @Override
    public synchronized boolean isStopped() {
        return state == STOPPED;
    }

    @Override
    public ServiceStatus fail() {
        return stop(Cause.FAILED);
    }

    @Override
    public synchronized ServiceStatus reset() {
        if (state == INITIAL || state == RESETTING) {
            return new ServiceStatus(this, this.state, this.cause);
        }
        if (state != STOPPED && state != FAILED) {
            throw new ServiceStatusException("Must be in a STOPPED state before reset()");
        }
        log.info("Resetting service: {}", getName());
        setState(State.RESETTING, Cause.RESET);
        try {
            doReset();
            setState(INITIAL, Cause.RESET);
            return new ServiceStatus(this, this.state, this.cause);
        } catch (Exception e) {
            log.error("Exception while trying to reset {}", getName(), e);
            setState(State.FAILED, Cause.FAILED_TO_RESET);
            return new ServiceStatus(this, this.state, this.cause);
        }

    }

    /**
     * Often not needed to do anything - but override if it does
     */
    @SuppressFBWarnings("ACEM_ABSTRACT_CLASS_EMPTY_METHODS")
    protected void doReset() {

    }

    protected synchronized ServiceStatus start(Cause cause) {
        if (state == RUNNING || state == STARTING) {
            log.debug("{} already started, no action taken", getName());
            return new ServiceStatus(this, this.state, this.cause);
        }
        if (state == STOPPING) {
            throw new ServiceStatusException("Cannot start() when state is " + state.name());
        }
        if (state == FAILED) {
            throw new ServiceStatusException("Cannot start() when state is " + state.name() + ".  Call reset() first");
        }

        State beginningState = getState();
        log.info("Starting service: {}", getName());
        setState(STARTING, cause);
        if (servicesExecutor.execute(RelatedServicesExecutor.Action.START, cause)) {
            try {
                doStart();
                setState(RUNNING, cause);
                this.cause = cause;
            } catch (Exception e) {
                String msg = "Exception occurred while trying to start " + getName();
                log.error(msg, e);
                setState(FAILED, Cause.FAILED_TO_START);
            }
        } else {
            //revert to beginning state, as we could not complete
            setState(beginningState, Cause.DEPENDENCY_FAILED);
        }

        return new ServiceStatus(this, this.state, this.cause);
    }

    @Override
    public ServiceStatus start() {
        return start(Cause.STARTED);
    }

    protected abstract void doStart() throws Exception;

    @Override
    public synchronized State getState() {
        return state;
    }

    //    @SuppressFBWarnings("IS2_INCONSISTENT_SYNC") // disagree with FB on this.  This method is private, and is only access via synchronized methods
    private synchronized void setState(State state, Cause cause) {
        if (state != this.state) {
            State previousState = this.state;
            this.state = state;
            this.cause = cause;
            log.debug("{} has changed status from {} to {}", getName(), previousState, getState());
            publishStatusChange(previousState, cause);
        }
    }

    @Override
    public ServiceStatus dependencyFail() {
        return stop(Cause.DEPENDENCY_FAILED);
    }

    @Override
    public ServiceStatus dependencyStop() {
        return stop(Cause.DEPENDENCY_STOPPED);
    }

    @Override
    public synchronized I18NKey getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public synchronized void setDescriptionKey(I18NKey descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    @Override
    public synchronized String getDescription() {
        if (descriptionKey == null) {
            return "";
        }
        return translate.from(descriptionKey);
    }

    public synchronized int getInstanceNumber() {
        return instanceNumber;
    }

    public synchronized void setInstanceNumber(int instanceNumber) {
        this.instanceNumber = instanceNumber;
    }


    protected void publishStatusChange(State previousState, Cause cause) {
        log.debug("publishing status change in {}.  Status is now {}", this.getName(), this.getState());
        eventBus.publish(new ServiceBusMessage(this, previousState, getState(), cause));
    }


}
