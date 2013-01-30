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

import uk.co.q3c.v7.demo.dao.DAOBaseTest;
import uk.co.q3c.v7.demo.dao.orient.OrientDemoUsageLogDAO;
import uk.co.q3c.v7.persist.orient.custom.OrientCustomType_DateTime;
import uk.co.q3c.v7.persist.orient.custom.OrientCustomType_Locale;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;

/**
 * Uses DemoUsageLog as the DAO but for no particular reason - the methods tested are all from OrientDAOBase
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
}
