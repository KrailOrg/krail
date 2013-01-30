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
package uk.co.q3c.v7.demo.usage;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import javax.inject.Provider;

import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.V7SecurityManager;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.demo.dao.DemoUsageLogDAO;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.WebBrowser;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DemoUsageTest {

	DemoUsage demoUsage;

	@Mock
	Subject subject;

	@Mock
	V7SecurityManager securityManager;

	@Mock
	V7Navigator navigator;

	@Mock
	Provider<DemoUsageLogDAO> daoPro;

	@Mock
	Provider<WebBrowser> browserPro;

	@Mock
	V7ViewChangeEvent event;

	@Mock
	DemoUsageLogDAO dao;

	@Mock
	WebBrowser browser;

	@Before
	public void setup() {
		demoUsage = new DemoUsage(securityManager, navigator, daoPro, browserPro);
	}

	@Test
	public void beforeViewChange() {

		// given

		// when
		boolean response = demoUsage.beforeViewChange(event);
		// then
		assertThat(response).isTrue();

	}

	@Test
	public void afterViewChange() {

		// given
		DemoUsageLog entry = new DemoUsageLog();
		when(daoPro.get()).thenReturn(dao);
		when(dao.newEntity()).thenReturn(entry);
		when(browserPro.get()).thenReturn(browser);
		when(browser.getLocale()).thenReturn(Locale.CANADA_FRENCH);
		when(browser.getAddress()).thenReturn("81.81.81.81");
		when(event.getViewName()).thenReturn("view 1");
		// when
		demoUsage.afterViewChange(event);
		// then
		verify(dao).save(entry);
		assertThat(entry.getEvent()).isEqualTo("view change to view 1");

	}

	@Test
	public void subjectStatus_loggedin() {

		// given
		DemoUsageLog entry = new DemoUsageLog();
		when(subject.isAuthenticated()).thenReturn(true);
		when(daoPro.get()).thenReturn(dao);
		when(dao.newEntity()).thenReturn(entry);
		when(browserPro.get()).thenReturn(browser);
		when(browser.getLocale()).thenReturn(Locale.CANADA_FRENCH);
		when(browser.getAddress()).thenReturn("81.81.81.81");

		// when
		demoUsage.updateStatus(subject);
		// then
		verify(dao).save(entry);
		assertThat(entry.getEvent()).isEqualTo("login");

	}

	@Test
	public void subjectStatus_loggedout() {

		// given
		DemoUsageLog entry = new DemoUsageLog();
		when(subject.isAuthenticated()).thenReturn(false);
		when(daoPro.get()).thenReturn(dao);
		when(dao.newEntity()).thenReturn(entry);
		when(browserPro.get()).thenReturn(browser);
		when(browser.getLocale()).thenReturn(Locale.CANADA_FRENCH);
		when(browser.getAddress()).thenReturn("81.81.81.81");
		// when
		demoUsage.updateStatus(subject);
		// then
		verify(dao).save(entry);
		assertThat(entry.getEvent()).isEqualTo("logout");

	}
}
