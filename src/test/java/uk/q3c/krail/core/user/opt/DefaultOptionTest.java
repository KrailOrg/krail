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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.ui.DataTypeToUI;
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.i18n.TestLabelKey;
import uk.q3c.krail.i18n.Translate;

import javax.annotation.Nonnull;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.q3c.krail.core.user.profile.RankOption.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultOptionTest {

    DefaultOption option;
    MockContext contextObject;
    MockContext2 contextObject2;
    Class<MockContext> context = MockContext.class;
    Class<MockContext2> context2 = MockContext2.class;

    @Mock
    private DataTypeToUI dataTypeToUI;
    @Mock
    private UserHierarchy defaultHierarchy;
    @Mock
    private UserHierarchy hierarchy2;
    @Mock
    private OptionCache optionCache;
    private OptionKey<Integer> optionKey1;
    private OptionKey<Integer> optionKey2;
    @Mock
    private Translate translate;

    @Before
    public void setup() {
        contextObject = new MockContext();
        option = new DefaultOption(optionCache, defaultHierarchy);
        optionKey1 = new OptionKey<>(5, context, TestLabelKey.key1, "q");
        optionKey2 = new OptionKey<>(5, context2, TestLabelKey.key1, "q");
    }




    @Test
    public void set_simplest() {
        //given
        when(defaultHierarchy.rankName(0)).thenReturn("specific");
        OptionCacheKey cacheKey = new OptionCacheKey(defaultHierarchy, SPECIFIC_RANK, 0, optionKey1);
        //when
        option.set(5, optionKey1);
        //then
        verify(optionCache).write(cacheKey, Optional.of(5));
    }

    @Test
    public void set_with_hierarchy() {
        //given
        when(hierarchy2.rankName(0)).thenReturn("specific");
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy2, SPECIFIC_RANK, 0, optionKey1);
        //when
        option.set(5, hierarchy2, optionKey1);
        //then
        verify(optionCache).write(cacheKey, Optional.of(5));
    }

    @Test
    public void set_with_all_args() {
        //given
        when(hierarchy2.rankName(2)).thenReturn("specific");
        OptionKey<Integer> optionKey2 = new OptionKey<>(999, context2, TestLabelKey.key1, TestLabelKey.key1, "q");
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy2, SPECIFIC_RANK, 2, optionKey2);
        //when
        option.set(5, hierarchy2, 2, optionKey2);
        //then
        verify(optionCache).write(cacheKey, Optional.of(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void set_with_all_args_rank_too_low() {
        //given
        when(hierarchy2.rankName(2)).thenReturn("specific");
        OptionKey<Integer> optionKey2 = new OptionKey<>(999, context, TestLabelKey.key1, TestLabelKey.key1, "q");
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy2, SPECIFIC_RANK, 2, optionKey2);
        //when
        option.set(5, hierarchy2, -1, optionKey2);
        //then
    }

    @Test
    public void get_simplest() {
        //given
        when(defaultHierarchy.highestRankName()).thenReturn("high");
        OptionCacheKey cacheKey = new OptionCacheKey(defaultHierarchy, HIGHEST_RANK, optionKey1);
        when(optionCache.get(Optional.of(5),cacheKey)).thenReturn(Optional.of(8));
        //when
        Integer actual = option.get(optionKey1);
        //then
        assertThat(actual).isEqualTo(8);
    }

    @Test
    public void get_with_hierarchy() {
        //given
        when(hierarchy2.highestRankName()).thenReturn("high");
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy2, HIGHEST_RANK, optionKey1);
        when(optionCache.get(Optional.of(5),cacheKey)).thenReturn(Optional.of(8));
        //when
        Integer actual = option.get(hierarchy2, optionKey1);
        //then
        assertThat(actual).isEqualTo(8);
    }



    @Test
    public void get_with_all_args() {
        //given
        when(hierarchy2.highestRankName()).thenReturn("high");
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy2, HIGHEST_RANK, optionKey1);
        when(optionCache.get(Optional.of(5),cacheKey)).thenReturn(Optional.of(8));
        //when
        Integer actual = option.get(hierarchy2, optionKey1);
        //then
        assertThat(actual).isEqualTo(8);
    }

    @Test
    public void get_none_found() {
        //given
        when(defaultHierarchy.highestRankName()).thenReturn("high");
        OptionCacheKey cacheKey = new OptionCacheKey(defaultHierarchy, HIGHEST_RANK, optionKey2);
        when(optionCache.get(Optional.of(5),cacheKey)).thenReturn(Optional.empty());
        //when
        Integer actual = option.get(optionKey2);
        //then
        assertThat(actual).isEqualTo(5);
    }

    @Test
    public void get_lowest() {
        //given
        when(defaultHierarchy.lowestRankName()).thenReturn("low");
        OptionCacheKey cacheKey = new OptionCacheKey(defaultHierarchy, LOWEST_RANK, optionKey2);
        when(optionCache.get(Optional.of(5), cacheKey)).thenReturn(Optional.of(20));
        //when
        Integer actual = option.getLowestRanked(defaultHierarchy, optionKey2);
        //then
        assertThat(actual).isEqualTo(20);
    }

    @Test
    public void delete() {
        //given
        when(hierarchy2.rankName(1)).thenReturn("specific");
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy2, SPECIFIC_RANK, 1, optionKey2);
        when(optionCache.delete(cacheKey)).thenReturn(Optional.of(3));
        //when
        Object actual = option.delete(hierarchy2, 1, optionKey2);
        //then
        assertThat(actual).isEqualTo(Optional.of(3));
        verify(optionCache).delete(cacheKey);
    }


    static class MockContext implements OptionContext {

        @Nonnull
        @Override
        public Option getOption() {
            return null;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }


    }

    static class MockContext2 implements OptionContext {

        public static final OptionKey<Integer> key3 = new OptionKey<>(125, MockContext2.class, TestLabelKey.Static, TestLabelKey.Large);
        private static final OptionKey<Integer> key4 = new OptionKey<>(126, MockContext2.class, TestLabelKey.Private_Static, TestLabelKey.Large);
        public final OptionKey<Integer> key2 = new OptionKey<>(124, this, TestLabelKey.key2, TestLabelKey.Blank);
        private final OptionKey<Integer> key1 = new OptionKey<>(123, this, TestLabelKey.key1);

        @Nonnull
        @Override
        public Option getOption() {
            return null;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }


    }


}