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

package uk.q3c.krail.i18n;

import com.google.common.base.Converter;
import com.google.common.base.Enums;
import uk.q3c.krail.core.persist.InMemoryContainer;

/**
 * An entity for I18N patter for use with the {@link InMemoryContainer}
 * Created by David Sowerby on 30/06/15.
 */
public class PatternEntity {

    private String i18nkey;
    private String locale;
    private String value;

    public PatternEntity() {
    }

    public PatternEntity(PatternCacheKey cacheKey, String value) {
        final Enum<?> enumKey = cacheKey.getKey();
        Converter<String, ? extends Enum> keyBase = Enums.stringConverter(enumKey.getClass());
        String keyString = keyBase + "." + enumKey.name();
        this.i18nkey = keyString;
        this.locale = cacheKey.getRequestedLocale()
                              .toLanguageTag();
        this.value = value;
    }

    public String getI18nkey() {
        return i18nkey;
    }

    public String getValue() {
        return value;
    }

    public String getLocale() {
        return locale;
    }
}
