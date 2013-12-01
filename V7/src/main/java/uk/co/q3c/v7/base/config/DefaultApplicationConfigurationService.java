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
package uk.co.q3c.v7.base.config;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ResourceUtils;
import uk.co.q3c.v7.base.services.AbstractServiceI18N;
import uk.co.q3c.v7.base.services.Service;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

/**
 * This service provides a mechanism which can be used to manage the whole application configuration. It uses the Apache
 * Commons Configuration library, and specifically a {@link CompositeConfiguration} so that the configuration can be
 * extended in any way the developer wishes.
 * <p>
 * We have a preference for using HierarchicalINIConfiguration to provide a good level of human readability (when the
 * key value pairs are stored in a file) but any of the Apache {@link Configuration} implementations could be used.
 * <p>
 * See the {@link Service} javadoc for more detail about Services
 * 
 * @author David Sowerby
 * 
 */
@Singleton
public class DefaultApplicationConfigurationService extends AbstractServiceI18N implements
		ApplicationConfigurationService {

	private static Logger log = LoggerFactory.getLogger(DefaultApplicationConfigurationService.class);

	private final Deque<File> queue;
	private CompositeConfiguration configuration;

	@Inject
	protected DefaultApplicationConfigurationService(Translate translate) {
		super(translate);
		this.queue = new ArrayDeque<>();
		configure();
	}

	/**
	 * Override this method if you want to change the configuration files to use, or change the name and description
	 * keys. You will also then need to change the Guice binding for {@link ApplicationConfigurationService}
	 */
	protected void configure() {
		addConfiguration("V7.ini");
		setNameKey(LabelKey.Application_Configuration_Service);
		setDescriptionKey(DescriptionKey.Application_Configuration_Service);
	}

	/**
	 * @throws ConfigurationException
	 * 
	 */
	@Override
	public Status start() throws ConfigurationException {
		configuration = new CompositeConfiguration();
		Iterator<File> iter = queue.iterator();
		while (iter.hasNext()) {
			File file = iter.next();
			log.debug("adding configuration from {}", file.getAbsolutePath());
			if (file.exists()) {
				HierarchicalINIConfiguration config = new HierarchicalINIConfiguration(file);
				configuration.addConfiguration(config);
			} else {
				throw new ConfigurationException("Configuration file " + file.getAbsolutePath() + " does not exist");
			}
		}
		return Status.STARTED;
	}

	@Override
	public Status stop() {
		return Status.STOPPED;
	}

	@Override
	public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
		// nothing to do
	}

	/**
	 * 
	 @see uk.co.q3c.v7.base.config.ApplicationConfigurationService#getConfiguration()
	 */
	@Override
	public CompositeConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * 
	 @see uk.co.q3c.v7.base.config.ApplicationConfigurationService#addConfiguration(org.apache.commons.configuration.Configuration)
	 */
	@Override
	public void addConfiguration(File configurationFile) {
		queue.push(configurationFile);

	}

	@Override
	public void addConfiguration(String configurationFileName) {
		File f = new File(ResourceUtils.applicationBaseDirectory(), configurationFileName);
		addConfiguration(f);
	}
}
