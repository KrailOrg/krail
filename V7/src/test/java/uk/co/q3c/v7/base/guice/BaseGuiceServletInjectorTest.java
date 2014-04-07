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
package uk.co.q3c.v7.base.guice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.SecurityUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.shiro.V7SecurityManager;

import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class BaseGuiceServletInjectorTest {

	TestGuiceServletInjector out;

	// @Mock
	// ThreadLocal<ServletContext> ctx;

	@Mock
	ServletContextEvent servletContextEvent;

	@Mock
	ServletContext servletContext;

	@Before
	public void setup() {
		out = new TestGuiceServletInjector();

	}

	static File iniDir = new File("src/test/java");
	static VaadinService vaadinService;

	@BeforeClass
	public static void setupClass() {
		vaadinService = mock(VaadinService.class);
		when(vaadinService.getBaseDirectory()).thenReturn(iniDir);
		VaadinService.setCurrent(vaadinService);
	}

	@Test
	public void startAndStop() {

		// given
		// when(ctx.get()).thenReturn(servletContext);
		when(servletContextEvent.getServletContext()).thenReturn(servletContext);
		out.contextInitialized(servletContextEvent);
		// when
		Injector injector = out.getInjector();
		// then
		assertThat(SecurityUtils.getSecurityManager()).isInstanceOf(V7SecurityManager.class);
		assertThat(out.isAddAppModulesCalled()).isEqualTo(true);
		// verify(ctx).set(servletContext);
		assertThat(injector).isNotNull();

		// when
		out.contextDestroyed(servletContextEvent);
		// ## all services stopped
	}

	/**
	 * Context not initialised, injector not created
	 */
	@Test(expected = IllegalStateException.class)
	public void notInitialised() {

		// given
		// when(ctx.get()).thenReturn(servletContext);
		when(servletContextEvent.getServletContext()).thenReturn(servletContext);
		// when
		out.getInjector();
		// then

		fail("exception expected");
	}

}
