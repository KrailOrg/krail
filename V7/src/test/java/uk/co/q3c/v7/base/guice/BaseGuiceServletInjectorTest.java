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

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.services.Service;
import uk.co.q3c.v7.base.guice.services.ServicesManager;
import uk.co.q3c.v7.base.shiro.V7SecurityManager;

import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class BaseGuiceServletInjectorTest {

	TestGuiceServletInjector out;

	@Mock
	ThreadLocal<ServletContext> ctx;

	@Mock
	ServletContextEvent servletContextEvent;

	@Mock
	ServletContext servletContext;

	@Before
	public void setup() {
		out = new TestGuiceServletInjector(ctx);

	}

	@Test
	public void startAndStop() {

		// given
		when(ctx.get()).thenReturn(servletContext);
		when(servletContextEvent.getServletContext()).thenReturn(servletContext);
		out.contextInitialized(servletContextEvent);
		// when
		Injector injector = out.getInjector();
		// then
		assertThat(SecurityUtils.getSecurityManager()).isInstanceOf(V7SecurityManager.class);
		assertThat(out.isAddAppModulesCalled(), is(true));
		verify(ctx).set(servletContext);
		assertThat(injector, is(not(nullValue())));
		ServicesManager servicesManager = injector.getInstance(ServicesManager.class);
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));

		// when
		out.contextDestroyed(servletContextEvent);
		assertThat(servicesManager.getStatus(), is(Service.Status.STOPPED));
	}

	/**
	 * Context not initialised, injector not created
	 */
	@Test(expected = IllegalStateException.class)
	public void notInitialised() {

		// given
		when(ctx.get()).thenReturn(servletContext);
		when(servletContextEvent.getServletContext()).thenReturn(servletContext);
		// when
		out.getInjector();
		// then

		fail("exception expected");
	}

}
