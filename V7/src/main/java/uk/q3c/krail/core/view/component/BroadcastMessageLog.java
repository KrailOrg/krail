/*
 * Copyright (C) 2014 David Sowerby
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
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.vaadin.ui.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.push.PushMessageListener;
import uk.q3c.krail.core.push.PushMessageRouter;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;

/**
 * Displays all the messages received by the Broadcaster
 */
@UIScoped
public class BroadcastMessageLog extends TextArea implements PushMessageListener {
    private static Logger log = LoggerFactory.getLogger(BroadcastMessageLog.class);

    @Inject
    protected BroadcastMessageLog(Translate translate, PushMessageRouter router) {
        super();
        router.register("all", this);
        setCaption(translate.from(LabelKey.Broadcast_Messages));
        setImmediate(true);
    }

    @Override
    public void receiveMessage(String group, String message) {
        log.debug("Receiving message: '{}' for group: '{}'", message, group);
        StringBuilder buf = new StringBuilder(group);
        buf.append(":");
        buf.append(message);
        buf.append("\n");
        buf.append(this.getValue());
        this.setValue(buf.toString());
    }

}
