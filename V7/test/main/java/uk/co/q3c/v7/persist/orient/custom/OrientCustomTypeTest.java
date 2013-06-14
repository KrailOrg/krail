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
package uk.co.q3c.v7.persist.orient.custom;

import static org.fest.assertions.Assertions.*;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;

public class OrientCustomTypeTest {

	OObjectDatabaseTx db;

	@Before
	public void setup() {
		db = new OObjectDatabaseTx("memory:scratchpad");
		db.create();
		OObjectSerializerContext serializerContext = new OObjectSerializerContext();
		serializerContext.bind(new OrientCustomType_DateTime());
		OObjectSerializerHelper.bindSerializerContext(null, serializerContext);
		db.getEntityManager().registerEntityClass(CustomTypeTestEntity.class);
	}

	@Test
	public void newEntitySaveAndUpdate() {

		// given

		// when
		CustomTypeTestEntity entity = db.newInstance(CustomTypeTestEntity.class);
		entity.setDateTime(DateTime.now());
		entity.setLocale(Locale.CANADA_FRENCH);
		db.save(entity);
		db.commit();
		// then
		List<CustomTypeTestEntity> result = db.query(new OSQLSynchQuery<CustomTypeTestEntity>(
				"select * from CustomTypeTestEntity"));
		CustomTypeTestEntity entity2 = result.get(0);
		assertThat(entity2.getDateTime()).isEqualTo(entity.getDateTime());
		assertThat(entity2.getLocale()).isEqualTo(entity.getLocale());

	}

	@After
	public void teardown() {
		db.drop();
	}
}
