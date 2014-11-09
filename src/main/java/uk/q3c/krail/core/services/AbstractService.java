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

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * The easiest way to provide a Service is to sub-class either this class or {@link AbstractServiceI18N}. The behaviour
 * provided by this class uses {@link Dependency} annotations to select required options. For example, if Service B
 * depends on Service A, then service B must contain a field referencing Service A, annotated with {@link Dependency}.
 * The following then applies:
 * <ol>
 * <li>option {@link #requiredAtStart()}: If true, Service A will be started automatically before starting Service B.
 * If
 * Service A fails to start, Service B will also fail. If false, Service B will continue to start, and it is up to the
 * developer to ensure that the logic of Service B deals with the the alternative states of Service A. The default is
 * true<br>
 * <br>
 * <li>option {@link #stopOnStop()}: If true, if Service A fails (or is stopped), Service B will also be stopped, by a
 * call to its stop() method. If false, Service B does not respond to a failure in Service A. The default is true<br>
 * <br>
 * <li>option {@link #startOnRestart_true()}: If true, if Service B has a status of DEPENDENCY_FAILED, and Service A is
 * restarted, Service B will automatically attempt to start (it may not succeed if it has other dependencies which have
 * failed). If false, Service B will not respond to this change of status in Service A. The default is true
 * <p/>
 * Dedicated start and stop listeners are used to respond to dependencies changing their state to started or stopped
 * respectively, and are used to respond to state changes in dependencies. service change listeners are fired every
 * time
 * there is a change of state (and is used by the {@link ServicesMonitor})<br>
 *
 * @author David Sowerby
 */
public abstract class AbstractService implements Service, ServiceStartListener, ServiceStopListener {
    private static Logger log = LoggerFactory.getLogger(AbstractService.class);
    private final List<ServiceChangeListener> statusChangeListeners = new ArrayList<>();
    private final List<ServiceStartListener> serviceStartListeners = new ArrayList<>();
    private final List<ServiceStopListener> serviceStopListeners = new ArrayList<>();
    protected Status status = Status.INITIAL;
    private List<DependencyRecord> dependencies;

    protected AbstractService() {
        super();
    }

    @Override
    public boolean isStarted() {
        return status == Status.STARTED;
    }

    @Override
    public void addChangeListener(ServiceChangeListener listener) {
        statusChangeListeners.add(listener);
    }

    @Override
    public void removeChangeListener(ServiceChangeListener listener) {
        statusChangeListeners.remove(listener);
    }

    @Override
    public void dependencyServiceStopped(Service service) throws Exception {
        stop();
    }

    @Override
    public Status stop() throws Exception {
        if (status == Status.STOPPED) {
            log.debug("Attempting to stop service {}, but it is already stopped. No action taken", getName());
            return status;
        }
        log.info("Stopping service: {}", getName());
        try {
            doStop();
            setStatus(Status.STOPPED);
        } catch (Exception e) {
            log.error("Exception occurred while trying to stop the {}.", getName());
            setStatus(Status.FAILED_TO_STOP);
        }

        return status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    protected abstract void doStop() throws Exception;

    @Override
    public void dependencyServiceStarted(Service service) throws Exception {
        if (status == Status.DEPENDENCY_FAILED) {
            start();
        }
    }

    @Override
    public Status start() throws Exception {
        if (status == Status.STARTED) {
            log.debug("{} already started, no action taken", getName());
            return status;
        }
        log.info("Starting service: {}", getName());
        // make sure #dependencies is prepared
        getDependencies();

        // start all dependencies that should be there at the start
        for (DependencyRecord depRec : dependencies) {
            try {
                log.debug("Starting dependency {} from {}", depRec.service.getName(), getName());
                depRec.service.start();
            } catch (Exception e) {
                if (depRec.requiredAtStart) {
                    setStatus(Status.DEPENDENCY_FAILED);
                    throw new ServiceException("Dependency " + depRec.service.getName() + " failed to start", e);
                } else {
                    log.info("Dependency {} failed to start, but is optional.  Continuing to start {}",
                            depRec.service.getName(), getName());
                }

            }
        }

        // if we get this far we can start this service
        try {

            doStart();
            setStatus(Status.STARTED);
        } catch (Exception e) {
            String msg = "Exception occurred while trying to start " + getName();
            log.error(msg);
            setStatus(Status.FAILED_TO_START);
            throw new ServiceException(msg, e);
        }
        return status;
    }

    protected abstract void doStart() throws Exception;

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
                        if (depRec.startOnRestart) {
                            depRec.service.addStartListener(this);
                        }
                        if (depRec.stopOnFail) {
                            depRec.service.addStopListener(this);
                        }
                        dependencies.add(depRec);
                        log.debug("Service dependency {} identified", depRec.service.getName());
                    }
                }
            }
        }
        return dependencies;
    }

    @Override
    public void addStopListener(ServiceStopListener listener) {
        serviceStopListeners.add(listener);
    }

    @Override
    public void removeStopListener(ServiceStopListener listener) {
        serviceStopListeners.remove(listener);
    }

    protected void fireListeners(Status previousStatus) throws Exception {

        log.debug("firing status change listeners in {}.  Status is now {}", this.getName(), this.getStatus());
        for (ServiceChangeListener listener : statusChangeListeners) {
            listener.serviceStatusChange(this, previousStatus, status);
        }

        // prevents re-entrant use of iterator
        ImmutableList<ServiceStartListener> startListeners = ImmutableList.copyOf(serviceStartListeners);
        if (previousStatus != Status.INITIAL && status == Status.STARTED) {
            log.debug("Firing start listeners from {}", getName());
            for (ServiceStartListener listener : startListeners) {
                listener.dependencyServiceStarted(this);
            }
        }

        // prevents re-entrant use of iterator
        ImmutableList<ServiceStopListener> stopListeners = ImmutableList.copyOf(serviceStopListeners);
        if (previousStatus == Status.STARTED && isStopped()) {
            log.debug("Firing stop listeners from {}", getName());
            for (ServiceStopListener listener : stopListeners) {
                listener.dependencyServiceStopped(this);
            }
        }
    }

    @Override
    public void addStartListener(ServiceStartListener listener) {
        serviceStartListeners.add(listener);
    }

    @Override
    public void removeStartListener(ServiceStartListener listener) {
        serviceStartListeners.remove(listener);
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

    @Override
    public boolean isStopped() {
        return status == Status.STOPPED || (status == Status.FAILED) || status == Status.DEPENDENCY_FAILED;
    }


    protected void setStatus(Status status) throws Exception {
        if (status != this.status) {
            Status previousStatus = this.status;
            this.status = status;
            log.debug(getName() + " has changed status from {} to {}", previousStatus, getStatus());
            fireListeners(previousStatus);
        }
    }


}
