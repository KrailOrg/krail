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
package uk.q3c.krail.base.push;

import uk.q3c.krail.base.guice.uiscope.UIScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UIScoped
public class PushMessageRouter {

    public static final String ALL_MESSAGES = "all";

    private final Map<String, List<PushMessageListener>> groups = new HashMap<>();
    private final List<PushMessageListener> allGroup = new ArrayList<>();

    /**
     * Pass a message to the router for it then to pass it on to its listeners
     *
     * @param group
     * @param message
     */
    public void messageIn(String group, String message) {
        List<PushMessageListener> listenerGroup = groups.get(group);
        if (listenerGroup != null) {
            for (PushMessageListener listener : listenerGroup) {
                listener.receiveMessage(group, message);
            }
        }
        for (PushMessageListener listener : allGroup) {
            listener.receiveMessage(group, message);
        }
    }

    /**
     * Register a listener to receive messages for {@code group}. If you want the listener to receive all messages call
     * with {@code group}= {@link PushMessageRouter#ALL_MESSAGES}. If you want to register for more than one group,
     * make
     * multiple calls.
     *
     * @param group
     * @param listener
     */
    public void register(String group, PushMessageListener listener) {
        if (group == ALL_MESSAGES) {
            allGroup.add(listener);
        } else {
            List<PushMessageListener> listenerGroup = groups.get(group);
            if (listenerGroup == null) {
                listenerGroup = new ArrayList<>();
                groups.put(group, listenerGroup);
            }
            listenerGroup.add(listener);
        }

    }

    /**
     * Unregister a listener to receive messages for {@code group}.
     */
    public void unregister(String group, PushMessageListener listener) {
        if (group == ALL_MESSAGES) {
            allGroup.remove(listener);
        } else {
            List<PushMessageListener> listenerGroup = groups.get(group);
            if (listenerGroup != null) {
                listenerGroup.remove(listener);
            }
        }
    }

}
