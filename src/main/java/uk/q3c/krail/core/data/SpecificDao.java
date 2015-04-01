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

package uk.q3c.krail.core.data;

import java.util.List;
import java.util.Optional;

/**
 * An interface for Dao implementations which explicitly state the class they are used for, as opposed to the {@link StatementDao}, which
 * provides CRUD and basic query operations for any valid entity, and {@link BlockDao}, which is supports the processing of blocks of statement within a
 * transaction.
 *
 * Most methods simply call {@link Dao} with an EntityManager or non-JPA equivalent
 * <p>
 * Created by David Sowerby on 06/04/15.
 */
public interface SpecificDao<E extends KrailEntity<ID, VER>, ID, VER> {

    List<E> findAll();

    /**
     * @see Dao#findById(Object, Class, Object)
     */

    Optional<E> findById(ID id);

    /**
     * @see Dao#deleteById(Object, Class, Object)
     */
    Optional<E> deleteById(ID entityId);

    /**
     * Synonymous with {@link #findById(Object)}
     *
     * @param identity
     *         identity of the entity to load
     *
     * @return the object with id, or Optional.empty if not found
     */
    Optional<E> load(ID identity);

    /**
     * @see Dao#tableName(Object, Class)
     */
    String tableName();

    /**
     * Creates and returns a new instance of the entity class
     *
     * @return a new instance of the entity class
     */
    E newEntity();

    /**
     * @see Dao#save(Object, KrailEntity)
     */
    E save(E entity);


}
