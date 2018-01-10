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
package uk.q3c.krail.service.monitor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.service.Service;
import uk.q3c.krail.service.ServiceBusMessage;
import uk.q3c.krail.service.ServiceMonitor;
import uk.q3c.krail.service.ServiceStatusRecord;

import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Uses the GlobalBus to monitor changes to Service status
 */
@Singleton
@Listener
@ThreadSafe
public class DefaultServiceMonitor implements ServiceMonitor {

    private static final Logger log = LoggerFactory.getLogger(DefaultServiceMonitor.class);

    private final Map<Service, ServiceStatusRecord> services;

    @Inject
    public DefaultServiceMonitor(MessageBus globalBusProvider) {
        this.services = new MapMaker().weakKeys()
                .makeMap();
        globalBusProvider.subscribe(this);
    }

    @Handler
    synchronized public void serviceStatusChange(ServiceBusMessage busMessage) {
        Service service = busMessage.getService();
        if (!services.containsKey(service)) {
            ServiceStatusRecord serviceStatusRecord = new ServiceStatusRecord();
            serviceStatusRecord.setService(service);
            services.put(service, serviceStatusRecord);
            log.debug("registered service '{}'", service.getName());
        }
        ServiceStatusRecord status = services.get(service);
        status.setPreviousState(status.getCurrentState());
        status.setCurrentState(busMessage.getToState());
        //call LocalDateTime.now() just once, otherwise there are tiny differences between change time & start / stop times
        LocalDateTime serviceChangeTime = LocalDateTime.now();
        status.setStatusChangeTime(serviceChangeTime);
        if (service.isStarted()) {
            status.setLastStartTime(LocalDateTime.from(serviceChangeTime));
        }
        if (service.isStopped()) {
            status.setLastStopTime(LocalDateTime.from(serviceChangeTime));
        }
        services.put(service, status);
    }


    /**
     * Returns a list of service being monitored.  If a service has never started, it will not be in the list
     *
     * @return a list of service being monitored.
     */
    @Override
    public synchronized ImmutableList<Service> getMonitoredServices() {
        return ImmutableList.copyOf(services.keySet());
    }

    @Override
    public synchronized ServiceStatusRecord getServiceStatus(Service service) {
        return services.get(service);
    }

    public synchronized void clear() {
        services.clear();
    }
}