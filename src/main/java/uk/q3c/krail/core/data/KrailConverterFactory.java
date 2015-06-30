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
import com.vaadin.data.util.converter.DefaultConverterFactory;
import uk.q3c.krail.i18n.I18NKey;

import java.time.LocalDateTime;
import java.util.Locale;

public class KrailConverterFactory extends DefaultConverterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        if (modelType == String.class) {
            return (Converter<PRESENTATION, MODEL>) new StringConverter();
        } else if (modelType == Locale.class) {
            return (Converter<PRESENTATION, MODEL>) new LocaleConverter();
        } else if (modelType == LocalDateTime.class) {
            return (Converter<PRESENTATION, MODEL>) new DateTimeConverter();
        } else if (modelType == I18NKey.class) {
            return (Converter<PRESENTATION, MODEL>) new I18NKeyConverter();
        } else if (modelType == Enum.class) {
            return (Converter<PRESENTATION, MODEL>) new EnumConverter();
        }

        return super.createConverter(presentationType, modelType);

    }

}
