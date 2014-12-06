/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.view.component;

import com.vaadin.ui.Component;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;

public interface SubPagePanel extends Component, UserSitemapSorters {

    void moveToNavigationState();

    /**
     * Sets the sort type but only rebuilds the tree if {@code rebuild} is true. Useful to call with
     * {@code rebuild=false} if you want to make several changes to the tree before rebuilding, otherwise just use
     * {@link UserSitemapSorters#setOptionSortType(SortType)}
     *
     * @param sortType
     * @param rebuild
     */
    void setOptionSortType(SortType sortType, boolean rebuild);

    /**
     * Sets the sort direction but only rebuilds the tree if {@code rebuild} is true. Useful to call with
     * {@code rebuild=false} if you want to make several changes to the tree before rebuilding, otherwise just use
     * {@link UserSitemapSorters#setOptionSortAscending(boolean)}
     *
     * @param sortType
     * @param rebuild
     */

    void setSortAscending(boolean ascending, boolean rebuild);
}
