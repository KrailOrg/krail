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
package uk.co.q3c.v7.quartz.job;

import org.quartz.Job;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Implements the Quartz {@link JobFactory} interface to utilise Guice for creating jobs
 * 
 * @author David Sowerby
 * 
 */
public class V7JobFactory implements JobFactory {

	private final Injector injector;

	@Inject
	protected V7JobFactory(Injector injector) {
		super();
		this.injector = injector;
	}

	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
		Job job = injector.getInstance(bundle.getJobDetail().getJobClass());
		return job;
	}

	public JobListener newJobListener(JobListenerEntry entry) {
		JobListener listener = injector.getInstance(entry.getListenerClass());
		return listener;
	}

}
