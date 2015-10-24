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
package uk.q3c.krail.core.config;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinService;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.eventbus.GlobalBus;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.services.Service.State;
import uk.q3c.krail.core.services.ServiceException;
import uk.q3c.krail.i18n.*;
import uk.q3c.krail.testutil.MockCurrentLocale;
import uk.q3c.util.testutil.TestResource;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({EventBusModule.class, UIScopeModule.class, VaadinSessionScopeModule.class})
public class DefaultApplicationConfigurationServiceTest {

    static File iniDir;
    static VaadinService vaadinService;
    Map<Integer, IniFileConfig> iniFiles;
    @Mock
    Translate translate;
    DefaultApplicationConfigurationService service;
    @Inject
    ApplicationConfiguration configuration;

    @Inject
    @GlobalBus
    PubSubSupport<BusMessage> globalBus;

    CurrentLocale currentLocale = new MockCurrentLocale();

    @Mock
    I18NProcessor i18NProcessor;

    @BeforeClass
    public static void setupClass() {
        iniDir = TestResource.testJavaRootDir("krail");
        vaadinService = mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(iniDir);
        VaadinService.setCurrent(vaadinService);
    }

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        iniFiles = new HashMap<>();
        configuration.clear();
        service = new DefaultApplicationConfigurationService(translate, configuration, iniFiles);
        service.init(globalBus);
        currentLocale.setLocale(Locale.UK);
    }

    @Test
    public void loadOneFile() throws Exception {
        // given
        addConfig("krail.ini", 0, false);
        // when
        service.start();
        // then (one configuration is the in memory one added automatically)
        assertThat(configuration.getNumberOfConfigurations()).isEqualTo(2);
        assertThat(configuration.getBoolean("test")).isTrue();
        assertThat(configuration.getString("dbUser")).isEqualTo("monty");

    }

    protected void addConfig(String filename, int index, boolean optional) {
        checkNotNull(filename);
        IniFileConfig ifc = new IniFileConfig(filename, optional);
        iniFiles.put(index, ifc);
    }

    @Test
    public void loadTwoFiles() throws Exception {
        // given
        addConfig("krail.ini", 0, false);
        addConfig("test.krail.ini", 1, false);
        // when
        service.start();
        // then (one configuration is the in memory one added automatically)
        assertThat(configuration.getNumberOfConfigurations()).isEqualTo(3);
        assertThat(configuration.getBoolean("test")).isTrue();
        assertThat(configuration.getString("dbUser")).isEqualTo("python");

    }

    @Test
    public void stopStart() throws Exception {
        // given
        addConfig("krail.ini", 0, false);
        addConfig("test.krail.ini", 1, false);
        configuration.addProperty("in memory", "memory");
        // when
        service.start();
        // then (one configuration is the in memory one added automatically)
        assertThat(configuration.getNumberOfConfigurations()).isEqualTo(3);
        assertThat(service.getState()).isEqualTo(State.STARTED);
        assertThat(configuration.getString("in memory")).isEqualTo("memory");
        // then
        service.stop();
        assertThat(configuration.getNumberOfConfigurations()).isEqualTo(1);
        assertThat(service.getState()).isEqualTo(State.STOPPED);
        assertThat(configuration.getString("in memory")).isNull();
    }

    @Test(expected = ServiceException.class)
    public void fileMissing_notOptional() throws Exception {

        // given
        addConfig("rubbish.ini", 0, false);
        // when
        service.start();
        // then
        assertThat(configuration.getNumberOfConfigurations()).isEqualTo(1);
    }

    @Test
    public void fileMissing_optional() throws Exception {

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
        when(translate.from(LabelKey.Application_Configuration_Service)).thenReturn("Application Configuration " +
                "Service");
        when(translate.from(DescriptionKey.Application_Configuration_Service)).thenReturn("This service loads the " +
                "application configuration from krail.ini");
        // when

        // then
        assertThat(service.getNameKey()).isEqualTo(LabelKey.Application_Configuration_Service);
        assertThat(service.getName()).isEqualTo("Application Configuration Service");
        assertThat(service.getDescriptionKey()).isEqualTo(DescriptionKey.Application_Configuration_Service);
        assertThat(service.getDescription()).isEqualTo("This service loads the application configuration from krail"
                + ".ini");

    }

    @ModuleProvider
    protected AbstractModule module() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(Translate.class).toInstance(translate);
                bind(CurrentLocale.class).toInstance(currentLocale);
                bind(I18NProcessor.class).toInstance(i18NProcessor);
            }

        };
    }
}
