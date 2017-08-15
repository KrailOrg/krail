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

package uk.q3c.krail.core.user.profile;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.option.hierarchy.SimpleUserHierarchy;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class SimpleUserHierarchyTest {

    public static final String USER_ID = "fbaton";
    public static final String SYSTEM = "system";
    @Mock
    SubjectIdentifier subjectIdentifier;

    @Mock
    SubjectProvider subjectProvider;
    @Mock
    Translate translate;
    private SimpleUserHierarchy hierarchy;
    @Mock
    private Subject subject;

    @Before
    public void setup() {
        when(subjectIdentifier.userId()).thenReturn(USER_ID);
        when(subjectProvider.get()).thenReturn(subject);
        hierarchy = new SimpleUserHierarchy(subjectProvider, subjectIdentifier, translate);
    }


    @Test
    public void optionLayersForCurrentUser_user_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(true);
        //when
        List<String> actual = hierarchy.ranksForCurrentUser();
        //then
        assertThat(actual).containsExactly(USER_ID, SYSTEM);
    }

    @Test
    public void optionLayersForCurrentUser_user_not_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(false);
        //when
        List<String> actual = hierarchy.ranksForCurrentUser();
        //then
        assertThat(actual).containsExactly("system");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rankName_rank_too_low() {
        //given

        //when
        String actual = hierarchy.rankName(-1);
        //then
        //exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void rankName_rank_too_high_when_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(true);
        //when
        String actual = hierarchy.rankName(2);
        //then
        //exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void rankName_rank_too_high_when_not_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(false);
        //when
        String actual = hierarchy.rankName(1);
        //then
        //exception
    }

    @Test
    public void rankName_when_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(true);
        //when
        String actual0 = hierarchy.rankName(0);
        String actual1 = hierarchy.rankName(1);
        //then
        assertThat(actual0).isEqualTo(USER_ID);
        assertThat(actual1).isEqualTo(SYSTEM);
    }

    @Test
    public void rankName_when_not_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(false);
        //when
        String actual0 = hierarchy.rankName(0);
        //then
        assertThat(actual0).isEqualTo(SYSTEM);
    }

    @Test
    public void displayName() {
        //given
        String label = "Simple Hierarchy";
        when(translate.from(LabelKey.Simple_User_Hierarchy)).thenReturn(label);
        //when
        String actual = hierarchy.displayName();
        //then
        assertThat(actual).isEqualTo(label);
    }

    @Test
    public void highest_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(true);
        //when
        String actual = hierarchy.highestRankName();
        //then
        assertThat(actual).isEqualTo(USER_ID);
    }

    @Test
    public void highest_not_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(false);
        //when
        //when
        String actual = hierarchy.highestRankName();
        //then
        assertThat(actual).isEqualTo(SYSTEM);
    }

    @Test
    public void lowest_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(true);
        //when
        String actual = hierarchy.lowestRankName();
        //then
        assertThat(actual).isEqualTo(SYSTEM);
    }

    @Test
    public void lowest_not_authenticated() {
        //given
        when(subject.isAuthenticated()).thenReturn(false);
        //when
        String actual = hierarchy.lowestRankName();
        //then
        assertThat(actual).isEqualTo(SYSTEM);
    }
}