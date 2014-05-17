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
import java.util.Locale;

import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.NodeSorter;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.StandardPageKey;
import uk.co.q3c.v7.base.shiro.PageAccessController;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.status.UserStatus;
import uk.co.q3c.v7.base.user.status.UserStatusListener;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.LocaleChangeListener;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.vaadin.ui.MenuBar;

public class UserNavigationMenu extends MenuBar implements ApplicationMenu, UserStatusListener, LocaleChangeListener {
	private static Logger log = LoggerFactory.getLogger(UserNavigationMenu.class);
	public static final String sortedOpt = "sorted";
	private final Sitemap sitemap;
	private final V7Navigator navigator;
	private final SubjectProvider subjectProvider;
	private final PageAccessController pageAccessController;
	private final UserOption userOption;
	private boolean sorted;
	private final Translate translate;

	@Inject
	protected UserNavigationMenu(Sitemap sitemap, V7Navigator navigator, UserOption userOption,
			SubjectProvider subjectProvider, PageAccessController pageAccessController, UserStatus userStatus,
			CurrentLocale currentLocale, Translate translate) {
		super();
		this.sitemap = sitemap;
		this.navigator = navigator;
		this.subjectProvider = subjectProvider;
		this.pageAccessController = pageAccessController;
		this.userOption = userOption;
		this.translate = translate;
		userStatus.addListener(this);
		currentLocale.addListener(this);
		setId(ID.getId(this));

		build();
	}

	private void build() {
		this.removeItems();
		List<SitemapNode> roots = sitemap.getRoots();

		// which order, sorted or insertion?
		sorted = userOption.getOptionAsBoolean(this.getClass().getSimpleName(), sortedOpt, true);
		new NodeSorter(roots, sorted).sort();

		Subject subject = subjectProvider.get();
		for (SitemapNode node : roots) {
			if (pageAccessController.isAuthorised(subject, node)) {
				if (node.getLabelKey() != StandardPageKey.Login && node.getLabelKey() != StandardPageKey.Logout) {

					Command command = null;
					// we only attach a command if this is the last item in the chain
					if (sitemap.getChildCount(node) == 0) {
						command = new NavigationCommand(navigator, node);
					}
					String caption = translate.from(node.getLabelKey());
					MenuItem item = this.addItem(caption, command);
					addSubItems(item, node);
				}
			}
		}

	}

	/**
	 * Checks each node to ensure that the Subject has permission to view, and if so, adds it to this menu. Note that if
	 * a node is redirected, its pageAccessControl attribute will have been modified to be the same as the redirect
	 * target by the SitemapChecker.
	 * <p>
	 * Nodes which have a null label key are ignored, as they cannot be displayed. The logout page is never shown. The
	 * login page is only shown if the user has not logged in.
	 *
	 * @param parentNode
	 * @param childNode
	 */
	private void addSubItems(MenuItem item, SitemapNode node) {
		List<SitemapNode> children = sitemap.getChildren(node);

		// which order, sorted or insertion?
		new NodeSorter(children, sorted).sort();

		Subject subject = subjectProvider.get();

		for (SitemapNode childNode : children) {
			if (pageAccessController.isAuthorised(subject, childNode)) {
				Command command = null;
				// we only attach a command if this is the last item in the chain
				if (sitemap.getChildCount(childNode) == 0) {
					command = new NavigationCommand(navigator, childNode);
				}
				String caption = translate.from(childNode.getLabelKey());
				MenuItem subItem = item.addItem(caption, command);
				addSubItems(subItem, childNode);
			}
		}
	}

	@Override
	public void userStatusChanged() {
		build();
	}

	/**
	 * When locale changes the content is rebuilt to ensure that the sort order is correct for the new language
	 */
	@Override
	public void localeChanged(Locale toLocale) {
		log.debug("rebuilding to reflect new locale of {}", toLocale);
		build();

	}

}
