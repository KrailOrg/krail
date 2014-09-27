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
package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.view.component.*;
import uk.co.q3c.v7.base.view.layout.ApplicationViewLayout1;
import uk.co.q3c.v7.base.view.layout.ViewBaseWithLayout;
import uk.co.q3c.v7.i18n.Translate;

/**
 * This view provides the base for a fairly typical layout for an application. It is not expected that it will be used
 * directly, as the body needs to be defined by a sub-class. All the components in the view - except the body - can be
 * replaced by mapping their interfaces to different implementations in the {@link StandardComponentModule}. The body
 * component is created by overriding the {@link #createBody()} method
 *
 * @author David Sowerby 29 Aug 2013
 */
public class ApplicationView1 extends ViewBaseWithLayout {

    private final UserNavigationTree navTree;
    private final Breadcrumb breadcrumb;
    private final UserStatusPanel loginOut;
    private final UserNavigationMenu menu;
    private final SubpagePanel subpage;
    private final MessageBar messageBar;
    private final ApplicationLogo logo;
    private final ApplicationHeader header;
    private final ViewBody body;

    @Inject
    protected ApplicationView1(ApplicationViewLayout1 viewLayout, Translate translate, UserNavigationTree navTree,
                               Breadcrumb breadcrumb, UserStatusPanel loginOut, UserNavigationMenu menu,
                               SubpagePanel subpage, MessageBar messageBar, ApplicationLogo logo,
                               ApplicationHeader header) {
        super(viewLayout, translate);
        this.navTree = navTree;
        this.breadcrumb = breadcrumb;
        this.loginOut = loginOut;
        this.menu = menu;
        this.subpage = subpage;
        this.messageBar = messageBar;
        this.logo = logo;
        this.header = header;
        body = createBody();
        buildView();
    }

    /**
     * Override this to provide your own body component
     *
     * @return
     */
    protected ViewBody createBody() {
        return new DefaultViewBody();
    }

    protected com.vaadin.ui.TextArea buildView() {
        add(logo).width(50)
                 .height(70);
        add(header).widthUndefined()
                   .heightPercent(100);
        add(loginOut).width(100)
                     .heightPercent(100);
        add(menu).height(60);
        add(navTree);
        add(breadcrumb).height(45);
        add(body).heightPercent(100);
        add(subpage).height(55);
        add(messageBar).height(80);
        return null;
    }


    @Override
    protected void processParams(NavigationState navigationState) {
        body.processParams(navigationState);
    }

    @Override
    public String viewName() {
        return "ApplicationView1";
    }

    /**
     * Called immediately after construction of the view to enable setting up the view from URL parameters
     *
     * @param navigationState
     */
    @Override
    public void prepareView(NavigationState navigationState) {

    }

}
