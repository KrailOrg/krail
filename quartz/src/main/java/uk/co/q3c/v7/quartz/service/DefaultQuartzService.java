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
package uk.co.q3c.v7.quartz.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.config.ApplicationConfigurationService;
import uk.co.q3c.v7.base.services.AbstractServiceI18N;
import uk.co.q3c.v7.base.services.AutoStart;
import uk.co.q3c.v7.base.services.Service;
import uk.co.q3c.v7.i18n.Translate;
import uk.co.q3c.v7.quartz.job.V7JobFactory;
import uk.co.q3c.v7.quartz.job.JobEntry;
import uk.co.q3c.v7.quartz.job.JobListenerEntry;
import uk.co.q3c.v7.quartz.scheduler.DefaultSchedulerModule;
import uk.co.q3c.v7.quartz.scheduler.DefaultV7SchedulerFactory;
import uk.co.q3c.v7.quartz.scheduler.SchedulerModuleBase;
import uk.co.q3c.v7.quartz.scheduler.SchedulerConfiguration;
import uk.co.q3c.v7.quartz.scheduler.SchedulerListenerEntry;
import uk.co.q3c.v7.quartz.scheduler.SchedulerProvider;
import uk.co.q3c.v7.quartz.scheduler.TriggerListenerEntry;
import uk.co.q3c.v7.quartz.scheduler.V7Scheduler;
import uk.co.q3c.v7.quartz.scheduler.V7SchedulerFactory;
import uk.co.q3c.v7.quartz.scheduler.V7TriggerListener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Creates schedulers and attaches listeners and jobs to those schedulers, as defined in the associated Guice modules.
 * Note that this class has a dependency on the {@link ApplicationConfigurationService}, because the
 * {@link DefaultV7SchedulerFactory} may need the ApplicationConfiguration object
 * <p>
 * 
 * @see SchedulerModuleBase
 * @see DefaultSchedulerModule
 * 
 * @author David Sowerby
 * 
 */
@Singleton
public class DefaultQuartzService extends AbstractServiceI18N implements QuartzService {

	private final Set<SchedulerListenerEntry> schedulerListeners;
	@AutoStart
	private final ApplicationConfigurationService applicationConfigurationService;
	private static Logger log = LoggerFactory.getLogger(DefaultQuartzService.class);

	private final Map<String, SchedulerConfiguration> schedulerConfigurations;
	private final Set<JobEntry> jobs;
	private final Set<JobListenerEntry> jobListeners;
	private final Provider<V7SchedulerFactory> factoryProvider;
	private final Set<TriggerListenerEntry> triggerListeners;
	private final SchedulerProvider schedulerProvider;
	private final Injector injector;
	private final V7JobFactory jobFactory;

	@Inject
	public DefaultQuartzService(Translate translate, Map<String, SchedulerConfiguration> schedulerConfigurations,
			Set<SchedulerListenerEntry> schedulerListeners, Set<TriggerListenerEntry> triggerListeners,
			Provider<V7SchedulerFactory> factoryProvider,
			ApplicationConfigurationService applicationConfigurationService, SchedulerProvider schedulerProvider,
			Injector injector, V7JobFactory jobFactory, Set<JobEntry> jobs, Set<JobListenerEntry> jobListeners) {
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
	public Status start() throws Exception {
		constructSchedulers();
		attachTriggerListeners();
		attachSchedulerListeners();
		scheduleJobs();
		attachJobListeners();
		startSchedulers();
		setStatus(Status.STARTED);
		return status;
	}

	/**
	 * Starts all the schedulers which have a configuration property of autoStart==true, otherwise the scheduler remains
	 * in a standby state.
	 * 
	 * @throws SchedulerException
	 */
	private void startSchedulers() throws SchedulerException {
		for (Entry<String, SchedulerConfiguration> configurationEntry : schedulerConfigurations.entrySet()) {
			SchedulerConfiguration configuration = configurationEntry.getValue();
			if (configuration.isAutoStart()) {
				V7Scheduler scheduler = schedulerProvider.get(configuration.getName());
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
			V7Scheduler scheduler = schedulerProvider.get(entry.getSchedulerName());
			SchedulerListener listener = injector.getInstance(entry.getListenerClass());
			scheduler.getListenerManager().addSchedulerListener(listener);
		}
	}

	/**
	 * Attaches all the trigger listeners, to the appropriate schedulers, as defined in {@link #triggerListeners}
	 * 
	 * @throws SchedulerException
	 */
	private void attachTriggerListeners() throws SchedulerException {
		for (TriggerListenerEntry entry : triggerListeners) {
			V7Scheduler scheduler = schedulerProvider.get(entry.getSchedulerName());
			V7TriggerListener listener = injector.getInstance(entry.getListenerClass());
			listener.setName(entry.getTriggerName());
			scheduler.getListenerManager().addTriggerListener(listener);
		}
	}

	private void scheduleJobs() throws SchedulerException {
		for (Entry<String, SchedulerConfiguration> configurationEntry : schedulerConfigurations.entrySet()) {
			SchedulerConfiguration configuration = configurationEntry.getValue();
			String schedulerName = configuration.getName();
			V7Scheduler scheduler = schedulerProvider.get(schedulerName);
			scheduler.setJobFactory(jobFactory);
			for (JobEntry jobEntry : jobs) {
				if (jobEntry.getSchedulerName().equals(schedulerName)) {
					scheduler.scheduleJob(jobEntry.getJobBuilder().build(), jobEntry.getTriggerBuilder().build());
				}
			}
		}
	}

	private void attachJobListeners() throws SchedulerException {
		for (Entry<String, SchedulerConfiguration> configurationEntry : schedulerConfigurations.entrySet()) {
			SchedulerConfiguration configuration = configurationEntry.getValue();
			String schedulerName = configuration.getName();
			V7Scheduler scheduler = schedulerProvider.get(schedulerName);
			for (JobListenerEntry entry : jobListeners) {
				if (entry.getSchedulerName().equals(schedulerName)) {
					JobListener listener = jobFactory.newJobListener(entry);
					scheduler.getListenerManager().addJobListener(listener, entry.getJobMatchers());
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
			V7SchedulerFactory factory = factoryProvider.get();
			// the factory will combine scheduler configuration sources as needed
			factory.createScheduler(configuration);
		}
	}

	@Override
	public Status stop() {
		try {
			setStatus(Status.STOPPED);
			log.info("Quartz service stopped");
			return status;
		} catch (Exception e) {
			setStatus(Status.FAILED_TO_STOP);
			log.warn("Failed to stop the Quartz service", e);
			return status;
		}
	}

	/**
	 * Does nothing. Although this service requires the {@link ApplicationConfigurationService} to be started before
	 * starting itself, it would not matter if the {@link ApplicationConfigurationService} stopped later.
	 * 
	 * @see uk.co.q3c.v7.base.services.ServiceStatusChangeListener#serviceStatusChange(uk.co.q3c.v7.base.services.Service,
	 *      uk.co.q3c.v7.base.services.Service.Status, uk.co.q3c.v7.base.services.Service.Status)
	 */
	@Override
	public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {

	}

}