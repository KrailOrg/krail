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
package uk.co.q3c.v7.base.shiro;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.component.UserStatusPanel;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ UIScopeModule.class, I18NModule.class, VaadinSessionScopeModule.class })
public class V7SecurityManagerTest extends ShiroIntegrationTestBase {

	@Mock
	UserStatusPanel monitor1;

	@Mock
	UserStatusPanel monitor2;

	@Mock
	BasicUI ui;

	@Mock
	VaadinSessionProvider vsp;

	@Mock
	VaadinSession session;

	@Override
	@Before
	public void setupShiro() {

		super.setupShiro();

	}

	@Test
	public void login() {

		// given
		when(vsp.get()).thenReturn(session);
		V7SecurityManager securityManager = (V7SecurityManager) SecurityUtils.getSecurityManager();

		securityManager.setSessionProvider(vsp);
		UsernamePasswordToken token = new UsernamePasswordToken("xxx", "password");
		// when
		getSubject().login(token);
		// then stored in session
		verify(session).setAttribute(eq(Subject.class), any(Subject.class));

	}

	protected ScopedUI createUI() {
		UIKey uiKey = new UIKey(3);
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, uiKey);
		CurrentInstance.set(UI.class, ui);
		when(ui.getInstanceKey()).thenReturn(uiKey);

		return ui;
	}

	@ModuleProvider
	AbstractModule moduleProvider() {
		// creates the UIScope before injections
		createUI();
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}
		};
	}

}
