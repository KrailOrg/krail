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
import com.vaadin.data.Property;
import com.vaadin.ui.MenuBar;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.SubscribeTo;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapLabelChangeMessage;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapStructureChangeMessage;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;
import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.i18n.DescriptionKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.util.ID;

import javax.annotation.Nonnull;
import java.util.Optional;

@Listener
@SubscribeTo(SessionBus.class)
public class DefaultUserNavigationMenu extends MenuBar implements OptionContext, UserNavigationMenu {

    protected static final OptionKey<Integer> optionKeyMaximumDepth = new OptionKey<>(10, DefaultUserNavigationMenu.class, LabelKey.Maxiumum_Depth,
            DescriptionKey.Maximum_Menu_Depth);
    private static Logger log = LoggerFactory.getLogger(DefaultUserNavigationMenu.class);
    private final UserSitemap userSitemap;
    private final Option option;
    private final UserNavigationMenuBuilder builder;

    private boolean sorted = true;

    @Inject
    protected DefaultUserNavigationMenu(UserSitemap sitemap, Option option, UserNavigationMenuBuilder builder) {
        super();
        this.userSitemap = sitemap;
        this.option = option;
        this.builder = builder;
        setImmediate(true);
        builder.setUserNavigationMenu(this);
        setId(ID.getId(Optional.empty(), this));

    }

    @Override
    public MenuBar getMenuBar() {
        return this;
    }

    @Override
    public int getOptionMaxDepth() {
        return option.get(optionKeyMaximumDepth);
    }
    @Override
    public void setOptionMaxDepth(int depth) {
        option.set(depth, optionKeyMaximumDepth);
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

    @Handler
    public void labelsChanged(UserSitemapLabelChangeMessage busMessage) {
        build();
    }

    @Handler
    public void structureChanged(UserSitemapStructureChangeMessage busMessage) {
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

    @Nonnull
    @Override
    public Option getOption() {
        return option;
    }

    @Override
    public void optionValueChanged(Property.ValueChangeEvent event) {
        build();
    }


}
