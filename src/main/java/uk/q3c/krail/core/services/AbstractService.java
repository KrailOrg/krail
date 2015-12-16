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
package uk.q3c.krail.core.services;

import com.google.inject.Inject;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.GlobalBus;
import uk.q3c.krail.core.eventbus.GlobalBusProvider;
import uk.q3c.krail.core.eventbus.SubscribeTo;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import static com.google.common.base.Preconditions.checkArgument;
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
    private ServicesModel servicesModel;
    //    private List<DependencyRecord> dependencies;
    private I18NKey descriptionKey;
    private PubSubSupport<BusMessage> eventBus;
    private int instance = 0;

    @Inject
    protected AbstractService(Translate translate, ServicesModel servicesModel, GlobalBusProvider globalBusProvider) {
        super();
        this.translate = translate;
        this.servicesModel = servicesModel;
        eventBus = globalBusProvider.getGlobalBus();
    }

    @Override
    public synchronized boolean isStarted() {
        return state == STARTED;
    }


    @Override
    public synchronized ServiceStatus stop() {
        return stop(STOPPED);
    }

    @Nonnull
    @Override
    public synchronized ServiceStatus stop(@Nonnull Service.State reasonForStop) {
        checkArgument(Service.stopReasons.contains(reasonForStop));
        if (isStopped()) {
            log.debug("Attempting to stop service {}, but it is already stopped. No action taken", getName());
            return new ServiceStatus(this, state);
        }
        log.info("Stopping service: {}", getName());
        setState(STOPPING);
        servicesModel.stopDependantsOf(this, false);
        try {
            doStop();
            setState(reasonForStop);
        } catch (Exception e) {
            log.error("Exception occurred while trying to stop the {}.", getName());
            setState(FAILED_TO_STOP);
        }


        return new ServiceStatus(this, state);
    }

    protected abstract void doStop() throws Exception;

    @Override
    public synchronized String getName() {
        return translate.from(getNameKey());
    }


    @Override
    public synchronized boolean isStopped() {
        return stoppedStates.contains(state);
    }

    @Override
    public synchronized ServiceStatus fail() {
        return stop(FAILED);
    }

    @Override
    public synchronized ServiceStatus start() {
        if (state == STARTED) {
            log.debug("{} already started, no action taken", getName());
            return new ServiceStatus(this, state);
        }
        log.info("Starting service: {}", getName());
        setState(STARTING);
        if (servicesModel.startDependenciesFor(this)) {
            try {
                doStart();
                setState(STARTED);
            } catch (Exception e) {
                String msg = "Exception occurred while trying to start " + getName();
                log.error(msg, e);
                setState(FAILED_TO_START);
            }
        } else {
            setState(DEPENDENCY_FAILED);
        }

        return new ServiceStatus(this, state);
    }

    protected abstract void doStart() throws Exception;

    @Override
    public synchronized State getState() {
        return state;
    }

    protected void setState(State state) {
        if (state != this.state) {
            State previousState = this.state;
            this.state = state;
            log.debug(getName() + " has changed status from {} to {}", previousState, getState());
            publishStatusChange(previousState);
        }
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

    @Override
    public synchronized int getInstance() {
        return instance;
    }

    @Override
    public synchronized void setInstance(int instance) {
        this.instance = instance;
    }


    protected void publishStatusChange(State previousState) {
        log.debug("publishing status change in {}.  Status is now {}", this.getName(), this.getState());
        eventBus.publish(new ServiceBusMessage(this, previousState, getState()));
    }


}
