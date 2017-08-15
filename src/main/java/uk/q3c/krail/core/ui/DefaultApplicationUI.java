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
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.I18NProcessor;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.push.Broadcaster;
import uk.q3c.krail.core.push.PushMessageRouter;
import uk.q3c.krail.core.user.notify.VaadinNotification;
import uk.q3c.krail.core.view.component.*;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionContext;
import uk.q3c.krail.option.OptionKey;

/**
 * A common layout for a business-type application. This is a good place to start even if you replace it eventually.
 *
 * @author David Sowerby
 */

public class DefaultApplicationUI extends ScopedUI implements OptionContext {

    protected static final OptionKey<Boolean> optionBreadcrumbVisible = new OptionKey<>(Boolean.TRUE, DefaultApplicationUI.class, LabelKey
            .Breadcrumb_is_Visible,
            DescriptionKey.Breadcrumb_is_Visible);
    protected static final OptionKey<Boolean> optionNavTreeVisible = new OptionKey<>(Boolean.TRUE, DefaultApplicationUI.class, LabelKey
            .Navigation_Tree_is_Visible,
            DescriptionKey.Navigation_Tree_is_Visible);
    protected static final OptionKey<Boolean> optionMenuVisible = new OptionKey<>(Boolean.TRUE, DefaultApplicationUI.class, LabelKey.Navigation_Menu_is_Visible,
            DescriptionKey.Navigation_Menu_is_Visible);
    protected static final OptionKey<Boolean> optionMessageBarVisible = new OptionKey<>(Boolean.TRUE, DefaultApplicationUI.class, LabelKey
            .Message_bar_is_Visible,
            DescriptionKey.MessageBar_is_Visible);
    protected static final OptionKey<Boolean> optionSubPagePanelVisible = new OptionKey<>(Boolean.TRUE, DefaultApplicationUI.class, LabelKey
            .SubPage_Panel_is_Visible,
            DescriptionKey.SubPage_Panel_is_Visible);

    private final UserNavigationTree navTree;
    private final Breadcrumb breadcrumb;
    private final UserStatusPanel userStatus;
    private final UserNavigationMenu menu;
    private final SubPagePanel subpage;
    private final MessageBar messageBar;
    private final ApplicationLogo logo;
    private final ApplicationHeader header;
    private final LocaleSelector localeSelector;
    // this appears not to be used but does receive bus messages
    private final VaadinNotification vaadinNotification;
    private VerticalLayout baseLayout;
    private HorizontalLayout headerRow;
    private VerticalLayout mainArea;
    private Panel nonSplitPanel;
    private Option option;
    private HorizontalSplitPanel splitPanel;

    @SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
    @Inject
    protected DefaultApplicationUI(Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory, ApplicationLogo logo, ApplicationHeader
            header, UserStatusPanel userStatusPanel, UserNavigationMenu menu, UserNavigationTree navTree, Breadcrumb breadcrumb, SubPagePanel subpage, MessageBar messageBar, Broadcaster broadcaster, PushMessageRouter pushMessageRouter, ApplicationTitle applicationTitle, Translate translate, CurrentLocale currentLocale, I18NProcessor translator, LocaleSelector localeSelector, VaadinNotification vaadinNotification, Option option) {
        super(navigator, errorHandler, converterFactory, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator);
        this.navTree = navTree;
        this.breadcrumb = breadcrumb;
        this.userStatus = userStatusPanel;
        this.menu = menu;
        this.subpage = subpage;
        this.messageBar = messageBar;
        this.logo = logo;
        this.header = header;
        this.localeSelector = localeSelector;
        this.vaadinNotification = vaadinNotification;
        this.option = option;
    }

    @Override
    protected AbstractOrderedLayout screenLayout() {
        buildPage();
        return baseLayout;
    }

    protected void buildPage() {
        headerRow();
        messageBar();
        subPagePanel();
        getBaseLayout();
        mainArea();
        navTree();
        navMenu();
        breadcrumb();
        splitPanel();
        nonSplitPanel();

        setSizesForInjectComponents();
        baseLayout.addComponent(headerRow);
        baseLayout.addComponent(menu);
        baseLayout.addComponent(splitPanel);
        baseLayout.addComponent(nonSplitPanel);

        if (option.get(optionNavTreeVisible)) {
            baseLayout.setExpandRatio(splitPanel, 1f);
            baseLayout.setExpandRatio(nonSplitPanel, 0f);
        } else {
            baseLayout.setExpandRatio(nonSplitPanel, 1f);
            baseLayout.setExpandRatio(splitPanel, 0f);
        }
        baseLayout.addComponent(messageBar);
    }

