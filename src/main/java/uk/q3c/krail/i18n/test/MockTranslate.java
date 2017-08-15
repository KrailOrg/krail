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

package uk.q3c.krail.i18n.test;

import com.google.common.base.Joiner;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.i18n.UnsupportedLocaleException;
import uk.q3c.util.text.MessageFormatException;
import uk.q3c.util.text.MessageFormatMode;

import java.text.Collator;
import java.util.Locale;

import static com.google.common.base.Preconditions.*;


/**
 * Very simple Mock for testing with Translate - just returns the I18NKey.name(), with underscores replaced by spaces, and with parameters and Locale and Locale
 * appended ... for example: <br></br>
 * <p>
 * <p>
 * Created by David Sowerby on 09 Feb 2016
 */
public class MockTranslate implements Translate {
    private boolean failOnCheckLocaleIsSupported = false;
    private boolean returnNameOnly = true;
    private boolean failOnStrictness = false;


    @Override
    public String from(MessageFormatMode strictness, boolean checkLocaleIsSupported, I18NKey key, Locale locale, Object... arguments) {
        checkNotNull(key);
        checkNotNull(locale);
        if (checkLocaleIsSupported) {
            if (failOnCheckLocaleIsSupported) {
                throw new UnsupportedLocaleException(locale);
            }
        }
        if (failOnStrictness) {
            throw new MessageFormatException("Fake fail on strictness");
        }
        String n = ((Enum) key).name().replace('_', ' ');
        if (returnNameOnly) {
            return n;
        } else {
            String args = "";
            if (arguments.length > 0) {
                args = "-" + Joiner.on(",").join(arguments);

            }
            return n + "-" + locale.toLanguageTag() + args;
        }
    }

    @Override
    public String from(boolean checkLocaleIsSupported, I18NKey key, Locale locale, Object... arguments) {
        return from(MessageFormatMode.STRICT, checkLocaleIsSupported, key, locale, arguments);
    }

    @Override
    public String from(I18NKey key, Locale locale, Object... arguments) {
        return from(MessageFormatMode.STRICT, true, key, locale, arguments);
    }

    @Override
    public String from(I18NKey key, Object... arguments) {
        return from(MessageFormatMode.STRICT, true, key, Locale.UK, arguments);
    }

    @Override
    public Collator collator() {
        throw new RuntimeException("Not implemented in Mock");
    }


}