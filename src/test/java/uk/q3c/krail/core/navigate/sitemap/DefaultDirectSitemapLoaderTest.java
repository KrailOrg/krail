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
import fixture.testviews2.OptionsView;
import fixture.testviews2.ViewA;
import fixture.testviews2.ViewA1;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.config.ApplicationConfigurationModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.DefaultNavigator;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.DefaultDirectSitemapLoaderTest.TestDirectSitemapModule_A;
import uk.q3c.krail.core.navigate.sitemap.DefaultDirectSitemapLoaderTest.TestDirectSitemapModule_B;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.shiro.StandardShiroModule;
import uk.q3c.krail.core.ui.BasicUIProvider;
import uk.q3c.krail.core.ui.ScopedUIProvider;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.user.opt.UserOptionModule;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.StandardComponentModule;
import uk.q3c.krail.i18n.I18NModule;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.TestLabelKey;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestDirectSitemapModule_A.class, TestDirectSitemapModule_B.class, UIScopeModule.class,
        ViewModule.class, ShiroVaadinModule.class, I18NModule.class, SitemapModule.class, UserModule.class, UserOptionModule.class,
        ApplicationConfigurationModule.class, StandardShiroModule.class, StandardComponentModule.class,
        VaadinSessionScopeModule.class})
public class DefaultDirectSitemapLoaderTest {

    static String page1 = "private/page1";
    static String page2 = "public/options";
    static String page3 = "public/options/detail";
    @Inject
    Map<String, DirectSitemapEntry> map;
    @Inject
    DefaultDirectSitemapLoader loader;
    @Inject
    MasterSitemap sitemap;

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
                bind(Navigator.class).to(DefaultNavigator.class);
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(ScopedUIProvider.class).to(BasicUIProvider.class);

            }

        };
    }

    public static class TestDirectSitemapModule_A extends DirectSitemapModule {

        @Override
        protected void define() {
            addEntry(page1, ViewA.class, LabelKey.Authorisation, PageAccessControl.PERMISSION);
        }

    }

    public static class TestDirectSitemapModule_B extends DirectSitemapModule {

        @Override
        protected void define() {
            addEntry(page2, OptionsView.class, TestLabelKey.Opt, PageAccessControl.PUBLIC);
            addEntry(page3, ViewA1.class, TestLabelKey.MoneyInOut, PageAccessControl.PUBLIC);
        }

    }
}
