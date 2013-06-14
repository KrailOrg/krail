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
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.SecurityUtils;
import org.junit.Before;
import org.junit.Test;

import uk.co.q3c.v7.base.shiro.V7SecurityManager;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

public class GuiceServletInjectorTest2 {

	BaseGuiceServletInjector out;

	ServletContext servletContext;

	ServletContextEvent servletContextEvent;

	Injector injector;

	@Before
	public void setup() {
		out = new TestGuiceServletInjector();
		servletContext = mock(ServletContext.class);
		servletContextEvent = mock(ServletContextEvent.class);
	}

	@Test
	public void getInjector() {

		// given
		when(servletContextEvent.getServletContext()).thenReturn(servletContext);
		out.contextInitialized(servletContextEvent);
		// when
		injector = out.getInjector();
		// then
		Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();
		System.out.println(bindings.size());
		assertThat(SecurityUtils.getSecurityManager()).isInstanceOf(V7SecurityManager.class);

	}
}
