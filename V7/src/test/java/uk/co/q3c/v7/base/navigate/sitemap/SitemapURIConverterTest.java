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

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.view.component.TestWithSitemap;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class SitemapURIConverterTest extends TestWithSitemap {

	SitemapURIConverter converter;

	@Inject
	StrictURIFragmentHandler uriHandler;

	// SitemapNode newNode1;
	// SitemapNode newNode2;
	// SitemapNode newNode3;
	// SitemapNode newNode4;
	// SitemapNode newNode5;
	// SitemapNode newNode6;

	@Override
	@Before
	public void setup() {
		super.setup();
		converter = new SitemapURIConverter(sitemap, uriHandler);
	}

	@Test
	public void nodeChainForUri() {

		// given
		buildSitemap(2);
		// when
		List<SitemapNode> results = converter.nodeChainForUri("a/a1", true);
		// then
		assertThat(results.size()).isEqualTo(2);
		assertThat(results.get(0).getUriSegment()).isEqualTo("a");
		assertThat(results.get(1).getUriSegment()).isEqualTo("a1");

		results = converter.nodeChainForUri("a/a1", false);
		// then
		assertThat(results.size()).isEqualTo(2);
		assertThat(results.get(0).getUriSegment()).isEqualTo("a");
		assertThat(results.get(1).getUriSegment()).isEqualTo("a1");

	}

	@Test
	public void nodeChainForUri_emptyString() {

		// given
		sitemap.addNode(newNode(""));
		// when
		List<SitemapNode> results = converter.nodeChainForUri("", true);
		// then
		assertThat(results.size()).isEqualTo(1);
		assertThat(results.get(0).getUriSegment()).isEqualTo("");

		results = converter.nodeChainForUri("", false);
		// then
		assertThat(results.size()).isEqualTo(1);
		assertThat(results.get(0).getUriSegment()).isEqualTo("");

	}

	@Test
	public void nodeChainForPartialUri() {

		// given
		buildSitemap(2);
		// when
		List<SitemapNode> results = converter.nodeChainForUri("a/a2", true);
		// then
		assertThat(results.size()).isEqualTo(1);
		assertThat(results.get(0).getUriSegment()).isEqualTo("a");

		results = converter.nodeChainForUri("a/a2/id=1", true);
		// then
		assertThat(results.size()).isEqualTo(1);
		assertThat(results.get(0).getUriSegment()).isEqualTo("a");

		// when
		results = converter.nodeChainForUri("a/a2", false);
		// then
		assertThat(results.size()).isEqualTo(0);

		results = converter.nodeChainForUri("a/a2/id=1", false);
		// then
		assertThat(results.size()).isEqualTo(0);

	}

	@Test
	public void hasNodeForUri() {

		// given
		buildSitemap(2);
		// when

		// then
		assertThat(converter.hasNodeForUri("a", true)).isTrue();
		assertThat(converter.hasNodeForUri("a/a1", true)).isTrue();
		assertThat(converter.hasNodeForUri("a/a2", true)).isTrue();

		assertThat(converter.hasNodeForUri("a", false)).isTrue();
		assertThat(converter.hasNodeForUri("a/a1", false)).isTrue();
		assertThat(converter.hasNodeForUri("a/a2", false)).isFalse();

	}

	@Test
	public void nodeForUri() {

		// given
		buildSitemap(2);
		// when

		// then
		assertThat(converter.nodeForUri("a", true).getUriSegment()).isEqualTo("a");
		assertThat(converter.nodeForUri("a/a1", true).getUriSegment()).isEqualTo("a1");
		assertThat(converter.nodeForUri("a/a1/a11", true).getUriSegment()).isEqualTo("a11");
		assertThat(converter.nodeForUri("a/a1/a12", true).getUriSegment()).isEqualTo("a1");
		assertThat(converter.nodeForUri("a2", true)).isNull();

		assertThat(converter.nodeForUri("a", false).getUriSegment()).isEqualTo("a");
		assertThat(converter.nodeForUri("a/a1", false).getUriSegment()).isEqualTo("a1");
		assertThat(converter.nodeForUri("a/a1/a11", false).getUriSegment()).isEqualTo("a11");
		assertThat(converter.nodeForUri("a/a1/a12", false)).isNull();
		assertThat(converter.nodeForUri("a2", false)).isNull();

	}

	@Test
	public void pageIsPublic() {

		// given
		buildSitemap(4);
		// when

		// then
		assertThat(converter.pageIsPublic("public")).isTrue();
		assertThat(converter.pageIsPublic("private")).isFalse();
		assertThat(converter.pageIsPublic("public/logout")).isTrue();
		assertThat(converter.pageIsPublic("private/wiggly")).isFalse();
		// invalid page should be false
		assertThat(converter.pageIsPublic("random")).isFalse();
		// with params
		assertThat(converter.pageIsPublic("public/logout/id=1")).isTrue();
		assertThat(converter.pageIsPublic("private/wiggly/id=1")).isFalse();

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
