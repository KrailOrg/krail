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

package uk.q3c.krail.core.shiro;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;

import java.util.Optional;

/**
 * Forms a base for sub-classing {@link AuthorizingRealm} while also setting up a cache from Guice if required
 * <p>
 * Created by David Sowerby on 02 Jan 2016
 */
public abstract class AuthorizingRealmBase extends AuthorizingRealm {

    public AuthorizingRealmBase(Optional<CacheManager> cacheManagerOpt) {
        super();
        if (cacheManagerOpt.isPresent()) {
            setCacheManager(cacheManagerOpt.get());
            setCachingEnabled(true);
        } else {
            setCachingEnabled(false);
        }
    }
}
