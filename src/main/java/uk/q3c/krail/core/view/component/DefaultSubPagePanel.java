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
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.I18N;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapLabelChangeMessage;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapStructureChangeMessage;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;
import uk.q3c.krail.core.option.VaadinOptionContext;
import uk.q3c.krail.eventbus.GlobalMessageBus;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionChangeMessage;
import uk.q3c.krail.option.OptionKey;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@I18N
@Listener
@SubscribeTo({GlobalMessageBus.class, SessionBus.class})
@AssignComponentId
public class DefaultSubPagePanel extends NavigationButtonPanel implements VaadinOptionContext, SubPagePanel {
    public static final OptionKey<SortType> optionSortType = new OptionKey<>(SortType.ALPHA, DefaultSubPagePanel.class, LabelKey.Sort_Type, DescriptionKey
            .Sort_Type);
    public static final OptionKey<Boolean> optionSortAscending = new OptionKey<>(Boolean.TRUE, DefaultSubPagePanel.class, LabelKey.Sort_Ascending,
            DescriptionKey
                    .Sort_Ascending);
    private static Logger log = LoggerFactory.getLogger(DefaultSubPagePanel.class);
    private final UserSitemap userSitemap;
    private final Option option;
    private final UserSitemapSorters sorters;


    @Inject
    protected DefaultSubPagePanel(Navigator navigator, UserSitemap userSitemap, Option option, UserSitemapSorters sorters) {
        super(navigator, userSitemap);
        this.userSitemap = userSitemap;
        this.option = option;
        this.sorters = sorters;
        this.addFilter(new NoNavFilter());
        sorters.setOptionSortAscending(getOptionSortAscending());
        sorters.setOptionKeySortType(getOptionSortType());
    }

    public final boolean getOptionSortAscending() {
        return option.get(optionSortAscending);
    }

    @Override
    public void setOptionSortAscending(boolean ascending) {
        setSortAscending(ascending, true);
    }

    @Override
    public void setSortAscending(boolean ascending, boolean rebuild) {
        sorters.setOptionSortAscending(ascending);
        option.set(optionSortAscending, ascending);
        rebuildRequired = true;
        if (rebuild) {
            build();
        }
    }

    @Override
    protected void build() {
        if (rebuildRequired) {
            log.debug("building");
            // premature calls can be made before the navigator has started up properly
            if (getNavigator().getCurrentNavigationState() != null) {
                UserSitemapNode currentNode = getNavigator().getCurrentNode();
                if (currentNode == null) {
                    log.debug("currentNode is null, it has probably been removed by change of authorisation");
                } else {
                    log.debug("current node is '{}'", userSitemap.uri(currentNode));
                }
                List<UserSitemapNode> authorisedSubNodes = userSitemap.getChildren(currentNode);
                Collections.sort(authorisedSubNodes, getSortComparator());
                organiseButtons(authorisedSubNodes);
                rebuildRequired = false;
            }
        } else {
            log.debug("build not required");
        }
    }

    @Override
    public Comparator<UserSitemapNode> getSortComparator() {
        return sorters.getSortComparator();
    }

    public final SortType getOptionSortType() {
        return option.get(optionSortType);
    }

    @Override
    public void setOptionKeySortType(SortType sortType) {
        checkNotNull(sortType);
        setOptionSortType(sortType, true);
    }

    @Override
    public void setOptionSortType(SortType sortType, boolean rebuild) {
        sorters.setOptionKeySortType(sortType);
        option.set(optionSortType, sortType);
        rebuildRequired = true;
        if (rebuild) {
            build();
        }
    }

    @Handler
    public void labelsChanged(UserSitemapLabelChangeMessage busMessage) {
        rebuildRequired = true;
        build();

    }

    @Handler
    public void structureChanged(UserSitemapStructureChangeMessage busMessage) {
        rebuildRequired = true;
        build();
    }


    @Override
    public Option optionInstance() {
        return option;
    }

    @Handler
    public void optionValueChanged(OptionChangeMessage<?> optionChangeMessage) {
        if (optionChangeMessage.getOptionKey().getContext() == DefaultSubPagePanel.class) {
            rebuildRequired = true;
            build();
        }
    }

}
