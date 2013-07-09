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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;

public abstract class DefaultViewBase implements V7View {
	private static Logger log = LoggerFactory.getLogger(DefaultViewBase.class);

	private HorizontalSplitPanel layout;
	private final UserNavigationTree navtree;
	private Label label;
	private final V7Navigator navigator;
	private GridLayout grid;

	@Inject
	protected DefaultViewBase(V7Navigator navigator, UserNavigationTree navtree) {
		super();
		this.navtree = navtree;
		this.navigator = navigator;
		buildUI();
	}

	protected void buildUI() {
		layout = new HorizontalSplitPanel();
		label = new Label("This is the " + this.getClass().getSimpleName());
		grid = new GridLayout(3, 3);

		grid.addComponent(label, 1, 1);
		grid.setSizeFull();
		grid.setColumnExpandRatio(0, 0.33f);
		grid.setColumnExpandRatio(1, 0.33f);
		grid.setColumnExpandRatio(2, 0.33f);

		grid.setRowExpandRatio(0, 0.5f);
		grid.setRowExpandRatio(2, 0.5f);

		label.setSizeFull();
		layout.setSplitPosition(200f, Unit.PIXELS);
		layout.setFirstComponent(navtree);
		layout.setSecondComponent(grid);

	}

	@Override
	public void enter(V7ViewChangeEvent event) {
		log.debug("entered view: " + this.getClass().getSimpleName() + " with uri: " + navigator.getNavigationState());
		List<String> params = navigator.getNavigationParams();
		processParams(params);
	}

	@Override
	public Component getUiComponent() {
		return layout;
	}

	public Label getLabel() {
		return label;
	}

	protected abstract void processParams(List<String> params);

	public V7Navigator getNavigator() {
		return navigator;
	}
}
