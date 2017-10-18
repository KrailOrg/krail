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
import com.vaadin.ui.MenuBar;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapLabelChangeMessage;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapStructureChangeMessage;
import uk.q3c.krail.core.option.VaadinOptionContext;
import uk.q3c.krail.core.vaadin.ID;
import uk.q3c.krail.eventbus.GlobalBus;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionChangeMessage;
import uk.q3c.krail.option.OptionKey;

import java.util.Optional;

@Listener
@SubscribeTo({SessionBus.class, GlobalBus.class})
public class DefaultUserNavigationMenu extends MenuBar implements VaadinOptionContext, UserNavigationMenu {

    protected static final OptionKey<Integer> optionKeyMaximumDepth = new OptionKey<>(10, DefaultUserNavigationMenu.class, LabelKey.Maxiumum_Depth,
            DescriptionKey.Maximum_Menu_Depth);
    private static Logger log = LoggerFactory.getLogger(DefaultUserNavigationMenu.class);
    private final Option option;
    private final UserNavigationMenuBuilder builder;

    private boolean sorted = true;

    @Inject
    protected DefaultUserNavigationMenu(Option option, UserNavigationMenuBuilder builder) {
        super();
        this.option = option;
        this.builder = builder;
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
        option.set(optionKeyMaximumDepth, depth);
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


    @Override
    public Option optionInstance() {
        return option;
    }

    @Handler
    public void optionValueChanged(OptionChangeMessage<?> event) {
        if (event.getOptionKey().getContext().equals(DefaultUserNavigationMenu.class)) {
            build();
        }
    }


}
