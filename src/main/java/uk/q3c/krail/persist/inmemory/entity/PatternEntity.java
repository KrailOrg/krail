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

package uk.q3c.krail.persist.inmemory.entity;

import uk.q3c.krail.i18n.persist.PatternCacheKey;
import uk.q3c.krail.persist.InMemory;
import uk.q3c.util.data.EnumConverter;

/**
 * An entity for I18N pattern for use with the {@link InMemory}
 * Created by David Sowerby on 30/06/15.
 */
public class PatternEntity {

    private final String i18nkey;
    private final String locale;
    private final String value;



    public PatternEntity(PatternCacheKey cacheKey, String value) {
        Enum enumKey = cacheKey.getKeyAsEnum();
        this.i18nkey = new EnumConverter().convertToString(enumKey);
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
