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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapChangeListener;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionProperty;

import com.google.inject.Inject;
import com.vaadin.ui.MenuBar;

public class DefaultUserNavigationMenu extends MenuBar implements UserNavigationMenu, UserSitemapChangeListener {
	private static Logger log = LoggerFactory.getLogger(DefaultUserNavigationMenu.class);
	private final UserSitemap userSitemap;
	private final UserOption userOption;
	private final UserNavigationMenuBuilder builder;
	private boolean sorted = true;

	// private final Translate translate;

	@Inject
	protected DefaultUserNavigationMenu(UserSitemap sitemap, UserOption userOption, UserNavigationMenuBuilder builder) {
		super();
		this.userSitemap = sitemap;
		this.userOption = userOption;
		this.builder = builder;
		setImmediate(true);
		builder.setUserNavigationMenu(this);
		userSitemap.addListener(this);
		setId(ID.getId(this));
	}

	@Override
	public void build() {
		log.debug("rebuilding");
		clear();
		builder.build();
	}

	@Override
	public MenuBar getMenuBar() {
		return this;
	}

	@Override
	public void setMaxDepth(int depth) {
		userOption.setOption(getClass().getSimpleName(), UserOptionProperty.MAX_DEPTH, depth);
		build();
	}

	@Override
	public int getMaxDepth() {
		return userOption.getOptionAsInt(getClass().getSimpleName(), UserOptionProperty.MAX_DEPTH, 10);
	}

	@Override
	public void clear() {
		this.removeItems();
		log.debug("contents cleared");
	}

	@Override
	public void labelsChanged() {
		build();
	}

	@Override
	public void structureChanged() {
		build();
	}

	@Override
	public boolean isSorted() {
		log.debug("Sorted is {}", sorted);
		return sorted;
	}

	@Override
	public void setSorted(boolean sorted) {
		this.sorted = sorted;
		build();
	}

}
