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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Base implementation of {@link BundleReader} for database sources. Sub-class to provide the appropriate {@code patternDaoProvider}, and include the
 * sub-class as an
 * I18N source by calling {@link I18NModule#bundleSource(String, Class)}.
 * <p>
 * Created by David Sowerby on 16/04/15.
 */
public abstract class DatabaseBundleReaderBase extends BundleReaderBase implements DatabaseBundleReader {
    private static Logger log = LoggerFactory.getLogger(DatabaseBundleReaderBase.class);
    private Provider<PatternDao> patternDaoProvider;

    @Inject
    protected DatabaseBundleReaderBase(Provider<PatternDao> patternDaoProvider) {
        super();
        this.patternDaoProvider = patternDaoProvider;
    }


    @Override
    public Optional<String> getValue(PatternCacheKey cacheKey, String source, boolean autoStub, boolean stubWithKeyName, String stubValue) {
        log.debug("getValue for cacheKey {}, source '{}'", cacheKey, source);
        Optional<String> value = patternDaoProvider.get()
                                                   .getValue(cacheKey);
        // TODO should use Optional in autoStub (https://github.com/davidsowerby/krail/issues/367)
        String v = value.isPresent() ? value.get() : null;
        return autoStub(cacheKey, v, autoStub, stubWithKeyName, stubValue);
    }


    @Override
    public void writeStubValue(@Nonnull PatternCacheKey cacheKey, @Nonnull String stubValue) {
        patternDaoProvider.get()
                          .write(cacheKey, stubValue);
    }
}
