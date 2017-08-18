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

package uk.q3c.krail.core.navigate.sitemap.set;

import com.google.inject.Inject;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.slf4j.Logger;
import uk.q3c.krail.config.ApplicationConfiguration;
import uk.q3c.krail.config.config.ConfigKeys;
import uk.q3c.krail.core.navigate.sitemap.Sitemap;
import uk.q3c.krail.core.navigate.sitemap.SitemapLockedException;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.BusProvider;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.google.common.base.Preconditions.*;
import static org.slf4j.LoggerFactory.*;

/**
 * Created by David Sowerby on 05 Jan 2016
 */
public class DefaultSitemapQueue<T extends Sitemap> implements SitemapQueue<T> {
    private static Logger log = getLogger(DefaultSitemapQueue.class);
    private final PubSubSupport<BusMessage> eventBus;
    private final ApplicationConfiguration applicationConfiguration;

    private BlockingQueue<T> queue;

    @Inject
    protected DefaultSitemapQueue(BusProvider busProvider, ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        queue = new ArrayBlockingQueue<>(10, true);
        eventBus = busProvider.get();
    }

    /**
     * BlockingQueue does not block on peek(), so we need to provide a block until a model is added
     */
    @Override
    public synchronized T getCurrentModel() {
        long timeoutPeriod = applicationConfiguration.getLong(ConfigKeys.SITEMAP_LOAD_TIMEOUT_PERIOD, 20000);
        boolean timedOut = false;
        long elapsed = 0;
        while (queue.isEmpty() && !timedOut) {
            try {
                LocalDateTime start = LocalDateTime.now();
                log.info("waiting for model to be added");
                wait(timeoutPeriod);
                LocalDateTime stop = LocalDateTime.now();
                elapsed = Duration.between(start, stop)
                                  .toMillis();
                if (elapsed > timeoutPeriod) {
                    timedOut = true;
                }
            } catch (InterruptedException e) {
                // do nothing, stay in loop to make sure there is now an entry in queue
            }
        }
        if (timedOut) {
            String msg = "Master Sitemap loading timed out after " + elapsed + "ms";
            throw new SitemapTimeoutException(msg);
        }
        return queue.peek();
    }

    @Override
    public synchronized boolean addModel(T newModel) {
        checkNotNull(newModel);
        if (!newModel.isLocked()) {
            throw new SitemapLockedException("Sitemap must be locked before being added");
        }
        boolean result = queue.offer(newModel);
        if (result) {
            log.debug("Adding new model succeeded");
        } else {
            log.warn("Adding new model failed, maximum models reached");
        }
        notifyAll();
        return result;
    }

    @Override
    public synchronized boolean publishNextModel() {
        if (queue.size() < 2) {
            log.warn("Attempted to publish next model when there are none to publish");
            return false;
        }
        //by removing the head, the next model is 'published'
        queue.remove();
        eventBus.publish(new SitemapChangedMessage());
        log.info("New Master Sitemap published");
        return true;
    }

    @Override
    public int size() {
        return queue.size();
    }
}
