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

import static com.google.inject.multibindings.Multibinder.*;

import org.quartz.JobBuilder;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import uk.co.q3c.v7.quartz.service.QuartzService;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * An abstract base class to simplify the specification of jobs to be scheduled. The {@link QuartzService} instantiates
 * the Quartz {@link Scheduler}, and then schedules the jobs specified by sub-class(es) of this base class.
 * <p>
 * {@link JobListener} is not currently supported through Guice integration, though there is nothing to prevent their
 * use in your code. According to the {@link JobListener} javadoc, "In general, applications that use a
 * <code>Scheduler</code> will not have use for this mechanism."
 * <p>
 * V7 assumes a Scheduler will be used
 * 
 * @author David Sowerby
 * 
 */
public abstract class QuartzJobModule extends AbstractModule {

	private Multibinder<JobEntry> jobs;

	@Override
	protected void configure() {
		jobs = newSetBinder(binder(), JobEntry.class);
		addJobs();

	}

	/**
	 * Override this method to add jobs as required, using calls to {@link #addJob(String, JobBuilder, TriggerBuilder)}
	 */
	protected abstract void addJobs();

	/**
	 * Add the job 'specification' by supplying a prepared jobBuilder and triggerBuilder. Do NOT invoke the build()
	 * method of either - the idea is to leave the building until the {@link QuartzService} is started, in keeping with
	 * the Guice recommendation to keep modules fast and side-effect free.
	 * <p>
	 * 
	 * @see https://code.google.com/p/google-guice/wiki/ModulesShouldBeFastAndSideEffectFree
	 * 
	 * @param schedulerName
	 * @param jobBuilder
	 * @param triggerBuilder
	 */
	protected void addJob(String schedulerName, JobBuilder jobBuilder, TriggerBuilder<? extends Trigger> triggerBuilder) {
		JobEntry entry = new JobEntry(schedulerName, jobBuilder, triggerBuilder);
		jobs.addBinding().toInstance(entry);
	}
}
