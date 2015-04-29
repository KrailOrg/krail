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
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.config.ApplicationConfigurationModule;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.NavigationModule;
import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModuleTest.TestDirectSitemapModule1;
import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModuleTest.TestDirectSitemapModule2;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.shiro.StandardShiroModule;
import uk.q3c.krail.core.ui.BasicUIProvider;
import uk.q3c.krail.core.ui.ScopedUIProvider;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.user.opt.OptionModule;
import uk.q3c.krail.core.view.LoginView;
import uk.q3c.krail.core.view.PrivateHomeView;
import uk.q3c.krail.core.view.PublicHomeView;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.DefaultComponentModule;
import uk.q3c.krail.i18n.I18NModule;
import uk.q3c.krail.i18n.LabelKey;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestDirectSitemapModule1.class, TestDirectSitemapModule2.class, UIScopeModule.class, EventBusModule.class, ViewModule.class, ShiroVaadinModule
        .class, I18NModule.class, SitemapModule.class, UserModule.class, ApplicationConfigurationModule.class, StandardShiroModule.class, DefaultComponentModule.class, VaadinSessionScopeModule.class, OptionModule.class, NavigationModule.class})
public class DirectSitemapModuleTest {

    @Inject
    Map<String, DirectSitemapEntry> map;

    @Test
    public void addEntry() {

        // given

        // when

        // then
        assertThat(map).hasSize(5);
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
        assertThat(entry.getPositionIndex()).isEqualTo(1);

        entry = map.get("public/login");
        assertThat(entry.getViewClass()).isEqualTo(LoginView.class);
        assertThat(entry.getPageAccessControl()).isEqualTo(PageAccessControl.GUEST);
        assertThat(entry.getLabelKey()).isEqualTo(LabelKey.Log_In);
        assertThat(entry.getRoles()).isEqualTo("roles");
        assertThat(entry.getPositionIndex()).isEqualTo(1);

        entry = map.get("private/roles");
        assertThat(entry.getViewClass()).isEqualTo(LoginView.class);
        assertThat(entry.getPageAccessControl()).isEqualTo(PageAccessControl.ROLES);
        assertThat(entry.getLabelKey()).isEqualTo(LabelKey.Log_In);
        assertThat(entry.getRoles()).isEqualTo("roles");
        assertThat(entry.getPositionIndex()).isEqualTo(500);

        entry = map.get("private/noroles");
        assertThat(entry.getViewClass()).isEqualTo(LoginView.class);
        assertThat(entry.getPageAccessControl()).isEqualTo(PageAccessControl.PUBLIC);
        assertThat(entry.getLabelKey()).isEqualTo(LabelKey.Log_In);
        assertThat(entry.getRoles()).isNullOrEmpty();
        assertThat(entry.getPositionIndex()).isEqualTo(300);

    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
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
            addEntry("public/login", LoginView.class, LabelKey.Log_In, PageAccessControl.GUEST, "roles");
            addEntry("private/roles", LoginView.class, LabelKey.Log_In, PageAccessControl.ROLES, "roles", 500);
            addEntry("private/noroles", LoginView.class, LabelKey.Log_In, PageAccessControl.PUBLIC, 300);
        }

    }
}
