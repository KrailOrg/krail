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
package uk.q3c.krail.base.view.component;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.ReferenceUserSitemap;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.base.navigate.Navigator;
import uk.q3c.krail.base.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.base.navigate.URIFragmentHandler;
import uk.q3c.krail.base.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.base.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.base.user.opt.DefaultUserOption;
import uk.q3c.krail.base.user.opt.DefaultUserOptionStore;
import uk.q3c.krail.base.user.opt.UserOption;
import uk.q3c.krail.base.user.opt.UserOptionStore;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.Translate;

import java.text.Collator;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class})
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
    UserOption userOption;

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
        breadcrumb = new DefaultBreadcrumb(navigator, userSitemap, currentLocale);
    }

    @Test
    public void buildAndViewChange() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.a11Node);
        // when
        breadcrumb.moveToNavigationState();
        // then
        assertThat(breadcrumb.getButtons()
                             .size()).isEqualTo(4);
        assertThat(breadcrumb.getComponentCount()).isEqualTo(4);
        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getCaption()).isEqualTo(userSitemap.publicNode.getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .getCaption()).isEqualTo(userSitemap.aNode.getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .getCaption()).isEqualTo(userSitemap.a1Node.getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(3)
                             .getCaption()).isEqualTo(userSitemap.a11Node.getLabel());

        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getNode()).isEqualTo(userSitemap.publicNode);
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .getNode()).isEqualTo(userSitemap.aNode);
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .getNode()).isEqualTo(userSitemap.a1Node);
        assertThat(breadcrumb.getButtons()
                             .get(3)
                             .getNode()).isEqualTo(userSitemap.a11Node);

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
        when(navigator.getCurrentNode()).thenReturn(userSitemap.b1Node);
        // when
        breadcrumb.afterViewChange(null);
        // then
        assertThat(breadcrumb.getButtons()
                             .size()).isEqualTo(4);
        assertThat(breadcrumb.getComponentCount()).isEqualTo(4);
        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getCaption()).isEqualTo(userSitemap.privateNode.getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .getCaption()).isEqualTo(userSitemap.bNode.getLabel());
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .getCaption()).isEqualTo(userSitemap.b1Node.getLabel());

        assertThat(breadcrumb.getButtons()
                             .get(0)
                             .getNode()).isEqualTo(userSitemap.privateNode);
        assertThat(breadcrumb.getButtons()
                             .get(1)
                             .getNode()).isEqualTo(userSitemap.bNode);
        assertThat(breadcrumb.getButtons()
                             .get(2)
                             .getNode()).isEqualTo(userSitemap.b1Node);

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
        when(navigator.getCurrentNode()).thenReturn(userSitemap.a11Node);
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
                bind(UserOption.class).to(DefaultUserOption.class);
                bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
            }

        };
    }
}
