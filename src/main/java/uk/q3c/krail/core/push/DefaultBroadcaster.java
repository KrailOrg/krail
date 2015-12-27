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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.config.ApplicationConfiguration;
import uk.q3c.krail.core.config.ConfigKeys;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class DefaultBroadcaster implements Broadcaster {

    private static Logger log = LoggerFactory.getLogger(DefaultBroadcaster.class);
    private final ExecutorService executorService;
    private final Map<String, List<BroadcastListener>> groups = new HashMap<>();
    private final List<BroadcastListener> allGroup = new ArrayList<>();
    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    protected DefaultBroadcaster(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        executorService = Executors.newSingleThreadExecutor();
    }


    @Override
    public synchronized Broadcaster register(@Nonnull String group, @Nonnull BroadcastListener listener) {
        checkNotNull(group);
        checkNotNull(listener);
        log.debug("adding listener: {}", listener.getClass()
                                                 .getName());
        if (group.equals(ALL_MESSAGES)) {
            allGroup.add(listener);
        } else {
            List<BroadcastListener> listenerGroup = groups.get(group);
            if (listenerGroup == null) {
                listenerGroup = new ArrayList<>();
                groups.put(group, listenerGroup);
            }
            listenerGroup.add(listener);
        }
        return this;
    }


    @Override
    public synchronized Broadcaster unregister(@Nonnull String group, @Nonnull BroadcastListener listener) {
        checkNotNull(group);
        checkNotNull(listener);
        if (group.equals(ALL_MESSAGES)) {
            allGroup.remove(listener);
        } else {
            List<BroadcastListener> listenerGroup = groups.get(group);
            if (listenerGroup != null) {
                listenerGroup.remove(listener);
            }
        }
        return this;
    }


    @Override
    public synchronized Broadcaster broadcast(@Nonnull final String group, @Nonnull final String message) {
        checkNotNull(group);
        checkNotNull(message);
        if (applicationConfiguration.getBoolean(ConfigKeys.SERVER_PUSH_ENABLED, true)) {
            log.debug("broadcasting message: {}", message);
            List<BroadcastListener> listenerGroup = groups.get(group);
            if (listenerGroup != null) {
                for (final BroadcastListener listener : listenerGroup)
                    executorService.execute(() -> listener.receiveBroadcast(group, message));
            }
            for (final BroadcastListener listener : allGroup)
                executorService.execute(() -> listener.receiveBroadcast(group, message));
        } else {
            log.debug("server push is disabled, message not broadcast");
        }
        return this;
    }

}
