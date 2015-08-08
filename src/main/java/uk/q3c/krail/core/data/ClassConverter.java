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

import org.apache.commons.lang3.ClassUtils;
import uk.q3c.util.MessageFormat;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Converts an Class to a String representation and back.
 * <p>
 * Created by David Sowerby on 27/06/15.
 */
public class ClassConverter implements OptionConverter<Class<?>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> convertToModel(@Nonnull String value) throws ConversionException {
        checkNotNull(value);
        try {
            return ClassUtils.getClass(this.getClass()
                                           .getClassLoader(), value);
        } catch (Exception e) {
            String msg = MessageFormat.format("Failed to convert String '{0}' to Class", value);
            throw new ConversionException(msg, e);
        }
    }


    @Override
    public String convertToString(@Nonnull Class<?> value) {
        checkNotNull(value);
        return value.getCanonicalName();
    }
}
