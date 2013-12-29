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
package uk.co.q3c.v7.base.services;

import static org.assertj.core.api.Assertions.*;

import com.google.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class })
public class AbstractServiceI18NTest {

	static class TestService extends AbstractServiceI18N {
		@Inject
		protected TestService(Translate translate) {
			super(translate);
		}

		@Override
		public Status start() {

			return null;
		}

		@Override
		public Status stop() {

			return null;
		}

		@Override
		public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
		}

	}

	@Inject
	TestService service;

	@Before
	public void setup() {

	}

	@Test
	public void name() {

		// given

		// when
		service.setNameKey(TestLabelKey.Home);
		// then
		assertThat(service.getName()).isEqualTo("home");
		// when
		service.setDescriptionKey(TestLabelKey.Private);
		// then
		assertThat(service.getDescription()).isEqualTo("Private");
	}
}
