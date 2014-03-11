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

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class ApplicationConfigurationTest {

	private HierarchicalINIConfiguration config1;
	private HierarchicalINIConfiguration config2;

	@Inject
	InheritingConfiguration configuration;
	private HierarchicalINIConfiguration config3;

	@Test
	public void override() throws ConfigurationException {

		// given
		config1 = config("config1.ini");
		config2 = config("config2.ini");
		configuration.addConfiguration(config1);
		configuration.addConfiguration(config2);
		String key1 = "a.k1";

		// then
		assertThat(configuration.getNumberOfConfigurations()).isEqualTo(3);
		assertThat(configuration.getString(key1)).isEqualTo(config2.getProperty(key1));
		assertThat(configuration.getSourceUsed(key1)).isEqualTo(config2);

		// when in-memory updated
		configuration.setProperty(key1, "memory");
		configuration.getString(key1);
		assertThat(configuration.getString(key1)).isEqualTo("memory");
		assertThat(configuration.getSourceUsed(key1)).isEqualTo(configuration.getConfiguration(2));
		assertThat(configuration.getSourceUsed(key1)).isInstanceOf(BaseConfiguration.class);

		// add another, does it still have base configuration last?
		// when
		config3 = config("config3.ini");
		configuration.addConfiguration(config3);
		assertThat(configuration.getString(key1)).isEqualTo("memory");
		assertThat(configuration.getSourceUsed(key1)).isEqualTo(configuration.getConfiguration(3));
		assertThat(configuration.getSourceUsed(key1)).isInstanceOf(BaseConfiguration.class);

	}

	private HierarchicalINIConfiguration config(String filename) throws ConfigurationException {
		File root = new File(".");
		File dir = new File(root, "src/test/java/uk/co/q3c/v7/base/config");
		File file = new File(dir, filename);
		HierarchicalINIConfiguration config = new HierarchicalINIConfiguration(file);
		return config;
	}

}
