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

import java.text.Collator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemapNode;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.i18n.CurrentLocale;
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
public class BreadcrumbTest {

	DefaultBreadcrumb breadcrumb;

	@Mock
	V7Navigator navigator;

	@Mock
	CurrentLocale currentLocale;

	@Mock
	MasterSitemap sitemap;

	@Inject
	Translate translate;

	MasterSitemapNode masterNode7;

	@Mock
	UserOption userOption;

	@Inject
	ReferenceUserSitemap userSitemap;

	Collator collator;

	@Before
	public void setup() {
		userSitemap.populate();
		createBreadcrumb();

	}

	@Test
	public void buildAndViewChange() {

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.a11Node);
		// when
		breadcrumb.moveToNavigationState();
		// then
		assertThat(breadcrumb.getButtons().size()).isEqualTo(4);
		assertThat(breadcrumb.getComponentCount()).isEqualTo(4);
		assertThat(breadcrumb.getButtons().get(0).getCaption()).isEqualTo(userSitemap.publicNode.getLabel());
		assertThat(breadcrumb.getButtons().get(1).getCaption()).isEqualTo(userSitemap.aNode.getLabel());
		assertThat(breadcrumb.getButtons().get(2).getCaption()).isEqualTo(userSitemap.a1Node.getLabel());
		assertThat(breadcrumb.getButtons().get(3).getCaption()).isEqualTo(userSitemap.a11Node.getLabel());

		assertThat(breadcrumb.getButtons().get(0).getNode()).isEqualTo(userSitemap.publicNode);
		assertThat(breadcrumb.getButtons().get(1).getNode()).isEqualTo(userSitemap.aNode);
		assertThat(breadcrumb.getButtons().get(2).getNode()).isEqualTo(userSitemap.a1Node);
		assertThat(breadcrumb.getButtons().get(3).getNode()).isEqualTo(userSitemap.a11Node);

		assertThat(breadcrumb.getButtons().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(2).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(3).isVisible()).isTrue();

		// given
		when(navigator.getCurrentNode()).thenReturn(userSitemap.b1Node);
		// when
		breadcrumb.afterViewChange(null);
		// then
		assertThat(breadcrumb.getButtons().size()).isEqualTo(4);
		assertThat(breadcrumb.getComponentCount()).isEqualTo(4);
		assertThat(breadcrumb.getButtons().get(0).getCaption()).isEqualTo(userSitemap.privateNode.getLabel());
		assertThat(breadcrumb.getButtons().get(1).getCaption()).isEqualTo(userSitemap.bNode.getLabel());
		assertThat(breadcrumb.getButtons().get(2).getCaption()).isEqualTo(userSitemap.b1Node.getLabel());

		assertThat(breadcrumb.getButtons().get(0).getNode()).isEqualTo(userSitemap.privateNode);
		assertThat(breadcrumb.getButtons().get(1).getNode()).isEqualTo(userSitemap.bNode);
		assertThat(breadcrumb.getButtons().get(2).getNode()).isEqualTo(userSitemap.b1Node);

		assertThat(breadcrumb.getButtons().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(2).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(3).isVisible()).isFalse();

		// given
		NavigationButton step = breadcrumb.getButtons().get(1);
		// when button clicked
		step.click();
		// then
		verify(navigator).navigateTo(step.getNode());
	}

	private void createBreadcrumb() {
		breadcrumb = new DefaultBreadcrumb(navigator, userSitemap);
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
			}

		};
	}
}
