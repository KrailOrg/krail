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

import com.google.inject.Provider;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

/**
 * Base implementation of {@link BundleReader} for database sources. Sub-class to provide the appropriate {@code patternDaoProvider}
 * <p>
 * <p>
 * Created by David Sowerby on 11/07/15.
 */
public abstract class DatabaseBundleWriterBase implements DatabaseBundleWriter {

    private Class<? extends Enum> enumClass;
    private Provider<PatternDao> patternDaoProvider;
    private Translate translate;

    protected DatabaseBundleWriterBase(Provider<PatternDao> patternDaoProvider, Translate translate) {
        this.patternDaoProvider = patternDaoProvider;
        this.translate = translate;
    }

    /**
     * Unsupported
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public <E extends Enum<E>> void setBundle(EnumResourceBundle<E> bundle) {
        throw new UnsupportedOperationException("Use setBundle(sampleKey) instead");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E> & I18NKey> void setBundle(E sampleKey) {
        enumClass = I18NKey.enumClass(sampleKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Locale locale, Optional<String> bundleName) throws IOException {
        Enum[] constants = enumClass.getEnumConstants();
        for (Enum e : constants) {
            I18NKey i18NKey = (I18NKey) e;
            String value = translate.from(i18NKey, locale);
            doWrite(e, locale, value);
        }
    }


    private <E extends Enum<E> & I18NKey> void doWrite(Enum key, Locale locale, String value) {
        E key1 = (E) key;
        PatternCacheKey cacheKey = new PatternCacheKey(key1, locale);
        patternDaoProvider.get()
                          .write(cacheKey, value);
    }
}
