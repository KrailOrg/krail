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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.NodeSorter;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.LocaleChangeListener;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

public abstract class NavigationButtonPanel extends HorizontalLayout implements V7ViewChangeListener,
		LocaleChangeListener, Button.ClickListener, Breadcrumb {
	private final List<NavigationButton> buttons = new ArrayList<>();
	private final V7Navigator navigator;
	private final UserSitemap sitemap;
	private final Translate translate;

	public static final String sortedOpt = "sorted";
	protected boolean usesSort = true;
	private final UserOption userOption;

	@Inject
	protected NavigationButtonPanel(V7Navigator navigator, UserSitemap sitemap, CurrentLocale currentLocale,
			Translate translate, UserOption userOption) {
		this.navigator = navigator;
		navigator.addViewChangeListener(this);
		this.sitemap = sitemap;
		this.translate = translate;
		this.setSizeUndefined();
		this.setSpacing(true);
		this.userOption = userOption;
		ID.getId(this);

	}

	protected abstract void moveToNavigationState();

	protected void organiseButtons(List<UserSitemapNode> nodeList) {
		if (usesSort) {
			boolean sorted = userOption.getOptionAsBoolean(this.getClass().getSimpleName(), sortedOpt, true);
			// which order, sorted or insertion?
			new NodeSorter(nodeList, sorted).sort();

		}
		int maxIndex = (nodeList.size() > buttons.size() ? nodeList.size() : buttons.size());
		for (int i = 0; i < maxIndex; i++) {
			// nothing left in chain
			if (i + 1 > nodeList.size()) {
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
				setupButton(button, nodeList.get(i));
			}

		}
	}

	protected NavigationButton createButton() {
		NavigationButton button = new NavigationButton();
		button.addStyleName(BaseTheme.BUTTON_LINK);
		button.addClickListener(this);
		buttons.add(button);
		String id = ID.getIdIndex(buttons.size() - 1, this, button);
		button.setId(id);
		this.addComponent(button);
		return button;
	}

	private void setupButton(NavigationButton button, UserSitemapNode sitemapNode) {

		button.setNode(sitemapNode);
		button.setVisible(true);

	}

	@Override
	public void localeChanged(Locale toLocale) {
		for (NavigationButton button : buttons) {
			I18NKey<?> key = button.getNode().getLabelKey();
			button.setCaption(translate.from(key, toLocale));
		}
	}

	@Override
	public boolean beforeViewChange(V7ViewChangeEvent event) {
		// do nothing
		return true;
	}

	@Override
	public void afterViewChange(V7ViewChangeEvent event) {
		moveToNavigationState();
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

	public Translate getTranslate() {
		return translate;
	}

}
