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

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.ApplicationConfigurationModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultDirectSitemapLoaderTest.TestDirectSitemapModule_A;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultDirectSitemapLoaderTest.TestDirectSitemapModule_B;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.shiro.StandardShiroModule;
import uk.co.q3c.v7.base.ui.BasicUIProvider;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.user.UserModule;
import uk.co.q3c.v7.base.view.ViewModule;
import uk.co.q3c.v7.base.view.component.StandardComponentModule;
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
@GuiceContext({ TestDirectSitemapModule_A.class, TestDirectSitemapModule_B.class, UIScopeModule.class,
		ViewModule.class, ShiroVaadinModule.class, I18NModule.class, SitemapServiceModule.class,
		UserModule.class, ApplicationConfigurationModule.class, StandardShiroModule.class,
		StandardComponentModule.class, VaadinSessionScopeModule.class })
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

	public static class TestDirectSitemapModule_A extends DirectSitemapModule {

		@Override
		protected void define() {
			addEntry(page1, View1.class, LabelKey.Authorisation, PageAccessControl.PERMISSION);
		}

	}

	public static class TestDirectSitemapModule_B extends DirectSitemapModule {

		@Override
		protected void define() {
			addEntry(page2, OptionsView.class, TestLabelKey.Opt, PageAccessControl.PUBLIC);
			addEntry(page3, View2.class, TestLabelKey.MoneyInOut, PageAccessControl.PUBLIC);
		}

	}

	@Test
	public void load() {

		// given

		// when
		boolean result = loader.load();
		// then

		assertThat(sitemap.getNodeCount()).isEqualTo(5);
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
				bind(V7Navigator.class).to(DefaultV7Navigator.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(ScopedUIProvider.class).to(BasicUIProvider.class);

			}

		};
	}
}
