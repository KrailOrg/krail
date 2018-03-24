/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.view;

import com.google.inject.Inject;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import uk.q3c.krail.core.guice.SerializationSupport;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;

public abstract class StandardPageViewBase extends ViewBase {

    protected GridLayout grid;
    private Label label;

    @Inject
    protected StandardPageViewBase(Translate translate, SerializationSupport serializationSupport) {
        super(translate, serializationSupport);
    }

    @Override
    public void doBuild(ViewChangeBusMessage busMessage) {

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

        setRootComponent(grid);
    }

    public Label getLabel() {
        return label;
    }




}
