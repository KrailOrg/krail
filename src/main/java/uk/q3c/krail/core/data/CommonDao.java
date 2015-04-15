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
import java.util.List;
import java.util.Optional;

/**
 * A dao which provides only single statements  (save, delete etc), each wrapped in a transaction.   To use multiple statements (a statement block) wrapped
 * in a single transaction, see  {@link BlockDao}
 * <p>
 * Implementations usually use a {@link Dao} for data access
 *
 * @param <ID>
 *         the Id type
 * @param <VER>
 *         the type used to represent an entity version
 *
 * @author David Sowerby 29 Jan 2013
 */
public interface CommonDao<ID, VER> {

    /**
     * @see Dao#save(Object, KrailEntity)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> E save(@Nonnull E entity);


    @Nonnull
    <E extends KrailEntity<ID, VER>> ID getIdentity(@Nonnull E entity);

    /**
     * @see Dao#findAll(Object, Class)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> List<E> findAll(@Nonnull Class<E> entityClass);


    @Nonnull
    <E extends KrailEntity<ID, VER>> VER getVersion(@Nonnull E entity);

    /**
     * @see Dao#tableName(Object, Class)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> String tableName(@Nonnull Class<E> entityClass);

    /**
     * @see Dao#delete(Object, KrailEntity)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> void delete(@Nonnull E entity);


    /**
     * @see Dao#deleteById(Object, Class, Object)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> Optional<E> deleteById(@Nonnull Class<E> entityClass, @Nonnull ID entityId);

    /**
     * Synonymous with {@link #findById(Class, Object)}
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> Optional<E> load(@Nonnull Class<E> entityClass, @Nonnull ID identity);

    /**
     * @see Dao#merge(Object, KrailEntity)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> E merge(@Nonnull E entity);

    /**
     * @see Dao#findById(Object, Class, Object)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> Optional<E> findById(@Nonnull Class<E> entityClass, @Nonnull ID id);

    /**
     * @see Dao#count(Object, Class)
     */
    @Nonnull
    <E extends KrailEntity<ID, VER>> long count(@Nonnull Class<E> entityClass);

    /**
     * @return connection String for this Dao
     *
     * @see Dao#connectionUrl(Object)
     */
    String connectionUrl();
}
