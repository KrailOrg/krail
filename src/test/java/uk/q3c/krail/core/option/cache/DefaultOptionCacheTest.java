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

package uk.q3c.krail.core.option.cache;

import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCache;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCacheLoader;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCacheProvider;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.core.persist.common.option.OptionDao;
import uk.q3c.krail.core.persist.common.option.OptionSource;
import uk.q3c.util.testutil.LogMonitor;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultOptionCacheTest {


    @Inject
    LogMonitor logMonitor;

    DefaultOptionCache optionCache;
    @Mock
    DefaultOptionCacheProvider cacheProvider;
    @Mock
    MockCache cache;


    @Mock
    LoadingCache<OptionCacheKey, Optional<?>> cache2;
    @Mock
    OptionCacheKey cacheKey;
    @Mock
    OptionDao dao;


    DefaultOptionCache optionCache2;
    @Mock
    private DefaultOptionCacheLoader cacheLoader;
    @Mock
    private OptionSource daoProvider;

    @Before
    public void setup() {
        cache = new MockCache();
        logMonitor.addClassFilter(DefaultOptionCache.class);
        when(cacheProvider.get()).thenReturn(cache);
        optionCache = new DefaultOptionCache(dao, cacheProvider);
    }

    @After
    public void tearDown() {
        logMonitor.close();
    }

    @Test
    public void get_has_value() throws ExecutionException {
        //given
        cache.setValue(cacheKey, Optional.of(11));
        //when
        Optional<Integer> actual = optionCache.get(Optional.of(5), cacheKey);
        //then
        assertThat(actual.get()).isEqualTo(11);
    }

    @Test
    public void get_has_no_value() throws ExecutionException {
        //given
        cache.setValue(cacheKey, Optional.empty());
        //when
        Optional<Integer> actual = optionCache.get(Optional.of(5), cacheKey);
        //then
        assertThat(actual.get()).isEqualTo(5);
    }

    // TODO problems with throwing exceptions
    //    @Test
    //    public void cache_exception() throws ExecutionException {
    //        //given
    //        //noinspection unchecked
    //        when(cache.getUnchecked(cacheKey)).thenThrow(UncheckedExecutionException.class);
    //        //when
    //        Integer actual = optionCache.get(5, cacheKey);
    //        //then
    //        assertThat(actual).isEqualTo(5);
    //        assertThat(logMonitor.errorCount()).isEqualTo(1);
    //    }
    //
    //    @Test
    //    public void cache_error() throws ExecutionException {
    //        //given
    //        when(cache.getUnchecked(cacheKey)).thenThrow(ExecutionError.class);
    //        //when
    //        Integer actual = optionCache.get(5, cacheKey);
    //        //then
    //        assertThat(actual).isEqualTo(5);
    //        assertThat(logMonitor.errorCount()).isEqualTo(1);
    //    }

    @Test
    public void get_wrong_type() throws ExecutionException {
        //given
        cache.setValue(cacheKey, Optional.of("aa"));
        //when
        Optional<Integer> actual = optionCache.get(Optional.of(5), cacheKey);
        //then
        assertThat(actual).isEqualTo(Optional.of(5));
        assertThat(logMonitor.errorCount()).isEqualTo(1);
    }

    @Test
    public void write() {
        //given
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        //when
        optionCache.write(cacheKey, Optional.of(10));
        //then
        verify(dao).write(cacheKey, Optional.of(10));
    }

    @Test
    public void get_if_present_not_present() {
        //given
        when(cache.getIfPresent(cacheKey)).thenReturn(null);
        //when
        Object actual = optionCache.getIfPresent(cacheKey);
        //then
        assertThat(actual).isNull();
    }

    @Test
    public void get_if_present_present() {
        //given
        Optional<?> opt = Optional.of(10);
        cache.setValue(cacheKey, opt);
        //when
        Optional<?> actual = optionCache.getIfPresent(cacheKey);
        //then
        assertThat(actual).isNotNull();
        assertThat(actual.get()).isEqualTo(10);
    }

    @Test
    public void delete() {
        //given
        when(cacheProvider.get()).thenReturn(cache2);
        optionCache2 = new DefaultOptionCache(dao, cacheProvider);
        //when
        optionCache2.delete(cacheKey);
        //then
        verify(dao).deleteValue(cacheKey);
        verify(cache2).invalidate(cacheKey);
    }

    @Test
    public void flush() {
        //given
        when(cacheProvider.get()).thenReturn(cache2);
        optionCache2 = new DefaultOptionCache(dao, cacheProvider);
        //when
        optionCache2.flush();

        //then
        verify(cache2).invalidateAll();
    }
}