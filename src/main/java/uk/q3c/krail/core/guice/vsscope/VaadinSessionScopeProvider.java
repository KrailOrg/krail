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
package uk.q3c.krail.core.guice.vsscope;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.vaadin.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @param <T>
 *
 * @author David Sowerby
 */
class VaadinSessionScopeProvider<T> implements Provider<T> {
    private static Logger log = LoggerFactory.getLogger(VaadinSessionScopeProvider.class);
    private final VaadinSessionScope vaadinSessionScope;
    private final Key<T> key;
    private final Provider<T> unscoped;

    VaadinSessionScopeProvider(VaadinSessionScope vaadinSessionScope, Key<T> key, Provider<T> unscoped) {
        this.vaadinSessionScope = vaadinSessionScope;
        this.key = key;
        this.unscoped = unscoped;
    }

    @Override
    public T get() {
        // get the scope cache for the current UI
        log.debug("looking for a VaadinSessionScoped instance of {}", key.getClass()
                                                                         .getName());

        // get the current VaadinSession
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        log.debug("looking for cache for key in current VaadinSession ");
        Map<Key<?>, Object> scopedObjects = this.vaadinSessionScope.getScopedObjectMap(vaadinSession);

        // retrieve an existing instance if possible

        @SuppressWarnings("unchecked") T current = (T) scopedObjects.get(key);

        if (current != null) {
            log.debug("returning existing instance of {}", current.getClass()
                                                                  .getSimpleName());
            return current;
        }

        // or create the first instance and cache it
        current = unscoped.get();
        scopedObjects.put(key, current);
        log.debug("new instance of {} created, as none in cache", current.getClass()
                                                                         .getSimpleName());
        return current;
    }
}
