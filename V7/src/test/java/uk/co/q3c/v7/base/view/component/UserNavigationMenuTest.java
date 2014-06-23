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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.ui.MenuBar.MenuItem;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.*;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.shiro.PageAccessController;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.status.UserStatus;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.*;

import java.text.Collator;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class UserNavigationMenuTest {

	@Inject
	MasterSitemap masterSitemap;

	@Mock
	PageAccessController pageAccessController;

	@Mock
	Subject subject;

	@Mock
	SubjectProvider subjectProvider;

	@Inject
	CurrentLocale currentLocale;

	@Mock
	UserOption userOption;

	Locale locale;

	@Mock
	V7Navigator navigator;

	@Mock
	UserStatus userStatus;

	UserSitemap userSitemap;

	@Inject
	MockUserSitemap mus;

	@Inject
	Translate translate;

	UserNavigationMenu menu;

	Collator collator;

	private MasterSitemapNode privateHomeNode;

	private MasterSitemapNode publicHomeNode;

	private MasterSitemapNode privateChildNode1;

	private MasterSitemapNode privateChildNode2;

	private MasterSitemapNode publicChildNode1;

	private MasterSitemapNode publicChildNode2;

	@Before
	public void setup() {
		menu = createMenu();
		locale = currentLocale.getLocale();
		collator = Collator.getInstance();
		buildSitemap();
		when(subjectProvider.get()).thenReturn(subject);
		when(pageAccessController.isAuthorised(subject, privateHomeNode)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, privateChildNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, privateChildNode2)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, publicHomeNode)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, publicChildNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, publicChildNode2)).thenReturn(true);

	}

	@Test
	public void build_sorted() {

		// given
		when(
				userOption.getOptionAsBoolean(UserNavigationMenu.class.getSimpleName(), UserNavigationMenu.sortedOpt,
						true)).thenReturn(true);
		// when
		menu = createMenu();
		// then
		assertThat(menu.getItems()).hasSize(2);
		MenuItem m0 = menu.getItems().get(0);
		MenuItem m1 = menu.getItems().get(1);

		checkItem(m0, "Private", privateHomeNode, 2, false);
		MenuItem m0_0 = m0.getChildren().get(0);
		checkItem(m0_0, "Small", privateChildNode1, 0, true);
		MenuItem m0_1 = m0.getChildren().get(1);
		checkItem(m0_1, "Splash", privateChildNode2, 0, true);

		assertThat(m1.getChildren()).hasSize(2);
		assertThat(m1.getText()).isEqualTo("Public");
		// ## labels correct
		// ##commands valid

	}

	private void checkItem(MenuItem menuItem, String label, SitemapNode node, int childCount, boolean hasCommand) {
		if (childCount == 0) {
			assertThat(menuItem.getChildren()).isNull();
		} else {
			assertThat(menuItem.getChildren()).hasSize(childCount);
		}
		assertThat(menuItem.getText()).isEqualTo(label);
		assertThat(menuItem.isVisible()).isTrue();
		if (hasCommand) {
			assertThat(menuItem.getCommand()).isInstanceOf(NavigationCommand.class);
			NavigationCommand command = (NavigationCommand) menuItem.getCommand();
			assertThat(command.getNavigator()).isEqualTo(navigator);
			assertThat(command.getNode()).isEqualTo(node);
		} else {
			assertThat(menuItem.getCommand()).isNull();
		}

	}

	@Test
	public void notShowingLoginLogout() {

		// given
		buildSitemap();
		MasterSitemapNode loginNode = newNode(StandardPageKey.Login, "login");
		masterSitemap.addChild(null, loginNode);
		MasterSitemapNode logoutNode = newNode(StandardPageKey.Logout, "login");
		masterSitemap.addChild(null, logoutNode);
		// when
		menu = createMenu();
		// then
		assertThat(menu.getItems()).hasSize(2);
	}

	@Test
	public void notShowingIfNotAuthorised() {

		// given
		privateChildNode2.setPageAccessControl(PageAccessControl.PERMISSION);
		when(pageAccessController.isAuthorised(subject, privateChildNode2)).thenReturn(false);
		// when
		menu = createMenu();
		// then
		assertThat(menu.getItems()).hasSize(2);
		MenuItem m0 = menu.getItems().get(0);
		MenuItem m1 = menu.getItems().get(1);
		checkItem(m0, "Private", privateHomeNode, 1, false);
		MenuItem m0_0 = m0.getChildren().get(0);
		checkItem(m0_0, "Small", privateChildNode1, 0, true);

		assertThat(m1.getChildren()).hasSize(2);
		assertThat(m1.getText()).isEqualTo("Public");

	}

	private void buildSitemap() {

		privateHomeNode = newNode(LabelKey.Private, "private");
		publicHomeNode = newNode(LabelKey.Public, "public");
		masterSitemap.addChild(null, privateHomeNode);
		masterSitemap.addChild(null, publicHomeNode);

		privateChildNode1 = newNode(LabelKey.Small, "small");
		privateChildNode2 = newNode(LabelKey.Splash, "splash");
		masterSitemap.addChild(privateHomeNode, privateChildNode1);
		masterSitemap.addChild(privateHomeNode, privateChildNode2);

		publicChildNode1 = newNode(LabelKey.Refresh_Account, "refresh-account");
		publicChildNode2 = newNode(LabelKey.Enable_Account, "enable-account");
		masterSitemap.addChild(publicHomeNode, publicChildNode1);
		masterSitemap.addChild(publicHomeNode, publicChildNode2);
	}

	private UserNavigationMenu createMenu() {
		return new UserNavigationMenu(userSitemap, navigator, userOption, subjectProvider, pageAccessController,
				userStatus, currentLocale, translate);
	}

	protected MasterSitemapNode newNode(I18NKey<?> key, String urlSegment) {
		MasterSitemapNode node0 = new MasterSitemapNode();
		node0.setLabelKey(key);
		node0.setUriSegment(urlSegment);
		node0.setViewClass(PublicHomeView.class);
		node0.setPageAccessControl(PageAccessControl.PUBLIC);
		return node0;
	}

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
