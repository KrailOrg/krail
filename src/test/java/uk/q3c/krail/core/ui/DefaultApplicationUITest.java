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

package uk.q3c.krail.core.ui;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.v7.ui.Tree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.i18n.I18NProcessor;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.push.DefaultBroadcaster;
import uk.q3c.krail.core.push.DefaultPushMessageRouter;
import uk.q3c.krail.core.user.notify.VaadinNotification;
import uk.q3c.krail.core.view.component.ApplicationHeader;
import uk.q3c.krail.core.view.component.ApplicationLogo;
import uk.q3c.krail.core.view.component.Breadcrumb;
import uk.q3c.krail.core.view.component.LocaleSelector;
import uk.q3c.krail.core.view.component.MessageBar;
import uk.q3c.krail.core.view.component.SubPagePanel;
import uk.q3c.krail.core.view.component.UserNavigationMenu;
import uk.q3c.krail.core.view.component.UserNavigationTree;
import uk.q3c.krail.core.view.component.UserStatusPanel;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.test.TestOptionModule;
import uk.q3c.krail.testutil.guice.vsscope.TestVaadinSessionScopeModule;
import uk.q3c.krail.testutil.persist.TestPersistenceModuleVaadin;
import uk.q3c.util.UtilModule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestOptionModule.class, TestPersistenceModuleVaadin.class, TestVaadinSessionScopeModule.class, UtilModule.class})
public class DefaultApplicationUITest {


    DefaultApplicationUI ui;
    @Mock
    MenuBar menuBar;
    @Mock
    Tree tree;
    @Mock
    private ApplicationTitle applicationTitle;
    @Mock
    private Breadcrumb breadcrumb;
    @Mock
    private DefaultBroadcaster broadcaster;

    @Mock
    private CurrentLocale currentLocale;
    @Mock
    private ErrorHandler errorHandler;
    @Mock
    private ApplicationHeader header;
    @Mock
    private LocaleSelector localeSelector;
    @Mock
    private ApplicationLogo logo;
    @Mock
    private UserNavigationMenu menu;
    @Mock
    private MessageBar messageBar;
    @Mock
    private UserNavigationTree navTree;
    @Mock
    private Navigator navigator;
    @Inject
    private Option option;
    @Mock
    private DefaultPushMessageRouter pushMessageRouter;
    @Mock
    private SubPagePanel subpage;
    @Mock
    private Translate translate;
    @Mock
    private I18NProcessor translator;
    @Mock
    private UserStatusPanel userStatusPanel;
    @Mock
    private VaadinNotification vaadinNotification;

    @Before
    public void setup() {
        when(localeSelector.getComponent()).thenReturn(new Label());
        when(navTree.getTree()).thenReturn(tree);
        when(menu.getMenuBar()).thenReturn(menuBar);
        ui = new DefaultApplicationUI(navigator, errorHandler, logo, header, userStatusPanel, menu, navTree, breadcrumb, subpage, messageBar, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator, localeSelector, vaadinNotification, option);
    }

    @Test
    public void allVisible() {
        //given

        //when
        ui.doLayout();
        //then
        verify(navTree).setVisible(true);
        verify(menu).setVisible(true);
        verify(breadcrumb).setVisible(true);
        verify(messageBar).setVisible(true);
        verify(subpage).setVisible(true);
    }

    @Test
    public void turnedOff() {
        //given
        option.set(DefaultApplicationUI.optionBreadcrumbVisible, false);
        option.set(DefaultApplicationUI.optionMenuVisible, false);
        option.set(DefaultApplicationUI.optionNavTreeVisible, false);
        option.set(DefaultApplicationUI.optionMessageBarVisible, false);
        option.set(DefaultApplicationUI.optionSubPagePanelVisible, false);
        //when

        ui.doLayout();
        //then
        verify(navTree).setVisible(false);
        verify(menu).setVisible(false);
        verify(breadcrumb).setVisible(false);
        verify(messageBar).setVisible(false);
        verify(subpage).setVisible(false);
    }

    @Test
    public void getters() throws Exception {
        //given

        //when
        ui.doLayout();

        //then
        assertThat(ui.getNavTree()).isNotNull();
        assertThat(ui.getMenu()).isNotNull();
        assertThat(ui.getBreadcrumb()).isNotNull();
        assertThat(ui.getMessageBar()).isNotNull();
        assertThat(ui.getSubpage()).isNotNull();
        assertThat(ui.getUserStatus()).isNotNull();
        assertThat(ui.getLogo()).isNotNull();
        assertThat(ui.getHeader()).isNotNull();
        assertThat(ui.optionInstance()).isNotNull();
    }
}