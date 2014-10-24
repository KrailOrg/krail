/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.co.q3c.v7.base.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.ReferenceUserSitemap;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.DefaultCurrentLocale;
import uk.co.q3c.v7.i18n.I18NModule;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the structure of the reference site map
 * 
 * @author dsowerby
 *
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinSessionScopeModule.class, I18NModule.class})
public class ReferenceSitemapTest {

	private static String[] expected = new String[] { "", "-Public", "--Log Out", "--ViewA", "---ViewA1",
			"----ViewA11", "--Log In", "--Public Home", "", "-Private", "--Private Home", "--ViewB", "---ViewB1",
			"----ViewB11" };

	@Inject
	ReferenceUserSitemap userSitemap;

	@Test
	public void output() {

		// given
		userSitemap.clear();
		// when
		userSitemap.populate();
		// then
		System.out.println(userSitemap);
		String output[] = userSitemap.toString().split("\\r?\\n");
		List<String> actualList = Arrays.asList(output);
		assertThat(actualList).containsOnly(expected);
	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(CurrentLocale.class).to(DefaultCurrentLocale.class);
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
			}

		};
	}
}
