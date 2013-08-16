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
package uk.co.q3c.v7.base.view.template;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the only {@link ViewLayout} provided within the V7 base library. It is intended that others will be provided
 * in a companion library. It provides little more than standard {@link VerticalLayout} functionality, except that it
 * uses the standard {@link ViewLayout} interface.
 * 
 * @author David Sowerby 29 Mar 2013
 * 
 */
public class VerticalViewLayout extends ViewLayoutBase {

	protected VerticalViewLayout() {
		super();

	}

	@Override
	public void assemble(ViewConfig config) {
		VerticalLayout vl = new VerticalLayout();
		for (Component c : components) {
			vl.addComponent(c);
		}
		layoutRoot = vl;
	}

	@Override
	public ViewConfig defaultConfig() {
		DefaultViewConfig config = new DefaultViewConfig();
		return config;
	}

}
