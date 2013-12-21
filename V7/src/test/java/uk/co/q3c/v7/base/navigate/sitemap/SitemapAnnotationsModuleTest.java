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

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;

import com.google.common.collect.ImmutableList;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.Component;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ SitemapAnnotationsModule.class })
public class SitemapAnnotationsModuleTest {

	@Inject
	View1 view1;

	@Inject
	ImmutableList<AnnotationSitemapEntry> sitemapEntries;

	@View(uri = "a", labelKeyName = "home", isPublic = true, viewClass = PublicHomeView.class)
	static class View1 implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

		@Override
		public String viewName() {

			return getClass().getSimpleName();
		}

	}

	@Test
	public void view1Only() {

		// given

		// when

		// then
		assertThat(sitemapEntries).isNotNull();
		assertThat(sitemapEntries).hasSize(1);
		AnnotationSitemapEntry entry = sitemapEntries.get(0);
		assertThat(entry.isPublicPage()).isTrue();
		assertThat(entry.getLabelKeyName()).isEqualTo("home");
		assertThat(entry.getUriSegment()).isEqualTo("a");
		assertThat(entry.getViewClass()).isEqualTo(PublicHomeView.class);

	}
}
