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
package uk.co.q3c.v7.quartz.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.ApplicationConfigurationModule;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.quartz.scheduler.DefaultQuartzSchedulerModule;
import uk.co.q3c.v7.quartz.scheduler.QuartzSchedulerModuleBase;
import uk.co.q3c.v7.quartz.scheduler.SchedulerProvider;
import uk.co.q3c.v7.quartz.scheduler.V7Scheduler;
import uk.co.q3c.v7.quartz.service.DefaultQuartzServiceTest2.TestSchedulerModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, DefaultQuartzSchedulerModule.class, ApplicationConfigurationModule.class,
		TestSchedulerModule.class })
public class DefaultQuartzServiceTest2 {

	public static class TestSchedulerModule extends QuartzSchedulerModuleBase {

		@Override
		protected void addConfigurations() {
			addConfiguration("test", false);
		}

	}

	@Inject
	DefaultQuartzService service;

	@Inject
	SchedulerProvider provider;

	@Test
	public void secondScheduler() throws Exception {

		// given
		// given

		// when
		service.start();
		// then
		V7Scheduler defaultScheduler = provider.get();
		assertThat(defaultScheduler.isStarted()).isTrue();
		assertThat(defaultScheduler.getMetaData().getSchedulerName()).isEqualTo("default");

		V7Scheduler testScheduler = provider.get("test");
		assertThat(testScheduler).isNotNull();
		assertThat(testScheduler.isStarted()).isFalse();
		assertThat(testScheduler.getMetaData().getSchedulerName()).isEqualTo("test");
	}

}
