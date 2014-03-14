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

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import uk.co.q3c.v7.base.config.ApplicationConfiguration;
import uk.co.q3c.v7.base.config.ApplicationConfigurationModule;
import uk.co.q3c.v7.base.config.InheritingConfiguration;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, ApplicationConfigurationModule.class, DefaultQuartzSchedulerModule.class })
public class SchedulerProviderTest {

	@Inject
	Provider<Scheduler> provider;

	@Inject
	Translate translate;

	@Inject
	ApplicationConfiguration applicationConfiguration;

	@Inject
	Provider<InheritingConfiguration> inheritingConfigurationProvider;

	@Test
	public void getWhenThereIsOnlyOne() throws SchedulerException {

		// given
		SchedulerConfiguration config = new SchedulerConfiguration().name("first");
		V7SchedulerFactory factory = new DefaultV7SchedulerFactory(translate, applicationConfiguration,
				inheritingConfigurationProvider);
		factory.createScheduler(config);
		// when

		// then

		fail("not written");
	}

	@Test
	public void getTheDefault() {

		// given

		// when

		// then

		fail("not written");
	}

	@Test
	public void getWithName() {

		// given

		// when

		// then

		fail("not written");
	}

	@Test
	public void noSchedulersDefined() {

		// given

		// when

		// then

		fail("not written");
	}

	@Test
	public void removeTheDefault() {

		// given

		// when

		// then

		fail("not written");
	}

	@Test
	public void setInvalidDefault() {

		// given

		// when

		// then

		fail("not written");
	}

}
