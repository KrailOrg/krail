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
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.view.component.BroadcastMessageLog;

import com.google.inject.Inject;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@UIScoped
public class PushView extends ViewBaseGrid {

	private Button sendButton;

	private Label infoArea;

	private HorizontalLayout inputLayout;
	private TextField groupInput;
	private TextField messageInput;

	private final Broadcaster broadcaster;

	private final BroadcastMessageLog messageLog;

	@Inject
	protected PushView(V7Navigator navigator, Broadcaster broadcaster, BroadcastMessageLog messageLog) {
		super(navigator);
		this.broadcaster = broadcaster;
		this.messageLog = messageLog;
		buildView();
	}

	@Override
	protected void processParams(List<String> params) {
	}

	@SuppressWarnings("serial")
	private void buildView() {

		groupInput = new TextField("Group");
		messageInput = new TextField("Message");

		sendButton = new Button("Send message");
		sendButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				broadcaster.broadcast(groupInput.getValue(), messageInput.getValue());
			}
		});

		inputLayout = new HorizontalLayout(groupInput, messageInput, sendButton);
		inputLayout.setComponentAlignment(sendButton, Alignment.BOTTOM_CENTER);

		infoArea = new Label();
		infoArea.setContentMode(ContentMode.HTML);
		infoArea.setSizeFull();
		infoArea.setValue("Test using multiple browser tabs or instances");

		setCentreCell(inputLayout);
		setTopLeftCell(infoArea);
		setBottomCentreCell(messageLog);
	}

	@Override
	public void setIds() {
		super.setIds();
		grid.setId(ID.getId(this.getClass().getSimpleName(), grid));
		sendButton.setId(ID.getId("send", this, sendButton));
		groupInput.setId(ID.getId("group", this, groupInput));
		messageInput.setId(ID.getId("message", this, messageInput));
		messageLog.setId(ID.getId(this, messageLog));
	}
}
