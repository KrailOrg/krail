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

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.vaadin.ui.MenuBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapChangeListener;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionConsumer;
import uk.q3c.util.ID;

public class DefaultUserNavigationMenu extends MenuBar implements UserOptionConsumer, UserNavigationMenu,
        UserSitemapChangeListener {

    public enum UserOptionProperty {
        MAX_DEPTH
    }

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
        userOption.configure(this, UserOptionProperty.class);
        setImmediate(true);
        builder.setUserNavigationMenu(this);
        userSitemap.addListener(this);
        setId(ID.getId(Optional.absent(), this));
    }

    @Override
    public MenuBar getMenuBar() {
        return this;
    }

    @Override
    public int getOptionMaxDepth() {
        return userOption.get(10, UserOptionProperty.MAX_DEPTH);
    }
    @Override
    public void setOptionMaxDepth(int depth) {
        userOption.set(depth, UserOptionProperty.MAX_DEPTH);
        build();
    }

    @Override
    public void build() {
        log.debug("rebuilding");
        clear();
        builder.build();
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

    @Override
    public UserOption getUserOption() {
        return userOption;
    }
}
