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
package uk.co.q3c.v7.base.guice;

import java.util.List;

import com.google.inject.Module;

import fixture.TestUIModule;

public class TestGuiceServletInjector extends BaseGuiceServletInjector {

	private boolean addAppModulesCalled;

	// private final ThreadLocal<ServletContext> testctx;

	protected TestGuiceServletInjector() {
		super();
		// this.testctx = testctx;
	}

	@Override
	protected void addAppModules(List<Module> baseModules) {

		baseModules.add(new TestUIModule());
		baseModules.add(new DummyModule());
		addAppModulesCalled = true;
	}

	// @Override
	// protected ThreadLocal<ServletContext> createThreadLocalServletContext() {
	// return testctx;
	// }

	public boolean isAddAppModulesCalled() {
		return addAppModulesCalled;
	}

}
