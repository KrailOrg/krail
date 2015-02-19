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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapChangeListener;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.I18N;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@I18N
public class DefaultSubPagePanel extends NavigationButtonPanel implements OptionContext, SubPagePanel,
        UserSitemapChangeListener {
    public enum OptionProperty {SORT_ASCENDING, SORT_TYPE}
    private static Logger log = LoggerFactory.getLogger(DefaultSubPagePanel.class);
    private final UserSitemap userSitemap;
    private final Option option;
    private final UserSitemapSorters sorters;

    @Inject
    protected DefaultSubPagePanel(Navigator navigator, UserSitemap userSitemap, Option option,
                                  UserSitemapSorters sorters, CurrentLocale currentLocale) {
        super(navigator, userSitemap, currentLocale);
        this.userSitemap = userSitemap;
        this.option = option;
        this.sorters = sorters;
        option.init(this);
        sorters.setOptionSortAscending(getOptionSortAscending());
        sorters.setOptionSortType(getOptionSortType());
    }

    public boolean getOptionSortAscending() {
        return option.get(true, OptionProperty.SORT_ASCENDING);
    }

    @Override
    public void setOptionSortAscending(boolean ascending) {
        setSortAscending(ascending, true);
    }

    @Override
    public void setSortAscending(boolean ascending, boolean rebuild) {
        sorters.setOptionSortAscending(ascending);
        option.set(ascending, OptionProperty.SORT_ASCENDING);
        rebuildRequired = true;
        if (rebuild) {
            build();
        }
    }

    @Override
    protected void build() {
        if (rebuildRequired) {
            log.debug("building");
            UserSitemapNode currentNode = getNavigator().getCurrentNode();
            log.debug("current node is '{}'", userSitemap.uri(currentNode));
            List<UserSitemapNode> authorisedSubNodes = userSitemap.getChildren(currentNode);
            Collections.sort(authorisedSubNodes, getSortComparator());
            organiseButtons(authorisedSubNodes);
            rebuildRequired = false;
        } else {
            log.debug("build not required");
        }
    }

    @Override
    public Comparator<UserSitemapNode> getSortComparator() {
        return sorters.getSortComparator();
    }

    public SortType getOptionSortType() {
        SortType sortType = option.get(SortType.ALPHA, OptionProperty.SORT_TYPE);
        return sortType;
    }

    @Override
    public void setOptionSortType(SortType sortType) {
        setOptionSortType(sortType, true);
    }

    @Override
    public void setOptionSortType(SortType sortType, boolean rebuild) {
        sorters.setOptionSortType(sortType);
        option.set(sortType, OptionProperty.SORT_TYPE);
        rebuildRequired = true;
        if (rebuild) {
            build();
        }
    }

    @Override
    public void labelsChanged() {
        rebuildRequired = true;
        build();

    }

    @Override
    public void structureChanged() {
        rebuildRequired = true;
        build();
    }

    @Override
    public Option getOption() {
        return option;
    }
}
