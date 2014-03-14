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

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, DefaultQuartzSchedulerModule.class })
public class V7SchedulerFactoryTest {

	@Inject
	V7SchedulerFactory factory;

	@Inject
	V7SchedulerFactory factory2;

	@Inject
	SchedulerProvider provider;

	@Test
	public void createScheduler() throws SchedulerException {

		// given

		// when
		Scheduler s1 = factory.getScheduler();
		Scheduler s2 = factory.getScheduler();
		Scheduler s3 = provider.get();
		// then

		assertThat(s1).isEqualTo(s2).isEqualTo(s3);
		assertThat(s1).isInstanceOf(V7Scheduler.class);
	}

	@Test
	public void createWithConfig() throws SchedulerException {

		// given
		SchedulerConfiguration config = new SchedulerConfiguration().name("first");
		// when
		V7Scheduler s1 = factory.createScheduler(config);
		// then
		assertThat(s1).isInstanceOf(V7Scheduler.class);
		assertThat(s1.getMetaData().getSchedulerName()).isEqualTo("first");
	}

	@Test
	public void createWithConfig_nullName() throws SchedulerException {

		// given
		SchedulerConfiguration config = new SchedulerConfiguration();
		// when
		V7Scheduler s1 = factory.createScheduler(config);
		// then
		assertThat(s1).isInstanceOf(V7Scheduler.class);
		assertThat(s1.getMetaData().getSchedulerName()).isEqualTo("QuartzScheduler");

		// when second scheduler created
		config.name("second instance");
		V7Scheduler s2 = factory2.createScheduler(config);
		// then
		assertThat(s2).isInstanceOf(V7Scheduler.class);
		assertThat(s2.getMetaData().getSchedulerName()).isEqualTo("second instance");
		assertThat(s1).isNotEqualTo(s2);

	}

}
