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
package uk.co.q3c.v7.demo.dao;

import static org.fest.assertions.Assertions.*;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.q3c.v7.demo.usage.DemoUsageLog;

public abstract class DAOBaseTest {

	protected DemoUsageLogDAO dao;

	@Test
	public void newEntitySaveAndUpdate() {

		// given

		// when
		DemoUsageLog entity = dao.newEntity();
		entity.setEvent("login");
		entity.setDateTime(DateTime.now());
		entity.setLocaleString(Locale.FRENCH.toString());
		entity.setSourceIP("80.2.03.99");
		dao.save(entity);

		// then
		assertThat(dao.getIdentity(entity)).isNotNull();

		// when
		dao.commit();
		List<DemoUsageLog> result = dao.findAll();
		DemoUsageLog entity2 = result.get(0);

		// then
		assertThat(dao.getIdentity(entity2)).isEqualTo(dao.getIdentity(entity));
		assertThat(entity2.getSourceIP()).isEqualTo(entity.getSourceIP());
		assertThat(entity2.getEvent()).isEqualTo(entity.getEvent());
		assertThat(entity2.getDateTime()).isEqualTo(entity.getDateTime());
		assertThat(entity2.getLocaleString()).isEqualTo(entity.getLocaleString());
	}

	@Test
	public void load() {
		// given
		DemoUsageLog entity = dao.newEntity();

		entity.setEvent("login");
		entity.setSourceIP("80.2.03.99");
		dao.save(entity);
		dao.commit();

		// when
		Object eid1 = dao.getIdentity(entity);
		DemoUsageLog entity3 = dao.load(eid1);

		// then
		assertThat(entity.getSourceIP()).isEqualTo(entity3.getSourceIP());
		assertThat(entity.getEvent()).isEqualTo(entity3.getEvent());
	}
}
