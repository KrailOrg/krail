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

import static com.google.inject.multibindings.Multibinder.*;

import org.quartz.Scheduler;
import org.quartz.SchedulerListener;

import uk.co.q3c.v7.quartz.job.QuartzJobModule;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

/**
 * Provides a base class for configuring the schedulers. {@link QuartzService} constructs the {@link V7Scheduler}s from
 * the configurations provided by sub-classes of this module. To define jobs, either use sub-classes of
 * {@link QuartzJobModule}, or inject an instance of {@link Scheduler} and add jobs directly
 * 
 * @author David Sowerby
 * 
 */
public abstract class QuartzSchedulerModuleBase extends AbstractModule {

	private Multibinder<TriggerListenerEntry> triggerListeners;

	private MapBinder<String, SchedulerConfiguration> schedulerConfigurations;

	private Multibinder<SchedulerListenerEntry> schedulerListeners;

	@Override
	protected void configure() {

		schedulerConfigurations = MapBinder.newMapBinder(binder(), String.class, SchedulerConfiguration.class);
		schedulerListeners = newSetBinder(binder(), SchedulerListenerEntry.class);
		triggerListeners = newSetBinder(binder(), TriggerListenerEntry.class);

		addConfigurations();
		addSchedulerListeners();
		addTriggerListeners();
	}

	protected abstract void addSchedulerListeners();

	protected abstract void addTriggerListeners();

	protected abstract void addConfigurations();

	/**
	 * Adds a configuration to the binder with the specified name and autoStart. The configuration can then be modified
	 * further as required.
	 * 
	 * @param schedulerName
	 * @param autoStart
	 * @return
	 */
	protected SchedulerConfiguration addConfiguration(String schedulerName, boolean autoStart) {
		SchedulerConfiguration config = new SchedulerConfiguration();
		config.name(schedulerName).autoStart(autoStart);
		schedulerConfigurations.addBinding(schedulerName).toInstance(config);
		return config;
	}

	/**
	 * Creates and adds an entry in {@link #schedulerListeners}. An instance of listenerClass is instantiated using a
	 * Guice injector in the QuartzService
	 * 
	 * @param schedulerName
	 * @param listenerClass
	 */
	protected void addSchedulerListener(String schedulerName, Class<? extends SchedulerListener> listenerClass) {
		SchedulerListenerEntry entry = new SchedulerListenerEntry(schedulerName, listenerClass);
		schedulerListeners.addBinding().toInstance(entry);
	}

	/**
	 * Creates and adds an entry in {@link #triggerListeners}. An instance of listenerClass is instantiated using a
	 * Guice injector in the {@link uk.co.q3c.v7.quartz.service.QuartzService}
	 * 
	 * @param schedulerName
	 * @param listenerClass
	 */
	protected void addTriggerListener(String schedulerName, String triggerName,
			Class<? extends V7TriggerListener> listenerClass) {
		TriggerListenerEntry entry = new TriggerListenerEntry(schedulerName, triggerName, listenerClass);
		triggerListeners.addBinding().toInstance(entry);
	}

}
