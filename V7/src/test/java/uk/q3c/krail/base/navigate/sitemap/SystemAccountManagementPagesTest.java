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
package uk.q3c.krail.base.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.base.config.ApplicationConfigurationModule;
import uk.q3c.krail.base.guice.uiscope.UIScopeModule;
import uk.q3c.krail.base.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.base.navigate.DefaultV7Navigator;
import uk.q3c.krail.base.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.base.navigate.URIFragmentHandler;
import uk.q3c.krail.base.navigate.V7Navigator;
import uk.q3c.krail.base.shiro.ShiroVaadinModule;
import uk.q3c.krail.base.shiro.StandardShiroModule;
import uk.q3c.krail.base.ui.BasicUIProvider;
import uk.q3c.krail.base.ui.ScopedUIProvider;
import uk.q3c.krail.base.user.UserModule;
import uk.q3c.krail.base.view.ViewModule;
import uk.q3c.krail.base.view.component.StandardComponentModule;
import uk.q3c.krail.i18n.I18NModule;
import uk.q3c.krail.i18n.LabelKey;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({SystemAccountManagementPages.class, UIScopeModule.class, ViewModule.class, ShiroVaadinModule.class,
        I18NModule.class, SitemapModule.class, UserModule.class, ApplicationConfigurationModule.class,
        StandardShiroModule.class, StandardComponentModule.class, VaadinSessionScopeModule.class})
public class SystemAccountManagementPagesTest {

    @Inject
    Map<String, DirectSitemapEntry> map;

    @Inject
    DefaultDirectSitemapLoader loader;

    @Inject
    MasterSitemap sitemap;

    @Test
    public void check() {

        // given

        // when
        loader.load();
        // then

        assertThat(sitemap.hasUri("system-account")).isTrue();
        assertThat(sitemap.hasUri("system-account/refresh-account")).isTrue();
        assertThat(sitemap.hasUri("system-account/unlock-account")).isTrue();
        assertThat(sitemap.hasUri("system-account/enable-account")).isTrue();
        assertThat(sitemap.hasUri("system-account/request-account")).isTrue();
        assertThat(sitemap.hasUri("system-account/reset-account")).isTrue();

        SitemapNode node = sitemap.nodeFor("system-account");
        assertThat(node.getLabelKey()).isEqualTo(LabelKey.System_Account);

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
