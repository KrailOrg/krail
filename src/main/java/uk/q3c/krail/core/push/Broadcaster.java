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

import uk.q3c.krail.core.ui.ScopedUI;

/**
 * Implementations 'broadcast' messages to registered {@link Broadcaster.BroadcastListener}s. using Vaadin Server Push.  {@link ScopedUI} implements {@link
 * Broadcaster.BroadcastListener}, so any UIs sub-classed from it will listen for broadcast messages.
 * <p>
 * Created by David Sowerby on 27/05/15.
 */
public interface Broadcaster {
    String ALL_MESSAGES = "all";

    Broadcaster register(String group, BroadcastListener listener);

    Broadcaster unregister(String group, BroadcastListener listener);

    Broadcaster broadcast(String group, String message);

    interface BroadcastListener {
        void receiveBroadcast(String group, String message);
    }
}
