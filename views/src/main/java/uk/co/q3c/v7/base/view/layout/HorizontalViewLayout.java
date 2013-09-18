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
package uk.co.q3c.v7.base.view.layout;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;

public class HorizontalViewLayout extends VerticalViewLayout {

	protected HorizontalViewLayout() {
		super();
	}

	@Override
	protected AbstractSplitPanel newVaadinSplitPanel() {
		return new HorizontalSplitPanel();
	}

	@Override
	protected AbstractOrderedLayout newVaadinLayout() {
		return new HorizontalLayout();
	}

	@Override
	public ViewConfig defaultConfig() {
		ViewConfig config = new DefaultViewConfig();
		config.heightUnit(Unit.PERCENTAGE).height(100).noWidth();
		return config;
	}

}
