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

package uk.q3c.krail.testutil.i18n;

import uk.q3c.krail.core.i18n.I18NKey;
import uk.q3c.krail.core.i18n.Translate;

import java.text.Collator;
import java.util.Locale;

/**
 * Very simple Mock for testing with Translate - just returns the I18NKey.name(), with underscores replaced by spaces, and with parameters and Locale ignored
 * <p>
 * Created by David Sowerby on 09 Feb 2016
 */
public class MockTranslate implements Translate {
    @Override
    public String from(boolean checkLocaleIsSupported, I18NKey key, Locale locale, Object... arguments) {
        return from(key, arguments);
    }

    @Override
    public String from(I18NKey key, Locale locale, Object... arguments) {
        return from(key, arguments);
    }

    @Override
    public String from(I18NKey key, Object... arguments) {
        return (((Enum) key).name()).replace('_', ' ');
    }

    @Override
    public Collator collator() {
        throw new RuntimeException("Not yet implemented");
    }
}
