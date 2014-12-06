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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.TestI18NModule;
import fixture.testviews2.My_AccountView;
import fixture.testviews2.OptionsView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.DefaultFileSitemapLoaderTest2.TestFileSitemapModule;
import uk.q3c.krail.core.user.opt.*;
import uk.q3c.krail.core.view.testviews.subview.MoneyInOutView;
import uk.q3c.krail.core.view.testviews.subview.TransferView;
import uk.q3c.krail.i18n.DefaultI18NProcessor;
import uk.q3c.krail.i18n.I18NProcessor;
import uk.q3c.krail.i18n.TestLabelKey;
import uk.q3c.util.testutil.TestResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests {@link DefaultFileSitemapLoader} with multiple input files
 *
 * @author dsowerby
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestFileSitemapModule.class, TestI18NModule.class, VaadinSessionScopeModule.class, TestUserOptionModule.class})
public class DefaultFileSitemapLoaderTest2 {

    List<SitemapLoader> loaders;
    LoaderReportBuilder lrb;
    @Inject
    DefaultFileSitemapLoader loader;
    @Inject
    MasterSitemap masterSitemap;

    @Before
    public void setup() throws IOException {
        loaders = new ArrayList<>();
        loaders.add(loader);
    }

    @Test
    public void multipleInputFiles() {

        // given
        // when
        loader.load();
        lrb = new LoaderReportBuilder(loaders);
        loader.getSitemap()
              .setReport(lrb.getReport()
                            .toString());
        StringBuilder report = lrb.getReport();

        // then

        for (MasterSitemapNode node : masterSitemap.getAllNodes()) {
            validateNode(node);
        }

        // Note: this currently only works if there are errors
        assertThat(report).contains("src/test/java/uk/q3c/krail/core/navigate/sitemap_good1.properties");
        assertThat(report).contains("uk/q3c/krail/core/navigate/sitemap_good1.properties");
        System.out.println(report.toString());
    }

    private void validateNode(MasterSitemapNode node) {
        String uri = masterSitemap.uri(node);
        System.out.println("validating " + uri);
        switch (uri) {

            case "my-account":
                assertThat(masterSitemap.getChildCount(node)).isEqualTo(3);
                assertThat(node.getUriSegment()).isEqualTo("my-account");
                assertThat(node.getViewClass()).isEqualTo(My_AccountView.class);
                assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.My_Account);
                assertThat(node.isPublicPage()).isTrue();
                assertThat(node.hasRoles()).isFalse();
                break;

            case "my-account/transfers":
                assertThat(masterSitemap.getChildCount(node)).isEqualTo(0);
                assertThat(node.getUriSegment()).isEqualTo("transfers");
                assertThat(node.getViewClass()).isEqualTo(TransferView.class);
                assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Transfers);
                assertThat(node.isPublicPage()).isTrue();
                assertThat(node.hasRoles()).isFalse();
                break;

            case "my-account/money-in-out":
                assertThat(masterSitemap.getChildCount(node)).isEqualTo(0);
                assertThat(node.getUriSegment()).isEqualTo("money-in-out");
                assertThat(node.getViewClass()).isEqualTo(MoneyInOutView.class);
                assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.MoneyInOut);
                assertThat(node.isPublicPage()).isFalse();
                assertThat(node.hasRoles()).isTrue();
                break;

            case "my-account/options":
                assertThat(masterSitemap.getChildCount(node)).isEqualTo(0);
                assertThat(node.getUriSegment()).isEqualTo("options");
                assertThat(node.getViewClass()).isEqualTo(OptionsView.class);
                assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Opt);
                assertThat(node.isPublicPage()).isFalse();
                assertThat(node.hasRoles()).isTrue();
                break;
            case "information":
                assertThat(masterSitemap.getChildCount(node)).isEqualTo(3);
                assertThat(node.getUriSegment()).isEqualTo("information");
                assertThat(node.getViewClass()).isEqualTo(null);
                assertThat(node.getLabelKey()).isEqualTo(null);
                assertThat(node.isPublicPage()).isTrue();
                assertThat(node.hasRoles()).isFalse();
                break;
            case "information/offers":
                assertThat(masterSitemap.getChildCount(node)).isEqualTo(0);
                assertThat(node.getUriSegment()).isEqualTo("offers");
                assertThat(node.getViewClass()).isEqualTo(MoneyInOutView.class);
                assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.MoneyInOut);
                assertThat(node.isPublicPage()).isFalse();
                assertThat(node.hasRoles()).isTrue();
                break;
            case "information/terms":
                assertThat(masterSitemap.getChildCount(node)).isEqualTo(0);
                assertThat(node.getUriSegment()).isEqualTo("terms");
                assertThat(node.getViewClass()).isEqualTo(TransferView.class);
                assertThat(node.getLabelKey()).isEqualTo(null);
                assertThat(node.isPublicPage()).isTrue();
                assertThat(node.hasRoles()).isFalse();
                break;
            case "information/services":
                assertThat(masterSitemap.getChildCount(node)).isEqualTo(0);
                assertThat(node.getUriSegment()).isEqualTo("services");
                assertThat(node.getViewClass()).isEqualTo(OptionsView.class);
                assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Opt);
                assertThat(node.isPublicPage()).isFalse();
                assertThat(node.hasRoles()).isTrue();
                break;

            default:
                fail("unexpected uri: '" + uri + "'");
        }
    }

    @ModuleProvider
    protected AbstractModule module() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
                bind(UserSitemap.class).to(DefaultUserSitemap.class);
            }

        };
    }

    public static class TestFileSitemapModule extends FileSitemapModule {

        @Override
        protected void define() {
            File a = new File(TestResource.testJavaRootDir("krail"), "uk/q3c/krail/core/navigate/sitemap_good" +
                    ".properties");
            File b = new File(TestResource.testJavaRootDir("krail"), "uk/q3c/krail/core/navigate/sitemap_good1" + "" +
                    ".properties");

            addEntry("a", new SitemapFile(a.getAbsolutePath()));
            addEntry("b", new SitemapFile(b.getAbsolutePath()));
        }

    }

}
