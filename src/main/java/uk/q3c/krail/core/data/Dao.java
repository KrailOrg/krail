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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * A general purpose Dao interface
 * <p>
 * Created by David Sowerby on 08/04/15.
 */
public interface Dao<ID, VER> {

    /**
     * Saves {@code entity} as a new item, or updates the existing one if there is already one with the same Id
     *

     *         
     * @param entity
     *         the entity to save
     * @param <E>
     *         the type of the entity
     *
     * @return the saved entity (which could have been merged with an existing entity)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> E save(@Nonnull E entity);

    /**
     * Find an entity from its Id
     *

     *         
     * @param entityId
     *         the id of the entity to find
     * @param <E>
     *         the type of the entity
     *
     * @return Optional with entity found, or Optional.empty() if not found
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> Optional<E> findById(@Nonnull Class<E> entityClass, @Nonnull ID entityId);

    /**
     * Retrieves the table name for the {@code entityClass}.  The name is as defined by @Table, or defaults to the entityClass.getSimpleName()
     *

     *         
     * @param entityClass
     *         the class of the entity, used to identify
     * @param <E>
     *         the type of the entity
     *
     * @return the table name
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> String tableName(@Nonnull Class<E> entityClass);

    /**
     * Deletes {@code entity}, or fails silently if {@code entity} is not in the database.
     *

     *
     * @param <E>
     *         the type of the entity
     * @param entity
     *         the entity to delete
     */
    <E extends KrailEntity<ID, VER>> Optional<? extends KrailEntity> delete(@Nonnull E entity);

    /**
     * Deletes the entity identified by {@code entityId}, within class {@code entityClass}.  Returns the deleted entity or Optional.empty if none found to
     * match the {@code entityId}
     *

     *         
     * @param entityClass
     *         class of the entity to delete
     * @param entityId
     *         the id of the entity to find
     * @param <E>
     *         the type of the entity
     *
     * @return the deleted entity or Optional.empty if none found to
     * match the {@code entityId}
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> Optional<E> deleteById(@Nonnull Class<E> entityClass, @Nonnull ID entityId);

    /**
     * Merge the state of the given entity into the current persistence context.
     *

     *         
     * @param entity
     *         the entity to merge
     * @param <E>
     *         the type of the entity
     *
     * @return the merged entity
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> E merge(@Nonnull E entity);

    /**

     *         
     * @param entityClass
     *         class of the entity to return, which will be translated into the table name
     * @param <E>
     *         the type of the entity
     *
     * @return a list of all entities for {@code entityClass} accessible to this {@code entityManager}, an empty list if none found
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> List<E> findAll(@Nonnull Class<E> entityClass);

    /**
     * Returns the version for {@code entity}
     *

     *         
     * @param entity
     *         the entity for which the version is required
     * @param <E>
     *         the type of the entity
     *
     * @return the version of the entity
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> VER getVersion(@Nonnull E entity);


    /**
     * Returns the version for {@code entity}
     *

     *         
     * @param entity
     *         the entity for which the version is required
     * @param <E>
     *         the type of the entity
     *
     * @return the identity of the entity, may be null if the ID is  a nullable type
     */
    @Nullable
    <E extends KrailEntity<ID, VER>> ID getIdentity(@Nonnull E entity);

    /**
     * Count the entries for {@code entityClass}
     *

     *         
     * @param entityClass
     *         the entity class to be counted
     * @param <E>
     *
     * @return a count of all the entries for {@code entityClass}
     */
    <E extends KrailEntity<ID, VER>> long count(@Nonnull Class<E> entityClass);

    /**
     * Returns the url connection as specified by PersistenceUnitProperties.JDBC_URL
     *
     *
     * @return the url connection as specified by PersistenceUnitProperties.JDBC_URL
     */
    String connectionUrl();
}
