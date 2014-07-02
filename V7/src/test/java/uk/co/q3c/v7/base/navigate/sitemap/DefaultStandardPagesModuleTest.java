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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.q3c.v7.base.config.ApplicationConfigurationModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ StandardPagesModule.class, UIScopeModule.class, ViewModule.class, ShiroVaadinModule.class,
		I18NModule.class, SitemapModule.class, UserModule.class, ApplicationConfigurationModule.class,
		StandardShiroModule.class, StandardComponentModule.class, VaadinSessionScopeModule.class })
public class DefaultStandardPagesModuleTest {

	@Inject
	Map<String, DirectSitemapEntry> map;

	@Inject
	DefaultDirectSitemapLoader loader;

	@Inject
	MasterSitemap sitemap;

	@Inject
	SitemapChecker sitemapChecker;

	@Test
	public void check() {

		// given

		// when
		loader.load();
		// then

		assertThat(sitemap.hasUri("private/home")).isTrue();
		assertThat(sitemap.standardPageNode(StandardPageKey.Public_Home)).isNotNull();
		assertThat(sitemap.standardPageNode(StandardPageKey.Private_Home)).isNotNull();
		assertThat(sitemap.standardPageNode(StandardPageKey.Log_In)).isNotNull();
		assertThat(sitemap.standardPageNode(StandardPageKey.Log_Out)).isNotNull();
		SitemapNode privateNode = sitemap.nodeFor("private");
		assertThat(privateNode.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
		// when
		sitemapChecker.check();
		// then
		assertThat(privateNode.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
		assertThat(privateNode.getLabelKey()).isEqualTo(LabelKey.Private);
		assertThat(privateNode.getViewClass()).isNull();
		assertThat(privateNode.getRoles()).isNullOrEmpty();

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
