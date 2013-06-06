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
package uk.co.q3c.v7.persist.dao;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.TestV7IniProvider;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.demo.config.DemoIni;
import uk.co.q3c.v7.demo.config.TestDemoIniProvider;
import uk.co.q3c.v7.demo.dao.DAOBaseTest;
import uk.co.q3c.v7.demo.dao.orient.OrientDemoUsageLogDAO;
import uk.co.q3c.v7.persist.orient.dao.OrientDAO;
import uk.co.q3c.v7.persist.orient.db.OrientDbModule;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * Tests the common data access methods for all OrientDAOs. The test methods are actually from {@link DAOBaseTest}, this
 * class provides the OrientDb specific database setup and teardown. Make sure you call {@link #setup()} and
 * {@link #teardown()} from this class if you sub-class it.
 * 
 * @author David Sowerby 29 Jan 2013
 * 
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class OrientDAOBaseTest extends DAOBaseTest {

	OObjectDatabaseTx db;

	@Inject
	Provider<OrientDemoUsageLogDAO> daoPro;

	@Before
	public void setup() {
		dao = daoPro.get();
	}

	@After
	public void teardown() {

		((OrientDAO) dao).getDb().drop();
	}

	@ModuleProvider
	public AbstractModule orientModuleProvider() {
		// use provider to make sure ini is correctly initialised
		DemoIni ini = new TestDemoIniProvider().get();
		return new OrientDbModule(ini);
	}
}
