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
package uk.co.q3c.v7.base.navigate.sitemap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapBuilderTest.TestVaadinSessionScopeModule;
import uk.co.q3c.v7.base.shiro.VaadinSessionProvider;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinSession;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, TestVaadinSessionScopeModule.class })
public class UserSitemapBuilderTest extends TestWithSitemap {

	@Mock
	VaadinSessionProvider mockVaadinSessionProvider;

	@Mock
	VaadinSession vaadinSession;

	// Overrides the VaadinSeesionProvider so we can use a mock
	public static class TestVaadinSessionScopeModule extends VaadinSessionScopeModule {
		@Override
		protected void bindVaadinSessionProvider() {
		}
	}

	@Override
	@Before
	public void setup() {
		super.setup();
		when(mockVaadinSessionProvider.get()).thenReturn(vaadinSession);
	}

	@Test
	public void pageNotAuthorised() {
		// given
		when(subject.isAuthenticated()).thenReturn(false);
		buildMasterSitemap(8);
		when(pageAccessController.isAuthorised(subject, masterNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(false);
		when(pageAccessController.isAuthorised(subject, masterNode3)).thenReturn(true);

		// when
		createUserSitemap();

		// then
		assertThat(pageAccessController.isAuthorised(subject, masterNode1)).isEqualTo(true);
		assertThat(pageAccessController.isAuthorised(subject, masterNode2)).isEqualTo(false);
		assertThat(pageAccessController.isAuthorised(subject, masterNode3)).isEqualTo(true);

		assertThat(userSitemap.getAllNodes()).hasSize(2);
		assertThat(userSitemapContains(masterNode1)).isTrue();
		assertThat(userSitemapContains(masterNode2)).isFalse();
		assertThat(userSitemapContains(masterNode3)).isTrue();

		UserSitemapNode userNode3 = userSitemap.userNodeFor(masterNode3);
		assertThat(userNode3).isNotNull();
		assertThat(userSitemap.getParent(userNode3)).isNotNull();
		UserSitemapNode userNode3Parent = userSitemap.getParent(userNode3);
		assertThat(userNode3Parent.getMasterNode()).isEqualTo(masterNode1);
	}

	@Test
	public void redirects() {
		// given
		buildMasterSitemap(8);
		when(pageAccessController.isAuthorised(subject, masterNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(false);
		when(pageAccessController.isAuthorised(subject, masterNode3)).thenReturn(true);
		// when
		createUserSitemap();
		// then
		assertThat(userSitemap.getRedirects().keySet()).containsOnly("a");
	}

	@Test
	public void uriMap() {
		// given
		buildMasterSitemap(8);
		when(pageAccessController.isAuthorised(subject, masterNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(false);
		when(pageAccessController.isAuthorised(subject, masterNode3)).thenReturn(true);
		createUserSitemap();
		// when

		// then
		assertThat(userSitemap.uriMap.keySet()).containsOnly("1", "1/3");
	}

	private boolean userSitemapContains(SitemapNode masterNode) {
		return userSitemap.userNodeFor(masterNode) != null;
	}

	@Test
	public void userStatusChanged() {
		// given
		buildMasterSitemap(8);
		when(pageAccessController.isAuthorised(subject, masterNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(false);
		when(pageAccessController.isAuthorised(subject, masterNode3)).thenReturn(true);
		createUserSitemap();
		// when
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(true);
		masterSitemap.addRedirect("bb", "2");
		userSitemapBuilder.userStatusChanged();
		// then
		assertThat(userSitemap.uriMap.keySet()).containsOnly("1", "1/3", "2");
		assertThat(userSitemap.getRedirects().keySet()).containsOnly("a", "bb");
	}

	@Test
	public void standardPages() {
		// given
		buildMasterSitemap(8);
		when(pageAccessController.isAuthorised(subject, masterNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(false);
		when(pageAccessController.isAuthorised(subject, masterNode3)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, privateHomeNode)).thenReturn(false);
		when(pageAccessController.isAuthorised(subject, publicHomeNode)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, loginNode)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, logoutNode)).thenReturn(true);

		// when
		createUserSitemap();
		// then
		// never shown but needs to be in userSitemap to navigate to
		assertThat(userSitemap.standardPageNode(StandardPageKey.Logout)).isNotNull();
		assertThat(userSitemap.standardPageNode(StandardPageKey.Private_Home)).isNull();
		assertThat(userSitemap.standardPageNode(StandardPageKey.Public_Home)).isNotNull();
		assertThat(userSitemap.standardPageNode(StandardPageKey.Login)).isNotNull();

	}

	@Test
	public void translationAndLocaleChange() {
		// given
		buildMasterSitemap(8);
		when(pageAccessController.isAuthorised(subject, masterNode1)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, masterNode2)).thenReturn(false);
		when(pageAccessController.isAuthorised(subject, masterNode3)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, privateHomeNode)).thenReturn(false);
		when(pageAccessController.isAuthorised(subject, publicHomeNode)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, loginNode)).thenReturn(true);
		when(pageAccessController.isAuthorised(subject, logoutNode)).thenReturn(true);

		// when
		createUserSitemap();
		// then
		assertThat(userNode1.getCollationKey()).isNotNull();
		assertThat(userNode1.getLabel()).isEqualTo("Yes");
		assertThat(userNode3.getCollationKey()).isNotNull();
		assertThat(userNode3.getLabel()).isEqualTo("Enable Account");

		// when
		currentLocale.setLocale(Locale.GERMANY);
		assertThat(userNode1.getCollationKey()).isNotNull();
		assertThat(userNode1.getLabel()).isEqualTo("Ja");
		assertThat(userNode3.getCollationKey()).isNotNull();
		assertThat(userNode3.getLabel()).isEqualTo("Konto Aktivieren");
	}

	@Override
	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);

			}

		};
	}

	@ModuleProvider
	protected AbstractModule moduleProvider2() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(VaadinSessionProvider.class).toInstance(mockVaadinSessionProvider);
			}

		};
	}
}
