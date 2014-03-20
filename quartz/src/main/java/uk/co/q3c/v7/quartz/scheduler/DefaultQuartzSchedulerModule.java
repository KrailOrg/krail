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

import org.quartz.Scheduler;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.quartz.job.QuartzJobModule;

/**
 * Provides the configuration for the {@link QuartzService} to construct the Quartz {@link Scheduler}. This module also
 * defines the binding for the {@link V7SchedulerFactory}. You can create additional schedulers, scheduler listeners and
 * / or trigger listeners by creating another sub-class of {@link QuartzSchedulerModuleBase} and adding it to your
 * injector in your sub-class of {@link BaseGuiceServletInjector} module based on
 * <p>
 * To define jobs, either use sub-classes of {@link QuartzJobModule}, or obtain an instance of {@link V7Scheduler} from
 * {@link SchedulerProvider} and add jobs directly
 * 
 * @author David Sowerby
 * 
 */
public class DefaultQuartzSchedulerModule extends QuartzSchedulerModuleBase {

	@Override
	protected void configure() {
		super.configure();
		bindSchedulerFactory();

	}

	protected void bindSchedulerFactory() {
		bind(V7SchedulerFactory.class).to(DefaultV7SchedulerFactory.class);
	}

	@Override
	protected void addConfigurations() {
		addConfiguration("default", true);
	}

	@Override
	protected void addSchedulerListeners() {
	}

	@Override
	protected void addTriggerListeners() {
	}

}
