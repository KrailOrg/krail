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

import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.util.guava.GuavaCacheConfiguration;

import java.util.Optional;

/**
 * Provider for a {@link LoadingCache} with a {@link DefaultOptionCacheLoader}
 * <p>
 * Created by David Sowerby on 21/02/15.
 */
public class DefaultOptionCacheProvider implements OptionCacheProvider {
    private static Logger log = LoggerFactory.getLogger(DefaultOptionCacheProvider.class);
    private final GuavaCacheConfiguration cacheConfiguration;
    private final DefaultOptionCacheLoader cacheLoader;

    @Inject
    protected DefaultOptionCacheProvider(DefaultOptionCacheLoader cacheLoader, @OptionCacheConfig GuavaCacheConfiguration
            cacheConfiguration) {
        this.cacheLoader = cacheLoader;
        this.cacheConfiguration = cacheConfiguration;
    }

    @Override

    public LoadingCache<OptionCacheKey, Optional<?>> get() {
        //noinspection unchecked
        log.debug("returning new instance of cache");
        return cacheConfiguration.builder()
                                 .build(cacheLoader);
    }


}
