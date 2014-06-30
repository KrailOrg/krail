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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.UserSitemapSorters;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionProperty;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.ReferenceUserSitemap;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, UIScopeModule.class, VaadinSessionScopeModule.class })
public class DefaultUserNavigationTreeTest {

	private DefaultUserNavigationTree userNavigationTree;

	@Inject
	ReferenceUserSitemap userSitemap;

	@Inject
	CurrentLocale currentLocale;

	@Inject
	UserSitemapSorters sorters;

	@Mock
	V7Navigator navigator;

	@Inject
	DefaultUserOption userOption;

	DefaultUserNavigationTreeBuilder builder;

	@Before
	public void setUp() throws Exception {
		currentLocale.setLocale(Locale.UK);
		userSitemap.clear();
		userSitemap.populate();
		builder = new DefaultUserNavigationTreeBuilder(userSitemap);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void build() {
		// given

		userNavigationTree = newTree();
		List<UserSitemapNode> expectedNodes = new ArrayList<>(userSitemap.getAllNodes());

		// don't want the logout node
		expectedNodes.remove(userSitemap.logoutNode);

		// when
		userNavigationTree.setMaxDepth(1000);
		// then
		@SuppressWarnings("unchecked")
		List<UserSitemapNode> itemIds = (List<UserSitemapNode>) userNavigationTree.getItemIds();
		assertThat(itemIds).containsAll(expectedNodes);
		// ensure no extra ones, there isn't a containsOnly for a list
		assertThat(itemIds).hasSize(expectedNodes.size());
		assertThat(userNavigationTree.getParent(userSitemap.a11Node)).isEqualTo(userSitemap.a1Node);
		assertThat(userNavigationTree.getItemCaption(userSitemap.a11Node)).isEqualTo("ViewA11");
		assertThat(userNavigationTree.getItemCaption(userSitemap.publicHomeNode)).isEqualTo("Public Home");
	}

	@Test
	public void build_depthLimited() {
		// given
		userNavigationTree = newTree();
		List<UserSitemapNode> expectedNodes = new ArrayList<>(userSitemap.getAllNodes());

		// don't want the logout node
		expectedNodes.remove(userSitemap.logoutNode);
		// these beyond required depth
		expectedNodes.remove(userSitemap.a11Node);
		expectedNodes.remove(userSitemap.b11Node);
		expectedNodes.remove(userSitemap.a1Node);
		expectedNodes.remove(userSitemap.b1Node);

		// when
		userNavigationTree.setMaxDepth(2); // will cause rebuild
		// then
		@SuppressWarnings("unchecked")
		List<UserSitemapNode> itemIds = (List<UserSitemapNode>) userNavigationTree.getItemIds();
		assertThat(itemIds).containsAll(expectedNodes);
		// ensure no extra ones, there isn't a containsOnly for a list
		assertThat(itemIds).hasSize(expectedNodes.size());
	}

	@Test
	public void setMaxDepth() {

		// given
		userNavigationTree = newTree();

		// when
		userNavigationTree.setMaxDepth(3);
		// then
		assertThat(userNavigationTree.getMaxDepth()).isEqualTo(3);
		// userOption has been set
		int result = userOption.getOptionAsInt(DefaultUserNavigationTree.class.getSimpleName(),
				UserOptionProperty.MAX_DEPTH, -1);
		assertThat(result).isEqualTo(3);
	}

	@Test
	public void setMaxDepth_noRebuild() {

		// given
		userNavigationTree = newTree();

		// when
		userNavigationTree.setMaxDepth(2);
		// then
		assertThat(userNavigationTree.getMaxDepth()).isEqualTo(2);
		// userOption has been set
		int result = userOption.getOptionAsInt(DefaultUserNavigationTree.class.getSimpleName(),
				UserOptionProperty.MAX_DEPTH, -1);
		assertThat(result).isEqualTo(2);
	}

	@Test
	public void requiresRebuild() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
		userNavigationTree = newTree();
		userNavigationTree.build();
		// when
		userNavigationTree.setSortAscending(false);
		// then build has happened
		assertThat(userNavigationTree.isRebuildRequired()).isFalse();

		// when
		userNavigationTree.setSortAscending(true, false);
		userNavigationTree.setSortType(SortType.INSERTION, false);
		// then build has not happened
		assertThat(userNavigationTree.isRebuildRequired()).isTrue();
	}

	@Test
	public void localeChange() {

		// given
		userNavigationTree = newTree();
		userNavigationTree.build();

		// when
		currentLocale.setLocale(Locale.GERMANY);
		// then
		assertThat(userNavigationTree.getItemCaption(userSitemap.aNode)).isEqualTo("DE_ViewA");
	}

