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

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.TestLabelKey;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.testviews2.View1;
import fixture.testviews2.View2;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class })
public class DefaultSitemapCheckerTest {

	String uriNodeNoClass = "node/noclass";
	String uriNodeNoKey = "node/nokey";

	@Inject
	DefaultSitemapChecker checker;

	@Inject
	Sitemap sitemap;
	private SitemapNode nodeNoClass;
	private SitemapNode nodeNoKey;

	@Inject
	CurrentLocale currentLocale;
	private SitemapNode baseNode;

	@Test(expected = SitemapException.class)
	public void checkOnly() {

		// given
		buildSitemap(0);
		// when
		checker.check();
		// then

	}

	@Test(expected = SitemapException.class)
	public void replaceMissingViews() {

		// given
		buildSitemap(0);
		// when
		checker.replaceMissingViewWith(View1.class).check();
		// then

	}

	@Test(expected = SitemapException.class)
	public void replaceMissingKeys() {

		// given
		buildSitemap(0);
		// when
		checker.replaceMissingKeyWith(TestLabelKey.Home).check();
		// then

	}

	@Test
	public void replaceMissingViewsAndKeys() {

		// given
		buildSitemap(0);
		// when
		checker.replaceMissingViewWith(View1.class).replaceMissingKeyWith(TestLabelKey.Home).check();
		// then
		assertThat(checker.getMissingLabelKeys()).isEmpty();
		assertThat(checker.getMissingViewClasses()).isEmpty();
		assertThat(baseNode.getLabelKey()).isEqualTo(TestLabelKey.Home);
		assertThat(baseNode.getViewClass()).isEqualTo(View1.class);
		assertThat(nodeNoClass.getLabelKey()).isEqualTo(TestLabelKey.No);
		assertThat(nodeNoClass.getViewClass()).isEqualTo(View1.class);
		assertThat(nodeNoKey.getLabelKey()).isEqualTo(TestLabelKey.Home);
		assertThat(nodeNoKey.getViewClass()).isEqualTo(View2.class);
	}

	/**
	 * the root node "node" will have nothing set except the segment
	 * 
	 * @param index
	 */
	private void buildSitemap(int index) {
		Collator collator = Collator.getInstance(currentLocale.getLocale());
		nodeNoClass = sitemap.append(uriNodeNoClass);
		nodeNoClass.setLabelKey(TestLabelKey.No, currentLocale.getLocale(), collator);

		nodeNoKey = sitemap.append(uriNodeNoKey);
		nodeNoKey.setViewClass(View2.class);

		baseNode = sitemap.nodeFor("node");
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
