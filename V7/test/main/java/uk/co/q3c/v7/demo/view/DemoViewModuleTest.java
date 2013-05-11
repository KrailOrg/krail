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
package uk.co.q3c.v7.demo.view;

import static org.fest.assertions.Assertions.*;

import java.util.Map;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.Sitemap;
import uk.co.q3c.v7.base.navigate.StandardPageKeys;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.view.ApplicationViewModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.base.view.V7View;

import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.TestHelper;
import fixture.TestUIModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, TestUIModule.class, StandardViewModule.class,
		V7ShiroVaadinModule.class, IniModule.class, DemoModule.class })
public class DemoViewModuleTest extends UITestBase {

	@Inject
	Map<String, Provider<V7View>> viewProMap;

	@Inject
	V7Ini ini;

	@Inject
	Sitemap sitemap;

	@BeforeClass
	public static void setupClass() {
		uiClass = BasicUI.class;
	}

	@Test
	public void allStandardPagesHaveViews() {

		// given
		// when

		// then
		for (StandardPageKeys key : StandardPageKeys.values()) {
			String uri = sitemap.standardPageURI(key);
			assertThat(viewProMap.get(uri)).overridingErrorMessage(uri + " does not have a matching View").isNotNull();
		}

	}

	@ModuleProvider
	private ApplicationViewModule applicationViewModuleProvider() {
		return TestHelper.applicationViewModuleUsingSitemap();
	}

}