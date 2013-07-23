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

import static org.mockito.Mockito.*;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.ui.ScopedUI;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ UIScopeModule.class })
public class V7SecurityManagerTest extends ShiroIntegrationTestBase {

	@Mock
	LoginStatusMonitor monitor1;

	@Mock
	LoginStatusMonitor monitor2;

	@Mock
	BasicUI ui;

	@Override
	@Before
	public void setupShiro() {

		super.setupShiro();

	}

	@Test
	public void listeners() {

		// given
		V7SecurityManager securityManager = (V7SecurityManager) SecurityUtils.getSecurityManager();
		securityManager.addListener(monitor1);
		securityManager.addListener(monitor2);
		UsernamePasswordToken token = new UsernamePasswordToken("xxx", "password");
		// when
		getSubject().login(token);
		// then
		// subject may get re-created, so cannot rely on the instance
		verify(monitor1, times(1)).updateStatus();
		verify(monitor2, times(1)).updateStatus();

		// when
		getSubject().logout();
		// 1 already recorded, plus 1 for logout
		verify(monitor1, times(2)).updateStatus();
		verify(monitor2, times(2)).updateStatus();
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
				bind(URIPermissionFactory.class).to(DefaultURIPermissionFactory.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}
		};
	}

}
