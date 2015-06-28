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

import com.google.inject.Inject;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterFactory;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.util.MessageFormat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Optional;

/**
 * Default implementation for {@link StringPersistenceConverter}.  Uses {@link ConverterFactory} to supply the converters
 * <p>
 * Created by David Sowerby on 27/06/15.
 */
public class DefaultStringPersistenceConverter implements StringPersistenceConverter {

    private ConverterFactory converterFactory;

    @Inject
    public DefaultStringPersistenceConverter(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    @Override
    @Nullable
    public Optional<?> convertFromPersistence(@Nonnull OptionCacheKey cacheKey, @Nullable String valueFromPersistence) {
        if (valueFromPersistence == null) {
            return Optional.empty();
        }
        Object defaultValue = cacheKey.getOptionKey()
                                      .getDefaultValue();
        Converter<String, Object> converter = getConverter(Optional.of(defaultValue));
        Object result = converter.convertToModel(valueFromPersistence, Object.class, Locale.UK);
        if (result == null) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    /**
     * Identifies ConverterFactory to identify the correct converter type for the {@code value} provided.
     *
     * @throws ConverterException
     *         if no converter is found
     */
    @Nullable
    protected <V> Converter<String, V> getConverter(@Nonnull Optional<V> value) {

        @SuppressWarnings("unchecked") Class<V> valueClass = (Class<V>) value.get()
                                                                             .getClass();
        Converter<String, V> converter = converterFactory.createConverter(String.class, valueClass);
        if (converter == null) {
            String msg = MessageFormat.format("Data type of {0} is not supported in Option", value.getClass());
            throw new ConverterException(msg);
        }
        return converter;
    }

    @Override
    public <V> Optional<String> convertToPersistence(Optional<V> value) {
        if (!value.isPresent()) {
            return Optional.empty();
        }
        Converter<String, V> converter = getConverter(value);
        String result = converter.convertToPresentation(value.get(), String.class, Locale.UK);
        if (result == null) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }
}
