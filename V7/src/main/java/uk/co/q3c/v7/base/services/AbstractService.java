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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements basic Service functionality, including {@link Status} and {@link ServiceStatusChangeListener}
 * 
 * @author David Sowerby
 * 
 */
public abstract class AbstractService implements Service {
	private static Logger log = LoggerFactory.getLogger(AbstractService.class);
	private final List<ServiceStatusChangeListener> statusChangeListeners = new ArrayList<>();

	protected Status status = Status.INITIAL;

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public boolean isStarted() {
		return status == Status.STARTED;
	}

	@Override
	public void addListener(ServiceStatusChangeListener listener) {
		statusChangeListeners.add(listener);
	}

	@Override
	public void removeListener(ServiceStatusChangeListener listener) {
		statusChangeListeners.remove(listener);
	}

	protected void fireListeners(Status previousStatus) {
		log.debug("firing status change listeners in {}.  Status is now {}", this.getName(), this.getStatus());
		for (ServiceStatusChangeListener listener : statusChangeListeners) {
			listener.serviceStatusChange(this, previousStatus, status);
		}
	}

	@Override
	public void setStatus(Status status) {
		if (status != this.status) {
			Status previousStatus = this.status;
			this.status = status;
			fireListeners(previousStatus);
		}
	}
}
