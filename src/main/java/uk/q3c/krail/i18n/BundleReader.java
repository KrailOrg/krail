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


import uk.q3c.krail.core.user.opt.Option;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Common interface for implementations which locate and read a ResourceBundle from implementation specific sources -
 * for example, Java class, properties files, database. Krail uses this to replace code which normally has to be
 * provided in a {@link ResourceBundle.Control} sub-class.
 * <p>
 * <p>
 * Created by David Sowerby on 18/11/14.
 */
public interface BundleReader {

    /**
     * Gets a value from the bundle reader, using the {@code cacheKey} to identify it.  The other parameters are all
     * related to automatically stubbing a value if one does not exist - this allows pre-population of values, or it
     * can also be used to identify keys which have been called, thus allowing them to be prioritised for translation.
     * <p>
     * For class and property file readers, auto-stubbing will only occur if a bundle is found, but there is no entry
     * for the key.  If the bundle is not found, then no stubbing will occur.  Note also that for these types of
     * readers, stubbing is transient, as it is not possible to write directly back to source.  You can however use
     * {@link PatternUtility} to write the bundle out to another location.
     * <p>
     * Other reader implementations (database for example) should be able to write the stub back to persistence
     *
     * @param cacheKey
     *         the cacheKey to identify the value required
     * @param source
     *         the bundle source to be accessed, as defined by {@link I18NModule#addBundleReader(String, Class)}
     * @param autoStub
     *         if true, if the key does not return a value, a stub is automatically generated
     * @param stubWithKeyName
     *         if true, and a key is being auto-stubbed, the name of the key is used as the stub value
     * @param stubValue
     *         if {@code stubWithKeyName} is false, the value of this parameter is used to stub the value
     *
     * @return
     */
    Optional<String> getValue(PatternCacheKey cacheKey, String source, boolean autoStub, boolean stubWithKeyName,
                              String stubValue);

    /**
     * Depending on the setting of {@link Option}s, provide value stubs where keys do not have a value assigned
     *
     * @param cacheKey
     * @param value
     * @param autoStub
     * @param stubWithKeyName
     * @param stubValue
     *
     * @return
     */
    Optional<String> autoStub(PatternCacheKey cacheKey, String value, boolean autoStub, boolean stubWithKeyName,
                              String stubValue);

    /**
     * The same as calling {@link #getValue(PatternCacheKey, String, boolean, boolean, String)} with autoStub==false
     *
     * @param cacheKey
     * @param source
     * @return
     */
    Optional<String> getValue(PatternCacheKey cacheKey, String source);
}
