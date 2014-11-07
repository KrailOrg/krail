/*
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
package uk.q3c.krail.core.guice.uiscope;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.ui.ScopedUI;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a Guice scope based on a Vaadin UI, generally equivalent to a browser tab
 *
 * @author Will Temperley 2014
 * @author David Sowerby 2013
 */
public class UIScope implements Scope {

    private static Logger log = LoggerFactory.getLogger(UIScope.class);

    private static volatile UIScope current;

    private final Map<UIKey, Map<Key<?>, Object>> cache = new TreeMap<UIKey, Map<Key<?>, Object>>();

    public UIScope() {
        super();
        log.debug("creating UIScope {}", this);
    }

    public static UIScope getCurrent() {
        // double-checked locking with volatile
        UIScope scope = current;
        if (scope == null) {
            synchronized (UIScope.class) {
                scope = current;
                if (scope == null) {
                    current = new UIScope();
                    scope = current;
                }
            }
        }
        return scope;
    }

    <T> Map<Key<?>, Object> getScopedObjectMap(UIKey uiKey) {
        // return an existing cache instance
        if (cache.containsKey(uiKey)) {
            Map<Key<?>, Object> scopedObjects = cache.get(uiKey);
            log.debug("scope cache retrieved for UI key: {}", uiKey);
            return scopedObjects;
        } else {

            return createCacheEntry(uiKey);
        }
    }

    private Map<Key<?>, Object> createCacheEntry(UIKey uiKey) {
        Map<Key<?>, Object> uiEntry = new HashMap<Key<?>, Object>();
        cache.put(uiKey, uiEntry);
        log.debug("created a scope cache for UIScope with key: {}", uiKey);
        return uiEntry;
    }

    public void startScope(UIKey uiKey) {
        if (!cacheHasEntryFor(uiKey)) {
            createCacheEntry(uiKey);
        }
    }

    public boolean cacheHasEntryFor(UIKey uiKey) {
        return cache.containsKey(uiKey);
    }

    public boolean cacheHasEntryFor(ScopedUI ui) {
        return cacheHasEntryFor(ui.getInstanceKey());
    }

    public void releaseScope(UIKey uiKey) {
        cache.remove(uiKey);
    }

    /**
     * Removes all entries in the cache
     */
    public void flush() {
        cache.clear();
    }

    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        return new UIScopeProvider<T>(this, key, unscoped);
    }
}