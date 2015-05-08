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
import com.vaadin.server.VaadinService;
import fixture.TestConfigurationException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.config.ApplicationConfigurationModule;
import uk.q3c.krail.core.config.ConfigKeys;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.NavigationModule;
import uk.q3c.krail.core.navigate.sitemap.DefaultSitemapServiceTest.TestDirectSitemapModule;
import uk.q3c.krail.core.navigate.sitemap.DefaultSitemapServiceTest.TestFileSitemapModule;
import uk.q3c.krail.core.services.ServiceException;
import uk.q3c.krail.core.services.ServiceModule;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.shiro.StandardShiroModule;
import uk.q3c.krail.core.ui.DefaultUIModule;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.view.PublicHomeView;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.DefaultComponentModule;
import uk.q3c.krail.i18n.DescriptionKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.util.ResourceUtils;
import uk.q3c.util.testutil.TestResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test uses all standard implementations to inject into {@link DefaultSitemapService}.
 *
 * @author David Sowerby
 */

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestDirectSitemapModule.class, TestFileSitemapModule.class, UIScopeModule.class, ViewModule.class, EventBusModule.class, ServiceModule.class,
        ShiroVaadinModule.class, TestI18NModule.class, SitemapModule.class, UserModule.class, ApplicationConfigurationModule.class, StandardShiroModule
        .class, DefaultComponentModule.class, StandardPagesModule.class, VaadinSessionScopeModule.class, TestOptionModule.class, NavigationModule.class,
        DefaultUIModule.class})
public class DefaultSitemapServiceTest {

    static VaadinService vaadinService;
    private final int FILE_NODE_COUNT = 4;
    private final int DIRECT_NODE_COUNT = 2;
    private final int STANDARD_NODE_COUNT = 5;
    @Inject
    DefaultSitemapService service;

    @Inject
    MasterSitemap sitemap;
    HierarchicalINIConfiguration iniConfig;

    @BeforeClass
    public static void setupClass() {
        vaadinService = mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(ResourceUtils.userTempDirectory());
        VaadinService.setCurrent(vaadinService);
    }

    @Before
    public void setup() throws ConfigurationException {

        File inifile = new File(ResourceUtils.userTempDirectory(), "WEB-INF/krail.ini");
        iniConfig = new HierarchicalINIConfiguration(inifile);
        iniConfig.clear();
        iniConfig.save();
    }

    @After
    public void teardown() throws Exception {
        service.stop();
        iniConfig.clear();
        iniConfig.save();
    }

    @Test
    public void start() throws Exception {

        // given
        copySitemapPropertiesToTemp();
        // when
        service.start();
        // then
        assertThat(service.getReport()).isNotNull();
        assertThat(service.isStarted()).isTrue();
        assertThat(sitemap.getNodeCount()).isEqualTo(STANDARD_NODE_COUNT + FILE_NODE_COUNT + DIRECT_NODE_COUNT);
        assertThat(service.getSourceTypes()).containsOnly(SitemapSourceType.FILE, SitemapSourceType.DIRECT, SitemapSourceType.ANNOTATION);
        assertThat(sitemap.getReport()).isNotEmpty();
        System.out.println(sitemap.getReport());
    }

    /**
     * Copies a 'good' version of sitemap.properties to the
     */
    private void copySitemapPropertiesToTemp() {

        File source = new File(TestResource.testJavaRootDir("krail"), "uk/q3c/krail/core/navigate/sitemap_good" + "" +
                ".properties");

        if (!source.exists()) {
            throw new TestConfigurationException("Source file missing");
        }
        File destination = new File(ResourceUtils.applicationBaseDirectory(), "sitemap.properties");
        if (destination.exists()) {
            destination.delete();
        }
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            throw new TestConfigurationException("Unable to copy sitemap.properties", e);
        }
    }

    @Test
    public void nameAndDescription() {

        // given

        // when

        // then

        assertThat(service.getNameKey()).isEqualTo(LabelKey.Sitemap_Service);
        assertThat(service.getDescriptionKey()).isEqualTo(DescriptionKey.Sitemap_Service);
        assertThat(service.getName()).isEqualTo("Sitemap Service");
        assertThat(service.getDescription()).isEqualTo("This service creates the Sitemap using options from the " + "application configuration");
    }

    public void sourcesPropertyMissing() throws Exception {

        // given
        iniConfig.clear();
        iniConfig.save();

        // when
        service.start();
        // then
        assertThat(service.getReport()).isNotNull();
        assertThat(service.isStarted()).isTrue();
        assertThat(sitemap.getNodeCount()).isEqualTo(13);
        assertThat(service.getSourceTypes()).containsOnly(SitemapSourceType.FILE);
    }

    /**
     * Key is there but value is not
     *
     * @throws Exception
     */
    public void sourcesPropertyEmpty() throws Exception {

        // given
        List<String> sources = new ArrayList<>();
        iniConfig.setDelimiterParsingDisabled(true);
        iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES, sources);
        iniConfig.setDelimiterParsingDisabled(false);
        iniConfig.save();

        // when
        service.start();
        // then
        assertThat(service.getReport()).isNotNull();
        assertThat(service.isStarted()).isTrue();
        assertThat(sitemap.getNodeCount()).isEqualTo(13);
        assertThat(service.getSourceTypes()).containsOnly(SitemapSourceType.FILE);
    }

    @Test
    public void invalidSource_butValidOnePresent() throws Exception {
        // given
        List<String> sources = new ArrayList<>();
        sources.add("wiggly");
        sources.add("file");
        iniConfig.setDelimiterParsingDisabled(true);
        iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES, sources);
        iniConfig.setDelimiterParsingDisabled(false);
        iniConfig.save();

        // when
        service.start();
        // then
        assertThat(service.getReport()).isNotNull();
        assertThat(service.isStarted()).isTrue();
        assertThat(sitemap.getNodeCount()).isEqualTo(4);
    }

    @Test
    public void stop() {

        // given

        // when

        // then
        assertThat(service.isLoaded()).isFalse();

    }

    @Test
    public void absolutePathFor() {

        // given

        // when

        // then

        assertThat(service.absolutePathFor("wiggly.ini")).isEqualTo(new File(ResourceUtils.applicationBaseDirectory(), "wiggly.ini"));
        assertThat(service.absolutePathFor("/wiggly.ini")).isEqualTo(new File("/wiggly.ini"));
    }

    @Test(expected = ServiceException.class)
    public void invalidSource_noGoodOnes() throws Exception {

        // given
        List<String> sources = new ArrayList<>();
        sources.add("wiggly");
        sources.add("wobbly");
        iniConfig.setDelimiterParsingDisabled(true);
        iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES, sources);
        iniConfig.setDelimiterParsingDisabled(false);
        iniConfig.save();
        System.out.println(iniConfig.getProperty(ConfigKeys.SITEMAP_SOURCES));

        // when
        service.start();
        // then

    }



    public static class TestDirectSitemapModule extends DirectSitemapModule {

        @Override
        protected void define() {
            addEntry("direct", null, LabelKey.Home_Page, PageAccessControl.PUBLIC);
            addEntry("direct/a", PublicHomeView.class, LabelKey.Home_Page, PageAccessControl.PUBLIC);
            addRedirect("direct", "direct/a");
        }

    }

    public static class TestFileSitemapModule extends FileSitemapModule {

        @Override
        protected void define() {
            File a = new File(TestResource.testJavaRootDir("krail"), "uk/q3c/krail/core/navigate/sitemap_good" + ".properties");
            addEntry("a", new SitemapFile(a.getAbsolutePath()));
        }
    }

}
