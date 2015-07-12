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

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

/**
 * A common interface to enable the writing of {@link EnumResourceBundle} implementations.
 * <p/>
 * Created by David Sowerby on 25/11/14.
 */
public interface BundleWriter {

    /**
     * Sets the bundle to write
     *
     * @param bundle
     */
    <E extends Enum<E>> void setBundle(EnumResourceBundle<E> bundle);


    /**
     * Achieves the same as {@link #setBundle(EnumResourceBundle)}, but obtains the enum class from the {@code sampleKey}
     *
     * @param sampleKey
     */
    <E extends Enum<E> & I18NKey> void setBundle(E sampleKey);

    /**
     * Writes the bundle out, using the bundleName (if appropriate to the implementation)
     *
     * @param locale
     * @param bundleName
     *
     * @throws IOException
     */
    void write(Locale locale, Optional<String> bundleName) throws IOException;
}
