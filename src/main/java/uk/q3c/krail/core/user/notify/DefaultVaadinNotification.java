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

package uk.q3c.krail.core.user.notify;

import com.google.inject.Inject;
import com.vaadin.ui.Notification;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SubscribeTo;
import uk.q3c.krail.core.eventbus.UIBus;

/**
 * Created by David Sowerby on 26/05/15.
 */
@Listener
@SubscribeTo(UIBus.class)
public class DefaultVaadinNotification implements VaadinNotification {
    private static Logger log = LoggerFactory.getLogger(DefaultVaadinNotification.class);


    @Inject
    protected DefaultVaadinNotification() {
    }

    @Handler
    @Override
    public void errorMessage(ErrorNotificationMessage message) {
        log.debug("Received error message '{}'", message);
        Notification.show(message.getTranslatedMessage(), Notification.Type.ERROR_MESSAGE);
    }

    @Handler
    @Override
    public void warningMessage(WarningNotificationMessage message) {
        log.debug("Received warning message '{}'", message);
        Notification.show(message.getTranslatedMessage(), Notification.Type.WARNING_MESSAGE);
    }

    @Handler
    @Override
    public void informationMessage(InformationNotificationMessage message) {
        log.debug("Received information message '{}'", message);
        String s = message.getTranslatedMessage();
        Notification.show(s, Notification.Type.HUMANIZED_MESSAGE);
    }
}
