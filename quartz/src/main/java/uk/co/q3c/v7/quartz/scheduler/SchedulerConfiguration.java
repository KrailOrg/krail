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

import uk.co.q3c.v7.base.config.ApplicationConfiguration;
import uk.co.q3c.v7.quartz.service.DefaultQuartzService;

/**
 * Provides configuration for the {@link V7Scheduler} (a minor variation of the Quartz {@link Scheduler}). The
 * configuration source may be taken from one of three places. Please note the order of importance of these:
 * <ol>
 * <li>coded in a Guice module
 * <li>taken from a section of {@link ApplicationConfiguration}. Specify the section name with
 * {@link #configSectionName}
 * <li>taken from a dedicated properties file. Specify the file name with {@link #propertyFileName(String)}
 * </ol>
 * The 2nd and 3rd options do not need to exist. If they do, they will each override the property values of the
 * preceding methods, where properties with the same key exist in more than one source.
 * <p>
 * The logic for combining these sources is actually in the {@link DefaultV7SchedulerFactory}, and invoked only when
 * services are being started. This helps to avoid having conditional logic in the Guice modules.
 * <p>
 * Sets the org.quartz.threadPool.threadCount to 1 as a default, as without it, the creation of a Scheduler will fail
 * 
 * @see https://code.google.com/p/google-guice/wiki/AvoidConditionalLogicInModules
 * @see http://quartz-scheduler.org/documentation/quartz-2.x/configuration/
 * @see http://quartz-scheduler.org/documentation/quartz-1.x/cookbook/MultipleSchedulers
 * @author David Sowerby
 * 
 */
public class SchedulerConfiguration {

	private final Properties properties = new Properties();
	private String propertyFileName;
	private String configSectionName;
	private boolean autoStart;

	protected SchedulerConfiguration() {
		super();
		setProperty("org.quartz.threadPool.threadCount", Integer.toString(1));
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * Set the name for the scheduler. Note that during the construction of the scheduler in
	 * {@link DefaultQuartzService}, this is set to be the same as the key used to map this configuration in
	 * {@link DefaultSchedulerModule}. This is to avoid naming confusion, between the map key and the name of the
	 * scheduler itself.
	 * 
	 * @param instanceName
	 * @return
	 */
	public SchedulerConfiguration name(String instanceName) {
		setProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, instanceName);
		return this;
	}

	private void setProperty(String key, String value) {
		properties.put(key, value);
	}

	public String getName() {
		return properties.getProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME).toString();
	}

	/**
	 * If you wish to use a specific property file to configure the scheduler, specify the name here. (option 2 in the
	 * class javadoc). The path is assumed to be in from ResourceUtils#configurationDirectory()
	 * 
	 * @param filename
	 * @return
	 */
	public SchedulerConfiguration propertyFileName(String filename) {
		this.propertyFileName = filename;
		return this;
	}

	/**
	 * If you wish to use a section of {@link ApplicationConfiguration} to configure the scheduler, specify the section
	 * name here. (option 1 in the class javadoc). The path is assumed to be in from
	 * ResourceUtils#configurationDirectory()
	 * 
	 * @param filename
	 * @return
	 */
	public SchedulerConfiguration useConfigSection(String sectionName) {
		this.configSectionName = sectionName;
		return this;
	}

	public String getPropertyFileName() {
		return propertyFileName;
	}

	public String getConfigSectionName() {
		return configSectionName;
	}

	/**
	 * When true, the scheduler is started by the QuartzService. If false, the scheduler is created by the Quartz
	 * service, but not started.
	 * 
	 * @param autoStart
	 * @return
	 */
	public SchedulerConfiguration autoStart(boolean autoStart) {
		this.autoStart = autoStart;
		return this;
	}

	public boolean isAutoStart() {
		return autoStart;
	}
}
