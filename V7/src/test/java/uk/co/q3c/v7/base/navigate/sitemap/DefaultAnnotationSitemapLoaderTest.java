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

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultAnnotationSitemapLoaderTest.AnnotationsModule1;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultAnnotationSitemapLoaderTest.AnnotationsModule2;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.TestLabelKey;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.ui.Component;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnnotationsModule1.class, AnnotationsModule2.class })
public class DefaultAnnotationSitemapLoaderTest {

	@Inject
	DefaultAnnotationSitemapLoader loader;

	public static class AnnotationsModule1 extends AnnotationSitemapModule {

		@Override
		protected void define() {
			addEntry("fixture.", DescriptionKey.Confirm_Ok);
			addEntry("uk.co.q3c.v7.base.navigate.sitemap", TestLabelKey.Login);
		}

	}

	public static class AnnotationsModule2 extends AnnotationSitemapModule {

		@Override
		protected void define() {
			addEntry("fixture1", TestLabelKey.Home);
		}

	}

	@View(uri = "a", labelKeyName = "home", isPublic = true, permission = "demi-god")
	static class View1 implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

		@Override
		public String viewName() {

			return getClass().getSimpleName();
		}

	}

	@Inject
	Map<String, AnnotationSitemapEntry> map;

	@Test
	public void test() {
		// given
		// when
		loader.load();
		// then
		// assertThat(loader.viewCount()).isEqualTo(3);
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
