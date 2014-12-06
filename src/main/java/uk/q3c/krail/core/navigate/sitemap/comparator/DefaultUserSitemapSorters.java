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
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;

import java.util.Comparator;

/**
 * A set of comparators which can be used to sort {@link UserSitemapNode}s, with a lookup key and name to support
 * selection by an end user
 *
 * @author dsowerby
 */
public class DefaultUserSitemapSorters implements UserSitemapSorters {

    private final AlphabeticAscending alphaAscending;
    ;
    private final AlphabeticDescending alphaDescending;
    private final InsertionOrderAscending insertionAscending;
    private final InsertionOrderDescending insertionDescending;
    private final PositionIndexAscending positionAscending;
    private final PositionIndexDescending positionDescending;
    private boolean ascending = true;
    private Comparator<UserSitemapNode> selectedComparator;
    private SortType sortType = SortType.ALPHA;

    @Inject
    protected DefaultUserSitemapSorters(AlphabeticAscending alphaAscending, AlphabeticDescending alphaDescending,
                                        InsertionOrderAscending insertionAscending,
                                        InsertionOrderDescending insertionDescending,
                                        PositionIndexAscending positionAscending,
                                        PositionIndexDescending positionDescending) {
        this.alphaAscending = alphaAscending;
        this.alphaDescending = alphaDescending;
        this.insertionAscending = insertionAscending;
        this.insertionDescending = insertionDescending;
        this.positionAscending = positionAscending;
        this.positionDescending = positionDescending;
        select();
    }

    private void select() {
        switch (sortType) {
            case ALPHA:
                selectedComparator = (ascending) ? alphaAscending : alphaDescending;
                break;
            case INSERTION:
                selectedComparator = (ascending) ? insertionAscending : insertionDescending;
                break;
            case POSITION:
                selectedComparator = (ascending) ? positionAscending : positionDescending;
                break;
        }
    }

    @Override
    public void setOptionSortAscending(boolean ascending) {
        this.ascending = ascending;
        select();
    }

    public boolean isAscending() {
        return ascending;
    }

    public SortType getSortType() {
        return sortType;
    }

    @Override
    public void setOptionSortType(SortType sortType) {
        this.sortType = sortType;
        select();
    }

    @Override
    public Comparator<UserSitemapNode> getSortComparator() {
        return selectedComparator;
    }

    public enum SortType {
        ALPHA, INSERTION, POSITION
    }

}
