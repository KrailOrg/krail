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
package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;

import java.text.Collator;
import java.util.Locale;

import org.junit.Test;

import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.TestLabelKey;

public class SitemapNodeTest {

	@Test
	public void setLabelKey() {

		// given
		SitemapNode node = new SitemapNode(null, "");
		Locale locale = Locale.UK;
		// when

		node.setLabelKey(TestLabelKey.Yes, locale);
		// then
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getLabel()).isEqualTo("Yes");
	}

	@Test
	public void setLabelKey_de() {

		// given
		SitemapNode node = new SitemapNode(null, "");
		Locale locale = Locale.GERMAN;
		// when

		node.setLabelKey(TestLabelKey.Yes, locale);
		// then
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getLabel()).isEqualTo("Ja");
	}

	@Test
	public void constructor() {

		// given
		Locale locale = Locale.UK;
		Collator collator = Collator.getInstance(locale);

		// when
		SitemapNode node = new SitemapNode(null, "one", PublicHomeView.class, TestLabelKey.Yes, locale);
		// then
		assertThat(node.getUriSegment()).isEqualTo("one");
		assertThat(node.getViewClass()).isEqualTo(PublicHomeView.class);
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getLabel()).isEqualTo("Yes");

	}

	@Test
	public void constructor_de() {

		// given
		Locale locale = Locale.GERMAN;
		Collator collator = Collator.getInstance(locale);

		// when
		SitemapNode node = new SitemapNode(null, "one", PublicHomeView.class, TestLabelKey.Yes, locale);
		// then
		assertThat(node.getUriSegment()).isEqualTo("one");
		assertThat(node.getViewClass()).isEqualTo(PublicHomeView.class);
		assertThat(node.getLabel()).isEqualTo("Ja");

	}

}
