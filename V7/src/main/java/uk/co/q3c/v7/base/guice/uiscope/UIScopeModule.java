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
package uk.co.q3c.v7.base.guice.uiscope;

import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.demo.ui.BasicUI;
import uk.co.q3c.v7.demo.ui.SideBarUI;
import uk.co.q3c.v7.demo.view.DemoErrorView;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.vaadin.ui.UI;

public class UIScopeModule extends AbstractModule {
	private final UIScope uiScope;

	public UIScopeModule() {
		super();
		uiScope = UIScope.getCurrent();

	}

	@Override
	public void configure() {

		// tell Guice about the scope
		bindScope(UIScoped.class, uiScope);

		// make our scope instance injectable
		bind(UIScope.class).annotatedWith(Names.named("UIScope")).toInstance(uiScope);
		// TODO move to application module
		// see https://github.com/davidsowerby/v7/issues/55
		MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);
		mapbinder.addBinding(BasicUI.class.getName()).to(BasicUI.class);
		mapbinder.addBinding(SideBarUI.class.getName()).to(SideBarUI.class);

		// will be used if a view mapping is not found
		bind(ErrorView.class).to(DemoErrorView.class);
	}

	public UIScope getUiScope() {
		return uiScope;
	}

}