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
package uk.co.q3c.v7.base.navigate.sitemap;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.i18n.DefaultI18NProcessor;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.I18NProcessor;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

/**
 * Tests {@link DefaultFileSitemapLoader} with no files to process
 *
 *
 *
 * @author dsowerby
 *
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class DefaultFileSitemapLoaderTest3 {

	@Inject
	DefaultFileSitemapLoader loader;

	@Inject
	Sitemap sitemap;

	@Before
	public void setup() throws IOException {
	}

	@Test
	public void fail1() {

		// given

		// when
		boolean result = loader.load();

		// then
		assertThat(result).isFalse();

	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}

}
