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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class })
public class V7SchedulerFactoryTest {

	@Inject
	V7SchedulerFactory factory;

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
	public void createWithConfig() {

		// given

		// when

		// then

		fail("not written");
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(V7SchedulerFactory.class).in(Singleton.class);
				bind(Scheduler.class).toProvider(SchedulerProvider.class).in(Singleton.class);
			}

		};
	}
}
