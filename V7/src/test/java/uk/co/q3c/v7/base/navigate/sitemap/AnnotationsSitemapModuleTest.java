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

import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.sitemap.AnnotationsSitemapModuleTest.TestAnnotationsModule;
import uk.co.q3c.v7.base.navigate.sitemap.AnnotationsSitemapModuleTest.TestAnnotationsModule1;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.TestLabelKey;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.testviews2.TestAnnotatedView;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestAnnotationsModule.class, TestAnnotationsModule1.class })
public class AnnotationsSitemapModuleTest {

	@Inject
	TestAnnotatedView view2;

	@Inject
	Map<String, AnnotationSitemapEntry> sitemapEntries;

	public static class TestAnnotationsModule extends AnnotationSitemapModule {

		@Override
		protected void define() {
			addEntry("uk.co.q3c.v7.base.navigate", LabelKey.Home);
			addEntry("fixture", DescriptionKey.Confirm_Ok);
		}

	}

	public static class TestAnnotationsModule1 extends AnnotationSitemapModule {

		@Override
		protected void define() {
			addEntry("fixture1", TestLabelKey.Login);
		}

	}

	@Test
	public void combined() {

		// given

		// when

		// then

		assertThat(sitemapEntries).hasSize(3);
		assertThat(sitemapEntries.get("uk.co.q3c.v7.base.navigate").getLabelSample()).isEqualTo(LabelKey.Home);
		assertThat(sitemapEntries.get("fixture").getLabelSample()).isEqualTo(DescriptionKey.Confirm_Ok);
		assertThat(sitemapEntries.get("fixture1").getLabelSample()).isEqualTo(TestLabelKey.Login);
	}

}
