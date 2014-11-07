/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.push;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.base.config.ApplicationConfiguration;
import uk.q3c.krail.base.config.ConfigKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class Broadcaster {
    public static final String ALL_MESSAGES = "all";
    private static Logger log = LoggerFactory.getLogger(Broadcaster.class);
    private final ExecutorService executorService;
    private final Map<String, List<BroadcastListener>> groups = new HashMap<>();
    private final List<BroadcastListener> allGroup = new ArrayList<>();
    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    protected Broadcaster(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        executorService = Executors.newSingleThreadExecutor();
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
    public synchronized void register(String group, BroadcastListener listener) {
        log.debug("adding listener: {}", listener.getClass()
                                                 .getName());
        if (group == ALL_MESSAGES) {
            allGroup.add(listener);
        } else {
            List<BroadcastListener> listenerGroup = groups.get(group);
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
    public synchronized void unregister(String group, BroadcastListener listener) {
        if (group == ALL_MESSAGES) {
            allGroup.remove(listener);
        } else {
            List<BroadcastListener> listenerGroup = groups.get(group);
            if (listenerGroup != null) {
                listenerGroup.remove(listener);
            }
        }
    }

    /**
     * Send a message to registered listeners
     *
     * @param group
     * @param message
     */
    public synchronized void broadcast(final String group, final String message) {
        if (applicationConfiguration.getBoolean(ConfigKeys.SERVER_PUSH_ENABLED, true)) {
            log.debug("broadcasting message: {}", message);
            List<BroadcastListener> listenerGroup = groups.get(group);
            if (listenerGroup != null) {
                for (final BroadcastListener listener : listenerGroup)
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.receiveBroadcast(group, message);
                        }
                    });
            }
            for (final BroadcastListener listener : allGroup)
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        listener.receiveBroadcast(group, message);
                    }
                });
        } else {
            log.debug("server push is disabled, message not broadcast");
        }
    }

    public interface BroadcastListener {
        void receiveBroadcast(String group, String message);
    }

}
