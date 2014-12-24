/*
 * Copyright (C) 2014 David Sowerby
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
package uk.q3c.krail.core.view.component;

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
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.view.KrailViewChangeEvent;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.testutil.TestUserOptionModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class, VaadinSessionScopeModule.class})
public class DefaultSubPagePanelTest {

    DefaultSubPagePanel panel;

    @Inject
    ReferenceUserSitemap userSitemap;

    @Mock
    Navigator navigator;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    UserOption userOption;

    @Inject
    DefaultUserSitemapSorters sorters;

    @Mock
    KrailViewChangeEvent event;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
//        userOption.clear();
        currentLocale.setLocale(Locale.UK, false);
        userSitemap.populate();
        panel = new DefaultSubPagePanel(navigator, userSitemap, userOption, sorters, currentLocale);
    }

    @Test
    public void leaf() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.a11Node);
        // when
        panel.moveToNavigationState();
        // then
        List<NavigationButton> buttons = panel.getButtons();
        assertThat(buttons).hasSize(0);
    }

    @Test
    public void multi() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        // when
        panel.moveToNavigationState();
        // then
        List<UserSitemapNode> nodes = nodesFromButtons(panel.getButtons());
        List<UserSitemapNode> expected = userSitemap.publicSortedAlphaAscending();
        expected.add(userSitemap.logoutNode); // not filtered
        assertThat(nodes).containsAll(expected);
        assertThat(nodes).hasSameSizeAs(expected);
    }

    /**
     * There may be more buttons than nodes, as buttons are re-used and just made not visible of not needed, so only
     * copy nodes from buttons which are visible.
     *
     * @param buttons
     *
     * @return
     */
    List<UserSitemapNode> nodesFromButtons(List<NavigationButton> buttons) {
        List<UserSitemapNode> nodes = new ArrayList<>();
        for (NavigationButton button : buttons) {
            if (button.isVisible()) {
                nodes.add(button.getNode());
            }
        }
        return nodes;
    }

    @Test
    public void options() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        panel.moveToNavigationState();
        // when
        panel.setOptionSortType(SortType.INSERTION);
        panel.setOptionSortAscending(true);
        // then
        assertThat(panel.getOptionSortAscending()).isTrue();
        assertThat(panel.getOptionSortType()).isEqualTo(SortType.INSERTION);
    }

    @Test
    public void multi_filtered() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        // when
        panel.moveToNavigationState();
        // then
        List<UserSitemapNode> nodes = nodesFromButtons(panel.getButtons());
        assertThat(nodes).containsOnly(userSitemap.loginNode, userSitemap.aNode, userSitemap.publicHomeNode);
        // when
        panel.removeFilter(filter);
        panel.moveToNavigationState();
        // then
        nodes = nodesFromButtons(panel.getButtons());
        assertThat(nodes).containsOnly(userSitemap.loginNode, userSitemap.aNode, userSitemap.publicHomeNode,
                userSitemap.logoutNode);
    }

    @Test
    public void localeChanged() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);

        // when
        panel.moveToNavigationState();
        // then
        assertThat(panel.getButtons()
                        .get(0)
                        .getCaption()).isEqualTo("Log In");

        // when
        currentLocale.setLocale(Locale.GERMANY);
        // then
        assertThat(panel.getButtons()
                        .get(0)
                        .getCaption()).isEqualTo("Einloggen");
    }

    @Test
    public void sortSelection() {

        // given
        assertThat(panel.getOptionSortAscending()).isTrue();
        assertThat(panel.getOptionSortType()).isEqualTo(SortType.ALPHA);
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        // when
        panel.moveToNavigationState();
        // then
        List<UserSitemapNode> nodes = nodesFromButtons(panel.getButtons());
        assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedAlphaAscending());
        // when
        panel.setOptionSortAscending(false);
        // then
        nodes = nodesFromButtons(panel.getButtons());
        assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedAlphaDescending());
        // when
        panel.setSortAscending(true, false);
        panel.setOptionSortType(SortType.INSERTION);
        // then
        nodes = nodesFromButtons(panel.getButtons());
        assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedInsertionAscending());
        // when
        panel.setOptionSortAscending(false);
        // then
        nodes = nodesFromButtons(panel.getButtons());
        assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedInsertionDescending());
        // when
        panel.setSortAscending(true, false);
        panel.setOptionSortType(SortType.POSITION);
        // then
        nodes = nodesFromButtons(panel.getButtons());
        assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedPositionAscending());
        // when
        panel.setOptionSortAscending(false);
        // then
        nodes = nodesFromButtons(panel.getButtons());
        assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedPositionDescending());
    }

    @Test
    public void requiresRebuild() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        panel.moveToNavigationState();
        // when
        panel.setOptionSortAscending(false);
        // then build has happened
        assertThat(panel.isRebuildRequired()).isFalse();

        // when
        panel.setSortAscending(true, false);
        panel.setOptionSortType(SortType.INSERTION, false);
        // then build has not happened
        assertThat(panel.isRebuildRequired()).isTrue();
    }

    @Test
    public void structureChange() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        panel.moveToNavigationState();
        // when
        panel.structureChanged();
        // then make sure build has been called
        assertThat(panel.isRebuildRequired()).isFalse();
    }

    @Test
    public void afterViewChange() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        panel.moveToNavigationState();
        // when
        panel.afterViewChange(event);
        // then
        assertThat(panel.rebuildRequired).isFalse();
    }

    @Test
    public void id() {
        //given

        //when

        //then
        assertThat(panel.getId()).isEqualTo("DefaultSubPagePanel");
    }

    @ModuleProvider
    protected AbstractModule module() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
            }

        };
    }
}
