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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.ui.Component;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.base.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.base.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.base.navigate.URIFragmentHandler;
import uk.q3c.krail.base.navigate.sitemap.DefaultAnnotationSitemapLoaderTest.AnnotationsModule1;
import uk.q3c.krail.base.navigate.sitemap.DefaultAnnotationSitemapLoaderTest.AnnotationsModule2;
import uk.q3c.krail.base.shiro.PageAccessControl;
import uk.q3c.krail.base.user.opt.DefaultUserOption;
import uk.q3c.krail.base.user.opt.DefaultUserOptionStore;
import uk.q3c.krail.base.user.opt.UserOption;
import uk.q3c.krail.base.user.opt.UserOptionStore;
import uk.q3c.krail.base.view.V7View;
import uk.q3c.krail.base.view.V7ViewChangeEvent;
import uk.q3c.krail.i18n.DefaultI18NProcessor;
import uk.q3c.krail.i18n.DescriptionKey;
import uk.q3c.krail.i18n.I18NProcessor;
import uk.q3c.krail.i18n.TestLabelKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({AnnotationsModule1.class, AnnotationsModule2.class, TestI18NModule.class,
        VaadinSessionScopeModule.class})
public class DefaultAnnotationSitemapLoaderTest {

    @Inject
    DefaultAnnotationSitemapLoader loader;

    List<SitemapLoader> loaders;

    LoaderReportBuilder lrb;

    @Inject
    MasterSitemap sitemap;
    @Inject
    Map<String, AnnotationSitemapEntry> map;

    @Before
    public void setup() {
        loaders = new ArrayList<>();
        loaders.add(loader);
    }

    @Test
    public void test() {
        // given
        // when
        loader.load();
        lrb = new LoaderReportBuilder(loaders);
        System.out.println(lrb.getReport());
        // then
        assertThat(loader.getErrorCount()).isEqualTo(2);
        assertThat(sitemap.hasUri("a")).isTrue();
        assertThat(sitemap.getRedirectPageFor("a")).isEqualTo("a");
        assertThat(sitemap.getRedirectPageFor("home/redirected")).isEqualTo("a");
        assertThat(sitemap.getRedirectPageFor("home/splat")).isEqualTo("a");
        SitemapNode node = sitemap.nodeFor("a");
        assertThat(node.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
        assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Home);
        assertThat(node.getUriSegment()).isEqualTo("a");

    }

    @ModuleProvider
    protected AbstractModule module() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
                bind(UserSitemap.class).to(DefaultUserSitemap.class);
                bind(UserOption.class).to(DefaultUserOption.class);
                bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
            }

        };
    }

    public static class AnnotationsModule1 extends AnnotationSitemapModule {

        @Override
        protected void define() {
            addEntry("fixture.", DescriptionKey.Confirm_Ok);
            addEntry("uk.q3c.krail.base.navigate.sitemap", TestLabelKey.Login);
        }

    }

    public static class AnnotationsModule2 extends AnnotationSitemapModule {

        @Override
        protected void define() {
            addEntry("fixture1", TestLabelKey.Home);
        }

    }

    @View(uri = "a", labelKeyName = "Home", pageAccessControl = PageAccessControl.PERMISSION)
    @RedirectFrom(sourcePages = {"home/redirected", "home/splat"})
    static class View1 implements V7View {


        /**
         * Called after the view itself has been constructed but before {@link #buildView()} is called.  Typically
         * checks
         * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
         * configuration which affects the way the view is presented.
         *
         * @param event
         *         contains information about the change to this View
         */
        @Override
        public void beforeBuild(V7ViewChangeEvent event) {

        }

        /**
         * Builds the UI components of the view.  The view implementation may need to check whether components have
         * already
         * been constructed, as this method may be called when the View is selected again after initial construction.
         *
         * @param event
         *
         * @return the root component of the View, which is used to insert into the {@link ScopedUI} view area.
         */
        @Override
        public void buildView(V7ViewChangeEvent event) {

        }

        @Override
        public Component getRootComponent() {

            return null;
        }

        @Override
        public String viewName() {

            return getClass().getSimpleName();
        }

        @Override
        public void init() {
        }

        /**
         * Called immediately after the construction of the Views components (see {@link buildView}) to enable
         * setting up
         * the view from URL parameters
         *
         * @param event
         */
        @Override
        public void afterBuild(V7ViewChangeEvent event) {

        }


    }
}
