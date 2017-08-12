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

import com.google.inject.Inject;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.*;

public class KrailSecurityManager extends DefaultSecurityManager {
    private static Logger log = LoggerFactory.getLogger(KrailSecurityManager.class);


    public KrailSecurityManager(Optional<CacheManager> cacheManagerOpt) {
        setupCacheManager(cacheManagerOpt);
    }

    public KrailSecurityManager(Collection<Realm> realms, Optional<CacheManager> cacheManagerOpt) {
        super(realms);
        setupCacheManager(cacheManagerOpt);
    }

    /**
     * Method injection is needed because the constructor has to complete
     *
     * @see org.apache.shiro.mgt.SessionsSecurityManager#setSessionManager(org.apache.shiro.session.mgt.SessionManager)
     */
    @Inject
    @Override
    public void setSessionManager(SessionManager sessionManager) {
        checkNotNull(sessionManager);
        super.setSessionManager(sessionManager);
    }

    protected final void setupCacheManager(Optional<CacheManager> cacheManagerOpt) {
        if (cacheManagerOpt.isPresent()) {
            setCacheManager(cacheManagerOpt.get());
            log.debug("CacheManager set");
        }

    }
}




