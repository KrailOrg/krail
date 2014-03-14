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

import org.quartz.JobListener;
import org.quartz.Scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * An abstract base class to simplify the specification of jobs to be scheduled. The {@link QuartzService} instantiates
 * the Quartz {@link Scheduler}, and then schedules the jobs specified by sub-class(es) of this base class.
 * 
 * @author David Sowerby
 * 
 */
public abstract class QuartzJobModule extends AbstractModule {
	private Multibinder<JobListener> jobListeners;

	@Override
	protected void configure() {
		jobListeners = newSetBinder(binder(), JobListener.class);

	}

	protected void addJobListener() {

	}

}
