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

import org.quartz.Scheduler;
import uk.q3c.krail.base.services.Service;
import uk.q3c.krail.quartz.job.JobModuleBase;
import uk.q3c.krail.quartz.scheduler.DefaultSchedulerModule;

/**
 * Helps provide a separation between the configuration and instantiation of Quartz, in line with the Guice best
 * practice describe here: https://code.google.com/p/google-guice/wiki/ModulesShouldBeFastAndSideEffectFree
 * <p/>
 * The implementation of this interface takes care of creating the Quartz scheduler, using the configuration specified
 * by {@link DefaultSchedulerModule} or a sub-class, and schedules jobs as defined by sub-classes of {@link
 * JobModuleBase}
 * <p/>
 * Further control of the Quartz scheduler can be achieved in any part of your code by injecting the {@link Scheduler},
 * and manipulating it as required.
 *
 * @author David Sowerby
 */
public interface QuartzService extends Service {

}
