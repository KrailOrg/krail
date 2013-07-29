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

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import javax.inject.Inject;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.DefaultURIPermissionFactory;
import uk.co.q3c.v7.base.shiro.URIViewPermission;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, UIScopeModule.class })
public class UserNavigationTreeTest extends TestWithSitemap {

	@Inject
	CurrentLocale currentLocale;

	@Mock
	V7Navigator navigator;

	@Mock
	SubjectProvider subjectPro;

	@Mock
	Subject subject;

	@Inject
	Injector injector;

	DefaultURIPermissionFactory uriPermissionFactory;

	@Mock
	BasicUI ui;

	@Override
	@Before
	public void setup() {
		super.setup();
		when(subjectPro.get()).thenReturn(subject);
		when(subject.isPermitted(any(Permission.class))).thenReturn(true);
		when(subject.isPermitted(anyString())).thenReturn(true);
		createUI();
		uriPermissionFactory = injector.getInstance(DefaultURIPermissionFactory.class);
	}

	@Test
	public void emptySitemap() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildSitemap(0);
		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(0);
	}

	@Test
	public void singleBranch() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildSitemap(1);

		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(3);
		assertThat(unt.getItemIds()).containsOnly(newNode1, newNode2, newNode3);
		assertThat(unt.getParent(newNode2)).isEqualTo(newNode1);
		assertThat(unt.getParent(newNode3)).isEqualTo(newNode2);
		assertThat(unt.getParent(newNode1)).isEqualTo(null);
		assertThat(unt.isLeaf(newNode1)).isFalse();
		assertThat(unt.isLeaf(newNode2)).isFalse();
		assertThat(unt.isLeaf(newNode3)).isTrue();

	}

	@Test
	public void multiBranch() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildSitemap(2);

		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(6);
		assertThat(unt.getItemIds()).containsOnly(newNode1, newNode2, newNode3, newNode4, newNode5, newNode6);
		assertThat(unt.getParent(newNode2)).isEqualTo(newNode1);
		assertThat(unt.getParent(newNode3)).isEqualTo(newNode2);
		assertThat(unt.getParent(newNode1)).isEqualTo(null);
		assertThat(unt.getItemCaption(newNode1)).isEqualTo("home");
		assertThat(unt.getItemCaption(newNode2)).isEqualTo("home");

		assertThat(unt.getParent(newNode5)).isEqualTo(newNode4);
		assertThat(unt.getParent(newNode6)).isEqualTo(newNode5);
		assertThat(unt.getParent(newNode4)).isEqualTo(null);

	}

	@Test
	public void setLevel() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildSitemap(2);
		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);
		// then
		assertThat(unt.getMaxLevel()).isEqualTo(-1);
		// when
		unt.setMaxLevel(2);
		// then
		assertThat(unt.getItemIds()).containsOnly(newNode1, newNode2, newNode4, newNode5);
		assertThat(unt.isLeaf(newNode1)).isFalse();
		assertThat(unt.isLeaf(newNode2)).isTrue();

		// when
		unt.setMaxLevel(0);
		// then 0 not allowed
		assertThat(unt.getMaxLevel()).isEqualTo(2);

	}

	@Test
	public void localeUK() {

		// given
		currentLocale.setLocale(Locale.UK);
		buildSitemap(1);

		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);

		// then
		assertThat(unt.getItemCaption(newNode1)).isEqualTo("home");

	}

	@Test
	public void defaults() {

		// given
		buildSitemap(1);
		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);
		// then
		assertThat(unt.isImmediate()).isTrue();

	}

	@Test
	public void userSelection() {

		// given
		buildSitemap(2);
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);
		// when
		unt.setValue(newNode2);
		// then
		verify(navigator).navigateTo("a/a1");
	}

	@Test
	public void localeDE() {

		// given
		currentLocale.setLocale(Locale.GERMAN);
		buildSitemap(1);

		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);

		// then
		assertThat(unt.getItemCaption(newNode1)).isEqualTo("zu Hause");

	}

	/**
	 * https://github.com/davidsowerby/v7/issues/143
	 */
	@Test
	public void contextAware() {

		// given

		URIViewPermission privatePage = uriPermissionFactory.createViewPermission("private");
		URIViewPermission publicPage = uriPermissionFactory.createViewPermission("public");
		buildSitemap(4);

		when(subject.isPermitted(privatePage)).thenReturn(false);
		// represents the case where user not authenticated
		when(subject.isPermitted(publicPage)).thenReturn(false);
		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);
		// then
		assertThat(unt.containsId(newNode1)).isTrue();
		assertThat(unt.containsId(newNode2)).isFalse(); // logout
		assertThat(unt.containsId(newNode3)).isFalse();
		assertThat(unt.containsId(newNode4)).isFalse();

	}

	/**
	 * https://github.com/davidsowerby/v7/issues/148
	 */
	@Test
	public void excludeLogout() {

		// given
		buildSitemap(3);
		// when
		UserNavigationTree unt = new UserNavigationTree(sitemap, currentLocale, navigator, subjectPro,
				uriPermissionFactory);
		// then
		assertThat(unt.getItemIds().size()).isEqualTo(1);

	}

	/**
	 * https://github.com/davidsowerby/v7/issues/133
	 */
	@Test
	public void presentationOrder() {

		// given
		// List<SitemapNodeWrapper> words = new ArrayList<>();
		// words.add(new SitemapNodeWrapper(new SitemapNode(), "peach"));
		// words.add(new SitemapNodeWrapper(new SitemapNode(), "péché"));
		// words.add(new SitemapNodeWrapper(new SitemapNode(), "pêche"));
		// words.add(new SitemapNodeWrapper(new SitemapNode(), "Pêche"));
		// words.add(new SitemapNodeWrapper(new SitemapNode(), "sin"));
		// Collator collator = Collator.getInstance(Locale.UK);
		// collator.setStrength(Collator.TERTIARY);
		// Collections.sort(words, collator);
		// // when
		// for (SitemapNodeWrapper s : words) {
		// System.out.println(s);
		// }
		// System.out.println(" ------------------------------ ");
		// collator = Collator.getInstance(Locale.FRANCE);
		// collator.setStrength(Collator.TERTIARY);
		// Collections.sort(words, collator);
		// // then
		// for (SitemapNodeWrapper s : words) {
		// System.out.println(s);
		// }

	}

	// @SuppressWarnings("deprecation")
	protected ScopedUI createUI() {
		UIKey uiKey = new UIKey(3);
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, uiKey);
		CurrentInstance.set(UI.class, ui);
		when(ui.getInstanceKey()).thenReturn(uiKey);

		return ui;
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
