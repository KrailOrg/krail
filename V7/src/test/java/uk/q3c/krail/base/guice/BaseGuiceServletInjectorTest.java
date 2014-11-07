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
package uk.q3c.krail.base.guice;

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
import uk.q3c.krail.base.services.Service;
import uk.q3c.krail.base.services.ServicesMonitor;
import uk.q3c.krail.base.shiro.V7SecurityManager;
import uk.q3c.krail.testutil.LogMonitor;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class BaseGuiceServletInjectorTest {

    static File iniDir = new File("src/test/java");
    static VaadinService vaadinService;
    @Inject
    LogMonitor logMonitor;
    TestGuiceServletInjector out;
    @Mock
    ServletContextEvent servletContextEvent;
    @Mock
    ServletContext servletContext;
    @Mock
    Service service;

    @BeforeClass
    public static void setupClass() {
        vaadinService = mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(iniDir);
        VaadinService.setCurrent(vaadinService);
    }

    @Before
    public void setup() {
        out = new TestGuiceServletInjector();

    }

    @Test
    public void startAndStop() throws Exception {

        // given
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        out.contextInitialized(servletContextEvent);
        // when
        Injector injector = out.getInjector();
        // then
        assertThat(SecurityUtils.getSecurityManager()).isInstanceOf(V7SecurityManager.class);
        assertThat(out.isAddAppModulesCalled()).isEqualTo(true);
        assertThat(injector).isNotNull();

        // given
        ServicesMonitor servicesMonitor = injector.getInstance(ServicesMonitor.class);
        servicesMonitor.registerService(service);

        // when
        out.contextDestroyed(servletContextEvent);

        // then
        verify(service).stop(); // services stopped
        assertThat(logMonitor.infoLogs()).contains("Stopping all services");

    }

    @After
    public void teardown() {
        logMonitor.close();
    }

}
