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
package uk.q3c.krail.quartz.scheduler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;
import uk.q3c.krail.base.config.ApplicationConfiguration;
import uk.q3c.krail.base.config.ApplicationConfigurationModule;
import uk.q3c.krail.base.config.InheritingConfiguration;
import uk.q3c.krail.base.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.i18n.I18NModule;
import uk.q3c.krail.i18n.Translate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({I18NModule.class, ApplicationConfigurationModule.class, DefaultSchedulerModule.class,
        VaadinSessionScopeModule.class})
public class SchedulerProviderTest {

    @Inject
    SchedulerProvider provider;

    @Inject
    Translate translate;

    @Inject
    ApplicationConfiguration applicationConfiguration;

    @Inject
    Provider<InheritingConfiguration> inheritingConfigurationProvider;

    private DefaultKrailSchedulerFactory factory;

    @Before
    public void setup() throws SchedulerException, InterruptedException {
        factory = new DefaultKrailSchedulerFactory(translate, applicationConfiguration,
                inheritingConfigurationProvider);
        clearSchedulerRepo();
    }

    private void clearSchedulerRepo() throws SchedulerException, InterruptedException {
        Collection<Scheduler> lookupAll = SchedulerRepository.getInstance()
                                                             .lookupAll();
        List<String> names = new ArrayList<>();
        for (Scheduler scheduler : lookupAll) {
            names.add(scheduler.getSchedulerName());
        }
        for (String name : names) {
            System.out.println("removing " + name);
            SchedulerRepository.getInstance()
                               .remove(name);
        }

    }

    @Test
    public void getWhenThereIsOnlyOne() throws SchedulerException {

        // given
        SchedulerConfiguration config = new SchedulerConfiguration().name("first");
        factory.createScheduler(config);
        // when
        Scheduler s1 = provider.get();
        Scheduler s2 = provider.get();
        // then
        assertThat(s1).isNotNull();
        assertThat(s1).isEqualTo(s2);
        assertThat(s1.getSchedulerName()).isEqualTo("first");
    }

    @Test
    public void setDefaultByInstance() throws SchedulerException {

        // given
        SchedulerConfiguration config1 = new SchedulerConfiguration().name("first");
        SchedulerConfiguration config2 = new SchedulerConfiguration().name("second");
        // when
        KrailScheduler s = factory.createScheduler(config1);
        provider.setDefaultScheduler(s);
        factory.createScheduler(config2);
        // then
        Scheduler s1 = provider.get();
        Scheduler s2 = provider.get();
        assertThat(s1).isNotNull();
        assertThat(s1).isEqualTo(s2);
        assertThat(s1.getSchedulerName()).isEqualTo("first");
        assertThat(provider.get("first")).isEqualTo(s1);
        assertThat(provider.get("second")).isNotEqualTo(s1);
    }

    @Test
    public void setDefaultWithName() throws SchedulerException {
        // given
        SchedulerConfiguration config1 = new SchedulerConfiguration().name("first");
        SchedulerConfiguration config2 = new SchedulerConfiguration().name("second");
        // when
        factory.createScheduler(config1);
        provider.setDefaultScheduler("first");
        KrailScheduler s02 = factory.createScheduler(config2);
        // then
        Scheduler s1 = provider.get();
        Scheduler s2 = provider.get();
        assertThat(s1).isNotNull();
        assertThat(s1).isEqualTo(s2);
        assertThat(s1.getSchedulerName()).isEqualTo("first");
        assertThat(provider.get("first")).isEqualTo(s1);
        assertThat(provider.get("second")).isNotEqualTo(s1);

        // when
        provider.setDefaultScheduler("second");
        s1 = provider.get();
        assertThat(s1).isEqualTo(s02);
    }

    @Test
    public void getWithName() throws SchedulerException {

        // given
        SchedulerConfiguration config1 = new SchedulerConfiguration().name("first");
        SchedulerConfiguration config2 = new SchedulerConfiguration().name("second");
        // when
        KrailScheduler s01 = factory.createScheduler(config1);
        KrailScheduler s02 = factory.createScheduler(config2);
        // then
        KrailScheduler s1 = provider.get("first");
        KrailScheduler s2 = provider.get("second");

        assertThat(s1).isEqualTo(s01);
        assertThat(s2).isEqualTo(s02);

    }

    @Test()
    public void getWithInvalidName() throws SchedulerException {

        // given
        SchedulerConfiguration config1 = new SchedulerConfiguration().name("first");
        SchedulerConfiguration config2 = new SchedulerConfiguration().name("second");
        // when
        factory.createScheduler(config1);
        factory.createScheduler(config2);
        // then
        KrailScheduler s1 = provider.get("third");
        assertThat(s1).isNull();
    }

    @Test(expected = ProvisionException.class)
    public void noSchedulersDefined() {

        // given

        // when
        provider.get();
        // then

    }

    @Test
    public void removeTheDefault() throws SchedulerException {

        // given
        SchedulerConfiguration config1 = new SchedulerConfiguration().name("first");
        SchedulerConfiguration config2 = new SchedulerConfiguration().name("second");
        SchedulerConfiguration config3 = new SchedulerConfiguration().name("third");
        KrailScheduler s1 = factory.createScheduler(config1);
        factory.createScheduler(config2);
        factory.createScheduler(config3);
        provider.setDefaultScheduler("first");

        // when
        SchedulerRepository.getInstance()
                           .remove("first");
        // then
        assertThat(provider.get()).isNotNull();
        assertThat(provider.get()).isNotEqualTo(s1);
        assertThat(provider.get()
                           .getSchedulerName()).isEqualTo("second");
    }

    @Test(expected = ProvisionException.class)
    public void setInvalidDefault() throws SchedulerException {

        // given
        SchedulerConfiguration config1 = new SchedulerConfiguration().name("first");
        SchedulerConfiguration config2 = new SchedulerConfiguration().name("second");
        // when
        factory.createScheduler(config1);
        factory.createScheduler(config2);
        provider.setDefaultScheduler("rubbish");

    }

}
