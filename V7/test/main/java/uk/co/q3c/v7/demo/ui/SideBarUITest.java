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
package uk.co.q3c.v7.demo.ui;

import static org.fest.assertions.Assertions.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.BaseIniModule;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.view.ApplicationViewModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.demo.view.DemoModule;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.TestHelper;
import fixture.TestUIModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, TestUIModule.class, StandardViewModule.class, DemoModule.class,
		V7ShiroVaadinModule.class, BaseIniModule.class })
public class SideBarUITest extends UITestBase {

	@BeforeClass
	public static void setupClass() {
		uiClass = SideBarUI.class;
	}

	@Test
	public void text() {

		// given

		// when

		// then
		assertThat(ui().getTextArea().getValue()).isEqualTo(
				"This sidebar does nothing,except demonstrate the use of two UIs.  See the javadoc for "
						+ DemoUIProvider.class.getSimpleName());

	}

	public SideBarUI ui() {
		return (SideBarUI) ui;
	}

	@ModuleProvider
	private ApplicationViewModule applicationViewModuleProvider() {
		return TestHelper.applicationViewModuleUsingSitemap();
	}

}
