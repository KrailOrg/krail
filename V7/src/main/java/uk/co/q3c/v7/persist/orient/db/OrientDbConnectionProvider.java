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

import javax.inject.Inject;

import uk.co.q3c.v7.persist.orient.custom.OrientCustomType_DateTime;
import uk.co.q3c.v7.persist.orient.custom.OrientCustomType_Locale;

import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;

/**
 * There seemed no benefit to making this a Guice provider - it is only ever used by one class.
 */

public class OrientDbConnectionProvider {

	private final String dbURL;
	private boolean initialised = false;
	private final String pwd;
	private final String user;

	@Inject
	protected OrientDbConnectionProvider(@DbURL String dbURL, @DbUser String user, @DbPwd String pwd) {
		super();
		this.dbURL = dbURL;
		this.user = user;
		this.pwd = pwd;
	}

	public OObjectDatabaseTx get() {
		if (!initialised) {
			initialise();
		}

		OObjectDatabaseTx db = OObjectDatabasePool.global().acquire(dbURL, user, pwd);
		return db;
	}

	private void initialise() {
		OObjectDatabaseTx db = new OObjectDatabaseTx(dbURL);
		if (!db.exists()) {
			db.create();
		}
		OObjectSerializerContext serializerContext = new OObjectSerializerContext();
		serializerContext.bind(new OrientCustomType_DateTime());
		serializerContext.bind(new OrientCustomType_Locale());
		OObjectSerializerHelper.bindSerializerContext(null, serializerContext);
		initialised = true;
	}

}
