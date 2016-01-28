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

import com.google.common.collect.ImmutableList;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCacheLoader;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.core.persist.common.option.OptionSource;
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
    OptionSource daoProvider;

    MockOptionDao dao;

    @Mock
    OptionCacheKey cacheKey;

    @Mock
    UserHierarchy hierarchy1;

    @Before
    public void setup() {
        dao = new MockOptionDao();
        when(daoProvider.getActiveDao()).thenReturn(dao);
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
        dao.setHighestRankedValue(cacheKey, Optional.empty());
        //when
        Optional<?> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void load_one_from_store_highest() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(HIGHEST_RANK);
        dao.setHighestRankedValue(cacheKey, Optional.of(1));
        //when
        Optional<?> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(1);
    }



    @Test
    public void load_nothing_from_store_lowest() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(LOWEST_RANK);
        dao.setLowestRankedValue(cacheKey, Optional.empty());
        //when
        Optional<?> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void load_one_from_store_lowest() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(LOWEST_RANK);
        dao.setLowestRankedValue(cacheKey, Optional.of(1));
        //when
        Optional<?> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(1);
    }


    @Test
    public void load_specific_exists() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        dao.setValue(cacheKey, Optional.of(7));
        //when
        Optional<?> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(7);
    }

    @Test
    public void load_specific_not_exists() throws Exception {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        dao.setValue(cacheKey, Optional.empty());
        //when
        Optional<?> actual = loader.load(cacheKey);
        //then
        assertThat(actual.isPresent()).isFalse();
    }
}