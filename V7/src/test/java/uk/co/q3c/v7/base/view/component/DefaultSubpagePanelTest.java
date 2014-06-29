/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.base.view.component;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.ReferenceUserSitemap;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultSubpagePanelTest {

	DefaultSubpagePanel panel;

	@Inject
	ReferenceUserSitemap userSitemap;

	@Mock
	V7Navigator navigator;

	@Mock
	CurrentLocale currentLocale;

	@Mock
	Translate translate;

	@Mock
	UserOption userOption;

	@Before
	public void setup() {
		panel = new DefaultSubpagePanel(navigator, userSitemap, currentLocale, translate, userOption);
	}

	@Test
	public void t() {

		// given

		// when

		// then
		assertThat(true).isEqualTo(false);
	}

}
