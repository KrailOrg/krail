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
    private final SubPagePanel subpage;
    private final MessageBar messageBar;
    private final ApplicationLogo logo;
    private final ApplicationHeader header;
    private final ViewBody body;

    @Inject
    protected ApplicationView1(ApplicationViewLayout1 viewLayout, Translate translate, UserNavigationTree navTree,
                               Breadcrumb breadcrumb, UserStatusPanel loginOut, UserNavigationMenu menu,
                               SubPagePanel subpage, MessageBar messageBar, ApplicationLogo logo,
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
    }

    /**
     * Override this to provide your own body component
     *
     * @return
     */
    protected ViewBody createBody() {
        return new DefaultViewBody();
    }

    /**
     * Called after the view itself has been constructed but before {@link #buildView()} is called.  Typically checks
     * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
     * configuration which affects the way the view is presented.
     *
     * @param event
     *         contains information about the change to this View
     */
    @Override
    public void beforeBuild(V7ViewChangeEvent event) {

    }

    @Override
    public void buildView(V7ViewChangeEvent event) {
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
    }


    @Override
    public void afterBuild(V7ViewChangeEvent event) {
        super.afterBuild(event);
        body.processParams(event.getNavigationState());
    }

    @Override
    public String viewName() {
        return "ApplicationView1";
    }


}
