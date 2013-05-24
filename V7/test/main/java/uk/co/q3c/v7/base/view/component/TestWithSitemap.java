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

import org.junit.Before;

import uk.co.q3c.v7.base.navigate.Sitemap;
import uk.co.q3c.v7.base.navigate.SitemapNode;
import uk.co.q3c.v7.base.view.testviews.PublicHomeView;
import uk.co.q3c.v7.i18n.TestLabelKeys;

public class TestWithSitemap {

	protected Sitemap sitemap;

	protected SitemapNode newNode1;
	protected SitemapNode newNode2;
	protected SitemapNode newNode3;
	protected SitemapNode newNode4;
	protected SitemapNode newNode5;
	protected SitemapNode newNode6;

	@Before
	public void setup() {
		sitemap = new Sitemap();
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
		}

	}

	protected SitemapNode newNode(String urlSegment) {
		SitemapNode node0 = new SitemapNode();
		node0.setLabelKey(TestLabelKeys.Home);
		node0.setUrlSegment(urlSegment);
		node0.setViewClass(PublicHomeView.class);
		return node0;
	}
}
