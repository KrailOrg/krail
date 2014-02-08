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

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.I18NListener;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

@UIScoped
public class DefaultBreadcrumb extends HorizontalLayout implements I18NListener, V7ViewChangeListener,
		Button.ClickListener, Breadcrumb {

	private final List<BreadcrumbStep> steps = new ArrayList<>();
	private final V7Navigator navigator;
	private final Sitemap sitemap;
	private final Translate translate;

	@Inject
	protected DefaultBreadcrumb(V7Navigator navigator, Sitemap sitemap, CurrentLocale currentLocale, Translate translate) {
		this.navigator = navigator;
		navigator.addViewChangeListener(this);
		this.sitemap = sitemap;
		this.translate = translate;
		this.setSizeUndefined();
		this.setSpacing(true);
		ID.getId(this);

	}

	protected void moveToNavigationState() {
		List<SitemapNode> nodeChain = sitemap.nodeChainFor(navigator.getCurrentNode());
		int maxIndex = (nodeChain.size() > steps.size() ? nodeChain.size() : steps.size());
		for (int i = 0; i < maxIndex; i++) {
			// nothing left in chain
			if (i + 1 > nodeChain.size()) {
				// but steps still exist
				if (i < steps.size()) {
					steps.get(i).setVisible(false);
				}
			} else {
				// chain continues
				BreadcrumbStep step = null;
				// steps still exist, re-use
				if (i < steps.size()) {
					step = steps.get(i);
				} else {
					// create step
					step = new BreadcrumbStep();
					step.addStyleName(BaseTheme.BUTTON_LINK);
					step.addClickListener(this);
					steps.add(step);
					String id = ID.getIdIndex(steps.size() - 1, this, step);
					step.setId(id);
					this.addComponent(step);
				}
				setupStep(step, nodeChain.get(i));
			}

		}
	}

	private void setupStep(BreadcrumbStep step, SitemapNode sitemapNode) {

		step.setNode(sitemapNode);
		step.setVisible(true);

	}

	@Override
	public void localeChange(I18NTranslator translator) {
		for (BreadcrumbStep step : steps) {
			I18NKey<?> key = step.getNode().getLabelKey();
			step.setCaption(translate.from(key, translator.getLocale()));
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

	public List<BreadcrumbStep> getSteps() {
		return steps;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		BreadcrumbStep step = (BreadcrumbStep) event.getButton();
		navigator.navigateTo(step.getNode());

	}

}
