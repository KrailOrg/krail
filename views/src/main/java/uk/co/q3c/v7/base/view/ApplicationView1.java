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

import java.util.List;

import uk.co.q3c.v7.base.view.component.ApplicationHeader;
import uk.co.q3c.v7.base.view.component.ApplicationLogo;
import uk.co.q3c.v7.base.view.component.ApplicationMenu;
import uk.co.q3c.v7.base.view.component.Breadcrumb;
import uk.co.q3c.v7.base.view.component.DefaultComponentModule;
import uk.co.q3c.v7.base.view.component.DefaultViewBody;
import uk.co.q3c.v7.base.view.component.LoginStatusPanel;
import uk.co.q3c.v7.base.view.component.MessageBar;
import uk.co.q3c.v7.base.view.component.SubpagePanel;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;
import uk.co.q3c.v7.base.view.component.ViewBody;
import uk.co.q3c.v7.base.view.layout.ApplicationViewLayout1;
import uk.co.q3c.v7.base.view.layout.ViewBaseWithLayout;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;

/**
 * This view provides the base for a fairly typical layout for an application. It is not expected that it will be used
 * directly, as the body needs to be defined by a sub-class. All the components in the view - except the body - can be
 * replaced by mapping their interfaces to different implementations in the {@link DefaultComponentModule}. The body
 * component is created by overriding the {@link #createBody()} method
 * 
 * @author David Sowerby 29 Aug 2013
 * 
 */
public class ApplicationView1 extends ViewBaseWithLayout {

	private final UserNavigationTree navTree;
	private final Breadcrumb breadcrumb;
	private final LoginStatusPanel loginOut;
	private final ApplicationMenu menu;
	private final SubpagePanel subpage;
	private final MessageBar messageBar;
	private final ApplicationLogo logo;
	private final ApplicationHeader header;
	private final ViewBody body;

	@Inject
	protected ApplicationView1(ApplicationViewLayout1 viewLayout, Translate translate, UserNavigationTree navTree,
			Breadcrumb breadcrumb, LoginStatusPanel loginOut, ApplicationMenu menu, SubpagePanel subpage,
			MessageBar messageBar, ApplicationLogo logo, ApplicationHeader header) {
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

	protected void buildView() {
		add(logo).width(50).height(70);
		add(header).widthUndefined().heightPercent(100);
		add(loginOut).width(100).heightPercent(100);
		add(menu).height(60);
		add(navTree);
		add(breadcrumb).height(45);
		add(body).heightPercent(100);
		add(subpage).height(55);
		add(messageBar).height(80);
	}

	@Override
	protected void processParams(List<String> params) {
		body.processParams(params);
	}

	@Override
	public String viewName() {
		return "ApplicationView1";
	}

}
