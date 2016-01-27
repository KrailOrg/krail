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
import com.vaadin.ui.TextArea;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SubscribeTo;
import uk.q3c.krail.core.eventbus.UIBus;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.push.PushMessage;

/**
 * Displays all the messages received by the Broadcaster
 */
@UIScoped
@Listener
@SubscribeTo(UIBus.class)
public class BroadcastMessageLog extends TextArea {
    private static Logger log = LoggerFactory.getLogger(BroadcastMessageLog.class);

    @Inject
    protected BroadcastMessageLog(Translate translate) {
        super();
        setCaption(translate.from(LabelKey.Broadcast_Messages));
        setImmediate(true);
    }

    @Handler
    public void receiveMessage(PushMessage pushMessage) {
        log.debug("Receiving message: '{}' for group: '{}'", pushMessage.getMessage(), pushMessage.getGroup());
        StringBuilder buf = new StringBuilder(pushMessage.getGroup());
        buf.append(':');
        buf.append(pushMessage.getMessage());
        buf.append('\n');
        buf.append(this.getValue());
        this.setValue(buf.toString());
    }

}
