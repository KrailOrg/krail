/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.testviews2.TestAnnotatedView;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.sitemap.AnnotationSitemapModuleTest.TestAnnotationsModule;
import uk.q3c.krail.core.navigate.sitemap.AnnotationSitemapModuleTest.TestAnnotationsModule1;
import uk.q3c.krail.i18n.test.TestLabelKey;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestAnnotationsModule.class, TestAnnotationsModule1.class})
public class AnnotationSitemapModuleTest {

    @Inject
    TestAnnotatedView view2;

    @Inject
    Map<String, AnnotationSitemapEntry> sitemapEntries;

    @Test
    public void combined() {

        // given

        // when

        // then

        assertThat(sitemapEntries).hasSize(3);
        assertThat(sitemapEntries.get("uk.q3c.krail.core.navigate")
                                 .getLabelSample()).isEqualTo(LabelKey.Home_Page);
        assertThat(sitemapEntries.get("fixture")
                                 .getLabelSample()).isEqualTo(DescriptionKey.Confirm_Ok);
        assertThat(sitemapEntries.get("fixture1")
                                 .getLabelSample()).isEqualTo(TestLabelKey.Login);
    }

    public static class TestAnnotationsModule extends AnnotationSitemapModule {

        @Override
        protected void define() {
            addEntry("uk.q3c.krail.core.navigate", LabelKey.Home_Page);
            addEntry("fixture", DescriptionKey.Confirm_Ok);
        }

    }

    public static class TestAnnotationsModule1 extends AnnotationSitemapModule {

        @Override
        protected void define() {
            addEntry("fixture1", TestLabelKey.Login);
        }

    }

}
