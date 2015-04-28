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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.navigate.sitemap.FileSitemapModuleTest.TestFileSitemapModule1;
import uk.q3c.krail.core.navigate.sitemap.FileSitemapModuleTest.TestFileSitemapModule2;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @deprecated see <a href="https://github.com/davidsowerby/krail/issues/375">Issue 375</a>
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestFileSitemapModule1.class, TestFileSitemapModule2.class})
@Deprecated
public class FileSitemapModuleTest {

    static String f1 = "/home/temp/app";
    static String f2 = "/home/temp/lib1";
    static String f3 = "/home/temp/lib2";

    @Inject
    Map<String, SitemapFile> sources;

    @Test
    public void define() {

        // given

        // when

        // then

        assertThat(sources).hasSize(3);
        assertThat(sources.get("app")
                          .getFilePath()).isEqualTo(f1);
        assertThat(sources.get("lib1")
                          .getFilePath()).isEqualTo(f2);
        assertThat(sources.get("lib2")
                          .getFilePath()).isEqualTo(f3);
    }

    public static class TestFileSitemapModule1 extends FileSitemapModule {

        @Override
        protected void define() {
            addEntry("app", new SitemapFile(f1));
        }

    }

    public static class TestFileSitemapModule2 extends FileSitemapModule {

        @Override
        protected void define() {
            addEntry("lib1", new SitemapFile(f2));
            addEntry("lib2", new SitemapFile(f3));
        }

    }
}
