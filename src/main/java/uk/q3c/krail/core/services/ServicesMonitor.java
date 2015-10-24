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
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Monitors instances of {@link Service} implementations, and keeps a history of the most recent status changes (only
 * the current status and the most recent change, see {@link ServiceStatus}).
 * <p/>
 * There is also a {@link #stopAllServices()} method to stop all services if you really need it.
 * <p/>
 * Services are registered automatically by AOP code located in the {@link ServiceModule}
 * <p/>
 * Acknowledgement: developed from code contributed by https://github.com/lelmarir
 */
@Singleton
@Listener
public class ServicesMonitor {

    private static final Logger log = LoggerFactory.getLogger(ServicesMonitor.class);

    private final Map<Service, ServiceStatus> services;

    @Inject
    public ServicesMonitor() {
        this.services = new MapMaker().weakKeys()
                                      .makeMap();
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.q3c.krail.core.services.ServicesMonitor#registerService(uk.q3c.krail.core.services.Service)
     */
    //    synchronized public void registerService(Service service) {
    //        ServiceStatus serviceStatus = new ServiceStatus();
    //        services.put(service, serviceStatus);
    //        log.debug("registered service '{}'", service.getName());
    //
    //    }


    @Handler
    synchronized public void serviceStatusChange(ServiceBusMessage busMessage) {
        Service service = busMessage.getService();
        if (!services.containsKey(service)) {
            ServiceStatus serviceStatus = new ServiceStatus();
            services.put(service, serviceStatus);
            log.debug("registered service '{}'", service.getName());
        }
        ServiceStatus status = services.get(service);
        status.setPreviousState(busMessage.getFromState());
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

    synchronized public void stopAllServices() throws Exception {
        log.info("Stopping all services");
        for (Service service : services.keySet()) {
            service.stop();
        }
    }

    synchronized public ImmutableList<Service> getRegisteredServices() {
        return ImmutableList.copyOf(services.keySet());

    }

    public ServiceStatus getServiceStatus(Service service) {
        return services.get(service);
    }

    public void clear() {
        services.clear();
    }
}