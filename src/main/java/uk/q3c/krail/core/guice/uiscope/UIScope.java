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
package uk.q3c.krail.core.guice.uiscope;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.util.guice.GuiceKeyProxy;
import uk.q3c.util.guice.SerializationSupport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a Guice scope based on a Vaadin UI, generally equivalent to a browser tab
 *
 * @author Will Temperley 2014
 * @author David Sowerby 2013
 */
public class UIScope implements Scope, Serializable {

    private static Logger log = LoggerFactory.getLogger(UIScope.class);

    private static volatile UIScope current;

    private transient Map<UIKey, Map<Key<?>, Object>> cache = new TreeMap<UIKey, Map<Key<?>, Object>>();
    private SerializationSupport serializationSupport;

    public UIScope() {
        super();
    }

    public static UIScope getCurrent() {
        // double-checked locking with volatile
        UIScope scope = current;
        if (scope == null) {
            synchronized (UIScope.class) {
                scope = current;
                if (scope == null) {
                    current = new UIScope();
                    log.debug("created new UIScope");
                    scope = current;
                    log.debug("new UIScope is set as current");
                }
            }
        }
        return scope;
    }

    Map<Key<?>, Object> getScopedObjectMap(UIKey uiKey) {
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

    public ImmutableList<UIKey> scopeKeys() {
        return ImmutableList.copyOf(cache.keySet());
    }

    public boolean containsInstance(UIKey uiKey, Object containedInstance) {
        return cache.get(uiKey)
                    .containsValue(containedInstance);
    }

    /**
     * Writes out the cache by using {@link GuiceKeyProxy} to replace {@link Key}.  It also
     *
     * @param out
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        Map<UIKey, Map<GuiceKeyProxy, Serializable>> proxyMap = new HashMap<>();
        for (Map.Entry<UIKey, Map<Key<?>, Object>> entry : cache.entrySet()) {
            Map<GuiceKeyProxy, Serializable> targetDetail = new HashMap<>();
            proxyMap.put(entry.getKey(), targetDetail);
            Map<Key<?>, Object> sourceDetail = entry.getValue();
            for (Map.Entry<Key<?>, Object> detailEntry : sourceDetail.entrySet()) {
                GuiceKeyProxy proxy = new GuiceKeyProxy(detailEntry.getKey());
                Object detailValue = detailEntry.getValue();
                if (Serializable.class.isAssignableFrom(detailValue.getClass())) {
                    byte[] serValue = SerializationUtils.serialize(detailValue.getClass());
                    targetDetail.put(proxy, serValue);
                } else {
                    targetDetail.put(proxy, proxy);
                }

            }
        }
        out.writeObject(proxyMap);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        cache = new TreeMap<>();
        @SuppressWarnings("unchecked")
        Map<UIKey, Map<GuiceKeyProxy, Serializable>> proxyMap = (Map<UIKey, Map<GuiceKeyProxy, Serializable>>) in.readObject();
        for (Map.Entry<UIKey, Map<GuiceKeyProxy, Serializable>> entry : proxyMap.entrySet()) {
            Map<Key<?>, Object> cacheDetail = new HashMap<Key<?>, Object>();
            cache.put(entry.getKey(), cacheDetail);
            Map<GuiceKeyProxy, Serializable> sourceDetail = entry.getValue();
            for (Map.Entry<GuiceKeyProxy, Serializable> sourceDetailEntry : sourceDetail.entrySet()) {
                Object sourceDetailValue = sourceDetailEntry.getValue();
                Key<?> guiceKey = sourceDetailEntry.getKey().getKey();
                if (sourceDetailValue instanceof GuiceKeyProxy) {
                    GuiceKeyProxy proxy = (GuiceKeyProxy) sourceDetailValue;
                    cacheDetail.put(guiceKey, serializationSupport.getInjector().getInstance(proxy.getKey()));
                } else {
                    Object value = SerializationUtils.deserialize((byte[]) sourceDetailEntry.getValue());
                    cacheDetail.put(guiceKey, value);
                }

            }
        }
    }

    /**
     * This class is not instantiated through Guice, so we set {@link #serializationSupport} directly, usually from the UI
     * that owns this scope.  We need it for deserialisation
     */
    public void setSerializationSupport(SerializationSupport serializationSupport) {
        this.serializationSupport = serializationSupport;
    }
}