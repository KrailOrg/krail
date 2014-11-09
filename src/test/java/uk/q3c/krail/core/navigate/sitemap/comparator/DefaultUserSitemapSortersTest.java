/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.navigate.sitemap.comparator;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultUserSitemapSortersTest {

    @Inject
    DefaultUserSitemapSorters sorters;

    @Test
    public void defaultSort() {

        // given

        // when

        // then
        assertThat(sorters.getSortComparator()).isInstanceOf(AlphabeticAscending.class);
    }

    @Test
    public void selection() {

        // given

        // when
        sorters.setSortAscending(false);
        // then
        assertThat(sorters.getSortComparator()).isInstanceOf(AlphabeticDescending.class);
        // when
        sorters.setSortType(SortType.INSERTION);
        // then
        assertThat(sorters.getSortComparator()).isInstanceOf(InsertionOrderDescending.class);
        // when
        sorters.setSortAscending(true);
        // then
        assertThat(sorters.getSortComparator()).isInstanceOf(InsertionOrderAscending.class);
        sorters.setSortType(SortType.POSITION);
        // then
        assertThat(sorters.getSortComparator()).isInstanceOf(PositionIndexAscending.class);
        // when
        sorters.setSortAscending(false);
        // then
        assertThat(sorters.getSortComparator()).isInstanceOf(PositionIndexDescending.class);
    }

}
