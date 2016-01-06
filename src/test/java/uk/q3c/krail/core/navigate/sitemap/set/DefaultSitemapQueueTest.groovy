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

package uk.q3c.krail.core.navigate.sitemap.set

import net.engio.mbassy.bus.common.PubSubSupport
import spock.lang.Specification
import uk.q3c.krail.core.config.ApplicationConfiguration
import uk.q3c.krail.core.config.ConfigKeys
import uk.q3c.krail.core.eventbus.BusMessage
import uk.q3c.krail.core.eventbus.GlobalBusProvider
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode
import uk.q3c.krail.core.navigate.sitemap.Sitemap
import uk.q3c.krail.core.navigate.sitemap.SitemapLockedException
import uk.q3c.util.testutil.LogMonitor

import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.*

/**
 * Created by David Sowerby on 05 Jan 2016
 */
class DefaultSitemapQueueTest extends Specification {


    MasterSitemap sitemap1 = Mock(MasterSitemap)
    MasterSitemap sitemap2 = Mock(MasterSitemap)

    GlobalBusProvider globalBusProvider = Mock(GlobalBusProvider)
    PubSubSupport<BusMessage> globalBus = Mock(PubSubSupport)

    ApplicationConfiguration applicationConfiguration = Mock(ApplicationConfiguration)

    SitemapQueue<MasterSitemap> queue;

    LogMonitor logMonitor

    def setup() {
        globalBusProvider.get() >> globalBus
        queue = new DefaultSitemapQueue<>(globalBusProvider, applicationConfiguration)

        logMonitor = new LogMonitor()
        logMonitor.addClassFilter(DefaultSitemapQueue)
        sitemap1.isLocked() >> true
        sitemap2.isLocked() >> true
    }

    def "Empty queue blocks on call to current"() {
        given:

        applicationConfiguration.getLong(ConfigKeys.SITEMAP_LOAD_TIMEOUT_PERIOD, 20000) >> 20000

        when:

        LocalDateTime start = LocalDateTime.now()

        ExecutorService executor = Executors.newWorkStealingPool();
        Future<Sitemap> futureSitemap1 = executor.submit(getCurrentModelTask());
        Future<Sitemap> futureSitemap2 = executor.submit(getCurrentModelTask());
        Future<Boolean> futureAdder = executor.submit(addModelTask(sitemap2, 200));

        Sitemap result1 = futureSitemap1.get()
        Sitemap result2 = futureSitemap2.get()
        boolean added = futureAdder.get()


        LocalDateTime stop = LocalDateTime.now()
        then:

        result1 == sitemap2
        result2 == sitemap2

        Duration.between(start, stop).toMillis() > 199
    }

    def "block on currentMethod() expires on timeout, throws exception"() {
        given:

        applicationConfiguration.getLong(ConfigKeys.SITEMAP_LOAD_TIMEOUT_PERIOD, 20000) >> 1

        when:

        LocalDateTime start = LocalDateTime.now()

        ExecutorService executor = Executors.newWorkStealingPool();
        Future<Sitemap> futureSitemap1 = executor.submit(getCurrentModelTask());
        Future<Sitemap> futureSitemap2 = executor.submit(getCurrentModelTask());
        Future<Boolean> futureAdder = executor.submit(addModelTask(sitemap2, 200));

        futureSitemap1.get()
        futureSitemap2.get()
        futureAdder.get()



        then:
        thrown(ExecutionException)
    }

    def "AddModel, attempt to publish the first"() {
        when:
        queue.addModel(sitemap1)

        then:
        queue.size() == 1
        queue.getCurrentModel() == sitemap1

        when:

        queue.publishNextModel()

        then:

        queue.size() == 1
        queue.getCurrentModel() == sitemap1
        logMonitor.warnLogs().contains("Attempted to publish next model when there are none to publish")
    }

    def "PublishNextModel"() {
        given:

        queue.addModel(sitemap1)
        queue.addModel(sitemap2)


        when:
        boolean result = queue.publishNextModel()

        then:
        result
        queue.getCurrentModel() == sitemap2
        logMonitor.infoLogs().contains("New Master Sitemap published")
        1 * globalBus.publish(_ as SitemapChangedMessage)

    }

    def "add model, queue is full"() {
        when:

        //add 10 models (max for queue)
        List<Future> futures = new ArrayList<>()
        ExecutorService executor = Executors.newWorkStealingPool();
        for (int i = 1; i < 11; i++) {
            futures.add(executor.submit(addModelTask(Mock(Sitemap) as Sitemap<MasterSitemapNode>, 200)))
        }
        futures.each { it.get() }

        then:

        queue.size() == 10

        when:

        boolean result = queue.addModel(sitemap1)

        then:
        !result
        logMonitor.warnLogs().contains("Adding new model failed, maximum models reached")
    }

    def "add model, model not locked throws exception"() {
        given:
        MasterSitemap sitemap3 = Mock(MasterSitemap)

        when:
        queue.addModel(sitemap3)

        then:
        thrown(SitemapLockedException)
    }


    private Callable<Boolean> addModelTask(Sitemap sitemap, long delay) {
        return new Callable<Boolean>() {
            @Override
            Boolean call() throws Exception {
                TimeUnit.MILLISECONDS.sleep(delay);
                return queue.addModel(sitemap2)
            }
        }
    }

    private Callable<Sitemap> getCurrentModelTask() {
        new Callable<Sitemap>() {
            @Override
            Sitemap call() throws Exception {
                return queue.getCurrentModel()
            }
        }
    }


}
