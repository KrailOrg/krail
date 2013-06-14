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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Injector;

@RunWith(JMockit.class)
public class GuiceServletInjectorTest {

	class MockedGuice {

	}

	BaseGuiceServletInjector out;

	@Mocked
	ServletContext servletContext;

	@Mocked
	ServletContextEvent servletContextEvent;

	@Mocked
	Injector injector;

	@Before
	public void setup() {
		out = new TestGuiceServletInjector();

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

}
