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

import java.util.Map;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.uiscope.UIKeyProvider;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

@RunWith(JMockit.class)
@GuiceContext({})
// @GuiceContext({ DemoUIModule.class, V7ShiroVaadinModule.class, DemoViewModule.class, UIScopeModule.class,
// DemoDAOModule.class })
public class DemoUIProviderTest {

	@Tested
	DemoUIProvider uiProvider;
	@Injectable
	UISelectCounter selectCounter;
	@Injectable
	Injector injector;
	@Injectable
	Map<String, Provider<UI>> uiProMap;
	@Injectable
	UIKeyProvider mainwindowKeyProvider;

	@Mocked
	UIClassSelectionEvent event;

	@Before
	public void setup() {

	}

	@Test
	public void getUIClass_1() {

		// given
		new Expectations() {
			{
				selectCounter.getCounter();
				result = 1;
			}
		};
		// when
		Class<? extends UI> clazz = uiProvider.getUIClass(event);
		// then
		assertThat(clazz).isEqualTo(SideBarUI.class);

	}

	@Test
	public void getUIClass_2() {

		// given
		new Expectations() {
			{
				selectCounter.getCounter();
				result = 2;
			}
		};
		// when
		Class<? extends UI> clazz = uiProvider.getUIClass(event);
		// then
		assertThat(clazz).isEqualTo(DemoUI.class);

	}

	// @ModuleProvider
	// public AbstractModule orientModuleProvider() {
	// // use provider to make sure ini is correctly initialised
	// V7Ini ini = new TestV7IniProvider().get();
	// return new OrientDbModule(ini);
	// }
}
