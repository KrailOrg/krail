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

import static org.assertj.core.api.Assertions.*;

import javax.inject.Inject;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.LabelKey;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinService;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultApplicationConfigurationServiceTest {

	@Inject
	DefaultApplicationConfigurationService service;

	VaadinService vs;

	@Test
	public void load() throws ConfigurationException {
		// given
		// when
		service.start();
		CompositeConfiguration configuration = service.getConfiguration();
		// then (one configuration is the in memory one added automatically)
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(2);
		assertThat(configuration.getBoolean("test")).isTrue();
		assertThat(configuration.getString("dbUser")).isEqualTo("monty");
		// given
		// when
		service.addConfiguration("test7.V7.ini");
		// then
		assertThat(configuration.getBoolean("test")).isTrue();
		assertThat(configuration.getString("dbUser")).isEqualTo("monty");
		// when
		service.stop();
		service.start();
		// then
		configuration = service.getConfiguration();
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(3);
		assertThat(configuration.getBoolean("test")).isTrue();
		assertThat(configuration.getString("dbUser")).isEqualTo("python");
	}

	@Test(expected = ConfigurationException.class)
	public void fileMissing() throws ConfigurationException {

		// given

		// when
		service.addConfiguration("rubbish.ini");
		// then
		service.start();

	}

	@Test
	public void i18N() {

		// given

		// when

		// then
		assertThat(service.getNameKey()).isEqualTo(LabelKey.Application_Configuration_Service);
		assertThat(service.getName()).isEqualTo("Application Configuration Service");
		assertThat(service.getDescriptionKey()).isEqualTo(DescriptionKey.Application_Configuration_Service);
		assertThat(service.getDescription()).isEqualTo("This service loads the application configuration from V7.ini");

	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
			}

		};
	}
}
