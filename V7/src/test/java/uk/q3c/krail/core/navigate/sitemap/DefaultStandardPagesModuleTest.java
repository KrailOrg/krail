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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.TestI18NModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.config.ApplicationConfigurationModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.DefaultNavigator;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.shiro.StandardShiroModule;
import uk.q3c.krail.core.ui.BasicUIProvider;
import uk.q3c.krail.core.ui.ScopedUIProvider;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.StandardComponentModule;
import uk.q3c.krail.i18n.LabelKey;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({StandardPagesModule.class, UIScopeModule.class, ViewModule.class, ShiroVaadinModule.class,
        TestI18NModule.class, SitemapModule.class, UserModule.class, ApplicationConfigurationModule.class,
        StandardShiroModule.class, StandardComponentModule.class, VaadinSessionScopeModule.class})
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
                bind(Navigator.class).to(DefaultNavigator.class);
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(ScopedUIProvider.class).to(BasicUIProvider.class);

            }

        };
    }
}
