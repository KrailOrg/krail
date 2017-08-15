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

import uk.q3c.util.text.MessageFormat2;
import uk.q3c.util.text.MessageFormatMode;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

/**
 * Interface for providing translations of I18N patterns retrieved using {@link I18NKey}.  A check for supported locales can be disabled (generally used by
 * utility classes)
 *
 * Revised by David Sowerby on 14/07/15.
 * Created by David Sowerby on 23/10/14.
 */
public interface Translate extends Serializable {

    /**
     * Returns a translated pattern, with parameters substituted by the provided arguments.  Strictness of matching for pattern parameters
     * to arguments is determined by {@link MessageFormat2} - [strictness] is simply passed on.
     *
     * @param strictness See {@link MessageFormat2}
     * @param checkLocaleIsSupported if true, the locale is first checked to ensure that it is a supported locale, as defined by [I18NModule]
     * @param key                    the pattern key
     * @param locale                 the Locale to use for translation
     * @param arguments              any arguments used for the pattern parameters
     * @return the translated pattern, with parameters substituted by the provided arguments
     * @throws UnsupportedLocaleException if #checkLocaleIsSupported is true, and locale has not been defined as a supported locale in [I18NModule]
     */
    String from(MessageFormatMode strictness, boolean checkLocaleIsSupported, I18NKey key, Locale locale, Object... arguments);

    /**
     * The same as calling [from] with strictness = [MessageFormatMode.STRICT]
     */
    String from(boolean checkLocaleIsSupported, I18NKey key, Locale locale, Object... arguments);


    /**
     * The same as calling [from] with strictness = [MessageFormatMode.STRICT] and checkLocaleIsSupported==true
     */
    String from(I18NKey key, Locale locale, Object... arguments);


    /**
     * The same as calling [from] with strictness = [MessageFormatMode.STRICT], checkLocaleIsSupported==true, locale == CurrentLocale
     */
    String from(I18NKey key, Object... arguments);


    /**
     * convenience method to get Collator instance for the {@link CurrentLocale}
     *
     * @return Collator instance for the {@link CurrentLocale}
     */
    Collator collator();
}




