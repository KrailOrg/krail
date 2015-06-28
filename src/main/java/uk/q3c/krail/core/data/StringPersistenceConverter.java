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

package uk.q3c.krail.core.data;

import com.vaadin.data.util.converter.Converter;
import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;

import java.util.Optional;

/**
 * Conversion utility where values are converted to String before persisting
 * <p>
 * Created by David Sowerby on 27/06/15.
 */
public interface StringPersistenceConverter {

    /**
     * Returns a value converted from the String held in persistence.  The value type is determined by the {@link OptionKey#getDefaultValue()}
     *
     * @param cacheKey
     *         the cacheKey to identify the value
     * @param valueFromPersistence
     *         the String value held in persistence
     *
     * @return null if no entry for cacheKey is found, otherwise a value converted from persistence
     *
     * @throws ConverterException
     *         if no converter is available for the type of {@link OptionKey#getDefaultValue()}
     * @throws Converter.ConversionException
     *         if the conversion itself fails
     */
    Optional<?> convertFromPersistence(OptionCacheKey cacheKey, String valueFromPersistence);

    /**
     * Converts the supplied {@code value} to String for persistence
     *
     * @param value
     *         the value to be converted
     * @param <V>
     *         the value type
     *
     * @return String for persistence, null if value is null
     *
     * @throws ConverterException
     *         if no converter is available for the type of {@link OptionKey#getDefaultValue()}
     * @throws Converter.ConversionException
     *         if the conversion itself fails
     */
    <V> Optional<String> convertToPersistence(Optional<V> value);
}
