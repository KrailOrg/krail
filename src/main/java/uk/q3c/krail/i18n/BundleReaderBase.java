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

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by David Sowerby on 16/04/15.
 */
public abstract class BundleReaderBase implements BundleReader {

    @Override
    public Optional<String> autoStub(PatternCacheKey cacheKey, String value, boolean autoStub, boolean stubWithKeyName, String stubValue) {
        if (value == null) {
            if (autoStub) {
                I18NKey key = (I18NKey) cacheKey.getKey();
                String stub;
                if (stubWithKeyName) {
                    stub = ((Enum<?>) key).name();
                } else {
                    stub = stubValue;
                }
                writeStubValue(cacheKey, stub);
                return Optional.of(stub);
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(value);
    }


    @Override
    public Optional<String> getValue(@Nonnull PatternCacheKey cacheKey,  @Nonnull String source, @Nonnull String stubValue) {
        return getValue(cacheKey, source, true, false, stubValue);
    }

}
