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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.DefaultI18NProcessor;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.I18NProcessor;
import uk.co.q3c.v7.i18n.TestLabelKey;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class MasterSitemapTest {

	@Inject
	URIFragmentHandler uriHandler;

	MasterSitemap sitemap;

	@Before
	public void setup() {
		sitemap = new MasterSitemap(uriHandler);
	}

	@Test
	public void url() {

		// given
		MasterSitemapNode grandparent = new MasterSitemapNode("public", PublicHomeView.class, TestLabelKey.Home);
		MasterSitemapNode parent = new MasterSitemapNode("home", PublicHomeView.class, TestLabelKey.Home);
		MasterSitemapNode child = new MasterSitemapNode("login", LoginView.class, TestLabelKey.Login);
		sitemap.addChild(grandparent, parent);
		sitemap.addChild(parent, child);
		// when

		// then
		assertThat(sitemap.uri(grandparent)).isEqualTo("public");
		assertThat(sitemap.uri(parent)).isEqualTo("public/home");
		assertThat(sitemap.uri(child)).isEqualTo("public/home/login");
	}

	@Test
	public void append() {

		// given
		// when
		MasterSitemapNode node = sitemap.append("public/home");
		// then
		assertThat(node).isNotNull();
		assertThat(node.getUriSegment()).isEqualTo("home");
		assertThat(sitemap.getNodeCount()).isEqualTo(2);
		assertThat(sitemap.getParent(node).getUriSegment()).isEqualTo("public");

		// when
		node = sitemap.append("public/home/account");

		// then
		assertThat(node).isNotNull();
		assertThat(node.getUriSegment()).isEqualTo("account");
		assertThat(sitemap.getNodeCount()).isEqualTo(3);
		assertThat(sitemap.getParent(node).getUriSegment()).isEqualTo("home");
		assertThat(sitemap.getParent(sitemap.getParent(node)).getUriSegment()).isEqualTo("public");

		// when
		node = sitemap.append("public/home/transfer");

		// then
		assertThat(node).isNotNull();
		assertThat(node.getUriSegment()).isEqualTo("transfer");
		assertThat(sitemap.getNodeCount()).isEqualTo(4);
		assertThat(sitemap.getParent(node).getUriSegment()).isEqualTo("home");
		assertThat(sitemap.getParent(sitemap.getParent(node)).getUriSegment()).isEqualTo("public");

		// when
		node = sitemap.append("");

		// then
		assertThat(node).isNotNull();
		assertThat(node.getUriSegment()).isEqualTo("");
		assertThat(sitemap.getNodeCount()).isEqualTo(5);
		assertThat(sitemap.getRoots()).contains(node);
	}

	@Test
	public void nodeChainForSegments() {

		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		List<String> segments = new ArrayList<>();
		segments.add("public");
		segments.add("home");
		segments.add("view1");

		// when
		List<MasterSitemapNode> result = sitemap.nodeChainForSegments(segments, true);
		// then
		assertThat(result.size()).isEqualTo(3);
		assertThat(result.get(0).getUriSegment()).isEqualTo("public");
		assertThat(result.get(1).getUriSegment()).isEqualTo("home");
		assertThat(result.get(2).getUriSegment()).isEqualTo("view1");

		// given
		segments.remove(1);

		// when
		result = sitemap.nodeChainForSegments(segments, true);

		// then
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0).getUriSegment()).isEqualTo("public");

		// when
		result = sitemap.nodeChainForSegments(segments, false);

		// then
		assertThat(result.size()).isEqualTo(0);
	}

	@Test
	public void nodeChainForSegments_partial() {

		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		List<String> segments = new ArrayList<>();
		segments.add("public");
		segments.add("home");
		segments.add("viewx");

		// when
		List<MasterSitemapNode> result = sitemap.nodeChainForSegments(segments, true);
		// then
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getUriSegment()).isEqualTo("public");
		assertThat(result.get(1).getUriSegment()).isEqualTo("home");

	}

	@Test
	public void getRedirectFor() {

		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		sitemap.addRedirect("home", "public/home");
		// when redirect exists
		String page = sitemap.getRedirectPageFor("home");
		// then
		assertThat(page).isEqualTo("public/home");
		// when redirect does not exist
		page = sitemap.getRedirectPageFor("wiggly");
		assertThat(page).isEqualTo("wiggly");
	}

	@Test
	public void uris() {

		// given
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
	public void hasURINavState() {

		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		// when
		NavigationState navigationState1 = uriHandler.navigationState("public/home/view1");
		NavigationState navigationState3 = uriHandler.navigationState("public/home/view3");
		// then

		assertThat(sitemap.hasUri(navigationState1)).isTrue();
		assertThat(sitemap.hasUri(navigationState3)).isFalse();
	}

	@Test
	public void redirectFor() {

		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		sitemap.addRedirect("public/home/view1", "public/home/view2");
		// when
		MasterSitemapNode node1 = sitemap.nodeFor("public/home/view1");
		SitemapNode node2 = sitemap.nodeFor("public/home/view2");
		// then
		assertThat(sitemap.getRedirectNodeFor(node1)).isEqualTo(node2);

	}

	/**
 *
 */

	@Test
	public void nodeFor_uri() {

		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		// when
		MasterSitemapNode node1 = sitemap.nodeFor("public/home/view1");
		SitemapNode node2 = sitemap.nodeFor("public/home/view2");
		// then
		assertThat(node1.getUriSegment()).isEqualTo("view1");
		assertThat(sitemap.getParent(node1).getUriSegment()).isEqualTo("home");
		assertThat(node2.getUriSegment()).isEqualTo("view2");
	}

	@Test
	public void nodeFor_navState() {
		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		// when
		NavigationState navigationState = uriHandler.navigationState("public/home/view2");
		SitemapNode node1 = sitemap.nodeFor(navigationState);
		// then
		assertThat(node1.getUriSegment()).isEqualTo("view2");
	}

	@Test
	public void nodeFor_emptyString() {
		// given
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		sitemap.append("");
		// when
		MasterSitemapNode node1 = sitemap.nodeFor("");
		// then
		assertThat(node1.getUriSegment()).isEqualTo("");
		assertThat(sitemap.getParent(node1)).isNull();

	}

	@Test
	public void nodeNearestFor() {

		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("private/home/wiggly");
		// when
		MasterSitemapNode node1 = sitemap.nodeNearestFor(uriHandler.navigationState("public/home/view3"));
		SitemapNode node2 = sitemap.nodeNearestFor("public/home/view3");
		SitemapNode node3 = sitemap.nodeNearestFor("public/home");

		// then

		assertThat(node1).isEqualTo(node3);
		assertThat(node1).isEqualTo(node2);
	}

	@Test
	public void multiLevelRedirect() {

		// given
		sitemap.append("public/home/view1");
		sitemap.append("public/home/view2");
		sitemap.append("public/home/view3");
		sitemap.append("public/home/view4");
		sitemap.addRedirect("public/home/view1", "public/home/view2");
		sitemap.addRedirect("public/home/view2", "public/home/view3");
		// when

		// then
		assertThat(sitemap.getRedirectPageFor("public/home/view1")).isEqualTo("public/home/view3");
	}

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
