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

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.notify.UserNotifier;
import uk.co.q3c.v7.base.view.ViewBase;
import uk.co.q3c.v7.i18n.MessageKey;
import uk.co.q3c.v7.i18n.Translate;
import uk.co.q3c.v7.testapp.i18n.DescriptionKey;

import com.google.inject.Inject;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@UIScoped
public class NotificationsView extends ViewBase {
	private final UserNotifier userNotifier;
	private final Translate translate;

	private Button errorButton;
	private Button warnButton;
	private Button infoButton;
	protected GridLayout grid;
	private Panel buttonPanel;
	private Label infoArea;

	@Inject
	protected NotificationsView(V7Navigator navigator, UserNotifier userNotifier, Translate translate) {
		super(navigator);
		this.userNotifier = userNotifier;
		this.translate = translate;
		buildView();
	}

	@Override
	protected void processParams(List<String> params) {
	}

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

		errorButton = new Button("Fake an error");
		errorButton.setWidth("100%");
		errorButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				userNotifier.notifyError(MessageKey.Service_not_Started, "Fake Service");
			}
		});
		verticalLayout.addComponent(errorButton);

		warnButton = new Button("Fake a warning");
		warnButton.setWidth("100%");
		warnButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				userNotifier.notifyWarning(MessageKey.Service_not_Started, "Fake Service");
			}
		});
		verticalLayout.addComponent(warnButton);

		infoButton = new Button("Fake user information");
		infoButton.setWidth("100%");
		infoButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				userNotifier.notifyInformation(MessageKey.Service_not_Started, "Fake Service");
			}
		});
		verticalLayout.addComponent(infoButton);

		infoArea = new Label();
		infoArea.setContentMode(ContentMode.HTML);
		infoArea.setSizeFull();
		infoArea.setValue(translate.from(DescriptionKey.Notifications));
		grid.addComponent(infoArea, 0, 1, 1, 1);
	}

	@Override
	public void setIds() {
		super.setIds();
		grid.setId(ID.getId(this.getClass().getSimpleName(), grid));
		infoButton.setId(ID.getId("information", this, infoButton));
		warnButton.setId(ID.getId("warning", this, warnButton));
		errorButton.setId(ID.getId("error", this, errorButton));
	}
}
