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
package uk.q3c.krail.quartz.scheduler;

import org.quartz.Scheduler;
import uk.q3c.krail.base.guice.BaseGuiceServletInjector;
import uk.q3c.krail.quartz.job.JobModuleBase;

/**
 * Provides the configuration for the {@link QuartzService} to construct the Quartz {@link Scheduler}. This module also
 * defines the binding for the {@link KrailSchedulerFactory}. You can create additional schedulers, scheduler listeners
 * and
 * / or trigger listeners by creating another sub-class of {@link SchedulerModuleBase} and adding it to your
 * injector in your sub-class of {@link BaseGuiceServletInjector} module based on
 * <p/>
 * To define jobs, either use sub-classes of {@link JobModuleBase}, or obtain an instance of {@link KrailScheduler}
 * from
 * {@link SchedulerProvider} and add jobs directly
 *
 * @author David Sowerby
 */
public class DefaultSchedulerModule extends SchedulerModuleBase {

    @Override
    protected void configure() {
        super.configure();
        bindSchedulerFactory();

    }

    protected void bindSchedulerFactory() {
        bind(KrailSchedulerFactory.class).to(DefaultKrailSchedulerFactory.class);
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
