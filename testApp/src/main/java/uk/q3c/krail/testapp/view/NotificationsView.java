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
package uk.q3c.krail.testapp.view;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import uk.q3c.krail.base.user.notify.UserNotifier;
import uk.q3c.krail.base.view.V7ViewChangeEvent;
import uk.q3c.krail.base.view.ViewBase;
import uk.q3c.krail.i18n.MessageKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.testapp.i18n.TestAppDescriptionKey;
import uk.q3c.util.ID;

public class NotificationsView extends ViewBase {
    private final UserNotifier userNotifier;
    private final Translate translate;
    protected GridLayout grid;
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


    /**
     * Called after the view itself has been constructed but before {@link #buildView()} is called.  Typically checks
     * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
     * configuration which affects the way the view is presented.
     *
     * @param event
     *         contains information about the change to this View
     */
    @Override
    public void beforeBuild(V7ViewChangeEvent event) {

    }

    @Override
    public void buildView(V7ViewChangeEvent event) {
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
        infoArea.setValue(translate.from(TestAppDescriptionKey.Notifications));
        grid.addComponent(infoArea, 0, 1, 1, 1);
        setRootComponent(grid);
    }


    @Override
    public void setIds() {
        super.setIds();
        grid.setId(ID.getId(Optional.absent(), this, grid));
        infoButton.setId(ID.getId(Optional.of("information"), this, infoButton));
        warnButton.setId(ID.getId(Optional.of("warning"), this, warnButton));
        errorButton.setId(ID.getId(Optional.of("error"), this, errorButton));
    }


}
