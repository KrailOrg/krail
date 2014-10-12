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

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.q3c.util.ID;
import uk.co.q3c.util.NodeFilter;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.LocaleChangeListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class NavigationButtonPanel extends HorizontalLayout implements V7ViewChangeListener,
		LocaleChangeListener, Button.ClickListener {
	private static Logger log = LoggerFactory.getLogger(NavigationButtonPanel.class);
	private final List<NavigationButton> buttons = new ArrayList<>();
	private final LinkedList<NodeFilter<UserSitemapNode>> sourceFilters = new LinkedList<>();
	private final V7Navigator navigator;
	private final UserSitemap sitemap;

	protected boolean rebuildRequired = true;

	@Inject
	protected NavigationButtonPanel(V7Navigator navigator, UserSitemap sitemap, CurrentLocale currentLocale) {
		this.navigator = navigator;
		navigator.addViewChangeListener(this);
		this.sitemap = sitemap;
		this.setSizeUndefined();
		this.setSpacing(true);
		currentLocale.addListener(this);
        String id = ID.getId(Optional.absent(), this);
        setId(id);
    }

    public void moveToNavigationState() {
        log.debug("moving to navigation state");
        rebuildRequired = true;
        build();
    }

    protected abstract void build();

    ;

    /**
	 * Displays buttons to represent the supplied nodes.
	 * 
	 * @param nodeList
	 *            contains the list of buttons to display. It is assumed that these are in the right order
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
					buttons.get(i).setVisible(false);
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

	@Override
    public void localeChanged(Locale toLocale) {
        for (NavigationButton button : buttons) {
            button.setCaption(button.getNode()
                                    .getLabel());
        }
    }

	@Override
    public boolean beforeViewChange(V7ViewChangeEvent event) {
        // do nothing
        return true;
    }

	@Override
    public void afterViewChange(V7ViewChangeEvent event) {
        log.debug("Responding to view change");
        rebuildRequired = true;
        build();
    }

    @Override
    public void detach() {
        navigator.removeViewChangeListener(this);
        super.detach();

    }

	public List<NavigationButton> getButtons() {
        return buttons;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        NavigationButton button = (NavigationButton) event.getButton();
        navigator.navigateTo(button.getNode());

    }

    public V7Navigator getNavigator() {
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
