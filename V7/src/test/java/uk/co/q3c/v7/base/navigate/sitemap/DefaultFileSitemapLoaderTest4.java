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

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.I18NTranslator;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

/**
 * Tests {@link DefaultFileSitemapLoader} with empty define in module
 * 
 * 
 * 
 * @author dsowerby
 * 
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ uk.co.q3c.v7.base.navigate.sitemap.DefaultFileSitemapLoaderTest4.TestFileSitemapModule.class })
public class DefaultFileSitemapLoaderTest4 {

	public static class TestFileSitemapModule extends FileSitemapModule {

		@Override
		protected void define() {
		}

	}

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
		assertThat(loader.getSitemap().hasErrors()).isFalse();

	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}

}
