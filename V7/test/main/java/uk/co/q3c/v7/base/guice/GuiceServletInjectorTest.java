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

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class GuiceServletInjectorTest {

	GuiceServletInjector out;

	@Mock
	ServletContext servletContext;

	@Mock
	ServletContextEvent servletContextEvent;

	@Before
	public void setup() {
		out = new GuiceServletInjector();
	}

	@Test
	public void contextInitialized() {

		// given
		when(servletContextEvent.getServletContext()).thenReturn(servletContext);
		// when
		out.contextInitialized(servletContextEvent);
		// then
		verify(servletContext).setAttribute(eq(Injector.class.getName()), any(Injector.class));

	}

	@Test
	public void contextDestroyed() {

		// given
		when(servletContextEvent.getServletContext()).thenReturn(servletContext);
		// when
		out.contextDestroyed(servletContextEvent);

		// then
		verify(servletContext).removeAttribute(Injector.class.getName());

	}

	@Test
	public void getInjector() {

		// given

		// when
		Injector injector = out.getInjector();
		// then
		assertThat(SecurityUtils.getSecurityManager()).isNotNull();

	}

}
