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
package uk.co.q3c.v7.base.view.components;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.user.LoginStatusMonitor;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.Button;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class LoginStatusPanelTest {

	LoginStatusMonitor panel;

	@Mock
	Subject subject;

	@Mock
	V7Navigator navigator;

	Button loginoutBtn;

	@Before
	public void setup() {
		panel = new LoginStatusPanel(navigator);
		loginoutBtn = ((LoginStatusPanel) panel).getLogin_logout_Button();
	}

	@Test
	public void unknown() {

		// given
		when(subject.isRemembered()).thenReturn(false);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.getPrincipal()).thenReturn(null);

		// when
		panel.updateStatus(subject);
		// then
		assertThat(panel.getActionLabel()).isEqualTo("log in");
		assertThat(panel.getUserId()).isEqualTo("guest");

		// when
		loginoutBtn.click();
		// then
		verify(navigator).login();
	}

	@Test
	public void remembered() {

		// given
		when(subject.isRemembered()).thenReturn(true);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.getPrincipal()).thenReturn("userId");
		// when
		panel.updateStatus(subject);
		// then
		assertThat(panel.getActionLabel()).isEqualTo("log in");
		assertThat(panel.getUserId()).isEqualTo("userId?");
		// when
		loginoutBtn.click();
		// then
		verify(navigator).login();
	}

	@Test
	public void authenticated() {

		// given
		when(subject.isRemembered()).thenReturn(false);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.getPrincipal()).thenReturn("userId");
		// when
		panel.updateStatus(subject);
		// then
		assertThat(panel.getActionLabel()).isEqualTo("log out");
		assertThat(panel.getUserId()).isEqualTo("userId");
		// when
		loginoutBtn.click();
		// then
		verify(navigator).logout();
	}

}
