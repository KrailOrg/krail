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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.data.DataModule;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Default database implementation of BundleReader.  The binding for {@link PatternDao} is in {@link DataModule}
 *
 * Created by David Sowerby on 16/04/15.
 */
public class DefaultDatabaseBundleReader extends BundleReaderBase implements DatabaseBundleReader {
    private static Logger log = LoggerFactory.getLogger(DefaultDatabaseBundleReader.class);
    private PatternDao patternDao;

    @Inject
    protected DefaultDatabaseBundleReader(PatternDao patternDao) {
        super();
        this.patternDao = patternDao;
    }


    @Override
    public Optional<String> getValue(PatternCacheKey cacheKey, String source, boolean autoStub, boolean stubWithKeyName, String stubValue) {
        log.debug("getValue for cacheKey {}, source '{}'", cacheKey, source);
        Optional<String> value = patternDao.getValue(cacheKey);
        // TODO should use Optional in autoStub (https://github.com/davidsowerby/krail/issues/367)
        String v = value.isPresent() ? value.get() : null;
        return autoStub(cacheKey, v, autoStub, stubWithKeyName, stubValue);
    }


    @Override
    public void writeStubValue(@Nonnull PatternCacheKey cacheKey, @Nonnull String stubValue) {
        patternDao.write(cacheKey, stubValue);
    }
}
