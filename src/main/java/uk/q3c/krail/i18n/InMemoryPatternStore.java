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

package uk.q3c.krail.i18n;

import com.google.common.collect.ImmutableMap;

/**
 * Created by David Sowerby on 25/06/15.
 */
public interface InMemoryPatternStore {
    void put(PatternCacheKey cacheKey, String value);

    String remove(PatternCacheKey cacheKey);

    String get(PatternCacheKey cacheKey);

    ImmutableMap<PatternCacheKey, String> getAsMap();
}
