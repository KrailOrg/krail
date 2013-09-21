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

import static org.fest.assertions.Assertions.*;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.TestLabelKey;

public class SitemapTest {

	@Test
	public void url() {

		// given
		Locale locale = Locale.UK;
		Collator collator = Collator.getInstance(locale);
		Sitemap map = new Sitemap();
		SitemapNode grandparent = new SitemapNode("public", PublicHomeView.class, TestLabelKey.Home, locale, collator);
		SitemapNode parent = new SitemapNode("home", PublicHomeView.class, TestLabelKey.Home, locale, collator);
		SitemapNode child = new SitemapNode("login", LoginView.class, TestLabelKey.Login, locale, collator);
		map.addChild(grandparent, parent);
		map.addChild(parent, child);
		// when

		// then
		assertThat(map.uri(grandparent)).isEqualTo("public");
		assertThat(map.uri(parent)).isEqualTo("public/home");
		assertThat(map.uri(child)).isEqualTo("public/home/login");
	}

	@Test
	public void append() {

		// given
		Sitemap map = new Sitemap();
		// when
		SitemapNode node = map.append("public/home");
		// then
		assertThat(node).isNotNull();
		assertThat(node.getUriSegment()).isEqualTo("home");
		assertThat(map.getNodeCount()).isEqualTo(2);
		assertThat(map.getParent(node).getUriSegment()).isEqualTo("public");

		// when
		node = map.append("public/home/account");

		// then
		assertThat(node).isNotNull();
		assertThat(node.getUriSegment()).isEqualTo("account");
		assertThat(map.getNodeCount()).isEqualTo(3);
		assertThat(map.getParent(node).getUriSegment()).isEqualTo("home");
		assertThat(map.getParent(map.getParent(node)).getUriSegment()).isEqualTo("public");

		// when
		node = map.append("public/home/transfer");

		// then
		assertThat(node).isNotNull();
		assertThat(node.getUriSegment()).isEqualTo("transfer");
		assertThat(map.getNodeCount()).isEqualTo(4);
		assertThat(map.getParent(node).getUriSegment()).isEqualTo("home");
		assertThat(map.getParent(map.getParent(node)).getUriSegment()).isEqualTo("public");

		// when
		node = map.append("");

		// then
		assertThat(node).isNotNull();
		assertThat(node.getUriSegment()).isEqualTo("");
		assertThat(map.getNodeCount()).isEqualTo(5);
		assertThat(map.getRoots()).contains(node);
	}

	@Test
	public void nodeChainForSegments() {

		// given
		Sitemap map = new Sitemap();
		map.append("public/home/view1");
		map.append("public/home/view2");
		map.append("private/home/wiggly");
		List<String> segments = new ArrayList<>();
		segments.add("public");
		segments.add("home");
		segments.add("view1");

		// when
		List<SitemapNode> result = map.nodeChainForSegments(segments, true);
		// then
		assertThat(result.size()).isEqualTo(3);
		assertThat(result.get(0).getUriSegment()).isEqualTo("public");
		assertThat(result.get(1).getUriSegment()).isEqualTo("home");
		assertThat(result.get(2).getUriSegment()).isEqualTo("view1");

		// given
		segments.remove(1);

		// when
		result = map.nodeChainForSegments(segments, true);

		// then
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0).getUriSegment()).isEqualTo("public");

		// when
		result = map.nodeChainForSegments(segments, false);

		// then
		assertThat(result.size()).isEqualTo(0);
	}

	@Test
	public void nodeChainForSegments_partial() {

		// given
		Sitemap map = new Sitemap();
		map.append("public/home/view1");
		map.append("public/home/view2");
		map.append("private/home/wiggly");
		List<String> segments = new ArrayList<>();
		segments.add("public");
		segments.add("home");
		segments.add("viewx");

		// when
		List<SitemapNode> result = map.nodeChainForSegments(segments, true);
		// then
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getUriSegment()).isEqualTo("public");
		assertThat(result.get(1).getUriSegment()).isEqualTo("home");

	}

	@Test
	public void getRedirectFor() {

		// given
		Sitemap sitemap = new Sitemap();
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		sitemap.getRedirects().put("home", "public/home");
		// when redirect exists
		String page = sitemap.getRedirectFor("home");
		// then
		assertThat(page).isEqualTo("public/home");
		// when redirect does not exist
		page = sitemap.getRedirectFor("wiggly");
		assertThat(page).isEqualTo("wiggly");
	}

	@Test
	public void uris() {

		// given
		Sitemap sitemap = new Sitemap();
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");

		// when

		// then
		assertThat(sitemap.uris()).containsOnly("public/home/view1", "public/home/view2", "private/home/wiggly",
				"private/home", "private", "public/home", "public");

	}

	@Test
	public void hasUri() {

		// given
		Sitemap sitemap = new Sitemap();
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");

		// when

		// then
		assertThat(sitemap.hasUri("public/home")).isTrue();
		assertThat(sitemap.hasUri("private/home")).isTrue();
		assertThat(sitemap.hasUri("private/home/wiggly")).isTrue();

	}

	@Test
	public void privateNode() {

		// given
		Sitemap sitemap = new Sitemap();
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");

		// when

		// then
		assertThat(sitemap.getPrivateRoot()).isEqualTo("private");
		assertThat(sitemap.getParent(sitemap.getPrivateRootNode())).isNull();
		assertThat(sitemap.getPrivateRootNode().getUriSegment()).isEqualTo("private");

	}

	@Test
	public void publicNode() {

		// given
		Sitemap sitemap = new Sitemap();
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");

		// when

		// then
		assertThat(sitemap.getPublicRoot()).isEqualTo("public");
		assertThat(sitemap.getParent(sitemap.getPublicRootNode())).isNull();
		assertThat(sitemap.getPublicRootNode().getUriSegment()).isEqualTo("public");

	}

	@Test
	public void privateNodeRootNotSet() {

		// given
		Sitemap sitemap = new Sitemap();
		sitemap.setPrivateRoot(null);
		// when

		// then
		assertThat(sitemap.getPrivateRoot()).isNull();
		assertThat(sitemap.getPrivateRootNode()).isNull();

	}

	@Test
	public void publicNodeRootNotSet() {

		// given
		Sitemap sitemap = new Sitemap();
		sitemap.setPublicRoot(null);
		// when

		// then
		assertThat(sitemap.getPublicRoot()).isNull();
		assertThat(sitemap.getPublicRootNode()).isNull();

	}
}
