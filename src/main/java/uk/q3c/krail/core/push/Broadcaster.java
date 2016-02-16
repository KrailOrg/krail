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

import com.google.common.collect.ImmutableList;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.ui.ScopedUI;

import javax.annotation.Nonnull;

/**
 * Implementations 'broadcast' messages to registered {@link Broadcaster.BroadcastListener}s. using Vaadin Server Push.  {@link ScopedUI} implements {@link
 * Broadcaster.BroadcastListener}, so any UIs sub-classed from it will listen for broadcast messages.
 * <p>
 * Created by David Sowerby on 27/05/15.
 */
public interface Broadcaster {
    String ALL_MESSAGES = "all";

    /**
     * Register a listener to receive messages for {@code group}. If you want the listener to receive all messages call with {@code group}= {@link
     * DefaultPushMessageRouter#ALL_MESSAGES}. If you want to register for more than one group, make multiple calls.`
     *
     * @param group    the group to listen to
     * @param listener the listener that wants to receive messages for this group
     */
    Broadcaster register(@Nonnull String group, @Nonnull BroadcastListener listener);

    /**
     * Unregister a listener to receive messages for {@code group}.
     */
    Broadcaster unregister(@Nonnull String group, @Nonnull BroadcastListener listener);

    /**
     * Send a message to registered listeners
     *
     * @param group   the message group
     * @param message the message
     * @param sender  UIKey identifying the sender of the message
     */
    Broadcaster broadcast(@Nonnull String group, @Nonnull String message, @Nonnull UIKey sender);


    /**
     * Returns the group of listeners for {@code groupId}.  If the group is not registered, an empty list is returned
     *
     * @param groupId id for the group
     * @return the group of listeners for {@code groupId}.  If the group is not registered, an empty list is returned
     */
    @Nonnull
    ImmutableList<BroadcastListener> getListenerGroup(@Nonnull String groupId);

    interface BroadcastListener {
        void receiveBroadcast(@Nonnull String group, @Nonnull String message, @Nonnull UIKey sender, int messageId);
    }
}
