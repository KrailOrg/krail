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
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.ReferenceUserSitemap;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.VaadinEventBusModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.TestKrailI18NModule2;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.core.shiro.DefaultShiroModule;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.LocaleChangeBusMessage;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.mock.TestOptionModule;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;

import java.text.Collator;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestOptionModule.class, InMemoryModule.class, VaadinSessionScopeModule.class, VaadinEventBusModule.class,
        TestUIScopeModule.class, UtilModule.class, UtilsModule.class, TestKrailI18NModule2.class, DefaultShiroModule.class})
public class BreadcrumbTest {

    DefaultBreadcrumb breadcrumb;

    @Mock
    Navigator navigator;

    @Inject
    CurrentLocale currentLocale;

    @Mock
    MasterSitemap sitemap;

    @Inject
    Translate translate;

    MasterSitemapNode masterNode7;

    @Mock
    Option option;

    @Inject
    @SessionBus
    PubSubSupport<BusMessage> eventBus;

    @Inject
    ReferenceUserSitemap userSitemap;

    Collator collator;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        userSitemap.populate();
        createBreadcrumb();

    }

    private void createBreadcrumb() {
        breadcrumb = new DefaultBreadcrumb(navigator, userSitemap);
    }

    @Test
    public void buildAndViewChange() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.a11Node());
        // when
        breadcrumb.moveToNavigationState();
        // then
        assertThat(breadcrumb.getButtons()
                             .size()).isEqualTo(4);
        assertThat(breadcrumb.getComponentCount()).isEqualTo(4);
        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getCaption()).isEqualTo(userSitemap.publicNode()
                                                                 .getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .getCaption()).isEqualTo(userSitemap.aNode()
                                                                 .getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .getCaption()).isEqualTo(userSitemap.a1Node()
                                                                 .getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(3)
                             .getCaption()).isEqualTo(userSitemap.a11Node()
                                                                 .getLabel());

        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getNode()).isEqualTo(userSitemap.publicNode());
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .getNode()).isEqualTo(userSitemap.aNode());
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .getNode()).isEqualTo(userSitemap.a1Node());
        assertThat(breadcrumb.getButtons()
                             .get(3)
                             .getNode()).isEqualTo(userSitemap.a11Node());

        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .isVisible()).isTrue();
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .isVisible()).isTrue();
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .isVisible()).isTrue();
        assertThat(breadcrumb.getButtons()
                             .get(3)
                             .isVisible()).isTrue();

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.b1Node());
        // when
        breadcrumb.afterViewChange(null);
        // then
        assertThat(breadcrumb.getButtons()
                             .size()).isEqualTo(4);
        assertThat(breadcrumb.getComponentCount()).isEqualTo(4);
        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getCaption()).isEqualTo(userSitemap.privateNode()
                                                                 .getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .getCaption()).isEqualTo(userSitemap.bNode()
                                                                 .getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .getCaption()).isEqualTo(userSitemap.b1Node()
                                                                 .getLabel());

        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getNode()).isEqualTo(userSitemap.privateNode());
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .getNode()).isEqualTo(userSitemap.bNode());
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .getNode()).isEqualTo(userSitemap.b1Node());

        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .isVisible()).isTrue();
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .isVisible()).isTrue();
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .isVisible()).isTrue();
        assertThat(breadcrumb.getButtons()
                             .get(3)
                             .isVisible()).isFalse();

        // given
        NavigationButton step = breadcrumb.getButtons()
                                          .get(1);
        // when button clicked
        step.click();
        // then
        verify(navigator).navigateTo(step.getNode());
    }

    @Test
    public void localeChanged() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.a11Node());
        LogoutPageFilter filter = new LogoutPageFilter();
        breadcrumb.addFilter(filter);

        // when
        breadcrumb.moveToNavigationState();
        // then
        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getCaption()).isEqualTo("Public");

        // when
        currentLocale.setLocale(Locale.GERMANY);
        breadcrumb.localeChanged(new LocaleChangeBusMessage(this, Locale.GERMANY));
        // then
        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getCaption()).isEqualTo("Öffentlich");
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
}
