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
package uk.co.q3c.v7.base.guice.services;

import java.util.Deque;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.services.Service.Status;

import com.google.common.collect.ImmutableList;

/**
 * Monitors instances of {@link Service} implementations, and keeps a history of status changes Acknowledgement:
 * developed from code contributed by https://github.com/lelmarir
 */
@Singleton
public class ServicesMonitor implements ServiceStatusChangeListener {

	private static final Logger log = LoggerFactory.getLogger(ServicesMonitor.class);

	private final Map<Service, ServiceStatus> services;

	private final Deque<StatusChange> statusLog = new LinkedBlockingDeque<>();

	@Inject
	public ServicesMonitor() {
		this.services = new WeakHashMap<>();
	}

	/**
	 * Registers the service so status can be tracked, and attaches a listener to the service to monitor status changes.
	 * 
	 * @param service
	 */
	public void registerService(Service service) {
		ServiceStatus serviceStatus = new ServiceStatus();
		services.put(service, serviceStatus);
		service.addListener(this);

	}

	@Override
	public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
		StatusChange statusChange = new StatusChange();
		statusChange.setDateTime(DateTime.now());
		statusChange.setFromStatus(fromStatus);
		statusChange.setToStatus(toStatus);
		statusLog.add(statusChange);
	}

	/**
	 * Stop all registered services
	 */
	public void stop() {
		// TODO Auto-generated method stub

	}

	public ImmutableList<Service> getRegisteredServices() {
		return ImmutableList.copyOf(services.keySet());

	}

	public void clearStatusLog() {
		statusLog.clear();
	}

	public Deque<StatusChange> getStatusLog() {
		return statusLog;
	}

}