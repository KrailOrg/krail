/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.push;

import uk.q3c.krail.core.guice.uiscope.UIKey;

import java.io.Serializable;

/**
 * Created by David Sowerby on 27/05/15.
 */
public interface PushMessageRouter extends Serializable {

    /**
     * Pass a message to the router for it then to pass it on to its listeners.  Constructs a {@link PushMessage} and publishes it to the UIBus
     *
     * @param group     the message group
     * @param message   the message
     * @param sender    identifier of the sender
     * @param messageId message id
     */
    void messageIn(String group, String message, UIKey sender, int messageId);

}
