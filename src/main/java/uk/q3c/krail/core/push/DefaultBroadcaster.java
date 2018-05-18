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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vaadin.ui.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.config.ApplicationConfiguration;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.ui.ScopedUI;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static uk.q3c.krail.core.push.PushModuleKt.SERVER_PUSH_ENABLED;

@Singleton
@ThreadSafe
public class DefaultBroadcaster implements Broadcaster {

    private static Logger log = LoggerFactory.getLogger(DefaultBroadcaster.class);
    private final Map<String, List<BroadcastListener>> groups = new HashMap<>();
    private final List<BroadcastListener> allGroup = new ArrayList<>();
    private final ApplicationConfiguration applicationConfiguration;
    private final AtomicInteger messageCount;

    @Inject
    protected DefaultBroadcaster(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        messageCount = new AtomicInteger(0);
    }


    @Override
    public synchronized Broadcaster register(String group, BroadcastListener listener) {
        checkNotNull(group);
        checkNotNull(listener);
        checkArgument(!group.isEmpty(), "Group should not be an empty String");
        log.debug("adding listener: {}", listener.getClass()
                                                 .getName());
        if (ALL_MESSAGES.equals(group)) {
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
    public synchronized Broadcaster unregister(String group, BroadcastListener listener) {
        checkNotNull(group);
        checkNotNull(listener);
        if (ALL_MESSAGES.equals(group)) {
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
    public synchronized Broadcaster broadcast(final String group, final String message, Component sender) {
        checkNotNull(sender);
        ScopedUI scopedUI = (ScopedUI) sender.getUI();
        return broadcast(group, message, scopedUI.getInstanceKey());
    }

    @Override
    public synchronized Broadcaster broadcast(final String group, final String message, UIKey sender) {
        checkNotNull(group);
        checkNotNull(message);
        checkArgument(!group.isEmpty(), "Group should not be an empty String");
        int messageId = messageCount.incrementAndGet();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (applicationConfiguration.getPropertyValue(SERVER_PUSH_ENABLED, true)) {
            log.debug("broadcasting message: {} from: {}", messageId, sender);
            List<BroadcastListener> listenerGroup = groups.get(group);
            if (listenerGroup != null) {
                for (final BroadcastListener listener : listenerGroup)
                    executorService.execute(() -> listener.receiveBroadcast(group, message, sender, messageId));
            }
            for (final BroadcastListener listener : allGroup)
                executorService.execute(() -> listener.receiveBroadcast(group, message, sender, messageId));
        } else {
            log.debug("server push is disabled, message not broadcast");
        }
        closeExecutor(executorService);
        return this;
    }

    @Override

    public ImmutableList<BroadcastListener> getListenerGroup(String group) {
        checkNotNull(group);
        checkArgument(!group.isEmpty(), "Group should not be an empty String");
        if (Broadcaster.ALL_MESSAGES.equals(group)) {
            return ImmutableList.copyOf(allGroup);
        }
        List<BroadcastListener> listenerGroup = groups.get(group);
        if (listenerGroup == null) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(listenerGroup);
    }

    /**
     * Stops the @{code executor} with appropriate timeouts and logging
     *
     * @param executor the Executor to be shut down.
     */
    protected void closeExecutor(ExecutorService executor) {
        try {
            log.debug("Closing Executor, attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while shutting down Executor");
        } finally {
            if (!executor.isTerminated()) {
                log.error("forcing shutdown");
            }
            executor.shutdownNow();
            log.info("Services Executor shutdown finished");
        }
    }


}
