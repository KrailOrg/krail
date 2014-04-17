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
package uk.co.q3c.v7.testapp.view;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.risto.stepper.IntStepper;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.view.ViewBase;

import com.google.inject.Inject;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;

public class WidgetsetView extends ViewBase {
	private static Logger log = LoggerFactory.getLogger(WidgetsetView.class);
	private Button popupButton;
	protected GridLayout grid;
	private Panel buttonPanel;
	protected MessageBox messageBox;
	private Label infoArea;
	private IntStepper stepper;

	@Inject
	protected WidgetsetView(SessionObject sessionObject) {
		super();
		log.debug("Constructor injecting with session object");
		buildView();
	}

	@Override
	protected void processParams(List<String> params) {
	}

	@SuppressWarnings("serial")
	private void buildView() {
		buttonPanel = new Panel();
		VerticalLayout verticalLayout = new VerticalLayout();
		buttonPanel.setContent(verticalLayout);

		grid = new GridLayout(3, 4);

		grid.addComponent(buttonPanel, 1, 2);
		grid.setSizeFull();
		grid.setColumnExpandRatio(0, 0.400f);
		grid.setColumnExpandRatio(1, 0.20f);
		grid.setColumnExpandRatio(2, 0.40f);

		grid.setRowExpandRatio(0, 0.05f);
		grid.setRowExpandRatio(1, 0.15f);
		grid.setRowExpandRatio(2, 0.4f);
		grid.setRowExpandRatio(3, 0.15f);
		rootComponent = grid;

		popupButton = new Button("Popup message box");
		popupButton.setWidth("100%");
		popupButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				messageBox = MessageBox.showPlain(Icon.INFO, "Example 1", "Hello World!", ButtonId.OK);
			}
		});
		verticalLayout.addComponent(popupButton);

		stepper = new IntStepper("Stepper");
		stepper.setValue(5);
		verticalLayout.addComponent(stepper);

		infoArea = new Label();
		infoArea.setContentMode(ContentMode.HTML);
		infoArea.setSizeFull();
		infoArea.setValue("These components are used purely to ensure that the Widgetset has compiled and included add-ons");
		grid.addComponent(infoArea, 0, 1, 1, 1);
	}

	@Override
	public void setIds() {
		super.setIds();
		grid.setId(ID.getId(this.getClass().getSimpleName(), grid));
		popupButton.setId(ID.getId("popup", this, popupButton));
		stepper.setId(ID.getId(this, stepper));
	}
}
