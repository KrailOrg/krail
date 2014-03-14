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
package uk.co.q3c.v7.quartz.scheduler;

import java.util.Collection;
import java.util.Iterator;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

/**
 * @see http://quartz-scheduler.org/documentation/quartz-1.x/cookbook/MultipleSchedulers
 * @author David Sowerby
 * 
 */
public class SchedulerProvider implements Provider<Scheduler> {
	private String defaultName;

	@Inject
	protected SchedulerProvider() {
	}

	@Override
	public Scheduler get() {
		Scheduler scheduler = SchedulerRepository.getInstance().lookup(defaultName);
		// could have been removed
		if (scheduler == null) {
			Collection<Scheduler> schedulers = SchedulerRepository.getInstance().lookupAll();
			if (schedulers.size() == 0) {
				throw new ProvisionException("No schedulers have been defined");
			}
			Iterator<Scheduler> iterator = schedulers.iterator();
			scheduler = iterator.next();
			try {
				defaultName = scheduler.getSchedulerName();
			} catch (SchedulerException e) {
				throw new ProvisionException("Error retrieving scheduler name, see nested exception", e);
			}
		}
		return scheduler;
	}

	public Scheduler get(String schedulerName) {
		return SchedulerRepository.getInstance().lookup(schedulerName);

	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

}
