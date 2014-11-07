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
package uk.q3c.krail.quartz.service;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import uk.q3c.krail.base.config.ApplicationConfigurationService;
import uk.q3c.krail.base.services.AbstractServiceI18N;
import uk.q3c.krail.base.services.Dependency;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.quartz.job.JobEntry;
import uk.q3c.krail.quartz.job.JobListenerEntry;
import uk.q3c.krail.quartz.job.KrailJobFactory;
import uk.q3c.krail.quartz.scheduler.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Creates schedulers and attaches listeners and jobs to those schedulers, as defined in the associated Guice modules.
 * Note that this class has a dependency on the {@link ApplicationConfigurationService}, because the
 * {@link DefaultKrailSchedulerFactory} may need the ApplicationConfiguration object
 * <p/>
 *
 * @author David Sowerby
 * @see SchedulerModuleBase
 * @see DefaultSchedulerModule
 */
public class DefaultQuartzService extends AbstractServiceI18N implements QuartzService {

    private final Set<SchedulerListenerEntry> schedulerListeners;
    // config only needed at the start
    @Dependency(stopOnStop = false)
    private final ApplicationConfigurationService applicationConfigurationService;

    private final Map<String, SchedulerConfiguration> schedulerConfigurations;
    private final Set<JobEntry> jobs;
    private final Set<JobListenerEntry> jobListeners;
    private final Provider<KrailSchedulerFactory> factoryProvider;
    private final Set<TriggerListenerEntry> triggerListeners;
    private final SchedulerProvider schedulerProvider;
    private final Injector injector;
    private final KrailJobFactory jobFactory;

    @Inject
    public DefaultQuartzService(Translate translate, Map<String, SchedulerConfiguration> schedulerConfigurations,
                                Set<SchedulerListenerEntry> schedulerListeners,
                                Set<TriggerListenerEntry> triggerListeners, Provider<KrailSchedulerFactory>
            factoryProvider, ApplicationConfigurationService applicationConfigurationService,
                                SchedulerProvider schedulerProvider, Injector injector, KrailJobFactory jobFactory,
                                Set<JobEntry> jobs, Set<JobListenerEntry> jobListeners) {
        super(translate);
        this.schedulerConfigurations = schedulerConfigurations;
        this.schedulerListeners = schedulerListeners;
        this.factoryProvider = factoryProvider;
        this.applicationConfigurationService = applicationConfigurationService;
        this.triggerListeners = triggerListeners;
        this.schedulerProvider = schedulerProvider;
        this.injector = injector;
        this.jobFactory = jobFactory;
        this.jobs = jobs;
        this.jobListeners = jobListeners;
    }

    @Override
    protected void doStart() throws Exception {
        constructSchedulers();
        attachTriggerListeners();
        attachSchedulerListeners();
        scheduleJobs();
        attachJobListeners();
        startSchedulers();
    }

    /**
     * Starts all the schedulers which have a configuration property of autoStart==true, otherwise the scheduler
     * remains
     * in a standby state.
     *
     * @throws SchedulerException
     */
    private void startSchedulers() throws SchedulerException {
        for (Entry<String, SchedulerConfiguration> configurationEntry : schedulerConfigurations.entrySet()) {
            SchedulerConfiguration configuration = configurationEntry.getValue();
            if (configuration.isAutoStart()) {
                KrailScheduler scheduler = schedulerProvider.get(configuration.getName());
                scheduler.start();
            }
        }

    }

    /**
     * Attaches all the scheduler listeners defined in {@link #schedulerListeners}, to the appropriate schedulers.
     *
     * @throws SchedulerException
     */
    private void attachSchedulerListeners() throws SchedulerException {
        for (SchedulerListenerEntry entry : schedulerListeners) {
            KrailScheduler scheduler = schedulerProvider.get(entry.getSchedulerName());
            SchedulerListener listener = injector.getInstance(entry.getListenerClass());
            scheduler.getListenerManager()
                     .addSchedulerListener(listener);
        }
    }

    /**
     * Attaches all the trigger listeners, to the appropriate schedulers, as defined in {@link #triggerListeners}
     *
     * @throws SchedulerException
     */
    private void attachTriggerListeners() throws SchedulerException {
        for (TriggerListenerEntry entry : triggerListeners) {
            KrailScheduler scheduler = schedulerProvider.get(entry.getSchedulerName());
            KrailTriggerListener listener = injector.getInstance(entry.getListenerClass());
            listener.setName(entry.getTriggerName());
            scheduler.getListenerManager()
                     .addTriggerListener(listener);
        }
    }

    private void scheduleJobs() throws SchedulerException {
        for (Entry<String, SchedulerConfiguration> configurationEntry : schedulerConfigurations.entrySet()) {
            SchedulerConfiguration configuration = configurationEntry.getValue();
            String schedulerName = configuration.getName();
            KrailScheduler scheduler = schedulerProvider.get(schedulerName);
            scheduler.setJobFactory(jobFactory);
            for (JobEntry jobEntry : jobs) {
                if (jobEntry.getSchedulerName()
                            .equals(schedulerName)) {
                    scheduler.scheduleJob(jobEntry.getJobBuilder()
                                                  .build(), jobEntry.getTriggerBuilder()
                                                                    .build());
                }
            }
        }
    }

    private void attachJobListeners() throws SchedulerException {
        for (Entry<String, SchedulerConfiguration> configurationEntry : schedulerConfigurations.entrySet()) {
            SchedulerConfiguration configuration = configurationEntry.getValue();
            String schedulerName = configuration.getName();
            KrailScheduler scheduler = schedulerProvider.get(schedulerName);
            for (JobListenerEntry entry : jobListeners) {
                if (entry.getSchedulerName()
                         .equals(schedulerName)) {
                    JobListener listener = jobFactory.newJobListener(entry);
                    scheduler.getListenerManager()
                             .addJobListener(listener, entry.getJobMatchers());
                }
            }

        }
    }

    /**
     * Constructs all the schedulers defined in {@link #schedulerConfigurations}
     *
     * @throws SchedulerException
     */
    protected void constructSchedulers() throws SchedulerException {
        for (Entry<String, SchedulerConfiguration> configurationEntry : schedulerConfigurations.entrySet()) {
            SchedulerConfiguration configuration = configurationEntry.getValue();
            // force the scheduler name to be the same as map key to avoid errors
            configuration.name(configurationEntry.getKey());
            // create a factory
            KrailSchedulerFactory factory = factoryProvider.get();
            // the factory will combine scheduler configuration sources as needed
            factory.createScheduler(configuration);
        }
    }

    @Override
    public void doStop() throws Exception {
        setStatus(Status.STOPPED);
    }

}