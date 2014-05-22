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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;

import org.apache.shiro.authz.Permission;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.TestWithSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.shiro.PagePermission;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.user.opt.UserOptionProperty;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, UIScopeModule.class, VaadinSessionScopeModule.class })
public class DefaultUserNavigationTreeTest extends TestWithSitemap {

	@Mock
	V7Navigator navigator;

	@Inject
	Injector injector;

	@Mock
	BasicUI ui;

	@Mock
	NavigationState navigationState;

	@Override
	@Before
	public void setup() {
		super.setup();
		when(subject.isPermitted(any(Permission.class))).thenReturn(true);
		when(subject.isPermitted(anyString())).thenReturn(true);

		when(navigationState.getVirtualPage()).thenReturn("private/wiggly");
		when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
		when(navigator.getCurrentNavigationState()).thenReturn(navigationState);
		createUI();
	}

	@Test
	public void emptySitemap() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildMasterSitemap(0);
		createUserSitemap();
		// when
		DefaultUserNavigationTree unt = newTree();
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(0);
	}

	@Test
	public void singleBranch() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildMasterSitemap(1);
		allowAllNodes();
		createUserSitemap();

		// when
		DefaultUserNavigationTree unt = newTree();
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(3);
		@SuppressWarnings("unchecked")
		List<UserSitemapNode> nodes = (List<UserSitemapNode>) unt.getItemIds();
		assertThat(nodes).containsOnly(userNode1, userNode2, userNode3);
		assertThat(unt.getParent(userNode1)).isEqualTo(null);
		assertThat(unt.getParent(userNode2)).isEqualTo(userNode1);
		assertThat(unt.getParent(userNode3)).isEqualTo(userNode2);

		assertThat(unt.isLeaf(userNode1)).isFalse();
		assertThat(unt.isLeaf(userNode2)).isFalse();
		assertThat(unt.isLeaf(userNode3)).isTrue();

	}

	@Test
	public void multiBranch() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildMasterSitemap(2);
		allowAllNodes();
		createUserSitemap();
		// when
		DefaultUserNavigationTree unt = newTree();
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(6);
		@SuppressWarnings("unchecked")
		List<UserSitemapNode> nodes = (List<UserSitemapNode>) unt.getItemIds();
		assertThat(nodes).containsOnly(userNode1, userNode2, userNode3, userNode4, userNode5, userNode6);
		assertThat(unt.getParent(userNode2)).isEqualTo(userNode1);
		assertThat(unt.getParent(userNode3)).isEqualTo(userNode2);
		assertThat(unt.getParent(userNode1)).isEqualTo(null);
		assertThat(unt.getItemCaption(userNode1)).isEqualTo("home");
		assertThat(unt.getItemCaption(userNode2)).isEqualTo("home");

		assertThat(unt.getParent(userNode5)).isEqualTo(userNode4);
		assertThat(unt.getParent(userNode6)).isEqualTo(userNode5);
		assertThat(unt.getParent(userNode4)).isEqualTo(null);

	}

	/**
	 * When a node label key is missing, it cannot be displayed - and therefore neither can its children
	 */
	@Test
	public void multiBranch_nullLabelKey() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildMasterSitemap(5);
		allowAllNodes();
		createUserSitemap();
		// when
		DefaultUserNavigationTree unt = newTree();
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(3);
		@SuppressWarnings("unchecked")
		List<UserSitemapNode> nodes = (List<UserSitemapNode>) unt.getItemIds();
		assertThat(nodes).containsOnly(userNode4, userNode5, userNode6);

		assertThat(unt.getParent(userNode5)).isEqualTo(userNode4);
		assertThat(unt.getParent(userNode6)).isEqualTo(userNode5);
		assertThat(unt.getParent(userNode4)).isEqualTo(null);

	}

	@Test
	public void multiBranch_oneRequiresPermission() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildMasterSitemap(6);
		when(pageAccessController.isAuthorised(subject, masterNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode3)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode4)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode5)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode6)).thenReturn(false);
		createUserSitemap();
		// when
		DefaultUserNavigationTree unt = newTree();
		// then
		// assertThat(unt.getItemIds().size()).isEqualTo(5);
		@SuppressWarnings("unchecked")
		List<UserSitemapNode> nodes = (List<UserSitemapNode>) unt.getItemIds();
		assertThat(nodes).containsOnly(userNode1, userNode2, userNode3, userNode4, userNode5);
		assertThat(unt.getParent(userNode2)).isEqualTo(userNode1);
		assertThat(unt.getParent(userNode3)).isEqualTo(userNode2);
		assertThat(unt.getParent(userNode1)).isEqualTo(null);
		assertThat(unt.getItemCaption(userNode1)).isEqualTo("Yes");
		assertThat(unt.getItemCaption(userNode2)).isEqualTo("home");

		assertThat(unt.getParent(userNode5)).isEqualTo(userNode4);
		assertThat(unt.getParent(userNode4)).isEqualTo(null);

	}

	@Test
	public void setLevel() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildMasterSitemap(2);
		allowAllNodes();
		createUserSitemap();
		System.out.println(userSitemap);
		// when
		DefaultUserNavigationTree unt = newTree();
		// then
		assertThat(unt.getMaxDepth()).isEqualTo(-1);
		// when
		unt.setMaxDepth(2);
		// then
		@SuppressWarnings("unchecked")
		List<UserSitemapNode> nodes = (List<UserSitemapNode>) unt.getItemIds();
		for (UserSitemapNode userNode : nodes) {
			System.out.println(userNode.getMasterNode().getId());
		}
		assertThat(nodes).containsOnly(userNode1, userNode2, userNode4, userNode5);
		assertThat(unt.isLeaf(userNode1)).isFalse();
		assertThat(unt.isLeaf(userNode2)).isTrue();

		// when
		unt.setMaxDepth(0);
		// then 0 not allowed
		assertThat(unt.getMaxDepth()).isEqualTo(2);
		assertThat(
				userOption.getOptionAsInt(DefaultUserNavigationTree.class.getSimpleName(),
						UserOptionProperty.MAX_DEPTH, 1000)).isEqualTo(2);

	}

	@Test
	public void localeUK() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildMasterSitemap(1);
		allowAllNodes();
		createUserSitemap();

		// when
		DefaultUserNavigationTree unt = newTree();

		// then
		assertThat(unt.getItemCaption(userNode1)).isEqualTo("home");

	}

	@Test
	public void defaults() {

		// given
		buildMasterSitemap(1);
		allowAllNodes();
		createUserSitemap();
		// when
		DefaultUserNavigationTree unt = newTree();
		// then
		assertThat(unt.isImmediate()).isTrue();

	}

	@Test
	public void userSelection() {

		// given
		buildMasterSitemap(2);
		allowAllNodes();
		createUserSitemap();
		DefaultUserNavigationTree unt = newTree();
		// when
		unt.setValue(userNode2);
		// then
		verify(navigator).navigateTo("a/a1");
	}

	@Test
	public void localeDE() {

		// given
		currentLocale.setLocale(Locale.GERMAN);
		buildMasterSitemap(1);
		allowAllNodes();
		createUserSitemap();
		// when
		DefaultUserNavigationTree unt = newTree();

		// then
		assertThat(userNode1).isNotNull();
		assertThat(unt.getItemCaption(userNode1)).isEqualTo("zu Hause");

	}

	/**
	 * https://github.com/davidsowerby/v7/issues/148
	 */
	@Test
	public void excludeLogout() {

		// given
		buildMasterSitemap(3);
		allowAllNodes();
		createUserSitemap();
		// when
		DefaultUserNavigationTree unt = newTree();
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(1);

	}

	protected ScopedUI createUI() {
		UIKey uiKey = new UIKey(3);
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, uiKey);
		CurrentInstance.set(UI.class, ui);
		when(ui.getInstanceKey()).thenReturn(uiKey);

		return ui;
	}

	private void allowAllNodes() {
		when(pageAccessController.isAuthorised(subject, masterNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode3)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode4)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode5)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode6)).thenReturn(true);

	}

	private DefaultUserNavigationTree newTree() {
		return new DefaultUserNavigationTree(userSitemap, navigator, userOption);
	}

	@Override
	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {

				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}
}
