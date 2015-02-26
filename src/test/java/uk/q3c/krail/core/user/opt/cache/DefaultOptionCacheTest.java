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

package uk.q3c.krail.core.user.opt.cache;

import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.opt.OptionDao;
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
    LoadingCache<OptionCacheKey, Optional<Object>> cache;
    @Mock
    OptionCacheKey cacheKey;
    @Mock
    OptionDao dao;
    @Mock
    private DefaultOptionCacheLoader cacheLoader;
    @Mock
    private Provider<OptionDao> daoProvider;

    @Before
    public void setup() {
        when(daoProvider.get()).thenReturn(dao);
        logMonitor.addClassFilter(DefaultOptionCache.class);
        when(cacheProvider.get()).thenReturn(cache);
        optionCache = new DefaultOptionCache(daoProvider, cacheProvider);
    }

    @After
    public void tearDown() {
        logMonitor.close();
    }

    @Test
    public void get_has_value() throws ExecutionException {
        //given
        when(cache.getUnchecked(cacheKey)).thenReturn(Optional.of(11));
        //when
        Integer actual = optionCache.get(5, cacheKey);
        //then
        assertThat(actual).isEqualTo(11);
    }

    @Test
    public void get_has_no_value() throws ExecutionException {
        //given
        when(cache.getUnchecked(cacheKey)).thenReturn(Optional.empty());
        //when
        Integer actual = optionCache.get(5, cacheKey);
        //then
        assertThat(actual).isEqualTo(5);
    }

    @Test
    public void cache_exception() throws ExecutionException {
        //given
        //noinspection unchecked
        when(cache.getUnchecked(cacheKey)).thenThrow(UncheckedExecutionException.class);
        //when
        Integer actual = optionCache.get(5, cacheKey);
        //then
        assertThat(actual).isEqualTo(5);
        assertThat(logMonitor.errorCount()).isEqualTo(1);
    }

    @Test
    public void cache_error() throws ExecutionException {
        //given
        when(cache.getUnchecked(cacheKey)).thenThrow(ExecutionError.class);
        //when
        Integer actual = optionCache.get(5, cacheKey);
        //then
        assertThat(actual).isEqualTo(5);
        assertThat(logMonitor.errorCount()).isEqualTo(1);
    }

    @Test
    public void get_wrong_type() throws ExecutionException {
        //given
        when(cache.getUnchecked(cacheKey)).thenReturn(Optional.of("aa"));
        //when
        Integer actual = optionCache.get(5, cacheKey);
        //then
        assertThat(actual).isEqualTo(5);
        assertThat(logMonitor.errorCount()).isEqualTo(1);
    }

    @Test
    public void write() {
        //given
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        //when
        optionCache.write(cacheKey, 10);
        //then
        verify(dao).write(cacheKey, 10);
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
        when(cache.getIfPresent(cacheKey)).thenReturn(Optional.of(10));
        //when
        Object actual = optionCache.getIfPresent(cacheKey);
        //then
        assertThat(actual).isNotNull();
    }

    @Test
    public void delete() {
        //given

        //when
        optionCache.delete(cacheKey);
        //then
        verify(dao).delete(cacheKey);
        verify(cache).invalidate(cacheKey);
    }

    @Test
    public void flush() {
        //given

        //when
        optionCache.flush();

        //then
        verify(cache).invalidateAll();
    }
    //Moved to integration test, because cannot mock final class CacheStats
    //    @Test
    //    public void stats() {
    //        //given
    //        when(cache.stats()).thenReturn(stats);
    //        //when
    //
    //        //then
    //        verify(cache).stats();
    //    }
}