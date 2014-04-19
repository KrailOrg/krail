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
package uk.co.q3c.v7.base.view.component;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.DefaultSubjectIdentifier;
import uk.co.q3c.v7.base.shiro.SubjectIdentifier;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.user.status.UserStatus;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.Button;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class })
public class LoginStatusPanelTest {

	DefaultLoginStatusPanel panel;

	@Mock
	Subject subject;

	@Mock
	V7Navigator navigator;

	Button loginoutBtn;

	@Mock
	SubjectProvider subjectProvider;

	@Mock
	UserStatus userStatus;

	SubjectIdentifier subjectIdentifier;

	@Inject
	Translate translate;

	@Before
	public void setup() {
		when(subjectProvider.get()).thenReturn(subject);
		subjectIdentifier = new DefaultSubjectIdentifier(subjectProvider, translate);

		panel = new DefaultLoginStatusPanel(navigator, subjectProvider, translate, subjectIdentifier, userStatus);

		loginoutBtn = panel.getLogin_logout_Button();
	}

	@Test
	public void unknown() {

		// given
		when(subject.isRemembered()).thenReturn(false);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.getPrincipal()).thenReturn(null);

		// when
		panel.userStatusChanged();
		// then
		assertThat(panel.getActionLabel()).isEqualTo("log in");
		assertThat(panel.getUserId()).isEqualTo("Guest");

		// when
		loginoutBtn.click();
		// then
		verify(navigator).navigateTo(StandardPageKey.Login);
	}

	@Test
	public void remembered() {

		// given
		when(subject.isRemembered()).thenReturn(true);
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.getPrincipal()).thenReturn("userId");
		// when
		panel.userStatusChanged();
		// then
		assertThat(panel.getActionLabel()).isEqualTo("log in");
		assertThat(panel.getUserId()).isEqualTo("userId?");
		// when
		loginoutBtn.click();
		// then
		verify(navigator).navigateTo(StandardPageKey.Login);
	}

	@Test
	public void authenticated() {

		// given
		when(subject.isRemembered()).thenReturn(false);
		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.getPrincipal()).thenReturn("userId");
		// when
		panel.userStatusChanged();
		// then
		assertThat(panel.getActionLabel()).isEqualTo("log out");
		assertThat(panel.getUserId()).isEqualTo("userId");
		// when
		loginoutBtn.click();
		// then
		verify(navigator).navigateTo(StandardPageKey.Logout);
	}

}
