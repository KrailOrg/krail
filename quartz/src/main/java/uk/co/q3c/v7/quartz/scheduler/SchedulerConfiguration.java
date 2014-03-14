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

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import uk.co.q3c.v7.base.config.InheritingConfiguration;

/**
 * Provides configuration for the Quartz {@link Scheduler}. The configuration source may be taken from one of three
 * places. Please note the order of importance of these:
 * <ol>
 * <li>coded in a Guice module
 * <li>taken from the 'quartz-schedulerName' section of {@link InheritingConfiguration} ... for example
 * [quartz-scheduler1]
 * <li>taken from a dedicated properties file with the name schedulerName.properties
 * </ol>
 * The 2nd and 3rd options do not need to exist. If they do, they will each override the property values of the
 * preceding methods.
 * <p>
 * 
 * @see http://quartz-scheduler.org/documentation/quartz-2.x/configuration/
 * @see http://quartz-scheduler.org/documentation/quartz-1.x/cookbook/MultipleSchedulers
 * @author David Sowerby
 * 
 */
public class SchedulerConfiguration {

	private Properties properties;

	public Properties getProperties() {
		return properties;
	}

	public SchedulerConfiguration name(String instanceName) {
		setProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, instanceName);
		return this;
	}

	private void setProperty(String key, String value) {
		properties.put(key, value);
	}

}
