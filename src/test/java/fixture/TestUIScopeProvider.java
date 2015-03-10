/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package fixture;

import com.google.inject.Key;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by David Sowerby on 10/03/15.
 */
public class TestUIScopeProvider<T> implements Provider<T> {
    private static Logger log = LoggerFactory.getLogger(TestUIScopeProvider.class);
    private final TestUIScope uiScope;
    private final Key<T> key;
    private final Provider<T> unscoped;

    public TestUIScopeProvider(TestUIScope testUIScope, Key<T> key, Provider<T> unscoped) {
        uiScope = testUIScope;
        this.key = key;
        this.unscoped = unscoped;
    }


    @Override
    public T get() {
        Map<Key<?>, Object> scopedObjects = uiScope.getScopedObjectMap();
        T current = unscoped.get();
        scopedObjects.put(key, current);
        log.debug("new instance of " + current.getClass()
                                              .getSimpleName() + " created in TestUIScope, as none in cache");
        return current;
    }
}
