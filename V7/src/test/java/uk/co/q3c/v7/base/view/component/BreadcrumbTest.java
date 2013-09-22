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

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapURIConverter;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class BreadcrumbTest extends TestWithSitemap {

	DefaultBreadcrumb breadcrumb;

	@Mock
	V7Navigator navigator;

	@Mock
	SitemapURIConverter converter;

	@Mock
	CurrentLocale currentLocale;

	@Inject
	Translate translate;

	SitemapNode newNode7;

	Collator collator;

	@Override
	@Before
	public void setup() {
		super.setup();

	}

	@Test
	public void buildAndViewChange() {

		// given
		buildSitemap(2);
		List<SitemapNode> nodeChain = new ArrayList<>();
		nodeChain.add(newNode1);
		nodeChain.add(newNode2);
		nodeChain.add(newNode3);
		when(converter.nodeChainForUri(anyString(), eq(true))).thenReturn(nodeChain);
		when(currentLocale.getLocale()).thenReturn(Locale.UK);
		collator = Collator.getInstance(currentLocale.getLocale());
		newNode2.setLabelKey(TestLabelKey.Opt, currentLocale.getLocale(), collator);

		// when
		breadcrumb = new DefaultBreadcrumb(navigator, converter, currentLocale, translate);
		breadcrumb.moveToNavigationState();
		// then
		assertThat(breadcrumb.getSteps().size()).isEqualTo(3);
		assertThat(breadcrumb.getSteps().get(0).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getSteps().get(1).getCaption()).isEqualTo("option");
		assertThat(breadcrumb.getSteps().get(2).getCaption()).isEqualTo("home");

		assertThat(breadcrumb.getSteps().get(0).getNode()).isEqualTo(newNode1);
		assertThat(breadcrumb.getSteps().get(1).getNode()).isEqualTo(newNode2);
		assertThat(breadcrumb.getSteps().get(2).getNode()).isEqualTo(newNode3);

		assertThat(breadcrumb.getSteps().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(2).isVisible()).isTrue();

		// given
		nodeChain.remove(2);

		// when
		breadcrumb.afterViewChange(null);

		// then
		assertThat(breadcrumb.getSteps().size()).isEqualTo(3);
		assertThat(breadcrumb.getSteps().get(0).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getSteps().get(1).getCaption()).isEqualTo("option");
		assertThat(breadcrumb.getSteps().get(2).getCaption()).isEqualTo("home");

		assertThat(breadcrumb.getSteps().get(0).getNode()).isEqualTo(newNode1);
		assertThat(breadcrumb.getSteps().get(1).getNode()).isEqualTo(newNode2);
		assertThat(breadcrumb.getSteps().get(2).getNode()).isEqualTo(newNode3);

		assertThat(breadcrumb.getSteps().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(2).isVisible()).isFalse();

		// given
		nodeChain.add(newNode3);

		// when
		breadcrumb.afterViewChange(null);

		// then
		assertThat(breadcrumb.getSteps().size()).isEqualTo(3);
		assertThat(breadcrumb.getSteps().get(0).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getSteps().get(1).getCaption()).isEqualTo("option");
		assertThat(breadcrumb.getSteps().get(2).getCaption()).isEqualTo("home");

		assertThat(breadcrumb.getSteps().get(0).getNode()).isEqualTo(newNode1);
		assertThat(breadcrumb.getSteps().get(1).getNode()).isEqualTo(newNode2);
		assertThat(breadcrumb.getSteps().get(2).getNode()).isEqualTo(newNode3);

		assertThat(breadcrumb.getSteps().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(2).isVisible()).isTrue();

		// given
		nodeChain.remove(2);
		breadcrumb.afterViewChange(null);
		newNode7 = newNode("new");
		sitemap.addChild(newNode3, newNode7);
		nodeChain.add(newNode3);
		nodeChain.add(newNode7);

		// when
		breadcrumb.afterViewChange(null);

		// then
		assertThat(breadcrumb.getSteps().size()).isEqualTo(4);
		assertThat(breadcrumb.getSteps().get(0).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getSteps().get(1).getCaption()).isEqualTo("option");
		assertThat(breadcrumb.getSteps().get(2).getCaption()).isEqualTo("home");
		assertThat(breadcrumb.getSteps().get(3).getCaption()).isEqualTo("home");

		assertThat(breadcrumb.getSteps().get(0).getNode()).isEqualTo(newNode1);
		assertThat(breadcrumb.getSteps().get(1).getNode()).isEqualTo(newNode2);
		assertThat(breadcrumb.getSteps().get(2).getNode()).isEqualTo(newNode3);
		assertThat(breadcrumb.getSteps().get(3).getNode()).isEqualTo(newNode7);

		assertThat(breadcrumb.getSteps().get(0).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(1).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(2).isVisible()).isTrue();
		assertThat(breadcrumb.getSteps().get(3).isVisible()).isTrue();

		// given
		BreadcrumbStep step = breadcrumb.getSteps().get(2);
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
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
			}

		};
	}
}
