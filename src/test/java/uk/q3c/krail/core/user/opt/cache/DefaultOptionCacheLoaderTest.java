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

import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.opt.InMemoryOptionDao;
import uk.q3c.krail.core.user.opt.OptionDao;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import java.util.LinkedHashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.q3c.krail.core.user.profile.RankOption.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultOptionCacheLoaderTest {

    DefaultOptionCacheLoader loader;

    ImmutableList<String> rankNames1;

    LinkedHashMap<String, Object> resultMap;

    @Mock
    Provider<OptionDao> daoProvider;

    @Mock
    InMemoryOptionDao dao;

    @Mock
    OptionCacheKey cacheKey;

    @Mock
    UserHierarchy hierarchy1;

    @Before
    public void setup() {
        when(daoProvider.get()).thenReturn(dao);
        loader = new DefaultOptionCacheLoader(daoProvider);
        resultMap = new LinkedHashMap<>();
        rankNames1 = ImmutableList.of("a", "b", "c");
        when(hierarchy1.ranksForCurrentUser()).thenReturn(rankNames1);
        when(hierarchy1.persistenceName()).thenReturn("mock hierarchy");
        when(cacheKey.getHierarchy()).thenReturn(hierarchy1);
    }

    @Test
    public void load_nothing_from_store_highest() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(HIGHEST_RANK);
        when(dao.getHighestRankedValue(cacheKey)).thenReturn(Optional.empty());
        //when
        Optional<Object> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void load_one_from_store_highest() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(HIGHEST_RANK);
        when(dao.getHighestRankedValue(cacheKey)).thenReturn(Optional.of(1));
        //when
        Optional<Object> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(1);
    }



    @Test
    public void load_nothing_from_store_lowest() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(LOWEST_RANK);
        when(dao.getLowestRankedValue(cacheKey)).thenReturn(Optional.empty());
        //when
        Optional<Object> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void load_one_from_store_lowest() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(LOWEST_RANK);
        when(dao.getLowestRankedValue(cacheKey)).thenReturn(Optional.of(1));
        //when
        Optional<Object> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(1);
    }


    @Test
    public void load_specific_exists() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        when(dao.getValue(cacheKey)).thenReturn(Optional.of(7));
        //when
        Optional<Object> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(7);
    }

    @Test
    public void load_specific_not_exists() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        when(dao.getValue(cacheKey)).thenReturn(Optional.empty());
        //when
        Optional<Object> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isFalse();
    }
}