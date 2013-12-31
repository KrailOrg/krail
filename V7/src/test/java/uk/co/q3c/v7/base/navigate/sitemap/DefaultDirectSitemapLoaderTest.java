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

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultDirectSitemapLoaderTest.TestDirectSitemapModule_A;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultDirectSitemapLoaderTest.TestDirectSitemapModule_B;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.TestLabelKey;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.testviews2.OptionsView;
import fixture.testviews2.View1;
import fixture.testviews2.View2;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestDirectSitemapModule_A.class, TestDirectSitemapModule_B.class, I18NModule.class,
		DefaultStandardPagesModule.class })
public class DefaultDirectSitemapLoaderTest {

	@Inject
	Map<String, DirectSitemapEntry> map;

	@Inject
	DefaultDirectSitemapLoader loader;

	@Inject
	Sitemap sitemap;

	static String page1 = "private/page1";
	static String page2 = "public/options";
	static String page3 = "public/options/detail";

	static String permission1 = "p1";
	static String permission2 = "p2";
	static String permission3 = "p3";

	public static class TestDirectSitemapModule_A extends DirectSitemapModule {

		@Override
		protected void define() {
			addEntry(page1, View1.class, LabelKey.Authorisation, false, permission1);
		}

	}

	public static class TestDirectSitemapModule_B extends DirectSitemapModule {

		@Override
		protected void define() {
			addEntry(page2, OptionsView.class, TestLabelKey.Opt, true, permission2);
			addEntry(page3, View2.class, TestLabelKey.MoneyInOut, true, permission3);
		}

	}

	@Test
	public void load() {

		// given

		// when
		boolean result = loader.load();
		// then

		assertThat(sitemap.getNodeCount()).isEqualTo(9);
		assertThat(sitemap.hasUri(page1)).isTrue();
		assertThat(sitemap.hasUri(page2)).isTrue();
		assertThat(sitemap.hasUri(page3)).isTrue();
		assertThat(result).isTrue();
		System.out.println(sitemap);
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);

			}

		};
	}
}