    protected void headerRow() {
        headerRow = new HorizontalLayout(header, localeSelector.getComponent(), userStatus);
        headerRow.setWidth("100%");
        headerRow.setExpandRatio(header, 1f);

    }

    private void setSizesForInjectComponents() {
        logo.setWidth("100px");
        logo.setHeight("100px");

        header.setHeight("100%");
        userStatus.setSizeUndefined();

        navTree.setSizeFull();
        breadcrumb.setSizeUndefined();

        menu.setSizeUndefined();
        menu.setWidth("100%");

        subpage.setSizeUndefined();

        messageBar.setSizeUndefined();
        messageBar.setWidth("100%");
    }

    protected void messageBar() {
        messageBar.setVisible(option.get(optionMessageBarVisible));
    }

    protected void subPagePanel() {
        subpage.setVisible(option.get(optionSubPagePanelVisible));
    }

    protected void breadcrumb() {
        breadcrumb.setVisible(option.get(optionBreadcrumbVisible));
    }

    protected void navTree() {
        if (option.get(optionNavTreeVisible)) {
            navTree.build();
            navTree.setVisible(true);
            navTree.getTree()
                   .setImmediate(true);
        } else {
            navTree.setVisible(false);
        }
    }

    protected void navMenu() {
        if (option.get(optionMenuVisible)) {
            menu.build();
            menu.setVisible(true);
            menu.getMenuBar()
                .setImmediate(true);
        } else {
            menu.setVisible(false);
        }
    }

    protected void mainArea() {
        if (mainArea == null) {
            mainArea = new VerticalLayout(breadcrumb, getViewDisplayPanel(), subpage);
            mainArea.setSizeFull();
            mainArea.setExpandRatio(getViewDisplayPanel(), 1f);
        }
    }

    protected void splitPanel() {
        if (splitPanel == null) {
            splitPanel = new HorizontalSplitPanel();
            splitPanel.setWidth("100%");
            splitPanel.setSplitPosition(200, Unit.PIXELS);
        }
        if (option.get(optionNavTreeVisible)) {
            splitPanel.setFirstComponent(navTree);
            splitPanel.setSecondComponent(mainArea);
            splitPanel.setVisible(true);
        } else {
            splitPanel.setVisible(false);
        }
    }

    /**
     * used wqhen nav tree not required, and split panel also therefore not required
     */
    protected void nonSplitPanel() {
        if (nonSplitPanel == null) {
            nonSplitPanel = new Panel();
            nonSplitPanel.setSizeFull();
        }
        if (!option.get(optionNavTreeVisible)) {
            nonSplitPanel.setContent(mainArea);
            nonSplitPanel.setVisible(true);
        } else {
            nonSplitPanel.setVisible(false);
        }
    }

    public VerticalLayout getBaseLayout() {
        if (baseLayout == null) {
            baseLayout = new VerticalLayout();
        }
        return baseLayout;
    }

    public MessageBar getMessageBar() {
        return messageBar;
    }

    public UserNavigationTree getNavTree() {
        return navTree;
    }

    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }

    public UserStatusPanel getUserStatus() {
        return userStatus;
    }

    public UserNavigationMenu getMenu() {
        return menu;
    }

    public SubPagePanel getSubpage() {
        return subpage;
    }

    public ApplicationLogo getLogo() {
        return logo;
    }

    public ApplicationHeader getHeader() {
        return header;
    }

    /**
     * Returns the {@link Option} instance being used by this context
     *
     * @return the {@link Option} instance being used by this context
     */

    @Override
    public Option getOption() {
        return option;
    }

    @Override
    public void optionValueChanged(Property.ValueChangeEvent event) {
        //this causes random elements to disappear - better to need manual browser refresh for now
        //        super.doLayout();
        this.markAsDirtyRecursive();

    }




}