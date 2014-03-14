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
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;

/**
 * Enables the use of V7Scheduler to provide I18N support, but is otherwise the same as StdSchedulerFactory
 * 
 * @author David Sowerby
 * 
 */
public class V7SchedulerFactory extends StdSchedulerFactory {
	private static Logger log = LoggerFactory.getLogger(V7SchedulerFactory.class);
	private final Translate translate;

	@Inject
	protected V7SchedulerFactory(Translate translate) {
		super();
		this.translate = translate;
	}

	@Override
	protected Scheduler instantiate(QuartzSchedulerResources rsrcs, QuartzScheduler qs) {
		Scheduler scheduler = new V7Scheduler(translate, qs);
		return scheduler;
	}

}
