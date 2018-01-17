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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.ui.Component;
import fixture.testviews2.TestAnnotatedView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.eventbus.VaadinEventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.DefaultI18NProcessor;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.I18NProcessor;
import uk.q3c.krail.core.i18n.TestKrailI18NModule;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.DefaultAnnotationSitemapLoaderTest.AnnotationsModule1;
import uk.q3c.krail.core.navigate.sitemap.DefaultAnnotationSitemapLoaderTest.AnnotationsModule2;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.eventbus.mbassador.EventBusModule;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.test.TestLabelKey;
import uk.q3c.krail.option.mock.TestOptionModule;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;
import uk.q3c.util.clazz.ClassNameUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Loads the MasterSitemap using annotations - one class in this file ({@link View1}) and {@link TestAnnotatedView}
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({AnnotationsModule1.class, AnnotationsModule2.class, InMemoryModule.class, TestKrailI18NModule.class, VaadinSessionScopeModule.class,
        VaadinEventBusModule.class, EventBusModule.class, UIScopeModule.class, UtilsModule.class, UtilModule.class, TestOptionModule.class})
public class DefaultAnnotationSitemapLoaderTest {

    @Inject
    DefaultAnnotationSitemapLoader loader;

    @Inject
    ClassNameUtils classNameUtils;

    List<SitemapLoader> loaders;

    LoaderReportBuilder lrb;

    @Inject
    MasterSitemap sitemap;


    @Before
    public void setup() {
        loaders = new ArrayList<>();
        loaders.add(loader);
    }

    /**
     *
     */
    @Test
    public void test() {
        // given
        // when
        loader.load(sitemap);
        lrb = new LoaderReportBuilder(loaders, classNameUtils);
        System.out.println(lrb.getReport());
        // then
        assertThat(loader.getErrorCount()).isEqualTo(1);
        assertThat(sitemap.hasUri("a")).isTrue();
        assertThat(sitemap.getRedirectPageFor("a")).isEqualTo("a");
        assertThat(sitemap.getRedirectPageFor("home/redirected")).isEqualTo("a");
        assertThat(sitemap.getRedirectPageFor("home/splat")).isEqualTo("a");
        SitemapNode node = sitemap.nodeFor("a");
        assertThat(node.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
        assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Home);
        assertThat(node.getUriSegment()).isEqualTo("a");
        assertThat(node.getPositionIndex()).isEqualTo(33);

        node = sitemap.nodeFor("a/b");
        assertThat(node.getPageAccessControl()).isNotNull();
        assertThat(node.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
        assertThat(node.getUriSegment()).isEqualTo("b");
        assertThat(node.getLabelKey()).isEqualTo(DescriptionKey.Account_Locked);
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
            }

        };
    }

    public static class AnnotationsModule1 extends AnnotationSitemapModule {

        @Override
        protected void define() {
            addEntry("fixture.", DescriptionKey.Confirm_Ok);
            addEntry("uk.q3c.krail.core.navigate.sitemap", TestLabelKey.Login);
        }

    }

    public static class AnnotationsModule2 extends AnnotationSitemapModule {

        @Override
        protected void define() {
            addEntry("fixture1", TestLabelKey.Home);
        }

    }

    @View(uri = "a", labelKeyName = "Home", pageAccessControl = PageAccessControl.PERMISSION, positionIndex = 33)
    @RedirectFrom(sourcePages = {"home/redirected", "home/splat"})
    static class View1 implements KrailView {



        @Override
        public void beforeBuild(ViewChangeBusMessage busMessage) {

        }


        @Override
        public void buildView(ViewChangeBusMessage busMessage) {

        }

        @Override
        public Component getRootComponent() {

            return null;
        }


        @Override
        public void init() {
        }

        @Override
        public void afterBuild(AfterViewChangeBusMessage event) {

        }


        @Override
        public I18NKey getNameKey() {
            return null;
        }

        @Override
        public void setNameKey(I18NKey nameKey) {

        }

        @Override
        public I18NKey getDescriptionKey() {
            return null;
        }

        @Override
        public void setDescriptionKey(I18NKey descriptionKey) {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getDescription() {
            return null;
        }
    }
}
