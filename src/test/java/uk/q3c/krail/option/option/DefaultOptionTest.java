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

package uk.q3c.krail.option.option;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.Property;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.q3c.krail.core.option.OptionPermission;
import uk.q3c.krail.core.option.VaadinOptionContext;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.ui.DataTypeToUI;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.i18n.test.TestLabelKey;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionKey;
import uk.q3c.krail.option.OptionPermissionFailedException;
import uk.q3c.krail.option.UserHierarchy;
import uk.q3c.krail.option.persist.OptionCache;
import uk.q3c.krail.option.persist.OptionCacheKey;
import uk.q3c.krail.option.test.MockOptionPermissionVerifier;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.q3c.krail.option.RankOption.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultOptionTest {

    DefaultOption option;
    MockContext contextObject;
    MockContext2 contextObject2;
    Class<MockContext> context = MockContext.class;
    Class<MockContext2> context2 = MockContext2.class;
    @Mock
    SubjectProvider subjectProvider;
    @Mock
    SubjectIdentifier subjectIdentifier;
    @Mock
    Subject subject;
    @Mock
    private DataTypeToUI dataTypeToUI;
    @Mock
    private UserHierarchy defaultHierarchy;

    @Mock
    private OptionCache optionCache;
    private OptionKey<Integer> optionKey1;
    private OptionKey<Integer> optionKey2;
    @Mock
    private Translate translate;

    private MockOptionPermissionVerifier permissionVerifier;

    @Before
    public void setup() {
        permissionVerifier = new MockOptionPermissionVerifier();
        when(subjectIdentifier.userId()).thenReturn("ds");
        when(subjectProvider.get()).thenReturn(subject);
        when(defaultHierarchy.highestRankName()).thenReturn("ds");
        contextObject = new MockContext();
        option = new DefaultOption(optionCache, defaultHierarchy, permissionVerifier);
        optionKey1 = new OptionKey<>(5, context, TestLabelKey.key1, "q");
        optionKey2 = new OptionKey<>(5, context2, TestLabelKey.key1, "q");
    }


    @Test(expected = OptionPermissionFailedException.class)
    public void setNoPermissions() {
        // given
        permissionVerifier.throwException(true);
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, SPECIFIC_RANK, 0, optionKey1);
        //when
        option.set(optionKey1, 5);
        //then
    }

    @Test
    public void set_simplest() {
        //given
        when(subject.isPermitted(any(OptionPermission.class))).thenReturn(true);
        when(defaultHierarchy.rankName(0)).thenReturn("specific");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, SPECIFIC_RANK, 0, optionKey1);
        //when
        option.set(optionKey1, 5);
        //then
        verify(optionCache).write(cacheKey, Optional.of(5));
        assertThat(option.getHierarchy()).isEqualTo(defaultHierarchy);
    }


    @Test(expected = IllegalArgumentException.class)
    public void set_with_all_args_rank_too_low() {
        //given
        when(subject.isPermitted(any(OptionPermission.class))).thenReturn(true);
        when(defaultHierarchy.rankName(2)).thenReturn("specific");
        OptionKey<Integer> optionKey2 = new OptionKey<>(999, context, TestLabelKey.key1, TestLabelKey.key1, "q");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, SPECIFIC_RANK, 2, optionKey2);
        //when
        option.set(optionKey2, -1, 5);
        //then
    }

    @Test
    public void get_highest() {
        //given
        when(defaultHierarchy.highestRankName()).thenReturn("high");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, HIGHEST_RANK, optionKey1);
        when(optionCache.get(Optional.of(5), cacheKey)).thenReturn(Optional.of(8));
        //when
        Integer actual = option.get(optionKey1);
        //then
        assertThat(actual).isEqualTo(8);
    }


    @Test
    public void get_none_found() {
        //given
        when(defaultHierarchy.highestRankName()).thenReturn("high");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, HIGHEST_RANK, optionKey2);
        when(optionCache.get(Optional.of(5), cacheKey)).thenReturn(Optional.empty());
        //when
        Integer actual = option.get(optionKey2);
        //then
        assertThat(actual).isEqualTo(5);
    }

    @Test
    public void get_specific() throws Exception {
        //given
        when(defaultHierarchy.lowestRankName()).thenReturn("low");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, SPECIFIC_RANK, optionKey2);
        when(optionCache.get(any(), any())).thenAnswer(answerOf(20));
        //when
        Integer actual = option.getSpecificRanked(0, optionKey2);
        //then
        assertThat(actual).isEqualTo(20);
    }

    @Test
    public void get_specific_not_found_return_default() throws Exception {
        //given
        when(defaultHierarchy.lowestRankName()).thenReturn("low");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, SPECIFIC_RANK, optionKey2);
        when(optionCache.get(any(), any())).thenReturn(Optional.empty());
        //when
        Integer actual = option.getSpecificRanked(0, optionKey2);
        //then
        assertThat(actual).isEqualTo(5);
    }

    @Test
    public void get_specific_null_return_default() throws Exception {
        //given
        when(defaultHierarchy.lowestRankName()).thenReturn("low");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, SPECIFIC_RANK, optionKey2);
        when(optionCache.get(any(), any())).thenReturn(null);
        //when
        Integer actual = option.getSpecificRanked(0, optionKey2);
        //then
        assertThat(actual).isEqualTo(5);

    }

    @Test
    public void get_lowest() {
        //given
        when(defaultHierarchy.lowestRankName()).thenReturn("low");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, LOWEST_RANK, optionKey2);
        when(optionCache.get(any(), any())).thenAnswer(answerOf(20));
        //when
        Integer actual = option.getLowestRanked(optionKey2);
        //then
        assertThat(actual).isEqualTo(20);
    }

    protected Answer<Optional<Integer>> answerOf(Integer value) {
        return new Answer<Optional<Integer>>() {
            @Override
            public Optional<Integer> answer(InvocationOnMock invocation) throws Throwable {
                return Optional.of(value);
            }
        };
    }

    @Test
    public void delete() {
        //given
        when(subject.isPermitted(any(OptionPermission.class))).thenReturn(true);
        when(defaultHierarchy.rankName(1)).thenReturn("specific");
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, SPECIFIC_RANK, 1, optionKey2);
        when(optionCache.delete(any())).thenAnswer(answerOf(3));
        //when
        Object actual = option.delete(optionKey2, 1);
        //then
        assertThat(actual).isEqualTo(Optional.of(3));
        verify(optionCache).delete(cacheKey);
    }

    @Test(expected = OptionPermissionFailedException.class)
    public void delete_no_permissions() {
        //given
        permissionVerifier.throwException(true);
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(defaultHierarchy, SPECIFIC_RANK, 1, optionKey2);
        when(optionCache.delete(cacheKey)).thenAnswer(answerOf(3));
        //when
        Object actual = option.delete(optionKey2, 1);
        //then
    }

    static class MockContext implements VaadinOptionContext {


        @Override
        public Option optionInstance() {
            return null;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }


    }

    static class MockContext2 implements VaadinOptionContext {

        public static final OptionKey<Integer> key3 = new OptionKey<>(125, MockContext2.class, TestLabelKey.Static, TestLabelKey.Large);
        private static final OptionKey<Integer> key4 = new OptionKey<>(126, MockContext2.class, TestLabelKey.Private_Static, TestLabelKey.Large);
        public final OptionKey<Integer> key2 = new OptionKey<>(124, this, TestLabelKey.key2, TestLabelKey.Blank);
        private final OptionKey<Integer> key1 = new OptionKey<>(123, this, TestLabelKey.key1);


        @Override
        public Option optionInstance() {
            return null;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }


    }


}