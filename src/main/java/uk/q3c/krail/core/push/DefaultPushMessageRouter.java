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
package uk.q3c.krail.core.push;

import com.google.inject.Inject;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.slf4j.Logger;
import uk.q3c.krail.core.eventbus.UIBusProvider;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.eventbus.BusMessage;

import static org.slf4j.LoggerFactory.*;

@UIScoped
public class DefaultPushMessageRouter implements PushMessageRouter {
    public static final String ALL_MESSAGES = "all";
    private static Logger log = getLogger(DefaultPushMessageRouter.class);
    private PubSubSupport<BusMessage> uiBus;

    @Inject
    public DefaultPushMessageRouter(UIBusProvider uiBusProvider) {
        this.uiBus = uiBusProvider.get();
    }

    @Override
    public void messageIn(String group, String message) {
        log.debug("publishing message");
        uiBus.publish(new PushMessage(group, message));
    }

    @Override
    public void messageIn(String group, String message, UIKey sender, int messageId) {
        log.debug("publishing message from {}, with message id: {}", sender, messageId);
        uiBus.publish(new PushMessage(group, message));
    }


}
