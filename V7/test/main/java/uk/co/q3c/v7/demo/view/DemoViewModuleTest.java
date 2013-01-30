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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.config.V7Ini.StandardPageKey;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.ui.V7UIModule;
import uk.co.q3c.v7.base.view.V7View;

import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, V7UIModule.class, DemoViewModule.class,
		V7ShiroVaadinModule.class, IniModule.class })
public class DemoViewModuleTest extends UITestBase {

	@Inject
	Map<String, Provider<V7View>> viewProMap;

	@Inject
	V7Ini ini;

	@Override
	@Before
	public void setup() {

	}

	@Test
	public void allStandardPagesHaveViews() {

		// given

		// when

		// then
		for (StandardPageKey key : StandardPageKey.values()) {
			String uri = ini.standardPageURI(key);
			assertThat(viewProMap.get(uri)).overridingErrorMessage(uri + " does not have a matching View").isNotNull();
		}

	}

}
