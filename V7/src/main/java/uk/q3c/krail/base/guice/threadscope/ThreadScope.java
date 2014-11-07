/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.guice.threadscope;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import uk.q3c.krail.base.guice.threadscope.ThreadCache.Cache;

public class ThreadScope implements Scope {
    private final ThreadCache thread;

    ThreadScope(ThreadCache thread) {
        this.thread = thread;
    }

    /**
     * @see com.google.inject.Scope#scope(com.google.inject.Key, com.google.inject.Provider)
     */
    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
        return new Provider<T>() {
            @Override
            public T get() {
                Cache cache = thread.getCache();
                T value = cache.get(key);
                if (value == null) {
                    value = creator.get();
                    cache.add(key, value);
                }
                return value;
            }
        };
    }
}