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
package uk.co.q3c.v7.persist.orient.db;

import static org.fest.assertions.Assertions.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ IniModule.class })
public class DbConnectionProviderTest {

	@Inject
	OrientDbConnectionProvider dbcp;

	@Test
	public void get() {

		// given

		// when
		OObjectDatabaseTx db = dbcp.get();
		// then
		assertThat(db).isNotNull();
		assertThat(db.getUser().getName()).isEqualTo("admin");

	}

	@ModuleProvider
	public AbstractModule orientModule() {
		V7Ini ini = new V7Ini();
		ini.load();
		return new OrientDbModule(ini);
	}

}
