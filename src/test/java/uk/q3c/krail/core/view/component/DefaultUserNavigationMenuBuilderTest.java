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

package uk.q3c.krail.core.view.component;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.ReferenceUserSitemap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.data.DataModule;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.q3c.krail.core.persist.inmemory.option.DefaultInMemoryOptionStore;
import uk.q3c.krail.core.persist.inmemory.option.InMemoryOptionStore;
import uk.q3c.krail.testutil.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinSessionScopeModule.class, TestI18NModule.class, TestOptionModule.class, TestPersistenceModule.class, EventBusModule.class, TestUIScopeModule.class, DataModule.class})
public class DefaultUserNavigationMenuBuilderTest {

    DefaultUserNavigationMenuBuilder builder;

    @Inject
    ReferenceUserSitemap userSitemap;

    @Inject
    DefaultUserSitemapSorters sorters;

    @Inject
    MockOption option;

    @Mock
    Navigator navigator;

    private DefaultUserNavigationMenu userNavigationMenu;

    @Before
    public void setUp() throws Exception {
        builder = new DefaultUserNavigationMenuBuilder(userSitemap, navigator);
        userNavigationMenu = new DefaultUserNavigationMenu(option, builder);
        userSitemap.populate();
    }

    @Test
    public void construct() {
        // given
        // when

        // then
        assertThat(builder.getUserNavigationMenu()).isEqualTo(userNavigationMenu);
    }


    @ModuleProvider
    protected AbstractModule module() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(InMemoryOptionStore.class).to(DefaultInMemoryOptionStore.class);
            }

            @Provides
            protected UserSitemap sitemapProvider() {
                return userSitemap;
            }

        };
    }

}