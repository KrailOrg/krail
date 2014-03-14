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
import java.util.Properties;
import java.util.Set;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.config.ApplicationConfigurationService;
import uk.co.q3c.v7.base.config.InheritingConfiguration;
import uk.co.q3c.v7.base.services.AbstractServiceI18N;
import uk.co.q3c.v7.base.services.AutoStart;
import uk.co.q3c.v7.base.services.Service;
import uk.co.q3c.v7.i18n.Translate;
import uk.co.q3c.v7.quartz.scheduler.SchedulerConfiguration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultQuartzService extends AbstractServiceI18N implements QuartzService {

	private final Set<SchedulerListener> schedulerListeners;
	private Scheduler scheduler;
	@AutoStart
	private final ApplicationConfigurationService applicationConfigurationService;
	private static Logger log = LoggerFactory.getLogger(DefaultQuartzService.class);

	private final Map<String, SchedulerConfiguration> schedulerConfigurations;
	private final SchedulerFactory schedulerFactory;
	private final InheritingConfiguration applicationConfiguration;

	@Inject
	public DefaultQuartzService(Translate translate, Map<String, SchedulerConfiguration> schedulerConfigurations,
			Set<SchedulerListener> schedulerListeners, SchedulerFactory schedulerFactory,
			ApplicationConfigurationService applicationConfigurationService,
			InheritingConfiguration applicationConfiguration) {
		super(translate);
		this.schedulerConfigurations = schedulerConfigurations;
		this.schedulerListeners = schedulerListeners;
		this.schedulerFactory = schedulerFactory;
		this.applicationConfigurationService = applicationConfigurationService;
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	public Status start() throws Exception {
		// constructSchedulers(schedulerConfigurations);
		scheduleJobs();
		attachJobListeners();
		attachTriggerListeners();
		attachSchedulerListeners();
		startScheduler();
		return status;
	}

	private void startScheduler() {
	}

	private void attachSchedulerListeners() throws SchedulerException {
		for (SchedulerListener listener : schedulerListeners) {
			scheduler.getListenerManager().addSchedulerListener(listener);
		}
	}

	private void attachTriggerListeners() {
	}

	private void attachJobListeners() {
	}

	private void scheduleJobs() {
	}

	protected void constructSchedulers() {
		for (Entry<String, SchedulerConfiguration> configurationEntry : schedulerConfigurations.entrySet()) {
			SchedulerConfiguration configuration = configurationEntry.getValue();
			// force the scheduler name to be the same as map key to avoid errors
			configuration.name(configurationEntry.getKey());
			// take what's been coded first (in a Guice module), then
			// check application configuration for entries, merge if there is
			Properties appConfigProperties = applicationConfiguration.getProperties("quartz-"
					+ configurationEntry.getKey());
			configuration.getProperties().putAll(appConfigProperties);
			// check for a properties file, merge if there is

			// create a factory

			// set the configuration

			// create the scheduler

			// start if auto-start
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
	 * Does nothing, this service is not dependent on other services, and therefore does not need to react to status
	 * change in any other service.
	 * 
	 * @see uk.co.q3c.v7.base.services.ServiceStatusChangeListener#serviceStatusChange(uk.co.q3c.v7.base.services.Service,
	 *      uk.co.q3c.v7.base.services.Service.Status, uk.co.q3c.v7.base.services.Service.Status)
	 */
	@Override
	public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {

	}

}