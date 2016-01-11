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
import com.vaadin.server.VaadinService;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
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
import uk.q3c.krail.core.navigate.sitemap.set.MasterSitemapQueue;
import uk.q3c.krail.core.services.Service;
import uk.q3c.krail.core.services.ServiceStatus;
import uk.q3c.krail.core.services.ServicesModel;
import uk.q3c.krail.core.services.ServicesModule;
import uk.q3c.krail.core.shiro.DefaultShiroModule;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.ui.DefaultUIModule;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.view.PublicHomeView;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.DefaultComponentModule;
import uk.q3c.krail.i18n.DescriptionKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.krail.testutil.TestPersistenceModule;
import uk.q3c.krail.util.DefaultResourceUtils;
import uk.q3c.krail.util.ResourceUtils;
import uk.q3c.krail.util.UtilsModule;

import java.io.File;
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
@GuiceContext({TestDirectSitemapModule.class, UIScopeModule.class, ViewModule.class, EventBusModule.class, ServicesModule.class,
        ShiroVaadinModule.class, TestI18NModule.class, SitemapModule.class, UserModule.class, ApplicationConfigurationModule.class, DefaultShiroModule.class,
        DefaultComponentModule.class, TestPersistenceModule.class, StandardPagesModule.class, VaadinSessionScopeModule.class, TestOptionModule.class,
        NavigationModule.class, UtilsModule.class, DefaultUIModule.class})
public class DefaultSitemapServiceTest {

    static VaadinService vaadinService;
    static ResourceUtils resourceUtils = new DefaultResourceUtils();
    private final int FILE_NODE_COUNT = 4;
    private final int DIRECT_NODE_COUNT = 2;
    private final int STANDARD_NODE_COUNT = 5;
    @Inject
    DefaultSitemapService service;
    @Inject
    ServicesModel servicesModel;
    @Inject
    MasterSitemapQueue masterSitemapQueue;
    HierarchicalINIConfiguration iniConfig;

    @BeforeClass
    public static void setupClass() {
        vaadinService = mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(resourceUtils.userTempDirectory());
        VaadinService.setCurrent(vaadinService);
    }

    @Before
    public void setup() throws ConfigurationException {

        File inifile = new File(resourceUtils.userTempDirectory(), "WEB-INF/krail.ini");
        iniConfig = new HierarchicalINIConfiguration(inifile);
        iniConfig.clear();
        iniConfig.save();
        servicesModel.addService(service.getServiceKey());
    }

    @After
    public void teardown() throws Exception {
        service.stop();
        iniConfig.clear();
        iniConfig.save();
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
        assertThat(masterSitemapQueue.getCurrentModel()
                                     .getNodeCount()).isEqualTo(13);
        assertThat(service.getSourceTypes()).containsOnly();
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
        assertThat(masterSitemapQueue.getCurrentModel()
                                     .getNodeCount()).isEqualTo(13);
        assertThat(service.getSourceTypes()).containsOnly();
    }

    @Test
    public void invalidSource_butValidOnePresent() throws Exception {
        // given
        List<String> sources = new ArrayList<>();
        sources.add("wiggly");
        sources.add("direct");
        iniConfig.setDelimiterParsingDisabled(true);
        iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES, sources);
        iniConfig.setDelimiterParsingDisabled(false);
        iniConfig.save();

        // when
        service.start();
        // then
        assertThat(service.getReport()).isNotNull();
        assertThat(service.isStarted()).isTrue();
        assertThat(masterSitemapQueue.getCurrentModel()
                                     .getNodeCount()).isEqualTo(7);
    }

    @Test
    public void stop() {

        // given

        // when

        // then
        assertThat(service.isLoaded()).isFalse();

    }

//

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
        ServiceStatus status = service.start();
        // then
        assertThat(status.getState()).isEqualTo(Service.State.STOPPED);
        assertThat(status.getCause()).isEqualTo(Service.Cause.FAILED_TO_START);

    }



    public static class TestDirectSitemapModule extends DirectSitemapModule {

        @Override
        protected void define() {
            addEntry("direct", null, LabelKey.Home_Page, PageAccessControl.PUBLIC);
            addEntry("direct/a", PublicHomeView.class, LabelKey.Home_Page, PageAccessControl.PUBLIC);
            addRedirect("direct", "direct/a");
        }

    }



}
