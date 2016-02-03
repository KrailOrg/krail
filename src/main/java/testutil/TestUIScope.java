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
package testutil;

import com.google.inject.Key;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.uiscope.UIScope;

import java.util.HashMap;
import java.util.Map;

public class TestUIScope extends UIScope {
    private static Logger log = LoggerFactory.getLogger(TestUIScope.class);
    private Map<Key<?>, Object> scopedObjects;

    public TestUIScope() {
        this.scopedObjects = new HashMap<>();
    }

    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        log.debug("returning UI scoped instance for {}", unscoped.getClass()
                                                                 .getName());
        return new TestUIScopeProvider<T>(this, key, unscoped);
    }

    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
}
