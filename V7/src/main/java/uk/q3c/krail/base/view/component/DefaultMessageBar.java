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
package uk.q3c.krail.base.view.component;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.base.guice.uiscope.UIScoped;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.ID;

@UIScoped
public class DefaultMessageBar extends Panel implements MessageBar {
    private static Logger log = LoggerFactory.getLogger(DefaultMessageBar.class);
    private final Translate translate;
    private Label label;
    private HorizontalLayout layout;

    @Inject
    protected DefaultMessageBar(Translate translate) {
        super();
        this.translate = translate;
        build();
    }

    private void build() {
        layout = new HorizontalLayout();
        label = new Label(translate.from(LabelKey.Message_Bar));
        label.setImmediate(true);
        layout.addComponent(label);
        this.setContent(layout);
        label.setId(ID.getId(Optional.absent(), this, label));
    }

    @Override
    public void errorMessage(String message) {
        log.debug("Received error message '{}'", message);
        String s = translate.from(LabelKey.Error)
                            .toUpperCase() + ": " + message;
        label.setValue(s);
    }

    @Override
    public void warningMessage(String message) {
        log.debug("Received warning message '{}'", message);
        String s = translate.from(LabelKey.Warning) + ": " + message;
        label.setValue(s);
    }

    @Override
    public void informationMessage(String message) {
        log.debug("Received information message '{}'", message);
        label.setValue(message);
    }

}
