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

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.notify.UserNotifier;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.StandardPageViewBase;
import uk.co.q3c.v7.i18n.MessageKey;

import com.google.inject.Inject;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class DemoPublicHomeView extends StandardPageViewBase implements PublicHomeView {

	private Button errorButton;
	private final UserNotifier userNotifier;
	private Button warnButton;
	private Button infoButton;

	@Inject
	public DemoPublicHomeView(V7Navigator navigator, UserNotifier userNotifier) {
		super(navigator);
		this.userNotifier = userNotifier;
	}

	@Override
	protected void processParams(List<String> params) {
	}

	@Override
	protected void buildView() {
		super.buildView();
		errorButton = new Button("Fake an error");
		errorButton.setId(ID.getId("error", this, errorButton));
		errorButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				userNotifier.notifyError(MessageKey.Service_not_Started, "Fake Service");
			}
		});
		grid.addComponent(errorButton, 0, 2);

		warnButton = new Button("Fake a warning");
		warnButton.setId(ID.getId("warning", this, errorButton));
		warnButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				userNotifier.notifyWarning(MessageKey.Service_not_Started, "Fake Service");
			}
		});
		grid.addComponent(warnButton, 1, 2);

		infoButton = new Button("Fake user information");
		infoButton.setId(ID.getId("information", this, errorButton));
		infoButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				userNotifier.notifyInformation(MessageKey.Service_not_Started, "Fake Service");
			}
		});
		grid.addComponent(infoButton, 2, 2);
	}
}
