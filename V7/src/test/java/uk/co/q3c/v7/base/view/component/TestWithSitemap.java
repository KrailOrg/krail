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

import java.text.Collator;
import java.util.Locale;

import org.junit.Before;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public abstract class TestWithSitemap {

	@Inject
	protected Translate translate;

	@Inject
	URIFragmentHandler uriHandler;

	@Inject
	protected Sitemap sitemap;

	protected SitemapNode newNode1;
	protected SitemapNode newNode2;
	protected SitemapNode newNode3;
	protected SitemapNode newNode4;
	protected SitemapNode newNode5;
	protected SitemapNode newNode6;

	Locale locale = Locale.UK;
	Collator collator;

	@Before
	public void setup() {

		collator = Collator.getInstance(locale);
	}

	protected void buildSitemap(int i) {

		switch (i) {
		case 0:
			break; // empty sitemap

		case 2:
			newNode4 = newNode("b");
			newNode5 = newNode("b1");
			newNode6 = newNode("b11");
			sitemap.addChild(newNode4, newNode5);
			sitemap.addChild(newNode5, newNode6);

		case 1:
			newNode1 = newNode("a");
			newNode2 = newNode("a1");
			newNode3 = newNode("a11");
			sitemap.addChild(newNode1, newNode2);
			sitemap.addChild(newNode2, newNode3);
			break;

		case 3:
			newNode1 = newNode("public");
			newNode1.setLabelKey(StandardPageKey.Public_Home, locale, collator);
			newNode2 = newNode("logout");
			newNode2.setLabelKey(StandardPageKey.Logout, locale, collator);
			sitemap.addChild(newNode1, newNode2);
			break;
		case 4:
			newNode1 = newNode("public");
			newNode1.setLabelKey(StandardPageKey.Public_Home, locale, collator);
			newNode2 = newNode("logout");
			newNode2.setLabelKey(StandardPageKey.Logout, locale, collator);
			newNode3 = newNode("private");
			newNode4 = newNode("wiggly");
			sitemap.addChild(newNode1, newNode2);
			sitemap.addChild(newNode3, newNode4);
			break;

		case 5: // one node has missing key
			newNode4 = newNode("b");
			newNode5 = newNode("b1");
			newNode6 = newNode("b11");
			sitemap.addChild(newNode4, newNode5);
			sitemap.addChild(newNode5, newNode6);
			newNode1 = new SitemapNode();
			newNode1.setUriSegment("a");
			newNode1.setPageAccessControl(PageAccessControl.PUBLIC);
			newNode2 = newNode("a1");
			newNode3 = newNode("a11");
			sitemap.addChild(newNode1, newNode2);
			sitemap.addChild(newNode2, newNode3);
			break;

		case 6: // one node is private
			newNode4 = newNode("b");
			newNode5 = newNode("b1");
			newNode6 = newNode("b11");
			newNode6.setPageAccessControl(PageAccessControl.PERMISSION);
			sitemap.addChild(newNode4, newNode5);
			sitemap.addChild(newNode5, newNode6);
			newNode1 = new SitemapNode();
			newNode1.setUriSegment("a");
			newNode1.setLabelKey(TestLabelKey.Yes, translate, collator);
			newNode1.setPageAccessControl(PageAccessControl.PUBLIC);
			newNode2 = newNode("a1");
			newNode2.setLabelKey(TestLabelKey.Home, translate, collator);
			newNode3 = newNode("a11");
			newNode3.setLabelKey(TestLabelKey.Yes, translate, collator);
			sitemap.addChild(newNode1, newNode2);
			sitemap.addChild(newNode2, newNode3);
			break;

		case 7: // redirect has no page access control
			newNode4 = newNode("b");
			newNode4.setPageAccessControl(null);
			newNode5 = newNode("b1");
			newNode6 = newNode("b11");

			String fromPage = sitemap.navigationState(newNode4).getVirtualPage();
			String toPage = sitemap.navigationState(newNode5).getVirtualPage();
			sitemap.addRedirect(fromPage, toPage);
			sitemap.addChild(newNode4, newNode5);
			sitemap.addChild(newNode5, newNode6);
			break;

		}

	}

	protected SitemapNode newNode(String urlSegment) {
		SitemapNode node0 = new SitemapNode();
		node0.setTranslate(translate);
		node0.setLabelKey(TestLabelKey.Home, locale, collator);
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
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
			}

		};
	}
}
