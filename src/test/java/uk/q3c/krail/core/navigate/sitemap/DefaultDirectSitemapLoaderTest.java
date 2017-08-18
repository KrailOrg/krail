/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.testviews2.OptionsView;
import fixture.testviews2.ViewA;
import fixture.testviews2.ViewA1;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.config.bind.ApplicationConfigurationModule;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.NavigationModule;
import uk.q3c.krail.core.navigate.sitemap.DefaultDirectSitemapLoaderTest.TestDirectSitemapModule_A;
import uk.q3c.krail.core.navigate.sitemap.DefaultDirectSitemapLoaderTest.TestDirectSitemapModule_B;
import uk.q3c.krail.core.service.ServicesModule;
import uk.q3c.krail.core.shiro.DefaultShiroModule;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.ui.DataTypeModule;
import uk.q3c.krail.core.ui.DefaultUIModule;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.DefaultComponentModule;
import uk.q3c.krail.i18n.bind.I18NModule;
import uk.q3c.krail.i18n.test.TestLabelKey;
import uk.q3c.krail.option.test.TestOptionModule;
import uk.q3c.krail.testutil.persist.TestPersistenceModule;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ServicesModule.class, TestDirectSitemapModule_A.class, TestDirectSitemapModule_B.class, UIScopeModule.class, ViewModule.class,
        ShiroVaadinModule.class, I18NModule.class, SitemapModule.class, UserModule.class, TestPersistenceModule.class, TestOptionModule.class,
        ApplicationConfigurationModule.class, DefaultShiroModule.class, DefaultComponentModule.class, VaadinSessionScopeModule.class, NavigationModule.class,
        EventBusModule.class, DefaultUIModule.class, DataTypeModule.class, UtilsModule.class, UtilModule.class})
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
        boolean result = loader.load(sitemap);
        // then

        assertThat(sitemap.getNodeCount()).isEqualTo(5);
        assertThat(sitemap.hasUri(page1)).isTrue();
        assertThat(sitemap.hasUri(page2)).isTrue();
        assertThat(sitemap.hasUri(page3)).isTrue();
        assertThat(result).isTrue();
        System.out.println(sitemap);
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
