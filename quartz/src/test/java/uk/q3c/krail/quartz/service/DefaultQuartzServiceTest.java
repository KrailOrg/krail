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
package uk.q3c.krail.quartz.service;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.base.config.ApplicationConfigurationModule;
import uk.q3c.krail.base.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.i18n.I18NModule;
import uk.q3c.krail.quartz.job.DefaultJobModule;
import uk.q3c.krail.quartz.scheduler.DefaultSchedulerModule;
import uk.q3c.krail.quartz.scheduler.SchedulerProvider;
import uk.q3c.krail.quartz.scheduler.V7Scheduler;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({I18NModule.class, DefaultSchedulerModule.class, ApplicationConfigurationModule.class,
        DefaultJobModule.class, VaadinSessionScopeModule.class})
public class DefaultQuartzServiceTest {

    static File iniDir = new File("src/test/java");
    static VaadinService vaadinService;
    @Inject
    DefaultQuartzService service;
    @Inject
    SchedulerProvider provider;

    @BeforeClass
    public static void setupClass() {
        vaadinService = mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(iniDir);
        VaadinService.setCurrent(vaadinService);
    }

    @Test
    public void defaultSingleScheduler() throws Exception {

        // given

        // when
        service.start();
        // then
        V7Scheduler scheduler = provider.get();
        assertThat(scheduler.isStarted()).isTrue();
        assertThat(scheduler.getMetaData()
                            .getSchedulerName()).isEqualTo("default");
    }

}
