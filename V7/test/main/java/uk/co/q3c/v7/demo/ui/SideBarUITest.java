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

import org.junit.Test;

import uk.co.q3c.v7.base.shiro.ShiroIntegrationTestBase;
import uk.co.q3c.v7.base.ui.BasicUI;

public class SideBarUITest extends ShiroIntegrationTestBase {

	@Test
	public void text() {
		// given
		// ui=
		// when

		// then

	}

	/**
	 * Use this method to create BasicUI instances, rather than the UIProvider It simulates the creation of a new
	 * CurrentInstance (which happens for each request)
	 * 
	 * @return
	 */
	protected BasicUI createSideBarUI() {
		// CurrentInstance.set(UI.class, null);
		// CurrentInstance.set(UIKey.class, null);
		// return (BasicUI) provider.createInstance(SideBarUI.class);
		return null;
	}
}
