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

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.ApplicationMenu;
import uk.co.q3c.v7.base.view.component.Breadcrumb;
import uk.co.q3c.v7.base.view.component.LoginStatusPanel;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;
import uk.co.q3c.v7.base.view.layout.ApplicationViewLayout1;
import uk.co.q3c.v7.base.view.layout.ViewBaseWithLayout;
import uk.co.q3c.v7.i18n.Translate;

public class ApplicationView1 extends ViewBaseWithLayout {

	private final UserNavigationTree navTree;
	private final Breadcrumb breadcrumb;
	private final LoginStatusPanel loginOut;
	private final ApplicationMenu menu;

	protected ApplicationView1(V7Navigator navigator, ApplicationViewLayout1 viewLayout, Translate translate,
			UserNavigationTree navTree, Breadcrumb breadcrumb, LoginStatusPanel loginOut, ApplicationMenu menu) {
		super(navigator, viewLayout, translate);
		this.navTree = navTree;
		this.breadcrumb = breadcrumb;
		this.loginOut = loginOut;
		this.menu = menu;
	}

	@Override
	protected void buildView() {
		// add(logo).width(50).height(70);
		// add(header).widthUndefined().heightPercent(100);
		// add(loginOut).width(100).heightPercent(100);
		// add((AbstractComponent) menu).height(60);
		// add(navTree);
		// add(breadcrumb).height(45);
		// add(body).heightPercent(100);
		// add(subpage).height(55);
		// add(messageBar).height(80);
	}

	@Override
	protected void processParams(List<String> params) {

	}
}
