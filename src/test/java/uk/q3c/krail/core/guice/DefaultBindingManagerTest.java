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
package uk.q3c.krail.core.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;
import org.apache.shiro.SecurityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.service.DefaultServicesModel;
import uk.q3c.krail.core.service.Service;
import uk.q3c.krail.core.service.ServiceKey;
import uk.q3c.krail.core.service.ServicesModel;
import uk.q3c.krail.core.shiro.KrailSecurityManager;
import uk.q3c.krail.testutil.dummy.Dummy;
import uk.q3c.krail.testutil.guice.TestBindingManager;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.testutil.LogMonitor;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({UtilsModule.class})
public class DefaultBindingManagerTest {

    static File iniDir = new File("src/test/java");
    static VaadinService vaadinService;
    @Inject
    LogMonitor logMonitor;

    @Mock
    ServletContextEvent servletContextEvent;
    @Mock
    ServletContext servletContext;
    @Mock
    Service service;
    @Mock
    ServicesModel servicesModel;


    @BeforeClass
    public static void setupClass() {
        vaadinService = mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(iniDir);
        VaadinService.setCurrent(vaadinService);
    }

    @Before
    public void setup() {
        logMonitor.addClassFilter(DefaultBindingManager.class);

    }

    @Test
    public void startAndStop() throws Exception {

        //then
//        assertThat(DefaultBindingManager.injector()).isNull();
        // given
        TestBindingManager bindingManager = new TestBindingManager();
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        bindingManager.contextInitialized(servletContextEvent);
        when(service.getServiceKey()).thenReturn(new ServiceKey(LabelKey.Yes));
        logMonitor.addClassFilter(DefaultServicesModel.class);


        // when
        Injector injector = bindingManager.getInjector();
        // then
        assertThat(SecurityUtils.getSecurityManager()).isInstanceOf(KrailSecurityManager.class);
        assertThat(injector).isNotNull();
        assertThat(injector.getInstance(Dummy.class)).isNotNull();
        assertThat(DefaultBindingManager.injector()).isEqualTo(injector);

        // when
        bindingManager.contextDestroyed(servletContextEvent);

        // then
        assertThat(logMonitor.infoLogs()).contains("Stopping all service");


    }



    @After
    public void teardown() {
        logMonitor.close();
    }


}
