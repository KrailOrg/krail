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
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.ReferenceUserSitemap;
import net.engio.mbassy.bus.MBassador;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.env.ServletEnvironmentModule;
import uk.q3c.krail.core.eventbus.VaadinEventBusModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.TestKrailI18NModule2;
import uk.q3c.krail.core.navigate.NavigationState;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapStructureChangeMessage;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.shiro.DefaultShiroModule;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.mock.MockOption;
import uk.q3c.krail.option.mock.TestOptionModule;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;
import uk.q3c.util.guice.SerializationSupport;
import uk.q3c.util.guice.SerializationSupportModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestKrailI18NModule2.class, DefaultShiroModule.class, InMemoryModule.class, DefaultSubPagePanelTest.LocalOptionModule.class, VaadinSessionScopeModule.class, VaadinEventBusModule.class,
        TestUIScopeModule.class, UtilModule.class, UserModule.class, UtilsModule.class, SerializationSupportModule.class, ServletEnvironmentModule.class})
public class DefaultSubPagePanelTest {

    DefaultSubPagePanel panel;

    @Inject
    ReferenceUserSitemap userSitemap;

    @Mock
    Provider<UserSitemap> userSitemapProvider;

    @Mock
    Navigator navigator;

    @Mock
    Provider<Navigator> navigatorProvider;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    Provider<Option> optionProvider;

    @Inject
    DefaultUserSitemapSorters sorters;

    @Mock
    AfterViewChangeBusMessage event;

    @Mock
    MBassador<BusMessage> eventBus;

    @Mock
    NavigationState currentNavigationState;

    @Mock
    SerializationSupport serializationSupport;


    @Before
    public void setup() {
        when(navigatorProvider.get()).thenReturn(navigator);
        when(navigator.getCurrentNavigationState()).thenReturn(currentNavigationState);
        when(userSitemapProvider.get()).thenReturn(userSitemap);
        Locale.setDefault(Locale.UK);
        currentLocale.setLocale(Locale.UK, false);
        userSitemap.populate();

        panel = new DefaultSubPagePanel(navigatorProvider, userSitemapProvider, optionProvider, sorters, serializationSupport);
    }


    /**
     * There may be more buttons than nodes, as buttons are re-used and just made not visible if not needed, so only
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
    public void sortSelection() {

        // given
        assertThat(panel.getOptionSortAscending()).isTrue();
        assertThat(panel.getOptionSortType()).isEqualTo(SortType.ALPHA);
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode());
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
        panel.setOptionKeySortType(SortType.INSERTION);
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
        panel.setOptionKeySortType(SortType.POSITION);
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
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode());
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        panel.moveToNavigationState();
        // when
        panel.setOptionSortAscending(false);
        // then build has happened
        assertThat(panel.getRebuildRequired()).isFalse();

        // when
        panel.setSortAscending(true, false);
        panel.setOptionSortType(SortType.INSERTION, false);
        // then build has not happened
        assertThat(panel.getRebuildRequired()).isTrue();
    }

    @Test
    public void structureChange() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode());
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        panel.moveToNavigationState();
        // when
        panel.structureChanged(new UserSitemapStructureChangeMessage());
        // then make sure build has been called
        assertThat(panel.getRebuildRequired()).isFalse();
    }

    @Test
    public void afterViewChange() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode());
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        panel.moveToNavigationState();
        // when
        panel.afterViewChange(event);
        // then
        assertThat(panel.isRebuildRequired()).isFalse();
    }

    @Test
    public void rebuildWithCurrentNodeNull() {
        //given
        when(navigator.getCurrentNode()).thenReturn(null);
        LogoutPageFilter filter = new LogoutPageFilter();
        panel.addFilter(filter);
        panel.moveToNavigationState();
        //when
        panel.structureChanged(new UserSitemapStructureChangeMessage());
        //then
        assertThat(panel.isRebuildRequired()).isFalse(); // we just want to make sure there is no NPE
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

    public static class LocalOptionModule extends TestOptionModule {
        @Override
        protected void bindOption() {
            bind(Option.class).toInstance(new MockOption());
        }

    }

}
