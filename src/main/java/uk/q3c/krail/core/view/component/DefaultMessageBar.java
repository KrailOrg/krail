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
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SubscribeTo;
import uk.q3c.krail.core.eventbus.UIBus;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.user.notify.ErrorNotificationMessage;
import uk.q3c.krail.core.user.notify.InformationNotificationMessage;
import uk.q3c.krail.core.user.notify.WarningNotificationMessage;
import uk.q3c.krail.core.vaadin.ID;
import uk.q3c.krail.i18n.Translate;

import java.util.Optional;

@UIScoped
@Listener
@SubscribeTo(UIBus.class)
public class DefaultMessageBar extends Panel implements MessageBar {
    private static Logger log = LoggerFactory.getLogger(DefaultMessageBar.class);
    private final Translate translate;
    private Label display;

    @Inject
    protected DefaultMessageBar(Translate translate) {
        super();
        this.translate = translate;
        build();
    }

    public Label getDisplay() {
        return display;
    }

    private void build() {
        HorizontalLayout layout = new HorizontalLayout();
        display = new Label(translate.from(LabelKey.Message_Bar));
        display.setImmediate(true);
        layout.addComponent(display);
        this.setContent(layout);
        display.setId(ID.getId(Optional.empty(), this, display));
    }

    @Handler
    @Override
    public void errorMessage(ErrorNotificationMessage message) {
        log.debug("Received error message '{}'", message);
        String s = translate.from(LabelKey.Error)
                            .toUpperCase() + ": " + message.getTranslatedMessage();
        display.setValue(s);
    }

    @Handler
    @Override
    public void warningMessage(WarningNotificationMessage message) {
        log.debug("Received warning message '{}'", message);
        String s = translate.from(LabelKey.Warning) + ": " + message.getTranslatedMessage();
        display.setValue(s);
    }

    @Handler
    @Override
    public void informationMessage(InformationNotificationMessage message) {
        log.debug("Received information message '{}'", message);
        display.setValue(message.getTranslatedMessage());
    }


}
