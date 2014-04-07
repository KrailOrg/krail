/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.testapp.view;

import uk.co.q3c.v7.base.view.ViewBase;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

/**
 * Creates a grid, 4 rows x 3 cols. The top row is just a spacer.
 * 
 * @author David Sowerby
 * 
 */
public abstract class ViewBaseGrid extends ViewBase {
	protected GridLayout grid;
	private int topMargin = 5;

	protected ViewBaseGrid() {
		super();
		setupGrid();
	}

	private void setupGrid() {
		grid = new GridLayout(3, 4);
		Panel topMarginPanel = new Panel();
		topMarginPanel.setHeight(topMargin + "px");
		topMarginPanel.setWidth("100%");

		grid.setSizeFull();
		grid.setColumnExpandRatio(0, 0.400f);
		grid.setColumnExpandRatio(1, 0.20f);
		grid.setColumnExpandRatio(2, 0.40f);

		grid.setRowExpandRatio(1, 0.40f);
		grid.setRowExpandRatio(2, 0.20f);
		grid.setRowExpandRatio(3, 0.40f);
		rootComponent = grid;
	}

	protected void setTopCentreCell(Component component) {
		grid.addComponent(component, 1, 1);
	}

	protected void setCentreCell(Component component) {
		grid.addComponent(component, 1, 2);
	}

	protected void setTopLeftCell(Component component) {
		grid.addComponent(component, 0, 1);
	}

	protected void setBottomCentreCell(Component component) {
		grid.addComponent(component, 1, 3);
	}

	/**
	 * Get the top margin in pixels
	 * 
	 * @return
	 */
	public int getTopMargin() {
		return topMargin;
	}

	/**
	 * Set the top margin in pixels
	 * 
	 * @return
	 */
	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}

}
