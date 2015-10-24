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
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.GlobalBus;
import uk.q3c.krail.core.eventbus.SubscribeTo;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static uk.q3c.krail.core.services.Service.State.*;

/**
 * The easiest way to provide a Service is to sub-class either this class or {@link AbstractServiceI18N}. The behaviour
 * provided by this class uses {@link Dependency} annotations to select required options. For example, if Service B
 * depends on Service A, then service B must contain a field referencing Service A, annotated with {@link Dependency}.
 * The following then applies:
 * <ol>
 * <li>option {@link Dependency#requiredAtStart()}: If true, Service A will be started automatically before starting Service B.
 * If
 * Service A fails to start, Service B will also fail. If false, Service B will continue to start, and it is up to the
 * developer to ensure that the logic of Service B deals with the the alternative states of Service A. The default is
 * true<br>
 * <br>
 * <li>option {@link Dependency#stopOnStop()}: If true, if Service A fails (or is stopped), Service B will also be stopped, by a
 * call to its stop() method. If false, Service B does not respond to a failure in Service A. The default is true<br>
 * <br>
 * <li>option {@link Dependency#startOnRestart()}: If true, if Service B has a status of DEPENDENCY_FAILED, and Service A is
 * restarted, Service B will automatically attempt to start (it may not succeed if it has other dependencies which have
 * failed). If false, Service B will not respond to this change of status in Service A. The default is true
 * <p>
 * Dedicated start and stop listeners are used to respond to dependencies changing their state to started or stopped
 * respectively, and are used to respond to state changes in dependencies. service change listeners are fired every
 * time
 * there is a change of state (and is used by the {@link ServicesMonitor})<br>
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
public abstract class AbstractService implements Service {

    private static Logger log = LoggerFactory.getLogger(AbstractService.class);
    private final Translate translate;
    protected State state = INITIAL;
    private List<DependencyRecord> dependencies;
    private I18NKey descriptionKey;
    private PubSubSupport<BusMessage> eventBus;
    private I18NKey nameKey = LabelKey.Unnamed;
    private EnumSet<State> stateOfStopped = EnumSet.of(STOPPED, FAILED, DEPENDENCY_FAILED);

    @Inject
    protected AbstractService(Translate translate) {
        super();
        this.translate=translate;

    }

    @Override
    public boolean isStarted() {
        return state == STARTED;
    }

    @Override
    public void init(PubSubSupport<BusMessage> eventBus) {
        this.eventBus = eventBus;
        eventBus.subscribe(this);
        eventBus.publish(new ServiceBusMessage(this, NON_EXISTENT, INITIAL));

    }

    /**
     * Responds to a {@link ServiceStoppedMessage}.  Checks to see whether the service that has stopped is a {@link Dependency} - if it is,  and {@link
     * Dependency#stopOnStop()} is true, this service also stops.
     *
     * @param busMessage
     *         the message to process, which identifies the service which has stopped
     *
     * @throws Exception
     *         if this service is required to stop but fails to stop correctly
     */
    @Override
    @Handler
    public void serviceStopped(ServiceStoppedMessage busMessage) throws Exception {
        Service service = busMessage.getService();
        if (service == this) {
            return;
        }


        boolean dependencyFound = false;
        for (DependencyRecord dep : getDependencies()) {
            if (dep.service.equals(service)) {
                if (dep.stopOnFail) {
                    log.info("Stopping {} service, because a run time dependency ({}) has stopped", this.getName(), service.getName());
                    dependencyFound = true;
                    stop();
                    break;
                } else {
                    dependencyFound = true;
                    log.debug("Service: {}. Dependency {} has stopped, but is not marked 'stopOnFail', so this service will continue", this.getName(),
                            service.getName());
                    break;
                }
            }
        }
        log.debug("Service: {}. Another service, '{}', has stopped, but is not a dependency, so this service will continue", this.getName(), service.getName());
    }

    @Override
    public State stop() throws Exception {
        if (state == STOPPED) {
            log.debug("Attempting to stop service {}, but it is already stopped. No action taken", getName());
            return state;
        }
        log.info("Stopping service: {}", getName());
        try {
            doStop();
            setState(STOPPED);
        } catch (Exception e) {
            log.error("Exception occurred while trying to stop the {}.", getName());
            setState(FAILED_TO_STOP);
        }

        return state;
    }

    protected abstract void doStop() throws Exception;

    private List<DependencyRecord> getDependencies() throws IllegalArgumentException, IllegalAccessException {
        if (dependencies == null) {
            dependencies = new ArrayList<>();
            Class<?> clazz = ServiceUtils.unenhancedClass(this);
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                Class<?> fieldClass = field.getType();
                // if it is a service field, add a listener to it
                if (Service.class.isAssignableFrom(fieldClass)) {
                    field.setAccessible(true);
                    Service dependency = (Service) field.get(this);
                    Dependency annotation = field.getAnnotation(Dependency.class);
                    if (annotation != null) {
                        DependencyRecord depRec = new DependencyRecord();
                        depRec.service = dependency;
                        depRec.requiredAtStart = annotation.requiredAtStart();
                        depRec.startOnRestart = annotation.startOnRestart();
                        depRec.stopOnFail = annotation.stopOnStop();
                        dependencies.add(depRec);
                        log.debug("Service dependency {} identified", depRec.service.getName());
                    }
                }
            }
        }
        return dependencies;
    }

    @Override
    public String getName() {
        return translate.from(nameKey);
    }

    /**
     * Responds to a {@link ServiceStartedMessage}.  Checks to see whether the service that has started is a {@link Dependency} - if it is, and this service is
     * in a state of {@link State#DEPENDENCY_FAILED}, and {@link Dependency#startOnRestart()} is true, then this service will attempt to start
     *
     * @param busMessage
     *         the message to process, which identifies the service which has stopped
     *
     * @throws Exception
     *         if this service is required to start but fails with an exception
     */
    @Handler
    public void serviceStarted(ServiceStartedMessage busMessage) throws Exception {
        Service service = busMessage.getService();
        log.debug("Service: {}.  Service started message received from {} ", this.getName(), service.getName());

        if (service == this) {
            log.debug("Ignoring bus message from itself");
            return;
        }
        if (this.getState()
                .equals(STARTED)) {
            log.debug("Service: {}. Another service, '{}', has started, but this service is already running, no change is made", this.getName(), service
                    .getName());
            return;
        }

        if (!this.getState()
                 .equals(DEPENDENCY_FAILED)) {
            log.debug("Service: {}. Another service, '{}', has started, but this service is not in a status of DEPENDENCY_FAILED, and will not therefore " +
                    "attempt a restart", this.getName(), service.getName());
            return;
        }


        boolean dependencyFound = false;
        for (DependencyRecord dep : getDependencies()) {
            if (dep.service.equals(service)) {
                if (dep.startOnRestart) {
                    log.info("Attempting to start {} service, because a run time dependency ({}) has started, and is marked as 'startOnRestart'", this
                            .getName(), service.getName());
                    dependencyFound = true;
                    start();
                    break;
                } else {
                    dependencyFound = true;
                    log.debug("Service: {}. Dependency {} has started, but is not marked 'startOnRestart', so no change made", this.getName(), service
                            .getName());
                    break;
                }
            }
        }
        log.debug("Service: {}. Another service, '{}', has started, but is not a dependency, so no change is made", this.getName(), service.getName());
    }

    @Override
    public State start() throws Exception {
        if (state == STARTED) {
            log.debug("{} already started, no action taken", getName());
            return state;
        }
        log.info("Starting service: {}", getName());


        // start all dependencies that should be there at the start
        for (DependencyRecord depRec : getDependencies()) {
            try {
                log.debug("Starting dependency {} from {}", depRec.service.getName(), getName());
                depRec.service.start();
            } catch (Exception e) {
                if (depRec.requiredAtStart) {
                    setState(DEPENDENCY_FAILED);
                    throw new ServiceException("Dependency " + depRec.service.getName() + " failed to start", e);
                } else {
                    log.info("Dependency {} failed to start, but is optional.  Continuing to start {}", depRec.service.getName(), getName());
                }

            }
        }

        // if we get this far we can start this service
        try {

            doStart();
            setState(STARTED);
        } catch (Exception e) {
            String msg = "Exception occurred while trying to start " + getName();
            log.error(msg);
            setState(FAILED_TO_START);
            throw new ServiceException(msg, e);
        }
        return state;
    }

    protected abstract void doStart() throws Exception;    @Override
    public State getState() {
        return state;
    }

    @Override
    public I18NKey getNameKey() {
        return nameKey;
    }

    @Override
    public void setNameKey(I18NKey nameKey) {
        this.nameKey = nameKey;
    }    protected void publishStatusChange(State previousState) throws Exception {

        log.debug("publishing status change in {}.  Status is now {}", this.getName(), this.getState());
        eventBus.publish(new ServiceBusMessage(this, previousState, getState()));

        // if we were not started before, tell dependencies we've started now
        if (!previousState.equals(STARTED)) {
            log.debug("Service {} is publishing service started message", this.getName());
            eventBus.publish(new ServiceStartedMessage(this));
        }
        // if we were started, tell dependencies we've stopped
        if (previousState == STARTED && isStopped()) {
            log.debug("Service {} is publishing service stopped message", this.getName());
            eventBus.publish(new ServiceStoppedMessage(this));
        }
    }

    @Override
    public I18NKey getDescriptionKey() {
        return descriptionKey;
    }    @Override
    public boolean isStopped() {
        return stateOfStopped.contains(state);
    }

    @Override
    public void setDescriptionKey(I18NKey descriptionKey) {
        this.descriptionKey = descriptionKey;
    }    protected void setState(State state) throws Exception {
        if (state != this.state) {
            State previousState = this.state;
            this.state = state;
            log.debug(getName() + " has changed status from {} to {}", previousState, getState());
            publishStatusChange(previousState);
        }
    }

    /**
     * returns the translation for {@code #descriptionKey}, or an empty String if {@link #descriptionKey} is null -
     * this
     * makes the descriptionKey optional
     *
     * @see uk.q3c.krail.core.services.Service#getDescription()
     */
    @Override
    public String getDescription() {
        if (descriptionKey == null) {
            return "";
        }
        return translate.from(descriptionKey);
    }

    private class DependencyRecord {
        Service service;
        boolean requiredAtStart;
        boolean startOnRestart;
        boolean stopOnFail;

        @Override
        public String toString() {
            return "DependencyRecord [service=" + service.getName() + ", requiredAtStart=" + requiredAtStart + ", " +
                    "startOnRestart=" + startOnRestart + ", stopOnFail=" + stopOnFail + "]";
        }

    }









}
