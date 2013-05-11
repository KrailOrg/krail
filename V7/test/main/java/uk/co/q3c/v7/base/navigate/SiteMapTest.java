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

import org.junit.Test;

import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.testviews.PublicHomeView;
import uk.co.q3c.v7.demo.i18n.DemoLabelKeys;

public class SiteMapTest {

	@Test
	public void url() {

		// given
		Sitemap map = new Sitemap();
		SiteMapNode grandparent = new SiteMapNode("public", PublicHomeView.class, DemoLabelKeys.home);
		SiteMapNode parent = new SiteMapNode("home", PublicHomeView.class, DemoLabelKeys.home);
		SiteMapNode child = new SiteMapNode("login", LoginView.class, DemoLabelKeys.login);
		map.addChild(grandparent, parent);
		map.addChild(parent, child);
		// when

		// then
		assertThat(map.url(grandparent)).isEqualTo("public");
		assertThat(map.url(parent)).isEqualTo("public/home");
		assertThat(map.url(child)).isEqualTo("public/home/login");
	}

	@Test
	public void append() {

		// given
		Sitemap map = new Sitemap();
		// when
		SiteMapNode node = map.append("public/home");
		// then
		assertThat(node).isNotNull();
		assertThat(node.getUrlSegment()).isEqualTo("home");
		assertThat(map.getNodeCount()).isEqualTo(2);
		assertThat(map.getParent(node).getUrlSegment()).isEqualTo("public");

		// when
		node = map.append("public/home/account");

		// then
		assertThat(node).isNotNull();
		assertThat(node.getUrlSegment()).isEqualTo("account");
		assertThat(map.getNodeCount()).isEqualTo(3);
		assertThat(map.getParent(node).getUrlSegment()).isEqualTo("home");
		assertThat(map.getParent(map.getParent(node)).getUrlSegment()).isEqualTo("public");

		// when
		node = map.append("public/home/transfer");

		// then
		assertThat(node).isNotNull();
		assertThat(node.getUrlSegment()).isEqualTo("transfer");
		assertThat(map.getNodeCount()).isEqualTo(4);
		assertThat(map.getParent(node).getUrlSegment()).isEqualTo("home");
		assertThat(map.getParent(map.getParent(node)).getUrlSegment()).isEqualTo("public");

	}

}
