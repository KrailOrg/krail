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
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.config.KrailApplicationConfigurationModule;
import uk.q3c.krail.core.eventbus.VaadinEventBusModule;
import uk.q3c.krail.core.guice.ServletEnvironmentModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.TestKrailI18NModule;
import uk.q3c.krail.core.navigate.NavigationModule;
import uk.q3c.krail.core.push.PushModule;
import uk.q3c.krail.core.shiro.DefaultShiroModule;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.ui.DataTypeModule;
import uk.q3c.krail.core.ui.DefaultUIModule;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.DefaultComponentModule;
import uk.q3c.krail.eventbus.mbassador.EventBusModule;
import uk.q3c.krail.option.mock.TestOptionModule;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;
import uk.q3c.util.guice.SerializationSupportModule;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MycilaJunitRunner.class)
@GuiceContext({StandardPagesModule.class, UIScopeModule.class, VaadinEventBusModule.class, ViewModule.class, ShiroVaadinModule.class,
        TestKrailI18NModule.class, SitemapModule.class, UserModule.class, KrailApplicationConfigurationModule.class, InMemoryModule.class, TestOptionModule
        .class, DefaultShiroModule.class, DefaultComponentModule.class, VaadinSessionScopeModule.class, NavigationModule.class, DefaultUIModule.class,
        DataTypeModule.class, ServletEnvironmentModule.class, SerializationSupportModule.class, PushModule.class, EventBusModule.class, UtilsModule.class, UtilModule.class})
public class DefaultStandardPagesModuleTest {

    @Inject
    Map<String, DirectSitemapEntry> map;

    @Inject
    DefaultDirectSitemapLoader loader;

    @Inject
    MasterSitemap sitemap;

    @Inject
    SitemapFinisher sitemapFinisher;

    @Test
    public void check() {

        // given

        // when
        loader.load(sitemap);
        // then

        assertThat(sitemap.hasUri("private/home")).isTrue();
        assertThat(sitemap.standardPageNode(StandardPageKey.Public_Home)).isNotNull();
        assertThat(sitemap.standardPageNode(StandardPageKey.Private_Home)).isNotNull();
        assertThat(sitemap.standardPageNode(StandardPageKey.Log_In)).isNotNull();
        assertThat(sitemap.standardPageNode(StandardPageKey.Log_Out)).isNotNull();
        SitemapNode privateNode = sitemap.nodeFor("private");
        assertThat(privateNode.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
        // when
        sitemapFinisher.check(sitemap);
        // then
        assertThat(privateNode.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
        assertThat(privateNode.getLabelKey()).isEqualTo(LabelKey.Private);
        assertThat(privateNode.getViewClass()).isNull();
        assertThat(privateNode.getRoles()).isNullOrEmpty();

    }


}
