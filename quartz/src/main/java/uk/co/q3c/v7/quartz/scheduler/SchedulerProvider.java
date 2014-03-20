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
import org.quartz.impl.SchedulerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

/**
 * Inject this class to acess all active schedulers. This is a wrapper class for {@link SchedulerRepository}, with the
 * addition of a default scheduler.
 * 
 * @see http://quartz-scheduler.org/documentation/quartz-1.x/cookbook/MultipleSchedulers
 * @author David Sowerby
 * 
 */
public class SchedulerProvider {
	private static Logger log = LoggerFactory.getLogger(SchedulerProvider.class);
	private String defaultSchedulerName;

	@Inject
	protected SchedulerProvider() {
	}

	/**
	 * Returns the default scheduler. If other schedulers are required, retrieve them by {@link #get(String)}
	 * 
	 * @see com.google.inject.Provider#get()
	 */
	public V7Scheduler get() {
		log.debug("Getting default scheduler,defaultSchedulerName='{}'", defaultSchedulerName);
		Collection<Scheduler> schedulers = SchedulerRepository.getInstance().lookupAll();
		Scheduler defaultScheduler;
		// make sure the default has not been removed from the repo
		if (defaultSchedulerName != null) {
			defaultScheduler = SchedulerRepository.getInstance().lookup(defaultSchedulerName);
			if (defaultScheduler != null) {
				return (V7Scheduler) defaultScheduler;
			}
		}
		if (schedulers.size() == 0) {
			throw new ProvisionException("No schedulers have been defined");
		} else { // take the next one as default
			Iterator<Scheduler> iterator = schedulers.iterator();
			V7Scheduler scheduler = (V7Scheduler) iterator.next();
			defaultScheduler = scheduler;
			return scheduler;
		}

	}

	/**
	 * returns the scheduler for the specified name, or null, if there is no scheduler with the required name
	 * 
	 * @param schedulerName
	 * @return
	 */
	public V7Scheduler get(String schedulerName) {
		return (V7Scheduler) SchedulerRepository.getInstance().lookup(schedulerName);

	}

	/**
	 * Sets the default scheduler.
	 * 
	 * @param defaultScheduler
	 */
	public synchronized void setDefaultScheduler(V7Scheduler defaultScheduler) {
		setDefaultScheduler(defaultScheduler.getMetaData().getSchedulerName());
	}

	/**
	 * Sets default scheduler to the one with the specified name. Throws a ProvisionException is there is no scheduler
	 * with that name in the {@link SchedulerRepository}
	 * 
	 * @param schedulerName
	 */
	public synchronized void setDefaultScheduler(String schedulerName) {
		V7Scheduler scheduler = (V7Scheduler) SchedulerRepository.getInstance().lookup(schedulerName);
		if (scheduler == null) {
			throw new ProvisionException("Unable to set default scheduler to " + schedulerName
					+ ", there is no scheduler with that name");
		}
		this.defaultSchedulerName = schedulerName;

	}

}
