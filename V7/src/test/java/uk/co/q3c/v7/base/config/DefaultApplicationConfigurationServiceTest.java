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

import static com.google.common.base.Preconditions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.services.Service.Status;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinService;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultApplicationConfigurationServiceTest {

	Map<Integer, IniFileConfig> iniFiles;

	@Inject
	Translate translate;

	DefaultApplicationConfigurationService service;

	static File iniDir = new File("src/test/java");
	static VaadinService vaadinService;

	@Inject
	ApplicationConfiguration configuration;

	@BeforeClass
	public static void setupClass() {
		vaadinService = mock(VaadinService.class);
		when(vaadinService.getBaseDirectory()).thenReturn(iniDir);
		VaadinService.setCurrent(vaadinService);
	}

	@Before
	public void setup() {
		iniFiles = new HashMap<>();
		configuration.clear();
		service = new DefaultApplicationConfigurationService(translate, configuration, iniFiles);
	}

	@Test
	public void loadOneFile() throws ConfigurationException {
		// given
		addConfig("V7.ini", 0, false);
		// when
		service.start();
		// then (one configuration is the in memory one added automatically)
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(2);
		assertThat(configuration.getBoolean("test")).isTrue();
		assertThat(configuration.getString("dbUser")).isEqualTo("monty");

	}

	@Test
	public void loadTwoFiles() throws ConfigurationException {
		// given
		addConfig("V7.ini", 0, false);
		addConfig("test.V7.ini", 1, false);
		// when
		service.start();
		// then (one configuration is the in memory one added automatically)
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(3);
		assertThat(configuration.getBoolean("test")).isTrue();
		assertThat(configuration.getString("dbUser")).isEqualTo("python");

	}

	@Test
	public void stopStart() throws ConfigurationException {
		// given
		addConfig("V7.ini", 0, false);
		addConfig("test.V7.ini", 1, false);
		configuration.addProperty("in memory", "memory");
		// when
		service.start();
		// then (one configuration is the in memory one added automatically)
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(3);
		assertThat(service.getStatus()).isEqualTo(Status.STARTED);
		assertThat(configuration.getString("in memory")).isEqualTo("memory");
		// then
		service.stop();
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(1);
		assertThat(service.getStatus()).isEqualTo(Status.STOPPED);
		assertThat(configuration.getString("in memory")).isNull();
	}

	@Test(expected = ConfigurationException.class)
	public void fileMissing_notOptional() throws ConfigurationException {

		// given
		addConfig("rubbish.ini", 0, false);
		// when
		service.start();
		// then
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(1);
	}

	@Test
	public void fileMissing_optional() throws ConfigurationException {

		// given
		addConfig("rubbish.ini", 0, true);
		// when
		service.start();
		// then
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(1);
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

	protected void addConfig(String filename, int index, boolean optional) {
		checkNotNull(filename);
		IniFileConfig ifc = new IniFileConfig(filename, optional);
		iniFiles.put(index, ifc);
	}
}
