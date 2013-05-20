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

import uk.co.q3c.v7.base.navigate.SitemapURIConverter;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.I18NListener;
import uk.co.q3c.v7.i18n.I18NTranslator;

import com.vaadin.ui.HorizontalLayout;

public class Breadcrumb extends HorizontalLayout implements I18NListener, V7ViewChangeListener {

	private final List<BreadcrumbStep> steps = new ArrayList<>();
	private final V7Navigator navigator;
	private final SitemapURIConverter converter;

	protected Breadcrumb(V7Navigator navigator, SitemapURIConverter converter) {
		this.navigator = navigator;
		navigator.addViewChangeListener(this);
		this.converter = converter;
		moveToNavigationState();
	}

	private void moveToNavigationState() {
		converter.nodeChainForUri(navigator.getNavigationState());
	}

	@Override
	public void localeChange(I18NTranslator translator) {
		moveToNavigationState();
	}

	@Override
	public boolean beforeViewChange(V7ViewChangeEvent event) {
		// return false;
		throw new RuntimeException("not yet implemented");
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

}
