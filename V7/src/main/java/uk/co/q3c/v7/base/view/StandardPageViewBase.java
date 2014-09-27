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
package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.NavigationState;

public abstract class StandardPageViewBase extends ViewBase {

    protected GridLayout grid;
    private Label label;

    @Inject
    protected StandardPageViewBase() {
        super();
        buildView();
    }

    @Override
    protected Component buildView() {

        label = new Label("This is the " + this.getClass()
                                               .getSimpleName());
        label.setHeight("100px");
        grid = new GridLayout(3, 3);

        grid.addComponent(label, 1, 1);
        grid.setSizeFull();
        grid.setColumnExpandRatio(0, 0.33f);
        grid.setColumnExpandRatio(1, 0.33f);
        grid.setColumnExpandRatio(2, 0.33f);

        grid.setRowExpandRatio(0, 0.4f);
        grid.setRowExpandRatio(1, 0.2f);
        grid.setRowExpandRatio(2, 0.4f);

        label.setSizeFull();

        return grid;
    }

    @Override
    public void setIds() {
        super.setIds();
        grid.setId(ID.getId(this.getClass()
                                .getSimpleName(), grid));
        label.setId(ID.getId(this.getClass()
                                 .getSimpleName(), label));
    }

    /**
     * Called immediately after construction of the view to enable setting up the view from URL parameters
     *
     * @param navigationState
     */
    @Override
    public void prepareView(NavigationState navigationState) {

    }

    public Label getLabel() {
        return label;
    }

    @Override
    protected void processParams(NavigationState navigationState) {
    }

}
