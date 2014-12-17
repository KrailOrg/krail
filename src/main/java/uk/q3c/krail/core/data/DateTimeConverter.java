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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * Implements a Converter to handle the Java 8 LocalDateTime type
 *
 * * @author David Sowerby 16 Dec 2014
 * @author David Sowerby 1 Apr 2013
 */
public class DateTimeConverter implements Converter<Date, LocalDateTime> {

    @Override
    public Class<LocalDateTime> getModelType() {
        return LocalDateTime.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }

    @Override
    public LocalDateTime convertToModel(Date value, Class<? extends LocalDateTime> targetType, Locale locale) throws
            com.vaadin.data.util.converter.Converter.ConversionException {

        return value.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
    }

    @Override
    public Date convertToPresentation(LocalDateTime value, Class<? extends Date> targetType, Locale locale) throws
            com.vaadin.data.util.converter.Converter.ConversionException {
        Instant instant = value.atZone(ZoneId.systemDefault())
                               .toInstant();
        return Date.from(instant);
    }

}
