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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.ReferenceUserSitemap;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.CurrentLocale;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule;
import uk.q3c.krail.testutil.i18n.TestI18NModule;
import uk.q3c.krail.testutil.option.TestOptionModule;
import uk.q3c.krail.testutil.persist.TestPersistenceModule;
import uk.q3c.util.UtilModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class, TestPersistenceModule.class, EventBusModule.class,
        TestUIScopeModule.class, TestOptionModule.class, UtilModule.class})
@Listener
public class DefaultUserSitemapTest {

    @Inject
    ReferenceUserSitemap userSitemap;

    @Inject
    CurrentLocale currentLocale;

    private boolean labelsChanged;

    private boolean structureChanged;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        labelsChanged = false;
        structureChanged = false;
    }

    @Test
    public void localeChange() {

        // given
        currentLocale.setLocale(Locale.UK);
        userSitemap.populate();
        // when
        currentLocale.setLocale(Locale.GERMANY);
        // then
        assertThat(userSitemap.getRoots()).containsOnly(userSitemap.publicNode(), userSitemap.privateNode());
        assertThat(userSitemap.publicNode()
                              .getLabel()).isEqualTo("Öffentlich");
        assertThat(userSitemap.privateNode()
                              .getLabel()).isEqualTo("Privat");
        assertThat(userSitemap.getParent(userSitemap.a11Node())).isEqualTo(userSitemap.a1Node());
        assertThat(labelsChanged).isTrue();
    }

    /**
     * Loaded is set by the builder, after selected content copied across from master sitemap. Should trigger structure
     * change events
     */
    @Test
    public void setLoaded() {

        // given
        currentLocale.setLocale(Locale.UK);
        userSitemap.populate();
        // when
        userSitemap.setLoaded(true);
        // then
        assertThat(structureChanged).isTrue();
    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {

                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
            }

        };
    }

    @Handler
    public void labelsChanged(UserSitemapLabelChangeMessage busMessage) {
        labelsChanged = true;

    }

    @Handler
    public void structureChanged(UserSitemapStructureChangeMessage busMessage) {
        structureChanged = true;

    }
}
