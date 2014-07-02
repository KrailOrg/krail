/*
 * Copyright (C) 2014 David Sowerby
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.i18n.DefaultCurrentLocale;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.ReferenceUserSitemap;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class DefaultSubpagePanelTest {

	DefaultSubpagePanel panel;

	@Inject
	ReferenceUserSitemap userSitemap;

	@Mock
	V7Navigator navigator;

	@Inject
	DefaultCurrentLocale currentLocale;

	@Mock
	Translate translate;

	@Inject
	UserOption userOption;

	@Inject
	DefaultUserSitemapSorters sorters;

	@Mock
	V7ViewChangeEvent event;

	@Before
	public void setup() {
		userOption.clear();
		currentLocale.setLocale(Locale.UK, false);
		userSitemap.populate();
		panel = new DefaultSubpagePanel(navigator, userSitemap, userOption, sorters, currentLocale);
	}

	@Test
	public void leaf() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.a11Node);
		// when
		panel.moveToNavigationState();
		// then
		List<NavigationButton> buttons = panel.getButtons();
		assertThat(buttons).hasSize(0);
	}

	@Test
	public void multi() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		// when
		panel.moveToNavigationState();
		// then
		List<UserSitemapNode> nodes = nodesFromButtons(panel.getButtons());
		List<UserSitemapNode> expected = userSitemap.publicSortedAlphaAscending();
		expected.add(userSitemap.logoutNode); // not filtered
		assertThat(nodes).containsAll(expected);
		assertThat(nodes).hasSameSizeAs(expected);
	}

	@Test
	public void options() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		panel.moveToNavigationState();
		// when
		panel.setSortType(SortType.INSERTION);
		panel.setSortAscending(true);
		// then
		assertThat(panel.getSortAscending()).isTrue();
		assertThat(panel.getSortType()).isEqualTo(SortType.INSERTION);
	}

	@Test
	public void multi_filtered() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		LogoutPageFilter filter = new LogoutPageFilter();
		panel.addFilter(filter);
		// when
		panel.moveToNavigationState();
		// then
		List<UserSitemapNode> nodes = nodesFromButtons(panel.getButtons());
		assertThat(nodes).containsOnly(userSitemap.loginNode, userSitemap.aNode, userSitemap.publicHomeNode);
		// when
		panel.removeFilter(filter);
		panel.moveToNavigationState();
		// then
		nodes = nodesFromButtons(panel.getButtons());
		assertThat(nodes).containsOnly(userSitemap.loginNode, userSitemap.aNode, userSitemap.publicHomeNode,
				userSitemap.logoutNode);
	}

	@Test
	public void localeChanged() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		LogoutPageFilter filter = new LogoutPageFilter();
		panel.addFilter(filter);

		// when
		panel.moveToNavigationState();
		// then
		assertThat(panel.getButtons().get(0).getCaption()).isEqualTo("Log In");

		// when
		currentLocale.setLocale(Locale.GERMANY);
		// then
		assertThat(panel.getButtons().get(0).getCaption()).isEqualTo("Einloggen");
	}

	@Test
	public void sortSelection() {

		// given
		assertThat(panel.getSortAscending()).isTrue();
		assertThat(panel.getSortType()).isEqualTo(SortType.ALPHA);
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		LogoutPageFilter filter = new LogoutPageFilter();
		panel.addFilter(filter);
		// when
		panel.moveToNavigationState();
		// then
		List<UserSitemapNode> nodes = nodesFromButtons(panel.getButtons());
		assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedAlphaAscending());
		// when
		panel.setSortAscending(false);
		// then
		nodes = nodesFromButtons(panel.getButtons());
		assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedAlphaDescending());
		// when
		panel.setSortAscending(true, false);
		panel.setSortType(SortType.INSERTION);
		// then
		nodes = nodesFromButtons(panel.getButtons());
		assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedInsertionAscending());
		// when
		panel.setSortAscending(false);
		// then
		nodes = nodesFromButtons(panel.getButtons());
		assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedInsertionDescending());
		// when
		panel.setSortAscending(true, false);
		panel.setSortType(SortType.POSITION);
		// then
		nodes = nodesFromButtons(panel.getButtons());
		assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedPositionAscending());
		// when
		panel.setSortAscending(false);
		// then
		nodes = nodesFromButtons(panel.getButtons());
		assertThat(nodes).containsExactlyElementsOf(userSitemap.publicSortedPositionDescending());
	}

	@Test
	public void requiresRebuild() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		LogoutPageFilter filter = new LogoutPageFilter();
		panel.addFilter(filter);
		panel.moveToNavigationState();
		// when
		panel.setSortAscending(false);
		// then build has happened
		assertThat(panel.isRebuildRequired()).isFalse();

		// when
		panel.setSortAscending(true, false);
		panel.setSortType(SortType.INSERTION, false);
		// then build has not happened
		assertThat(panel.isRebuildRequired()).isTrue();
	}

	@Test
	public void structureChange() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		LogoutPageFilter filter = new LogoutPageFilter();
		panel.addFilter(filter);
		panel.moveToNavigationState();
		// when
		panel.structureChanged();
		// then make sure build has been called
		assertThat(panel.isRebuildRequired()).isFalse();
	}

	@Test
	public void afterViewChange() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		LogoutPageFilter filter = new LogoutPageFilter();
		panel.addFilter(filter);
		panel.moveToNavigationState();
		// when
		panel.afterViewChange(event);
		// then
		assertThat(panel.rebuildRequired).isFalse();
	}

	/**
	 * There may be more buttons than nodes, as buttons are re-used and just made not visible of not needed, so only
	 * copy nodes from buttons which are visible.
	 * 
	 * @param buttons
	 * @return
	 */
	List<UserSitemapNode> nodesFromButtons(List<NavigationButton> buttons) {
		List<UserSitemapNode> nodes = new ArrayList<>();
		for (NavigationButton button : buttons) {
			if (button.isVisible()) {
				nodes.add(button.getNode());
			}
		}
		return nodes;
	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				// bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
				// bind(UserSitemap.class).to(DefaultUserSitemap.class);
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
			}

		};
	}
}
