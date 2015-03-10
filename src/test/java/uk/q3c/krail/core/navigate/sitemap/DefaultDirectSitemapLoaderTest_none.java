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
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * No pages to load but should not fail
 *
 * @author David Sowerby
 */

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class, TestOptionModule.class, EventBusModule.class, UIScopeModule.class})
public class DefaultDirectSitemapLoaderTest_none {

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
        assertThat(result).isFalse();
    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
                bind(UserSitemap.class).to(DefaultUserSitemap.class);
            }

        };
    }
}
