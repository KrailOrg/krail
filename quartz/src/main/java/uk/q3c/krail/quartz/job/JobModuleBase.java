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
package uk.q3c.krail.quartz.job;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.quartz.*;
import uk.q3c.krail.quartz.service.QuartzService;

import java.util.List;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

/**
 * An abstract base class to simplify the specification of jobs to be scheduled. The {@link QuartzService} instantiates
 * the Quartz {@link Scheduler}, and then schedules the jobs specified by sub-class(es) of this base class.
 * <p/>
 * {@link JobListener} is not currently supported through Guice integration, though there is nothing to prevent their
 * use in your code. According to the {@link JobListener} javadoc, "In general, applications that use a
 * <code>Scheduler</code> will not have use for this mechanism."
 * <p/>
 * Krail assumes a Scheduler will be used
 *
 * @author David Sowerby
 */
public abstract class JobModuleBase extends AbstractModule {

    private Multibinder<JobListenerEntry> jobListeners;
    private Multibinder<JobEntry> jobs;

    @Override
    protected void configure() {
        jobs = newSetBinder(binder(), JobEntry.class);
        jobListeners = newSetBinder(binder(), JobListenerEntry.class);
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
     * <p/>
     *
     * @param schedulerName
     * @param jobBuilder
     * @param triggerBuilder
     *
     * @see https://code.google.com/p/google-guice/wiki/ModulesShouldBeFastAndSideEffectFree
     */
    protected void addJob(String schedulerName, JobBuilder jobBuilder, TriggerBuilder<? extends Trigger>
            triggerBuilder) {
        JobEntry entry = new JobEntry(schedulerName, jobBuilder, triggerBuilder);
        jobs.addBinding()
            .toInstance(entry);
    }

    protected void addJobListener(String schedulerName, Class<? extends JobListener> listenerClass, JobKey jobKey) {
        JobListenerEntry entry = new JobListenerEntry(schedulerName, listenerClass);
        entry.listenTo(jobKey);
        jobListeners.addBinding()
                    .toInstance(entry);
    }

    protected void addJobListener(String schedulerName, Class<? extends JobListener> listenerClass,
                                  Matcher<JobKey> matcher) {
        JobListenerEntry entry = new JobListenerEntry(schedulerName, listenerClass);
        entry.listenTo(matcher);
        jobListeners.addBinding()
                    .toInstance(entry);
    }

    protected void addJobListener(String schedulerName, Class<? extends JobListener> listenerClass,
                                  List<Matcher<JobKey>> matchers) {
        JobListenerEntry entry = new JobListenerEntry(schedulerName, listenerClass);
        entry.listenTo(matchers);
        jobListeners.addBinding()
                    .toInstance(entry);
    }
}
