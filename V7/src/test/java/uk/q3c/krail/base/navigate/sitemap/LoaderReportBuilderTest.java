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
package uk.q3c.krail.base.navigate.sitemap;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.testutil.TestResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class LoaderReportBuilderTest {

    File templateFile;
    List<SitemapLoader> loaders;
    LoaderReportBuilder lrb;

    @Before
    public void setup() {
        templateFile = new File(TestResource.testJavaRootDir("V7"),
                "uk/q3c/krail/base/navigate/sitemap/LoadReportBuilderTest.template");
        loaders = new ArrayList<>();
        loaders.add(new MockAnnotationLoader());
        loaders.add(new MockDirectLoader());

        for (SitemapLoader loader : loaders) {
            loader.load();
        }

    }

    @Test
    public void buildReport() throws IOException {

        // given
        String template = FileUtils.readFileToString(templateFile);
        // when
        lrb = new LoaderReportBuilder(loaders);
        // then
        System.out.println(lrb.getReport()
                              .toString());

        assertThat(lrb.getReport()
                      .toString()).isEqualTo(template);

        // use this to generate a new template if structure changes
        // FileUtils.writeStringToFile(templateFile, lrb.getReport().toString());
    }

    class MockAnnotationLoader extends SitemapLoaderBase {

        @Override
        public boolean load() {
            addError("a", "Pattern with no params");
            addError("b", "Pattern with {0} params", 1);
            addError("b", "Pattern with {0} params, just as an {1}", new Object[]{2, "example"});
            addWarning("c", "Pattern with {0} params, just as an {1}", new Object[]{2, "example"});
            addInfo("a", "Pattern with {0} params", 1);
            addInfo("a", "Pattern with {0} params", 1);
            addInfo("a", "Pattern with {0} params, just as an {1}", new Object[]{2, "example"});
            return false;
        }
    }

    class MockDirectLoader extends SitemapLoaderBase {

        @Override
        public boolean load() {
            addError("a", "Pattern with no params");
            addError("b", "Pattern with {0} params", 1);
            addError("b", "Pattern with {0} params, just as an {1}", new Object[]{2, "example"});
            addWarning("c", "Pattern with {0} params, just as an {1}", new Object[]{2, "example"});
            addInfo("a", "Pattern with {0} params", 1);
            addInfo("a", "Pattern with {0} params", 1);
            addInfo("a", "Pattern with {0} params, just as an {1}", new Object[]{2, "example"});
            return false;
        }
    }

}
