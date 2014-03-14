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
import org.quartz.TriggerListener;
import org.quartz.ee.jmx.jboss.QuartzService;

import uk.co.q3c.v7.quartz.job.QuartzJobModule;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

/**
 * Provides the configuration for the {@link QuartzService} to construct the Quartz {@link Scheduler}. To define jobs,
 * either use sub-classes of {@link QuartzJobModule}, or inject an instance of {@link Scheduler} and add jobs directly
 * 
 * @author David Sowerby
 * 
 */
public class DefaultQuartzSchedulerModule extends AbstractModule {

	private Multibinder<SchedulerListener> schedulerListeners;
	private Multibinder<TriggerListener> triggerListeners;

	private MapBinder<String, SchedulerConfiguration> schedulerConfigurations;

	@Override
	protected void configure() {
		schedulerListeners = newSetBinder(binder(), SchedulerListener.class);
		triggerListeners = newSetBinder(binder(), TriggerListener.class);

		bind(V7SchedulerFactory.class).to(DefaultV7SchedulerFactory.class);
		bind(Scheduler.class).toProvider(SchedulerProvider.class).in(Singleton.class);

	}

}
