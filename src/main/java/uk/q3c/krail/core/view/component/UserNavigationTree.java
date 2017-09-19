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
import com.vaadin.ui.Tree;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;
import uk.q3c.krail.option.Option;

public interface UserNavigationTree extends Component, UserSitemapSorters {

    /**
     * Returns the Vaadin {@link Tree}
     *
     * @return
     */
    Tree getTree();

    void clear();

    int getOptionMaxDepth();

    /**
     * Set the maximum level or depth of the tree you want to be visible. A value of <=0 is ignored. This value is
     * stored in {@link Option}, and the tree is rebuilt.
     *
     * @param level
     */
    void setOptionMaxDepth(int maxDepth);

    /**
     * Populate the tree with the entries from the UserSitemap
     */
    void build();

    /**
     * Set the maximum level or depth of the tree you want to be visible. A value of <=0 is ignored. This value is
     * stored in {@link Option}. If {@code rebuild} is true, the tree is rebuilt.
     *
     * @param level
     */
    void setOptionMaxDepth(int maxDepth, boolean rebuild);

    /**
     * Sets the sort type but only rebuilds the tree if {@code rebuild} is true. Useful to call with
     * {@code rebuild=false} if you want to make several changes to the tree before rebuilding, otherwise just use
     * {@link UserSitemapSorters#setOptionKeySortType(SortType)}
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

    void setOptionSortAscending(boolean ascending, boolean rebuild);

}
