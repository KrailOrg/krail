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
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.ui.ApplicationTitle;
import uk.q3c.krail.core.vaadin.ID;

import java.util.Optional;

public class DefaultApplicationHeader extends Panel implements ApplicationHeader {
    private Label label;
    private ApplicationTitle applicationTitle;
    private Translate translate;


    @Inject
    protected DefaultApplicationHeader(ApplicationTitle applicationTitle, Translate translate) {
        super();
        this.applicationTitle = applicationTitle;
        this.translate = translate;
        build();
        setIds();
    }


    private void build() {
        HorizontalLayout layout = new HorizontalLayout();
        label = new Label(translate.from(applicationTitle.getTitleKey()));
        layout.addComponent(label);
        this.setContent(layout);

    }

    private void setIds() {
        this.setId(ID.getId(Optional.empty(), this));
        label.setId(ID.getId(Optional.empty(), this, label));
    }

    public Label getLabel() {
        return label;
    }
}
