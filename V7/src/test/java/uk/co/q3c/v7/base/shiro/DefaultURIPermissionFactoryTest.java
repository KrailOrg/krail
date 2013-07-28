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

import static org.fest.assertions.Assertions.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultURIPermissionFactoryTest {

	@Inject
	DefaultURIPermissionFactory factory;

	@Before
	public void setup() {

	}

	@Test
	public void createPermission() {

		// given
		// when
		URIViewPermission permission = factory.createViewPermission("private");
		// then
		assertThat(permission.toString()).isEqualTo("[uri]:[view]:[private]");

	}

	@Test
	public void createPermission_withWildcard() {

		// given
		// when
		URIViewPermission permission = factory.createViewPermission("private", true);

		// then
		assertThat(permission.toString()).isEqualTo("[uri]:[view]:[private]:[*]");
	}

	@ModuleProvider
	private AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}
}
