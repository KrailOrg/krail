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

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.demo.dao.DAOBaseTest;
import uk.co.q3c.v7.demo.dao.orient.OrientDemoUsageLogDAO;
import uk.co.q3c.v7.persist.orient.custom.OrientCustomType_DateTime;
import uk.co.q3c.v7.persist.orient.custom.OrientCustomType_Locale;
import uk.co.q3c.v7.persist.orient.db.OrientDbModule;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;

/**
 * Tests the common data access methods for all OrientDAOs. The test methods are actually from {@link DAOBaseTest}, this
 * class provides the OrientDb specific database setup and teardown. Make sure you call {@link #setup()} and
 * {@link #teardown()} from this class if you sub-class it.
 * 
 * @author David Sowerby 29 Jan 2013
 * 
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ IniModule.class })
public class OrientDAOBaseTest extends DAOBaseTest {

	OObjectDatabaseTx db;

	@Inject
	Provider<OrientDemoUsageLogDAO> daoPro;

	@Before
	public void setup() {
		db = new OObjectDatabaseTx("memory:scratchpad");
		db.create();
		OObjectSerializerContext serializerContext = new OObjectSerializerContext();
		serializerContext.bind(new OrientCustomType_DateTime());
		serializerContext.bind(new OrientCustomType_Locale());
		OObjectSerializerHelper.bindSerializerContext(null, serializerContext);
		dao = daoPro.get();
	}

	@After
	public void teardown() {
		db.drop();
	}

	@ModuleProvider
	public AbstractModule orientModuleProvider() {
		V7Ini ini = new V7Ini();
		ini.load();
		return new OrientDbModule(ini);
	}
}
