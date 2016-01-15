/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.config;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.util.testutil.TestResource;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class InheritingConfigurationTest {

    @Inject
    InheritingConfiguration configuration;
    private HierarchicalINIConfiguration config1;
    private HierarchicalINIConfiguration config2;
    private HierarchicalINIConfiguration config3;

    @Test
    public void override() throws ConfigurationException {

        // given
        config1 = config("config1.ini");
        config2 = config("config2.ini");
        String key1 = "a.k1";
        // when
        configuration.addConfiguration(config1);
        configuration.addConfiguration(config2);

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

        // add another, does it still have core configuration last?
        // when
        config3 = config("config3.ini");
        configuration.addConfiguration(config3);
        assertThat(configuration.getString(key1)).isEqualTo("memory");
        assertThat(configuration.getSourceUsed(key1)).isEqualTo(configuration.getConfiguration(3));
        assertThat(configuration.getSourceUsed(key1)).isInstanceOf(BaseConfiguration.class);

    }

    private HierarchicalINIConfiguration config(String filename) throws ConfigurationException {
        File root = TestResource.testJavaRootDir("krail");
        File dir = new File(root, "uk/q3c/krail/core/config");
        File file = new File(dir, filename);
        System.out.println(file.getAbsolutePath());
        HierarchicalINIConfiguration config = new HierarchicalINIConfiguration(file);
        return config;
    }

    @Test
    public void getSection() throws ConfigurationException {

        // given
        config1 = config("config1.ini");
        config2 = config("config2.ini");
        String key1 = "a";
        // when
        configuration.addConfiguration(config1);
        configuration.addConfiguration(config2);
        SubnodeConfiguration section = configuration.getSection(key1);
        // then
        assertThat(section).isNotNull();
        assertThat(section.getString("k1")).isEqualTo("2-1");
        assertThat(section.getString("k2")).isEqualTo("2-2");
    }

    @Test
    public void getSection_sectionNameNotPresent() throws ConfigurationException {

        // given
        config1 = config("config1.ini");
        config2 = config("config2.ini");
        String key1 = "a";
        // when
        configuration.addConfiguration(config1);
        configuration.addConfiguration(config2);
        SubnodeConfiguration section = configuration.getSection("rubbish section name");
        // then
        assertThat(section).isNull();
    }



}
