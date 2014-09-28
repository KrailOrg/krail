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

import com.google.inject.Inject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.config.ApplicationConfiguration;
import uk.co.q3c.v7.base.config.ConfigKeys;
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.component.BroadcastMessageLog;

public class PushView extends ViewBaseGrid {

    private final Broadcaster broadcaster;
    private final BroadcastMessageLog messageLog;
    private final ApplicationConfiguration applicationConfiguration;
    private TextField groupInput;
    private Label infoArea;
    private HorizontalLayout inputLayout;
    private TextField messageInput;
    private CheckBox pushEnabled;
    private Button sendButton;

    @Inject
    protected PushView(Broadcaster broadcaster, BroadcastMessageLog messageLog,
                       ApplicationConfiguration applicationConfiguration) {
        super();
        this.broadcaster = broadcaster;
        this.messageLog = messageLog;
        this.applicationConfiguration = applicationConfiguration;
    }


    @Override
    public void buildView(V7ViewChangeEvent event) {
        super.buildView(event);
        groupInput = new TextField("Group");
        groupInput.setWidth("100px");
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

        pushEnabled = new CheckBox("Push enabled");
        pushEnabled.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                applicationConfiguration.setProperty(ConfigKeys.SERVER_PUSH_ENABLED, (boolean) event.getProperty()
                                                                                                    .getValue());
            }

        });
        pushEnabled.setValue(true);

        infoArea = new Label();
        infoArea.setContentMode(ContentMode.HTML);
        infoArea.setSizeFull();
        infoArea.setValue("Test using multiple browser tabs or instances");

        setTopCentreCell(pushEnabled);
        setCentreCell(inputLayout);
        setTopLeftCell(infoArea);
        setBottomCentreCell(messageLog);

        getGrid().setComponentAlignment(pushEnabled, Alignment.MIDDLE_CENTER);
        getGrid().setComponentAlignment(inputLayout, Alignment.MIDDLE_CENTER);
    }


    @Override
    public void setIds() {
        super.setIds();
        getGrid().setId(ID.getId(this.getClass()
                                     .getSimpleName(), getGrid()));
        sendButton.setId(ID.getId("send", this, sendButton));
        groupInput.setId(ID.getId("group", this, groupInput));
        messageInput.setId(ID.getId("message", this, messageInput));
        messageLog.setId(ID.getId(this, messageLog));
        pushEnabled.setId(ID.getId(this, pushEnabled));
    }


}
