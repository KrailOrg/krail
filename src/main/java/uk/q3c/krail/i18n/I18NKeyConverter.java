/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.i18n;

import uk.q3c.util.data.ConversionException;
import uk.q3c.util.data.DataItemConverter;
import uk.q3c.util.data.EnumConverter;

/**
 * Converts an I18NKey in full - that is, the class and constant as a single String.
 * <p>
 * Created by David Sowerby on 27/06/15.
 */
public class I18NKeyConverter implements DataItemConverter<I18NKey> {
    /**
     * {@inheritDoc}
     */
    @Override
    public I18NKey convertToModel(String value) throws ConversionException {
        Enum e = new EnumConverter().convertToModel(value);
        return (I18NKey) e;
    }


    @Override
    public String convertToString(I18NKey value) {
        return new EnumConverter().convertToString((Enum) value);

    }
}
