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
import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.risto.stepper.IntStepper;
import uk.q3c.krail.base.view.V7ViewChangeEvent;
import uk.q3c.krail.base.view.ViewBase;
import uk.q3c.util.ID;

public class WidgetsetView extends ViewBase {
    private static Logger log = LoggerFactory.getLogger(WidgetsetView.class);
    protected MessageBox messageBox;
    private Panel buttonPanel;
    private Label infoArea;
    private Button popupButton;
    private IntStepper stepper;

    @Inject
    protected WidgetsetView(SessionObject sessionObject) {
        super();
        log.debug("Constructor injecting with session object");
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
        infoArea.setValue("These components are used purely to ensure that the Widgetset has compiled and included "
                + "add-ons");
        getGrid().addComponent(infoArea, 0, 1, 1, 1);
    }

    public GridLayout getGrid() {
        return (GridLayout) getRootComponent();
    }

    @Override
    public void setIds() {
        super.setIds();
        getGrid().setId(ID.getId(Optional.absent(), this, getGrid()));
        popupButton.setId(ID.getId(Optional.of("popup"), this, popupButton));
        stepper.setId(ID.getId(Optional.absent(), this, stepper));
    }
}
