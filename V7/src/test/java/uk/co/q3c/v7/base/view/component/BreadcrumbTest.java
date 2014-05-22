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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.Collator;
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
import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.TestWithSitemap;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.DefaultI18NProcessor;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.I18NProcessor;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class BreadcrumbTest extends TestWithSitemap {

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

	Collator collator;

	@Override
	@Before
	public void setup() {
		super.setup();

	}

	@Test
	public void buildAndViewChange() {

		// given
		buildMasterSitemap(2);
		List<MasterSitemapNode> nodeChain = new ArrayList<>();
		nodeChain.add(masterNode1);
		nodeChain.add(masterNode2);
		nodeChain.add(masterNode3);
		when(sitemap.nodeChainFor(any(MasterSitemapNode.class))).thenReturn(nodeChain);
		when(currentLocale.getLocale()).thenReturn(Locale.UK);
		collator = Collator.getInstance(currentLocale.getLocale());
		masterNode2.setLabelKey(TestLabelKey.Opt);

		// when
		breadcrumb = new DefaultBreadcrumb(navigator, userSitemap, currentLocale, translate, userOption);
		breadcrumb.moveToNavigationState();
		// then
		assertThat(breadcrumb.getButtons().size()).isEqualTo(3);
		assertThat(breadcrumb.getComponentCount()).isEqualTo(3);
		assertThat(breadcrumb.getButtons().get(0).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getButtons().get(1).getCaption()).isEqualTo("option");
		assertThat(breadcrumb.getButtons().get(2).getCaption()).isEqualTo("home");

		assertThat(breadcrumb.getButtons().get(0).getNode()).isEqualTo(masterNode1);
		assertThat(breadcrumb.getButtons().get(1).getNode()).isEqualTo(masterNode2);
		assertThat(breadcrumb.getButtons().get(2).getNode()).isEqualTo(masterNode3);

		assertThat(breadcrumb.getButtons().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(2).isVisible()).isTrue();

		// given
		nodeChain.remove(2);

		// when
		breadcrumb.afterViewChange(null);

		// then
		assertThat(breadcrumb.getButtons().size()).isEqualTo(3);
		assertThat(breadcrumb.getComponentCount()).isEqualTo(3);
		assertThat(breadcrumb.getButtons().get(0).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getButtons().get(1).getCaption()).isEqualTo("option");
		assertThat(breadcrumb.getButtons().get(2).getCaption()).isEqualTo("home");

		assertThat(breadcrumb.getButtons().get(0).getNode()).isEqualTo(masterNode1);
		assertThat(breadcrumb.getButtons().get(1).getNode()).isEqualTo(masterNode2);
		assertThat(breadcrumb.getButtons().get(2).getNode()).isEqualTo(masterNode3);

		assertThat(breadcrumb.getButtons().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(2).isVisible()).isFalse();

		// given
		nodeChain.add(masterNode3);

		// when
		breadcrumb.afterViewChange(null);

		// then
		assertThat(breadcrumb.getButtons().size()).isEqualTo(3);
		assertThat(breadcrumb.getComponentCount()).isEqualTo(3);
		assertThat(breadcrumb.getButtons().get(0).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getButtons().get(1).getCaption()).isEqualTo("option");
		assertThat(breadcrumb.getButtons().get(2).getCaption()).isEqualTo("home");

		assertThat(breadcrumb.getButtons().get(0).getNode()).isEqualTo(masterNode1);
		assertThat(breadcrumb.getButtons().get(1).getNode()).isEqualTo(masterNode2);
		assertThat(breadcrumb.getButtons().get(2).getNode()).isEqualTo(masterNode3);

		assertThat(breadcrumb.getButtons().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(2).isVisible()).isTrue();

		// given
		nodeChain.remove(2);
		breadcrumb.afterViewChange(null);
		masterNode7 = newNode("new");
		sitemap.addChild(masterNode3, masterNode7);
		nodeChain.add(masterNode3);
		nodeChain.add(masterNode7);

		// when
		breadcrumb.afterViewChange(null);

		// then
		assertThat(breadcrumb.getButtons().size()).isEqualTo(4);
		assertThat(breadcrumb.getComponentCount()).isEqualTo(4);
		assertThat(breadcrumb.getButtons().get(0).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getButtons().get(1).getCaption()).isEqualTo("option");
		assertThat(breadcrumb.getButtons().get(2).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getButtons().get(3).getCaption()).isEqualTo("home");

		assertThat(breadcrumb.getButtons().get(0).getNode()).isEqualTo(masterNode1);
		assertThat(breadcrumb.getButtons().get(1).getNode()).isEqualTo(masterNode2);
		assertThat(breadcrumb.getButtons().get(2).getNode()).isEqualTo(masterNode3);
		assertThat(breadcrumb.getButtons().get(3).getNode()).isEqualTo(masterNode7);

		assertThat(breadcrumb.getButtons().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(2).isVisible()).isTrue();
		assertThat(breadcrumb.getButtons().get(3).isVisible()).isTrue();

		// given
		NavigationButton step = breadcrumb.getButtons().get(2);
		// when button clicked
		step.click();
		// then
		verify(navigator).navigateTo(step.getNode());
	}

	@Override
	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}
}
