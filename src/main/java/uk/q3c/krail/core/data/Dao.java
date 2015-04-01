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

import uk.q3c.krail.util.QualityReview1;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * A Dao which requires the supply of an EntityManager (or non-JPA equivalent) for each method call. Methods are not transactional. Implementations are used to
 * compose {@link BlockDao} and {@link StatementDao} implementations, and not generally used directly.
 * <p>
 * Created by David Sowerby on 08/04/15.
 */
@QualityReview1
public interface Dao<ID, VER, EM> {

    /**
     * Saves {@code entity} as a new item, or updates the existing one if there is already one with the same Id
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entity
     *         the entity to save
     * @param <E>
     *         the type of the entity
     *
     * @return the saved entity (which could have been merged with an existing entity)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> E save(@Nonnull EM entityManager, @Nonnull E entity);

    /**
     * Find an entity from its Id
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entityId
     *         the id of the entity to find
     * @param <E>
     *         the type of the entity
     *
     * @return Optional with entity found, or Optional.empty() if not found
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> Optional<E> findById(@Nonnull EM entityManager, @Nonnull Class<E> entityClass, @Nonnull ID entityId);

    /**
     * Retrieves the table name for the {@code entityClass}.  The name is as defined by metadata, or defaults to the entityClass.getSimpleName()
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entityClass
     *         the class of the entity, used to identify
     * @param <E>
     *         the type of the entity
     *
     * @return the table name
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> String tableName(@Nonnull EM entityManager, @Nonnull Class<E> entityClass);

    /**
     * Deletes {@code entity}, or fails silently if {@code entity} is not in the database.
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entity
     *         the entity to delete
     * @param <E>
     *         the type of the entity
     */
    <E extends KrailEntity<ID, VER>> void delete(@Nonnull EM entityManager, @Nonnull E entity);

    /**
     * Deletes the entity identified by {@code entityId}, within class {@code entityClass}.  Returns the deleted entity or Optional.empty if none found to
     * match the {@code entityId}
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
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
    <E extends KrailEntity<ID, VER>> Optional<E> deleteById(@Nonnull EM entityManager, @Nonnull Class<E> entityClass, @Nonnull ID entityId);

    /**
     * Merge the state of the given entity into the current persistence context.
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entity
     *         the entity to merge
     * @param <E>
     *         the type of the entity
     *
     * @return the merged entity
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> E merge(@Nonnull EM entityManager, @Nonnull E entity);

    /**
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entityClass
     *         class of the entity to return, which will be translated into the table name
     * @param <E>
     *         the type of the entity
     *
     * @return a list of all entities for {@code entityClass} accessible to this {@code entityManager}, an empty list if none found
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> List<E> findAll(@Nonnull EM entityManager, @Nonnull Class<E> entityClass);

    /**
     * Returns the version for {@code entity}
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entity
     *         the entity for which the version is required
     * @param <E>
     *         the type of the entity
     *
     * @return the version of the entity
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> VER getVersion(@Nonnull EM entityManager, @Nonnull E entity);


    /**
     * Returns the version for {@code entity}
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entity
     *         the entity for which the version is required
     * @param <E>
     *         the type of the entity
     *
     * @return the identity of the entity, may be null if the ID is  a nullable type
     */
    @Nullable
    <E extends KrailEntity<ID, VER>> ID getIdentity(@Nonnull EM entityManager, @Nonnull E entity);

    /**
     * Count the entries for {@code entityClass}
     *
     * @param entityManager
     *         entityManager (or non-JPA equivalent) used to access the data
     * @param entityClass
     *         the entity class to be counted
     * @param <E>
     *
     * @return a count of all the entries for {@code entityClass}
     */
    <E extends KrailEntity<ID, VER>> long count(@Nonnull EM entityManager, @Nonnull Class<E> entityClass);

    /**
     * Returns the url connection as specified by PersistenceUnitProperties.JDBC_URL
     *
     * @param entityManager
     *         the entityManager making the connection
     *
     * @return the url connection as specified by PersistenceUnitProperties.JDBC_URL
     */
    String connectionUrl(@Nonnull EM entityManager);
}
