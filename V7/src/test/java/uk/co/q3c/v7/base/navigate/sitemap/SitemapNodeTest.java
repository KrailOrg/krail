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
package uk.co.q3c.v7.base.navigate.sitemap;

import static org.assertj.core.api.Assertions.*;

import java.text.Collator;
import java.util.Locale;

import com.google.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class SitemapNodeTest {

	@Inject
	Translate translate;

	@Test
	public void setLabelKey() {

		// given
		SitemapNode node = new SitemapNode();
		node.setTranslate(translate);
		Locale locale = Locale.UK;
		Collator collator = Collator.getInstance(locale);
		// when

		node.setLabelKey(TestLabelKey.Yes, locale, collator);
		// then
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getLabel()).isEqualTo("Yes");
		assertThat(node.getCollationKey()).isEqualTo(collator.getCollationKey("Yes"));
	}

	@Test
	public void setLabelKey_de() {

		// given
		SitemapNode node = new SitemapNode();
		node.setTranslate(translate);
		Locale locale = Locale.GERMAN;
		Collator collator = Collator.getInstance(locale);
		// when

		node.setLabelKey(TestLabelKey.Yes, locale, collator);
		// then
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getLabel()).isEqualTo("Ja");
		assertThat(node.getCollationKey()).isEqualTo(collator.getCollationKey("Ja"));
	}

	@Test
	public void constructor() {

		// given
		Locale locale = Locale.UK;
		Collator collator = Collator.getInstance(locale);

		// when
		SitemapNode node = new SitemapNode("one", PublicHomeView.class, TestLabelKey.Yes, locale, collator, translate);
		// then
		assertThat(node.getUriSegment()).isEqualTo("one");
		assertThat(node.getViewClass()).isEqualTo(PublicHomeView.class);
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getLabel()).isEqualTo("Yes");
		assertThat(node.getCollationKey()).isEqualTo(collator.getCollationKey("Yes"));

	}

	@Test
	public void constructor_de() {

		// given
		Locale locale = Locale.GERMAN;
		Collator collator = Collator.getInstance(locale);

		// when
		SitemapNode node = new SitemapNode("one", PublicHomeView.class, TestLabelKey.Yes, locale, collator, translate);
		// then
		assertThat(node.getUriSegment()).isEqualTo("one");
		assertThat(node.getViewClass()).isEqualTo(PublicHomeView.class);
		assertThat(node.getLabel()).isEqualTo("Ja");
		assertThat(node.getCollationKey()).isEqualTo(collator.getCollationKey("Ja"));

	}

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
