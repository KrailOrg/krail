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

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.apache.shiro.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.config.V7IniProvider;
import uk.co.q3c.v7.base.shiro.V7SecurityManager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

@RunWith(JMockit.class)
public class GuiceServletInjectorTest {

	class MockedGuice {

	}

	GuiceServletInjector out;

	@Mocked
	ServletContext servletContext;

	@Mocked
	ServletContextEvent servletContextEvent;

	@Mocked
	Injector injector;

	@Before
	public void setup() {
		out = new GuiceServletInjector();

	}

	@Test
	public void contextInitialized() {

		// given
		new Expectations() {
			{
				servletContextEvent.getServletContext();
				notStrict();
				result = servletContext;
			}
		};
		// when
		out.contextInitialized(servletContextEvent);
		// then
		new Verifications() {
			{
				servletContext.setAttribute(Injector.class.getName(), any);
			}
		};

	}

	@Test
	public void contextDestroyed() {

		// given
		new Expectations() {
			{
				servletContextEvent.getServletContext();
				notStrict();
				result = servletContext;
			}
		};
		// when
		out.contextDestroyed(servletContextEvent);

		// then
		new Verifications() {
			{
				servletContext.removeAttribute(Injector.class.getName());
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getInjector() {

		// given
		new Expectations() {
			@SuppressWarnings("unused")
			Guice mockedGuice;
			@SuppressWarnings("unused")
			SecurityUtils mockedSecurityUtils;
			V7IniProvider iniPro;
			{

				new V7IniProvider();

				iniPro.get();
				result = new V7Ini();

				Guice.createInjector((List<Module>) any);
				result = injector;

			}
		};

		// when
		final Injector createdInjector = out.getInjector();
		// then

		assertThat(createdInjector).isEqualTo(injector);
		new Verifications() {
			{

				SecurityUtils.setSecurityManager((V7SecurityManager) any);
			}
		};

	}
}
