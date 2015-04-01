/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.data;/*
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

import java.util.List;

/**
 * A non-database specific Dao
 *
 * @author David Sowerby 29 Jan 2013
 *
 * @param <E> The entity class
 *           @param <OID> the object Id type
 *                       @param <VER> the type used to represent an object version
 */
public interface Dao<E, OID, VER> {

    E save(E entity);

    void commit();

    void close();

    E newEntity();

    /**
     * Returns The object representing the entity id.
     *
     * @param entity
     *         for which you want the identity
     *
     * @return
     */

    OID getIdentity(E entity);

    E load(OID identity);

    List<E> findAll();

    VER getVersion(E entity);

    void beginTransaction();

    E delete(E entity);

}
