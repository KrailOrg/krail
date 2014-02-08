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

import java.util.List;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NListener;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.vaadin.ui.Button;

@UIScoped
public class DefaultBreadcrumb extends NavigationButtonPanel implements I18NListener, V7ViewChangeListener,
		Button.ClickListener, Breadcrumb {

	@Inject
	protected DefaultBreadcrumb(V7Navigator navigator, Sitemap sitemap, CurrentLocale currentLocale, Translate translate) {
		super(navigator, sitemap, currentLocale, translate);

	}

	@Override
	protected void moveToNavigationState() {
		List<SitemapNode> nodeChain = getSitemap().nodeChainFor(getNavigator().getCurrentNode());
		organiseButtons(nodeChain);

	}

}
