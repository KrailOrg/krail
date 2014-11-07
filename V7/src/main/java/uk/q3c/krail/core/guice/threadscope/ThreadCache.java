/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.guice.threadscope;

import com.google.inject.Key;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple thread scope implementation for Guice, Apache 2.0 licensed. Enjoy!
 *
 */

/**
 * @author Robbie Vanbrabant
 * @see CustomScopes#THREAD
 */
public class ThreadCache {
    // use lazy readFromEnvironment to avoid memory overhead when not using the scope?
    private static final ThreadLocal<Cache> THREAD_LOCAL = new ThreadLocal<Cache>() {
        @Override
        protected Cache initialValue() {
            return new Cache();
        }
    };

    public ThreadCache() {
    }

    public Cache getCache() {
        return THREAD_LOCAL.get();
    }

    /**
     * Execute this if you plan to reuse the same thread, e.g. in a servlet environment threads might get reused.
     * Preferably, call this method in a finally block to make sure that it executes, so that you avoid possible memory
     * leaks.
     */
    public void reset() {
        THREAD_LOCAL.remove();
    }

    /**
     * Cache class for type capture and minimizing ThreadLocal lookups.
     */
    public static class Cache {
        private final Map<Key<?>, Object> map = new HashMap<Key<?>, Object>();

        public Cache() {
        }

        // suppress warnings because the add method
        // captures the type
        @SuppressWarnings("unchecked")
        public <T> T get(Key<T> key) {
            return (T) map.get(key);
        }

        public <T> void add(Key<T> key, T value) {
            map.put(key, value);
        }
    }
}
