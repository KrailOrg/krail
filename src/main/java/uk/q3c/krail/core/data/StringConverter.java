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

import java.util.Locale;

/**
 * Slightly bizarre converting a String to a String, but it makes calling conversion methods simpler - the caller does not need to check for a string first.
 * <p>
 * Created by David Sowerby on 30/06/15.
 */
public class StringConverter implements Converter<String, String> {
    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
