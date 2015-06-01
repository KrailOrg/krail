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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.i18n.LabelKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class OptionCacheKeyTest {


    @Mock
    private UserHierarchy hierarchy;

    @Test
    public void copyConstructOtherRank() {
        //given
        when(hierarchy.persistenceName()).thenReturn("simple");
        when(hierarchy.highestRankName()).thenReturn("highest");
        when(hierarchy.lowestRankName()).thenReturn("lowest");
        OptionKey optKey = new OptionKey(23, LocaleContainer.class, LabelKey.Yes);
        OptionCacheKey key1 = new OptionCacheKey(hierarchy, RankOption.HIGHEST_RANK, optKey);
        //when
        OptionCacheKey key2 = new OptionCacheKey(key1, RankOption.LOWEST_RANK);
        //then
        assertThat(key1).isNotEqualTo(key2);
        assertThat(key2.getRankOption()).isEqualTo(RankOption.LOWEST_RANK);
    }
}