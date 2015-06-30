/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.data;

import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * Implements a Converter to handle {@link Locale}
 */
public class LocaleConverter implements Converter<String, Locale> {


    /**
     * {@inheritDoc}
     */
    @Override
    public Locale convertToModel(String value, Class<? extends Locale> targetType, Locale locale) throws ConversionException {
        return Locale.forLanguageTag(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(Locale value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value.toLanguageTag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Locale> getModelType() {
        return Locale.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