	@Test
	public void structureChange() {

		// given
		userNavigationTree = newTree();
		userNavigationTree.build();
		userNavigationTree.setSortAscending(false, false);
		// when
		userNavigationTree.structureChanged();
		// then make sure build has been called
		assertThat(userNavigationTree.isRebuildRequired()).isFalse();
	}

	@Test
	public void defaults() {

		// given

		// when
		userNavigationTree = newTree();
		// then
		assertThat(userNavigationTree.isImmediate()).isTrue();
		assertThat(userNavigationTree.getMaxDepth()).isEqualTo(10);
		assertThat(userNavigationTree.isRebuildRequired()).isTrue();

	}

	@Test
	public void userSelection() {

		// given
		userNavigationTree = newTree();
		userNavigationTree.build();
		// when
		userNavigationTree.setValue(userSitemap.a1Node);
		// then
		verify(navigator).navigateTo("public/a/a1");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void sorted() {

		// given
		userNavigationTree = newTree();

		// when
		userNavigationTree.build();
		// then
		Collection<UserSitemapNode> roots = (Collection<UserSitemapNode>) userNavigationTree.getTree().rootItemIds();
		assertThat(roots).containsExactly(userSitemap.privateNode, userSitemap.publicNode);
		Collection<UserSitemapNode> children = (Collection<UserSitemapNode>) userNavigationTree.getTree().getChildren(
				userSitemap.publicNode);
		assertThat(children).containsExactly(userSitemap.loginNode, userSitemap.publicHomeNode, userSitemap.aNode);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void sortSelection() {

		// given
		userNavigationTree = newTree();

		// when alpha ascending (default)
		userNavigationTree.build();

		Collection<UserSitemapNode> roots = (Collection<UserSitemapNode>) userNavigationTree.getTree().rootItemIds();
		assertThat(roots).containsExactly(userSitemap.privateNode, userSitemap.publicNode);
		Collection<UserSitemapNode> children = (Collection<UserSitemapNode>) userNavigationTree.getTree().getChildren(
				userSitemap.publicNode);
		assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedAlphaAscending());

		// when
		userNavigationTree.setSortAscending(false);
		// then
		roots = (Collection<UserSitemapNode>) userNavigationTree.getTree().rootItemIds();
		assertThat(roots).containsExactly(userSitemap.publicNode, userSitemap.privateNode);
		children = (Collection<UserSitemapNode>) userNavigationTree.getTree().getChildren(userSitemap.publicNode);
		assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedAlphaDescending());

		// when
		userNavigationTree.setSortAscending(true);
		userNavigationTree.setSortType(SortType.INSERTION);
		// then
		roots = (Collection<UserSitemapNode>) userNavigationTree.getTree().rootItemIds();
		assertThat(roots).containsExactly(userSitemap.privateNode, userSitemap.publicNode);
		children = (Collection<UserSitemapNode>) userNavigationTree.getTree().getChildren(userSitemap.publicNode);
		assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedInsertionAscending());

		// when
		userNavigationTree.setSortAscending(false);
		userNavigationTree.setSortType(SortType.POSITION);
		// then
		roots = (Collection<UserSitemapNode>) userNavigationTree.getTree().rootItemIds();
		assertThat(roots).containsExactly(userSitemap.privateNode, userSitemap.publicNode);
		children = (Collection<UserSitemapNode>) userNavigationTree.getTree().getChildren(userSitemap.publicNode);
		assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedPositionDescending());

		// when
		userNavigationTree.setSortAscending(false);
		userNavigationTree.setSortType(SortType.INSERTION);
		// then
		roots = (Collection<UserSitemapNode>) userNavigationTree.getTree().rootItemIds();
		assertThat(roots).containsExactly(userSitemap.publicNode, userSitemap.privateNode);
		children = (Collection<UserSitemapNode>) userNavigationTree.getTree().getChildren(userSitemap.publicNode);
		assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedInsertionDescending());

		// when
		userNavigationTree.setSortAscending(true);
		userNavigationTree.setSortType(SortType.POSITION);
		// then
		roots = (Collection<UserSitemapNode>) userNavigationTree.getTree().rootItemIds();
		assertThat(roots).containsExactly(userSitemap.publicNode, userSitemap.privateNode);
		children = (Collection<UserSitemapNode>) userNavigationTree.getTree().getChildren(userSitemap.publicNode);
		assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedPositionAscending());
	}

	private DefaultUserNavigationTree newTree() {
		return new DefaultUserNavigationTree(userSitemap, navigator, userOption, builder, sorters);
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {

				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
				bind(UserSitemapSorters.class).to(DefaultUserSitemapSorters.class);
			}

		};
	}

}
