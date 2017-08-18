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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.UIBus;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.vaadin.ID;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.i18n.LocaleChangeBusMessage;
import uk.q3c.util.forest.NodeFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Listener
@SubscribeTo({UIBus.class, SessionBus.class})
public abstract class NavigationButtonPanel extends HorizontalLayout implements
         Button.ClickListener {
    private static Logger log = LoggerFactory.getLogger(NavigationButtonPanel.class);
    private final List<NavigationButton> buttons = new ArrayList<>();
    private final LinkedList<NodeFilter<UserSitemapNode>> sourceFilters = new LinkedList<>();
    private final Navigator navigator;
    private final UserSitemap sitemap;

    protected boolean rebuildRequired = true;

    @Inject
    protected NavigationButtonPanel(Navigator navigator, UserSitemap sitemap) {
        this.navigator = navigator;
        this.sitemap = sitemap;
        this.setSizeUndefined();
        this.setSpacing(true);
        String id = ID.getId(Optional.empty(), this);
        setId(id);
    }

    public void moveToNavigationState() {
        log.debug("moving to navigation state");
        rebuildRequired = true;
        build();
    }

    protected abstract void build();

    /**
     * Displays buttons to represent the supplied nodes.
     *
     * @param nodeList
     *         contains the list of buttons to display. It is assumed that these are in the right order
     */
    protected void organiseButtons(List<UserSitemapNode> nodeList) {
        log.debug("{} nodes to display before filtering", nodeList.size());
        List<UserSitemapNode> filteredList = filteredList(nodeList);
        log.debug("{} nodes to display after filtering", filteredList.size());
        int maxIndex = (filteredList.size() > buttons.size() ? filteredList.size() : buttons.size());
        for (int i = 0; i < maxIndex; i++) {
            // nothing left in chain
            if (i + 1 > filteredList.size()) {
                // but buttons still exist
                if (i < buttons.size()) {
                    buttons.get(i)
                           .setVisible(false);
                }
            } else {
                // chain continues
                NavigationButton button = null;
                // steps still exist, re-use
                if (i < buttons.size()) {
                    button = buttons.get(i);
                } else {
                    button = createButton();
                }
                setupButton(button, filteredList.get(i));
            }

        }
    }

    protected NavigationButton createButton() {
        NavigationButton button = new NavigationButton();
        button.addStyleName(BaseTheme.BUTTON_LINK);
        button.addClickListener(this);
        buttons.add(button);
        String id = ID.getId(Optional.of(buttons.size() - 1), this, button);
        button.setId(id);
        this.addComponent(button);
        return button;
    }

    private void setupButton(NavigationButton button, UserSitemapNode sitemapNode) {

        button.setNode(sitemapNode);
        button.setVisible(true);

    }

    protected List<UserSitemapNode> filteredList(List<UserSitemapNode> list) {
        List<UserSitemapNode> newList = new ArrayList<>();
        for (UserSitemapNode node : list) {
            boolean accept = true;
            for (NodeFilter<UserSitemapNode> filter : sourceFilters) {
                if (!filter.accept(node)) {
                    accept = false;
                    break;
                }
            }

            if (accept) {
                newList.add(node);
            }
        }
        return newList;
    }

    @Handler
    public void localeChanged(LocaleChangeBusMessage busMessage) {
        log.debug("responding to locale change to {}", busMessage.getNewLocale());
        for (NavigationButton button : buttons) {
            button.setCaption(button.getNode()
                                    .getLabel());
        }
    }


    @Handler
    public void afterViewChange(AfterViewChangeBusMessage busMessage) {
        log.debug("Responding to view change");
        rebuildRequired = true;
        build();
    }



    public List<NavigationButton> getButtons() {
        return buttons;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        NavigationButton button = (NavigationButton) event.getButton();
        navigator.navigateTo(button.getNode());

    }

    public Navigator getNavigator() {
        return navigator;
    }

    public UserSitemap getSitemap() {
        return sitemap;
    }

    public void addFilter(NodeFilter<UserSitemapNode> filter) {
        sourceFilters.add(filter);
    }

    public void removeFilter(NodeFilter<UserSitemapNode> filter) {
        sourceFilters.remove(filter);
    }

    public boolean isRebuildRequired() {
        return rebuildRequired;
    }

}
