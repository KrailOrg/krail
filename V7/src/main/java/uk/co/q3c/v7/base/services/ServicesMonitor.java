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
package uk.co.q3c.v7.base.services;

import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.services.Service.Status;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Monitors instances of {@link Service} implementations, and keeps a history of the most recent status changes (only
 * the current status and the most recent change, see {@link ServiceStatus}).
 * <p>
 * There is also a {@link #stopAllServices()} method to stop all services if you really need it.
 * <p>
 * Services are registered automatically by AOP code located in the {@link ServicesMonitorModule}
 * <p>
 * Acknowledgement: developed from code contributed by https://github.com/lelmarir
 */
@Singleton
public class ServicesMonitor implements ServiceChangeListener {

	private static final Logger log = LoggerFactory.getLogger(ServicesMonitor.class);

	private final Map<Service, ServiceStatus> services;

	@Inject
	public ServicesMonitor() {
		this.services = new MapMaker().weakKeys().makeMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.co.q3c.v7.base.services.ServicesMonitor#registerService(uk.co.q3c.v7.base.services.Service)
	 */
	synchronized public void registerService(Service service) {
		ServiceStatus serviceStatus = new ServiceStatus();
		services.put(service, serviceStatus);
		service.addChangeListener(this);
		log.debug("registered service '{}'", service.getName());

	}

	@Override
	synchronized public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {

		ServiceStatus status = services.get(service);
		status.setPreviousStatus(fromStatus);
		status.setCurrentStatus(toStatus);
		status.setStatusChangeTime(DateTime.now());
		if (service.isStarted()) {
			status.setLastStartTime(DateTime.now());
		}
		if (service.isStopped()) {
			status.setLastStopTime(DateTime.now());
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

}