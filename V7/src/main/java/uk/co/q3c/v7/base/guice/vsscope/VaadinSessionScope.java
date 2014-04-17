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
package uk.co.q3c.v7.base.guice.vsscope;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.servlet.SessionScoped;
import com.vaadin.server.VaadinSession;

/**
 * Provides a Guice scope based on a {@link VaadinSession}. This was necessary because the standard
 * {@link SessionScoped} will only work with a UI (and not things like Views, which where a session scope is most
 * useful) if a UI has server push enabled. See https://github.com/davidsowerby/v7/issues/241
 * 
 * @author David Sowerby 2014
 * 
 */
public class VaadinSessionScope implements Scope {

	private static Logger log = LoggerFactory.getLogger(VaadinSessionScope.class);

	private static volatile VaadinSessionScope current;

	private final Map<VaadinSession, Map<Key<?>, Object>> cache = new HashMap<>();

	public VaadinSessionScope() {
		super();
		log.debug("creating VaadinSessionScope {}", this);
	}

	<T> Map<Key<?>, Object> getScopedObjectMap(VaadinSession vaadinSession) {
		// return an existing cache instance
		if (cache.containsKey(vaadinSession)) {
			Map<Key<?>, Object> scopedObjects = cache.get(vaadinSession);
			log.debug("scope cache retrieved for VaadinSession: {}", vaadinSession);
			return scopedObjects;
		} else {
			return createCacheEntry(vaadinSession);
		}
	}

	private Map<Key<?>, Object> createCacheEntry(VaadinSession vaadinSession) {
		Map<Key<?>, Object> sessionEntry = new HashMap<Key<?>, Object>();
		cache.put(vaadinSession, sessionEntry);
		log.debug("created a scope cache for VaadinSessionScope with key: {}", vaadinSession);
		return sessionEntry;
	}

	public void startScope(VaadinSession vaadinSession) {
		if (!cacheHasEntryFor(vaadinSession)) {
			createCacheEntry(vaadinSession);
		}
	}

	public boolean cacheHasEntryFor(VaadinSession vaadinSession) {
		return cache.containsKey(vaadinSession);
	}

	public void releaseScope(VaadinSession vaadinSession) {
		cache.remove(vaadinSession);
	}

	public static VaadinSessionScope getCurrent() {
		// double-checked locking with volatile
		VaadinSessionScope scope = current;
		if (scope == null) {
			synchronized (VaadinSessionScope.class) {
				scope = current;
				if (scope == null) {
					current = new VaadinSessionScope();
					scope = current;
				}
			}
		}
		return scope;
	}

	/**
	 * Removes all entries in the cache
	 */
	public void flush() {
		cache.clear();
	}

	@Override
	public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
		return new VaadinSessionScopeProvider<T>(this, key, unscoped);
	}
}