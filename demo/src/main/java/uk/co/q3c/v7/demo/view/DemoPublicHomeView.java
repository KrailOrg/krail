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
package uk.co.q3c.v7.demo.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.ViewBase;
import uk.co.q3c.v7.base.view.component.DefaultUserNavigationTree;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class DemoPublicHomeView extends ViewBase implements PublicHomeView {

	private HorizontalSplitPanel hlayout;
	private final DefaultUserNavigationTree navtree;
	private VerticalLayout vlayout;
	private Label label;
	private Button throwExceptionBtn;

	@Inject
	public DemoPublicHomeView(V7Navigator navigator, DefaultUserNavigationTree navtree) {
		super(navigator);
		this.navtree = navtree;
		navtree.setSizeFull();
	}

	@Override
	public void enter(V7ViewChangeEvent event) {

	}

	@Override
	protected void buildView() {
		hlayout = new HorizontalSplitPanel();
		rootComponent = hlayout;
		hlayout.setSplitPosition(200f, Unit.PIXELS);
		vlayout = new VerticalLayout();

		label = new Label(this.getClass().getName());
		// TODO I18N
		throwExceptionBtn = new Button("throw exception");
		throwExceptionBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				throw new RuntimeException("demonstrating the error view");
			}
		});

		vlayout.addComponent(label);
		vlayout.addComponent(throwExceptionBtn);
		hlayout.setFirstComponent(navtree);
		hlayout.setSecondComponent(vlayout);
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
