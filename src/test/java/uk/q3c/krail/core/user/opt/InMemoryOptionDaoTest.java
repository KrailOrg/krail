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
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class InMemoryOptionDaoTest {

    InMemoryOptionDao dao;


    @Mock
    InMemoryOptionStore store;

    @Mock
    OptionCacheKey cacheKeyHigh;

    @Mock
    OptionCacheKey cacheKeyLow;

    @Mock
    OptionCacheKey cacheKeySpecific;

    @Mock
    OptionCacheKey cacheKeySpecificMissing;

    ImmutableList<String> rankNames1 = ImmutableList.of("fbaton", "accounts", "finance", "Q3");
    @Mock
    OptionKey optionKey;
    @Mock
    private UserHierarchy hierarchy;
    private String hierarchyName1 = "mock hierarchy";

    @Before
    public void setup() {
        when(hierarchy.ranksForCurrentUser()).thenReturn(rankNames1);
        when(cacheKeyHigh.getHierarchy()).thenReturn(hierarchy);
        when(cacheKeyHigh.getRankOption()).thenReturn(RankOption.HIGHEST_RANK);
        when(cacheKeyHigh.getOptionKey()).thenReturn(optionKey);
        when(cacheKeyLow.getRankOption()).thenReturn(RankOption.LOWEST_RANK);
        when(cacheKeyLow.getHierarchy()).thenReturn(hierarchy);
        when(cacheKeyLow.getOptionKey()).thenReturn(optionKey);
        when(cacheKeySpecific.getRankOption()).thenReturn(RankOption.SPECIFIC_RANK);
        when(cacheKeySpecific.getHierarchy()).thenReturn(hierarchy);
        when(cacheKeySpecific.getOptionKey()).thenReturn(optionKey);
        when(cacheKeySpecific.getRequestedRankName()).thenReturn("accounts");
        when(cacheKeySpecificMissing.getRankOption()).thenReturn(RankOption.SPECIFIC_RANK);
        when(cacheKeySpecificMissing.getHierarchy()).thenReturn(hierarchy);
        when(cacheKeySpecificMissing.getOptionKey()).thenReturn(optionKey);
        when(cacheKeySpecificMissing.getRequestedRankName()).thenReturn("a");



        when(hierarchy.persistenceName()).thenReturn(hierarchyName1);
        dao = new InMemoryOptionDao(store);
    }


    @Test
    public void no_values_in_store() {
        //given

        //use a LinkedHashMap map to force an order different to hierarchy, for testing
        Map<String, Object> map = new TreeMap<>();
        when(store.valueMapForOptionKey(anyString(), anyList(), any())).thenReturn(map);
        //when
        LinkedHashMap<String, Object> actual = dao.getValuesForRanks(cacheKeyHigh, rankNames1);
        //then
        assertThat(actual).isEmpty();
        assertThat(dao.getHighestRankedValue(cacheKeyHigh)).isEqualTo(Optional.empty());
        assertThat(dao.getLowestRankedValue(cacheKeyLow)).isEqualTo(Optional.empty());
        assertThat(dao.getValue(cacheKeySpecific)).isEqualTo(Optional.empty());
    }


    @Test
    public void one_value_in_store() {
        //given
        //use a LinkedHashMap map to force an order different to hierarchy, for testing
        Map<String, Object> map = new TreeMap<>();
        map.put("accounts", 7);
        when(store.valueMapForOptionKey(anyString(), anyList(), any())).thenReturn(map);
        when(store.getValue("mock hierarchy", "accounts", optionKey)).thenReturn(5);
        //when
        LinkedHashMap<String, Object> actual = dao.getValuesForRanks(cacheKeyHigh, rankNames1);
        //then
        assertThat(actual).isNotEmpty();
        assertThat(actual).containsExactly(entry("accounts", 7));
        assertThat(dao.getHighestRankedValue(cacheKeyHigh)).isEqualTo(Optional.of(7));
        assertThat(dao.getLowestRankedValue(cacheKeyLow)).isEqualTo(Optional.of(7));
        assertThat(dao.getValue(cacheKeySpecific)).isEqualTo(Optional.of(5));
        assertThat(dao.getValue(cacheKeySpecificMissing)).isEqualTo(Optional.empty());
    }

    @Test
    public void multiple_values_in_store() {
        //given
        //use a LinkedHashMap map to force an order different to hierarchy, for testing
        Map<String, Object> map = new TreeMap<>();
        map.put("accounts", 7);
        map.put("fbaton", 3);
        map.put("finance", 5);
        map.put("Q3", 1);
        when(store.valueMapForOptionKey(anyString(), anyList(), any())).thenReturn(map);
        when(store.getValue("mock hierarchy", "accounts", optionKey)).thenReturn(7);
        //when
        LinkedHashMap<String, Object> actual = dao.getValuesForRanks(cacheKeyHigh, rankNames1);
        //then
        assertThat(actual).isNotEmpty();
        assertThat(actual).containsExactly(entry("fbaton", 3), entry("accounts", 7), entry("finance", 5), entry("Q3", 1));
        assertThat(dao.getHighestRankedValue(cacheKeyHigh)).isEqualTo(Optional.of(3));
        assertThat(dao.getLowestRankedValue(cacheKeyLow)).isEqualTo(Optional.of(1));
        assertThat(dao.getValue(cacheKeySpecific)).isEqualTo(Optional.of(7));
        assertThat(dao.getValue(cacheKeySpecificMissing)).isEqualTo(Optional.empty());
    }


    @Test
    public void write() {
        //given
        //when
        dao.write(cacheKeySpecific, 7);
        //then
        verify(store).setValue(hierarchyName1, "accounts", optionKey, 7);
    }

    @Test(expected = NullPointerException.class)
    public void writeNull() {
        //given
        //when
        dao.write(cacheKeySpecific, null);
        //then
    }

    @Test(expected = OptionKeyException.class)
    public void write_highest() {
        //given
        //when
        dao.write(cacheKeyHigh, 5);
        //then
    }

    @Test(expected = OptionKeyException.class)
    public void write_lowest() {
        //given
        //when
        dao.write(cacheKeyLow, 5);
        //then
    }

    @Test
    public void delete_exists() {
        //given
        when(store.deleteValue(anyString(), anyString(), any(OptionKey.class))).thenReturn(5);
        //when
        Object actual = dao.deleteValue(cacheKeySpecific);
        //then
        assertThat(actual).isEqualTo(5);
    }

    @Test
    public void delete_non_existent() {
        //given
        when(store.deleteValue(anyString(), anyString(), any(OptionKey.class))).thenReturn(null);
        //when
        Object actual = dao.deleteValue(cacheKeySpecific);
        //then
        assertThat(actual).isNull();
    }

    @Test(expected = OptionKeyException.class)
    public void delete_highest() {
        //given
        //when
        dao.deleteValue(cacheKeyHigh);
        //then
    }

    @Test(expected = OptionKeyException.class)
    public void delete_lowest() {
        //given
        //when
        dao.deleteValue(cacheKeyHigh);
        //then
    }





}