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
import org.apache.commons.lang3.ClassUtils;
import uk.q3c.krail.i18n.I18NException;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.util.MessageFormat;

import java.util.Locale;

/**
 * Converts an I18NKey in full - that is, the class and constant as a single String.
 * <p>
 * Created by David Sowerby on 27/06/15.
 */
public class I18NKeyConverter implements Converter<String, I18NKey> {
    /**
     * {@inheritDoc}
     */
    @Override
    public I18NKey convertToModel(String value, Class<? extends I18NKey> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }
        //slight mis-use of ClassUtils - it is just about the structure of the String
        String constantName = ClassUtils.getShortClassName(value);
        String className = ClassUtils.getPackageName(value);

        try {
            final Class<? extends Enum> clazz = (Class<? extends Enum>) Class.forName(className);
            final Enum anEnum = Enum.valueOf(clazz, constantName);
            if (I18NKey.class.isAssignableFrom(clazz)) {
                return (I18NKey) anEnum;
            }
            throw new I18NException("Does not implement I18NKey");
        } catch (Exception e) {
            String msg = MessageFormat.format("Failed to convert String '{0}' to Enum", value);
            throw new ConversionException(msg, e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(I18NKey value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }
        Enum e = (Enum) value;
        final String className = e.getClass()
                                  .getName();
        final String constantName = e.name();
        return className + "." + constantName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<I18NKey> getModelType() {
        return I18NKey.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
