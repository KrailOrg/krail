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

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Implements a Converter to handle {@link BigDecimal}
 */
public class BigDecimalConverter implements Converter<String, BigDecimal> {

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal convertToModel(String value, Class<? extends BigDecimal> targetType, Locale locale) throws ConversionException {
        return new BigDecimal(value);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(BigDecimal value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<BigDecimal> getModelType() {
        return BigDecimal.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
