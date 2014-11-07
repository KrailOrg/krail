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
package uk.q3c.krail.core.navigate;

import uk.q3c.krail.i18n.I18NKey;

import java.util.Set;

@SuppressWarnings("rawtypes")
public class LabelKeyForName {
    private final Class<? extends Enum> labelKeysClass;

    public LabelKeyForName(Class<? extends Enum> labelKeysClass) {
        super();
        this.labelKeysClass = labelKeysClass;

    }

    @SuppressWarnings({"unchecked"})
    public I18NKey<?> keyForName(String keyName, Set<String> missingEnums) {
        if (keyName == null) {
            // don't add to missingEnums, null can be legitimate
            return null;
        }
        try {
            Enum labelKey = Enum.valueOf(labelKeysClass, keyName);
            I18NKey<?> i18nKey = (I18NKey<?>) labelKey;
            return i18nKey;
        } catch (Exception e) {
            // flagAsMissing
            if (missingEnums != null) {
                missingEnums.add(keyName);
            }
            return null;
        }
    }
}
