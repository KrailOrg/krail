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
import uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModuleTest.TestDirectSitemapModule1;
import uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModuleTest.TestDirectSitemapModule2;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.shiro.StandardShiroModule;
import uk.co.q3c.v7.base.ui.BasicUIProvider;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.user.UserModule;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.PrivateHomeView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.ViewModule;
import uk.co.q3c.v7.base.view.component.StandardComponentModule;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.LabelKey;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestDirectSitemapModule1.class, TestDirectSitemapModule2.class, UIScopeModule.class,
		ViewModule.class, ShiroVaadinModule.class, I18NModule.class, SitemapServiceModule.class,
		UserModule.class, ApplicationConfigurationModule.class, StandardShiroModule.class,
		StandardComponentModule.class, VaadinSessionScopeModule.class })
public class DirectSitemapModuleTest {

	@Inject
	Map<String, DirectSitemapEntry> map;

	public static class TestDirectSitemapModule1 extends DirectSitemapModule {

		@Override
		protected void define() {
			addEntry("private/home", PrivateHomeView.class, LabelKey.Authorisation, PageAccessControl.PERMISSION);
		}

	}

	public static class TestDirectSitemapModule2 extends DirectSitemapModule {

		@Override
		protected void define() {
			addEntry("public/home", PublicHomeView.class, LabelKey.Home_Page, PageAccessControl.PUBLIC);
			addEntry("public/login", LoginView.class, LabelKey.Log_In, PageAccessControl.GUEST);
		}

	}

	@Test
	public void addEntry() {

		// given

		// when

		// then
		assertThat(map).hasSize(3);
		DirectSitemapEntry entry = map.get("private/home");
		assertThat(entry.getViewClass()).isEqualTo(PrivateHomeView.class);
		assertThat(entry.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
		assertThat(entry.getLabelKey()).isEqualTo(LabelKey.Authorisation);
		assertThat(entry.getRoles()).isNullOrEmpty();

		entry = map.get("public/home");
		assertThat(entry.getViewClass()).isEqualTo(PublicHomeView.class);
		assertThat(entry.getPageAccessControl()).isEqualTo(PageAccessControl.PUBLIC);
		assertThat(entry.getLabelKey()).isEqualTo(LabelKey.Home_Page);
		assertThat(entry.getRoles()).isNullOrEmpty();

		entry = map.get("public/login");
		assertThat(entry.getViewClass()).isEqualTo(LoginView.class);
		assertThat(entry.getPageAccessControl()).isEqualTo(PageAccessControl.GUEST);
		assertThat(entry.getLabelKey()).isEqualTo(LabelKey.Log_In);
		assertThat(entry.getRoles()).isNullOrEmpty();

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
