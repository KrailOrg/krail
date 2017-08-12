/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.persist.cache.option;

import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.persist.common.option.OptionDao;
import uk.q3c.krail.core.persist.inmemory.option.DefaultInMemoryOptionStore;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import java.util.Optional;

import static com.google.common.base.Preconditions.*;

/**
 * Extends {@link CacheLoader} implementation which finds the options appropriate for the key provided (see {@link
 * OptionCacheKey#getRankOption()}) - this may be the value for highest in rank, lowest in rank or a specific rank
 * for the {@link UserHierarchy}.
 * <p>
 * A DAO is used to enable selection of different persistence methods, including an in-memory option {@link
 * DefaultInMemoryOptionStore} for testing (provided as part of the Krail core).  For a JPA version see the krail-jpa library.
 * <p>
 * This implementation calls for all assigned values in a hierarchy for the current user, before selecting the one
 * with the highest or lowest rank.  This is because it is usually more efficient for I/O to load this way
 * <p>
 * Created by David Sowerby on 19/02/15.
 */
public class DefaultOptionCacheLoader extends CacheLoader<OptionCacheKey, Optional<?>> {
    private static Logger log = LoggerFactory.getLogger(DefaultOptionCacheLoader.class);
    private final OptionDao daoWrapper;

    @Inject
    public DefaultOptionCacheLoader(OptionDao daoWrapper) {
        this.daoWrapper = daoWrapper;
    }

    /**
     * Loads a value from persistence, returning an empty Optional if none found
     *
     * @param cacheKey the key whose value should be loaded.  Loading takes account of the setting of RankOption in the {@code cacheKey}
     * @return the value associated with {@code key}; The interface requires that this <b>must not be null</b> so this
     * implementation uses an Optional to return the value - which may be empty, as it is legitimate for there to be no
     * value in persistence - this also has the advantage that the cache then knows that there is no value for this key
     * and won't need to load again to find out.
     * @throws Exception            if unable to load the result
     * @throws InterruptedException if this method is interrupted. {@code InterruptedException} is treated like any
     *                              other {@code Exception} in all respects except that, when it is caught, the thread's interrupt status is set
     */
    @Override

    public Optional<?> load(final OptionCacheKey cacheKey) throws Exception {
        checkNotNull(cacheKey);
        log.debug("retrieving value for {}", cacheKey);
        return daoWrapper.getValue(cacheKey);

    }
}