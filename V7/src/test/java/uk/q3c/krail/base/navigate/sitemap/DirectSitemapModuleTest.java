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
import uk.q3c.krail.base.navigate.sitemap.DirectSitemapModuleTest.TestDirectSitemapModule1;
import uk.q3c.krail.base.navigate.sitemap.DirectSitemapModuleTest.TestDirectSitemapModule2;
import uk.q3c.krail.base.shiro.PageAccessControl;
import uk.q3c.krail.base.shiro.ShiroVaadinModule;
import uk.q3c.krail.base.shiro.StandardShiroModule;
import uk.q3c.krail.base.ui.BasicUIProvider;
import uk.q3c.krail.base.ui.ScopedUIProvider;
import uk.q3c.krail.base.user.UserModule;
import uk.q3c.krail.base.view.LoginView;
import uk.q3c.krail.base.view.PrivateHomeView;
import uk.q3c.krail.base.view.PublicHomeView;
import uk.q3c.krail.base.view.ViewModule;
import uk.q3c.krail.base.view.component.StandardComponentModule;
import uk.q3c.krail.i18n.I18NModule;
import uk.q3c.krail.i18n.LabelKey;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestDirectSitemapModule1.class, TestDirectSitemapModule2.class, UIScopeModule.class, ViewModule.class,
        ShiroVaadinModule.class, I18NModule.class, SitemapModule.class, UserModule.class,
        ApplicationConfigurationModule.class, StandardShiroModule.class, StandardComponentModule.class,
        VaadinSessionScopeModule.class})
public class DirectSitemapModuleTest {

    @Inject
    Map<String, DirectSitemapEntry> map;

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
}
