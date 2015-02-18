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

package uk.q3c.krail.core.user.opt;

import com.google.common.collect.ImmutableList;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.opt.cache.OptionKeyException;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;
import static uk.q3c.krail.core.user.profile.RankOption.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class InMemoryOptionDaoTest {

    InMemoryOptionDao dao;


    @Mock
    InMemoryOptionStore store;

    @Mock
    OptionCacheKey cacheKey;
    ImmutableList<String> rankNames1 = ImmutableList.of("fbaton", "accounts", "finance", "Q3");
    @Mock
    OptionKey optionKey;
    @Mock
    private UserHierarchy hierarchy;
    private String hierarchyName1 = "mock hierarchy";

    @Before
    public void setup() {
        when(hierarchy.ranksForCurrentUser()).thenReturn(rankNames1);
        when(cacheKey.getHierarchy()).thenReturn(hierarchy);
        when(hierarchy.persistenceName()).thenReturn(hierarchyName1);
        dao = new InMemoryOptionDao(store);
    }


    @Test(expected = OptionKeyException.class)
    public void values_wrong_key() {
        //given
        Map<String, Object> map = new TreeMap<>();
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        //when
        dao.getValuesForRanks(cacheKey, rankNames1);
        //then
    }

    @Test
    public void no_values_in_store() {
        //given
        when(cacheKey.getRankOption()).thenReturn(HIGHEST_RANK);
        when(cacheKey.getOptionKey()).thenReturn(optionKey);
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        //use a LinkedHashMap map to force an order different to hierarchy, for testing
        Map<String, Object> map = new TreeMap<>();
        when(store.valueMapForOptionKey(anyString(), anyList(), any())).thenReturn(map);
        //when
        LinkedHashMap<String, Object> actual = dao.getValuesForRanks(cacheKey, rankNames1);
        //then
        assertThat(actual).isEmpty();
    }


    @Test
    public void one_value_in_store() {
        //given
        when(cacheKey.getRankOption()).thenReturn(HIGHEST_RANK);
        when(cacheKey.getOptionKey()).thenReturn(optionKey);
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        //use a LinkedHashMap map to force an order different to hierarchy, for testing
        Map<String, Object> map = new TreeMap<>();
        map.put("accounts", 7);
        when(store.valueMapForOptionKey(anyString(), anyList(), any())).thenReturn(map);
        //when
        LinkedHashMap<String, Object> actual = dao.getValuesForRanks(cacheKey, rankNames1);
        //then
        assertThat(actual).isNotEmpty();
        assertThat(actual).containsExactly(entry("accounts", 7));
    }

    @Test
    public void multiple_values_in_store() {
        //given
        when(cacheKey.getRankOption()).thenReturn(LOWEST_RANK);
        when(cacheKey.getOptionKey()).thenReturn(optionKey);
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        //use a LinkedHashMap map to force an order different to hierarchy, for testing
        Map<String, Object> map = new TreeMap<>();
        map.put("accounts", 7);
        map.put("fbaton", 3);
        map.put("finance", 5);
        map.put("Q3", 12);
        when(store.valueMapForOptionKey(anyString(), anyList(), any())).thenReturn(map);
        //when
        LinkedHashMap<String, Object> actual = dao.getValuesForRanks(cacheKey, rankNames1);
        //then
        assertThat(actual).isNotEmpty();
        assertThat(actual).containsExactly(entry("fbaton", 3), entry("accounts", 7), entry("finance", 5), entry("Q3", 12));
    }


    @Test
    public void write() {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        when(cacheKey.getOptionKey()).thenReturn(optionKey);
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        //when
        dao.write(cacheKey, 7);
        //then
        verify(store).setValue(hierarchyName1, "a", optionKey, 7);
    }

    @Test(expected = NullPointerException.class)
    public void writeNull() {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        when(cacheKey.getOptionKey()).thenReturn(optionKey);
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        //when
        dao.write(cacheKey, null);
        //then
    }

    @Test(expected = OptionKeyException.class)
    public void write_highest() {
        //given
        when(cacheKey.getRankOption()).thenReturn(HIGHEST_RANK);
        //when
        dao.write(cacheKey, 5);
        //then
    }

    @Test(expected = OptionKeyException.class)
    public void write_lowest() {
        //given
        when(cacheKey.getRankOption()).thenReturn(LOWEST_RANK);
        //when
        dao.write(cacheKey, 5);
        //then
    }

    @Test
    public void delete_exists() {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        when(store.deleteValue(anyString(), anyString(), any(OptionKey.class))).thenReturn(5);
        //when
        Object actual = dao.delete(cacheKey);
        //then
        assertThat(actual).isEqualTo(5);
    }

    @Test
    public void delete_non_existent() {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        when(store.deleteValue(anyString(), anyString(), any(OptionKey.class))).thenReturn(null);
        //when
        Object actual = dao.delete(cacheKey);
        //then
        assertThat(actual).isNull();
    }

    @Test(expected = OptionKeyException.class)
    public void delete_highest() {
        //given
        when(cacheKey.getRankOption()).thenReturn(HIGHEST_RANK);
        //when
        dao.delete(cacheKey);
        //then
    }

    @Test(expected = OptionKeyException.class)
    public void delete_lowest() {
        //given
        when(cacheKey.getRankOption()).thenReturn(LOWEST_RANK);
        //when
        dao.delete(cacheKey);
        //then
    }

    @Test
    public void get_exists() {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        when(cacheKey.getOptionKey()).thenReturn(optionKey);
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        when(store.getValue(hierarchyName1, "a", optionKey)).thenReturn(7);
        //when
        Optional<Object> actual = dao.get(cacheKey);
        //then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(7);
    }

    @Test(expected = OptionKeyException.class)
    public void get_highest() {
        //given
        when(cacheKey.getRankOption()).thenReturn(HIGHEST_RANK);
        //when
        dao.get(cacheKey);
        //then
    }

    @Test(expected = OptionKeyException.class)
    public void get_lowest() {
        //given
        when(cacheKey.getRankOption()).thenReturn(LOWEST_RANK);
        //when
        dao.get(cacheKey);
        //then
    }

    @Test
    public void get_non_existent() {
        //given
        when(cacheKey.getRankOption()).thenReturn(SPECIFIC_RANK);
        when(cacheKey.getOptionKey()).thenReturn(optionKey);
        when(cacheKey.getRequestedRankName()).thenReturn("a");
        //when
        Optional<Object> actual = dao.get(cacheKey);
        //then
        assertThat(actual).isEqualTo(Optional.empty());

        verify(store).getValue(hierarchyName1, "a", optionKey);
    }


}