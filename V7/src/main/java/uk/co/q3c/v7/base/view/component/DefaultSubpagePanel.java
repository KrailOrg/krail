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
package uk.co.q3c.v7.base.view.component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapChangeListener;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.UserSitemapSorters;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionProperty;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18N;

import com.google.inject.Inject;

@I18N
public class DefaultSubpagePanel extends NavigationButtonPanel implements SubpagePanel, UserSitemapChangeListener {
	private static Logger log = LoggerFactory.getLogger(DefaultSubpagePanel.class);
	private final UserSitemap userSitemap;
	private final UserOption userOption;
	private final UserSitemapSorters sorters;

	@Inject
	protected DefaultSubpagePanel(V7Navigator navigator, UserSitemap userSitemap, UserOption userOption,
			UserSitemapSorters sorters, CurrentLocale currentLocale) {
		super(navigator, userSitemap, currentLocale);
		this.userSitemap = userSitemap;
		this.userOption = userOption;
		this.sorters = sorters;
		sorters.setSortAscending(getSortAscending());
		sorters.setSortType(getSortType());
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
	public void setSortAscending(boolean ascending) {
		setSortAscending(ascending, true);
	}

	@Override
	public void setSortAscending(boolean ascending, boolean rebuild) {
		sorters.setSortAscending(ascending);
		userOption.setOption(this.getClass().getSimpleName(), UserOptionProperty.SORT_ASCENDING, ascending);
		rebuildRequired = true;
		if (rebuild) {
			build();
		}
	}

	@Override
	public void setSortType(SortType sortType) {
		setSortType(sortType, true);
	}

	@Override
	public void setSortType(SortType sortType, boolean rebuild) {
		sorters.setSortType(sortType);
		userOption.setOption(this.getClass().getSimpleName(), UserOptionProperty.SORT_TYPE, sortType);
		rebuildRequired = true;
		if (rebuild) {
			build();
		}
	}

	@Override
	public Comparator<UserSitemapNode> getSortComparator() {
		return sorters.getSortComparator();
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

	public boolean getSortAscending() {
		boolean ascending = userOption.getOptionAsBoolean(this.getClass().getSimpleName(),
				UserOptionProperty.SORT_ASCENDING, true);
		return ascending;
	}

	public SortType getSortType() {
		SortType sortType = (SortType) userOption.getOptionAsEnum(this.getClass().getSimpleName(),
				UserOptionProperty.SORT_TYPE, SortType.ALPHA);
		return sortType;
	}
}
