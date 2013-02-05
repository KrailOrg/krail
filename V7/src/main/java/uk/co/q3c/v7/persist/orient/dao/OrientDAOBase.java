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
package uk.co.q3c.v7.persist.orient.dao;

import java.util.List;

import uk.co.q3c.v7.base.entity.EntityBase;
import uk.co.q3c.v7.demo.usage.DemoUsageLog;
import uk.co.q3c.v7.persist.dao.DAOBase;
import uk.co.q3c.v7.persist.orient.db.OrientDbConnectionProvider;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * Base class for OrientDB DAO classes. <b>NOTE:</b> Remember that OrientDB lazy-loads fields when you access them via
 * getters. If you look at the field values in the debugger, they will be null until their respective getters are
 * called.
 * 
 * @author David Sowerby 28 Jan 2013
 * 
 * @param <T>
 */
public abstract class OrientDAOBase<T extends EntityBase> implements DAOBase<T>, OrientDAO {
	private final OObjectDatabaseTx db;

	protected OrientDAOBase(OrientDbConnectionProvider dbPro) {
		super();
		this.db = dbPro.get();
		db.getEntityManager().registerEntityClass(entityClass());
	}

	protected abstract Class<T> entityClass();

	@Override
	public T save(T entity) {
		return db.save(entity);
	}

	@Override
	public void commit() {
		db.commit();
	}

	@Override
	public void close() {
		db.close();
	}

	@Override
	public T newEntity() {
		return db.newInstance(entityClass());
	}

	@Override
	public Object getIdentity(T entity) {
		return db.getIdentity(entity);
	}

	@Override
	public T load(Object identity) {
		// cast is needed, otherwise byte enhancer gets confused
		return db.load((ORID) identity);
	}

	@Override
	public List<T> findAll() {
		return db.query(new OSQLSynchQuery<DemoUsageLog>("select * from " + entityClass().getSimpleName()));
	}

	@Override
	public OObjectDatabaseTx getDb() {
		return db;
	}

}
