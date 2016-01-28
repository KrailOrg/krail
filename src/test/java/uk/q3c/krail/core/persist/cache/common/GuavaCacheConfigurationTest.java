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

package uk.q3c.krail.core.persist.cache.common;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCacheLoader;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class GuavaCacheConfigurationTest {

    GuavaCacheConfiguration configuration;

    @Mock
    Ticker ticker;

    @Mock
    DefaultOptionCacheLoader loader;

    @Mock
    private RemovalListener removalListener;

    @Before
    public void setup() {

    }

    @Test
    public void create() {
        //given

        //when
        configuration = new GuavaCacheConfiguration().concurrencyLevel(5)
                                                     .expireAfterAccess(100)
                                                     .expireAfterWrite(90)
                                                     .initialCapacity(80)
                                                     .maximumSize(70)
                                                     .recordStats()
                                                     .refreshAfterWrite(50)
                                                     .ticker(ticker)
                                                     .weakKeys()
                                                     .weakValue()
                                                     .removalListener(removalListener);
        //then
        Cache<Object, Object> cache = configuration.builder()
                                                   .build((CacheLoader) loader);
        assertThat(cache).isNotNull();
        assertThat(configuration.getConcurrencyLevel()).isEqualTo(5);
        assertThat(configuration.getExpireAfterAccessDuration()).isEqualTo(100);
        assertThat(configuration.getExpireAfterWriteDuration()).isEqualTo(90);
        assertThat(configuration.getInitialCapacity()).isEqualTo(80);
        assertThat(configuration.getRefreshAfterWriteDuration()).isEqualTo(50);
        assertThat(configuration.getTicker()).isEqualTo(ticker);
        assertThat(configuration.isRecordStats()).isTrue();
        assertThat(configuration.getRemovalListener()).isEqualTo(removalListener);
        assertThat(configuration.getExpireAfterAccessTimeUnit()).isEqualTo(TimeUnit.MINUTES);
        assertThat(configuration.getExpireAfterWriteTimeUnit()).isEqualTo(TimeUnit.MINUTES);
        assertThat(configuration.getRefreshAfterWriteTimeUnit()).isEqualTo(TimeUnit.MINUTES);
    }

    @Test
    public void withTimeUnits() {
        //given

        //when
        configuration = new GuavaCacheConfiguration().expireAfterWrite(100, TimeUnit.HOURS)
                                                     .expireAfterAccess(200, TimeUnit.MICROSECONDS)
                                                     .refreshAfterWrite(300, TimeUnit.NANOSECONDS);
        //then
        assertThat(configuration.getExpireAfterAccessTimeUnit()).isEqualTo(TimeUnit.MICROSECONDS);
        assertThat(configuration.getExpireAfterWriteTimeUnit()).isEqualTo(TimeUnit.HOURS);
        assertThat(configuration.getRefreshAfterWriteTimeUnit()).isEqualTo(TimeUnit.NANOSECONDS);
        assertThat(configuration.getExpireAfterAccessDuration()).isEqualTo(200);
        assertThat(configuration.getExpireAfterWriteDuration()).isEqualTo(100);
        assertThat(configuration.getRefreshAfterWriteDuration()).isEqualTo(300);
    }

    @Test
    public void nonCombiningEntries() {
        //given
        configuration = new GuavaCacheConfiguration().softValue()
                                                     .weakValue()
                                                     .weakKeys();
        //when

        //then
        assertThat(configuration.isSoftValues()).isTrue();
        assertThat(configuration.isWeakValues()).isTrue();
        assertThat(configuration.isWeakKeys()).isTrue();
    }
}