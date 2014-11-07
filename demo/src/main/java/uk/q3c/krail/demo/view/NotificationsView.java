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
package uk.q3c.krail.demo.view;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import uk.q3c.krail.base.user.notify.UserNotifier;
import uk.q3c.krail.base.view.KrailViewChangeEvent;
import uk.q3c.krail.base.view.ViewBase;
import uk.q3c.krail.demo.i18n.DescriptionKey;
import uk.q3c.krail.i18n.MessageKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.ID;

public class NotificationsView extends ViewBase {
    private final UserNotifier userNotifier;
    private final Translate translate;
    private Panel buttonPanel;
    private Button errorButton;
    private Label infoArea;
    private Button infoButton;
    private Button warnButton;

    @Inject
    protected NotificationsView(UserNotifier userNotifier, Translate translate) {
        super();
        this.userNotifier = userNotifier;
        this.translate = translate;
    }

    @Override
    public void buildView(KrailViewChangeEvent event) {
        buttonPanel = new Panel();
        VerticalLayout verticalLayout = new VerticalLayout();
        buttonPanel.setContent(verticalLayout);

        setRootComponent(new GridLayout(3, 4));

        getGrid().addComponent(buttonPanel, 1, 2);
        getGrid().setSizeFull();
        getGrid().setColumnExpandRatio(0, 0.400f);
        getGrid().setColumnExpandRatio(1, 0.20f);
        getGrid().setColumnExpandRatio(2, 0.40f);

        getGrid().setRowExpandRatio(0, 0.05f);
        getGrid().setRowExpandRatio(1, 0.15f);
        getGrid().setRowExpandRatio(2, 0.4f);
        getGrid().setRowExpandRatio(3, 0.15f);


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
        getGrid().addComponent(infoArea, 0, 1, 1, 1);
    }

    public GridLayout getGrid() {
        return (GridLayout) getRootComponent();
    }

    @Override
    public void setIds() {
        super.setIds();
        getGrid().setId(ID.getId(Optional.absent(), this, getGrid()));
        infoButton.setId(ID.getId(Optional.of("information"), this, infoButton));
        warnButton.setId(ID.getId(Optional.of("warning"), this, warnButton));
        errorButton.setId(ID.getId(Optional.of("error"), this, errorButton));
    }

    /**
     * Called after the view itself has been constructed but before {@link #buildView()} is called.  Typically checks
     * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
     * configuration which affects the way the view is presented.
     *
     * @param event
     *         contains information about the change to this View
     */
    @Override
    public void beforeBuild(KrailViewChangeEvent event) {

    }
}
