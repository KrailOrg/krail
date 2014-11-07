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
import com.vaadin.ui.MenuBar.MenuItem;
import fixture.ReferenceUserSitemap;
import fixture.TestI18NModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.base.guice.uiscope.UIScopeModule;
import uk.q3c.krail.base.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.base.navigate.Navigator;
import uk.q3c.krail.base.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.base.navigate.URIFragmentHandler;
import uk.q3c.krail.base.user.opt.*;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({UIScopeModule.class, VaadinSessionScopeModule.class, TestI18NModule.class})
public class DefaultUserNavigationMenuTest {

    @Inject
    ReferenceUserSitemap userSitemap;

    @Inject
    CurrentLocale currentLocale;

    @Mock
    Navigator navigator;

    @Inject
    DefaultUserOption userOption;

    @Inject
    Translate translate;


    DefaultUserNavigationMenuBuilder builder;

    private DefaultUserNavigationMenu userNavigationMenu;

    @Before
    public void setUp() throws Exception {
        Locale.setDefault(Locale.UK);
        currentLocale.setLocale(Locale.UK);
        userSitemap.clear();
        userSitemap.populate();
        builder = new DefaultUserNavigationMenuBuilder(userSitemap, navigator);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void build() {
        // given

        userNavigationMenu = newMenu();

        // when
        userNavigationMenu.build();
        // then

        List<String> captions = menuCaptions(null);
        assertThat(captions).containsOnly("Public", "Private");

        MenuItem pblic = childWithText("Public", null);
        captions = menuCaptions(pblic);
        assertThat(captions).containsOnly("Log In", "Public Home", "ViewA");
        assertThat(pblic.getCommand()).isNull();

        MenuItem login = childWithText("Log In", pblic);
        captions = menuCaptions(login);
        assertThat(captions).containsOnly();
        assertThat(login.getCommand()).isNotNull();

        MenuItem publicHome = childWithText("Public Home", pblic);
        captions = menuCaptions(publicHome);
        assertThat(captions).containsOnly();
        assertThat(publicHome.getCommand()).isNotNull();

        MenuItem viewA = childWithText("ViewA", pblic);
        captions = menuCaptions(viewA);
        assertThat(captions).containsOnly("ViewA1");
        assertThat(viewA.getCommand()).isNull();

        MenuItem viewA1 = childWithText("ViewA1", viewA);
        captions = menuCaptions(viewA1);
        assertThat(captions).containsOnly("ViewA11");
        assertThat(viewA1.getCommand()).isNull();

        MenuItem viewA11 = childWithText("ViewA11", viewA1);
        captions = menuCaptions(viewA11);
        assertThat(captions).containsOnly();
        assertThat(viewA11.getCommand()).isNotNull();

        MenuItem prvate = childWithText("Private", null);
        captions = menuCaptions(prvate);
        assertThat(captions).containsOnly("Private Home", "ViewB");
        assertThat(prvate.getCommand()).isNull();

        MenuItem privateHome = childWithText("Private Home", prvate);
        captions = menuCaptions(privateHome);
        assertThat(captions).containsOnly();
        assertThat(privateHome.getCommand()).isNotNull();

        MenuItem viewB = childWithText("ViewB", prvate);
        captions = menuCaptions(viewB);
        assertThat(captions).containsOnly("ViewB1");
        assertThat(viewB.getCommand()).isNull();

        MenuItem viewB1 = childWithText("ViewB1", viewB);
        captions = menuCaptions(viewB1);
        assertThat(captions).containsOnly("ViewB11");
        assertThat(viewB1.getCommand()).isNull();

        MenuItem viewB11 = childWithText("ViewB11", viewB1);
        captions = menuCaptions(viewB11);
        assertThat(captions).containsOnly();
        assertThat(viewB11.getCommand()).isNotNull();

    }

    private List<String> menuCaptions(MenuItem parentItem) {
        List<MenuItem> items = childrenOf(parentItem);
        List<String> captions = new ArrayList<>();
        if (items != null) {
            for (MenuItem item : items) {
                captions.add(item.getText());
            }
        }
        return captions;
    }

    private List<MenuItem> childrenOf(MenuItem parentItem) {
        List<MenuItem> items = null;
        if (parentItem == null) {
            items = userNavigationMenu.getItems();
        } else {
            items = parentItem.getChildren();
        }
        return items;
    }

    private MenuItem childWithText(String text, MenuItem parentItem) {
        List<MenuItem> items = childrenOf(parentItem);

        for (MenuItem item : items) {
            if (item.getText()
                    .equals(text)) {
                return item;
            }
        }
        return null;
    }

    private DefaultUserNavigationMenu newMenu() {
        return new DefaultUserNavigationMenu(userSitemap, userOption, builder);
    }

    @Test
    public void build_depthLimited() {
        // given

        userNavigationMenu = newMenu();

        // when
        userNavigationMenu.setMaxDepth(2);
        // then

        List<String> captions = menuCaptions(null);
        assertThat(captions).containsOnly("Public", "Private");

        MenuItem pblic = childWithText("Public", null);
        captions = menuCaptions(pblic);
        assertThat(captions).containsOnly("Log In", "Public Home", "ViewA");
        assertThat(pblic.getCommand()).isNull();

        MenuItem login = childWithText("Log In", pblic);
        captions = menuCaptions(login);
        assertThat(captions).containsOnly();
        assertThat(login.getCommand()).isNotNull();

        MenuItem publicHome = childWithText("Public Home", pblic);
        captions = menuCaptions(publicHome);
        assertThat(captions).containsOnly();
        assertThat(publicHome.getCommand()).isNotNull();

        MenuItem viewA = childWithText("ViewA", pblic);
        captions = menuCaptions(viewA);
        assertThat(captions).containsOnly();
        assertThat(viewA.getCommand()).isNotNull();

        MenuItem prvate = childWithText("Private", null);
        captions = menuCaptions(prvate);
        assertThat(captions).containsOnly("Private Home", "ViewB");
        assertThat(prvate.getCommand()).isNull();

        MenuItem privateHome = childWithText("Private Home", prvate);
        captions = menuCaptions(privateHome);
        assertThat(captions).containsOnly();
        assertThat(privateHome.getCommand()).isNotNull();

        MenuItem viewB = childWithText("ViewB", prvate);
        captions = menuCaptions(viewB);
        assertThat(captions).containsOnly();
        assertThat(viewB.getCommand()).isNotNull();

    }

    @Test
    public void setMaxDepth() {

        // given
        userNavigationMenu = newMenu();

        // when
        userNavigationMenu.setMaxDepth(3);
        // then
        assertThat(userNavigationMenu.getMaxDepth()).isEqualTo(3);
        // userOption has been set
        int result = userOption.getOptionAsInt(DefaultUserNavigationMenu.class.getSimpleName(),
                UserOptionProperty.MAX_DEPTH, -1);
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void localeChange() {

        // given
        userNavigationMenu = newMenu();
        userNavigationMenu.build();

        // when
        currentLocale.setLocale(Locale.GERMANY);
        // then
        List<String> captions = menuCaptions(null);
        assertThat(captions).containsOnly("Öffentlich", "Privat");
        String txt = translate.from(LabelKey.Public);
        MenuItem pblic = childWithText(txt, null);
        MenuItem publicHome = childWithText("Öffentliche Startseite", pblic);
        assertThat(publicHome.getText()).isEqualTo("Öffentliche Startseite");

    }

    @Test
    public void defaults() {

        // given

        // when
        userNavigationMenu = newMenu();
        // then
        assertThat(userNavigationMenu.isImmediate()).isTrue();
        assertThat(userNavigationMenu.getMaxDepth()).isEqualTo(10);

    }

    @Test
    public void userSelection() {

        // given
        userNavigationMenu = newMenu();
        userNavigationMenu.build();

        MenuItem pblic = childWithText("Public", null);
        MenuItem viewA = childWithText("ViewA", pblic);
        MenuItem viewA1 = childWithText("ViewA1", viewA);
        MenuItem viewA11 = childWithText("ViewA11", viewA1);

        // when
        viewA11.getCommand()
               .menuSelected(viewA11);
        // then
        verify(navigator).navigateTo(userSitemap.a11Node);
    }

    @Test
    public void sorted() {

        userNavigationMenu = newMenu();

        // when
        userNavigationMenu.build();
        // then

        List<String> captions = menuCaptions(null);
        assertThat(captions).containsExactly("Private", "Public");

        MenuItem pblic = childWithText("Public", null);
        captions = menuCaptions(pblic);
        // sorting is true by default
        assertThat(captions).containsExactly("Log In", "Public Home", "ViewA");

        // when
        currentLocale.setLocale(Locale.GERMANY);
        captions = menuCaptions(null);
        assertThat(captions).containsExactly("Privat", "Öffentlich");

        pblic = childWithText("Öffentlich", null);
        captions = menuCaptions(pblic);
        // sorting is true by default
        assertThat(captions).containsExactly("DE_ViewA", "Einloggen", "Öffentliche Startseite");

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
