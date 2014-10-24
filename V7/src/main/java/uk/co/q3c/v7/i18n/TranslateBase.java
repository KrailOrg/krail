/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.co.q3c.v7.i18n;

import uk.co.q3c.util.MessageFormat;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A base class for providing translations for {@link I18NKey} instances.  Alternative methods of retrieving a pattern
 * -
 * for example from a database - should only need to override the {@link #retrievePattern(I18NKey, Locale)} method
 * <p/>
 * Created by David Sowerby on 24/10/14.
 */
public abstract class TranslateBase implements Translate {
    private final CurrentLocale currentLocale;

    protected TranslateBase(CurrentLocale currentLocale) {
        this.currentLocale = currentLocale;
    }

    /**
     * Looks up key pattern from its associated source. The source used varies dependent on implementation.
     * <p/>
     * If the key is not present in the map, and key is an Enum, the enum.name() is returned. Before returning the
     * enum.name(), underscores are replaced with spaces.
     * <p/>
     * If the key is not present in the map, and key is not an Enum, the key.toString() is returned
     * <p/>
     * If arguments are supplied, these are applied to the pattern.  If key is null, a String "key is null"
     * is returned.  Any arguments which are also I18NKey types are also translated
     *
     * @param key
     *         the key to look up the I18N pattern
     * @param arguments
     *         the arguments used to expand the pattern, if required
     *
     * @return the translated value, or "key is null" if {@code key} is null
     */
    @Override
    public String from(I18NKey<?> key, Object... arguments) {
        return from(key, currentLocale.getLocale(), arguments);
    }

    /**
     * Looks up key pattern from its associated source. The source used varies dependent on implementation.
     * <p/>
     * If the key is not present in the map, and key is an Enum, the enum.name() is returned. Before returning the
     * enum.name(), underscores are replaced with spaces.
     * <p/>
     * If the key is not present in the map, and key is not an Enum, the key.toString() is returned
     * <p/>
     * If arguments are supplied, these are applied to the pattern.  If key is null, a String "key is null"
     * is returned.  Any arguments which are also I18NKey types are also translated
     *
     * @param key
     *         the key to look up the I18N pattern
     * @param arguments
     *         the arguments used to expand the pattern, if required
     *
     * @return the translated value, or "key is null" if {@code key} is null
     */
    @Override
    public String from(I18NKey<?> key, Locale locale, Object... arguments) {
        if (key == null) {
            return "key is null";
        }


        String pattern = retrievePattern(key, locale);

        //If no pattern defined use the enum name
        if (pattern == null) {
            return ((Enum) key).name()
                               .replace("_", " ");
        }
        if ((arguments == null) || (arguments.length == 0)) {
            return pattern;
        }

        // If any of the arguments are I18NKeys, translate them as well
        List<Object> args = new ArrayList(Arrays.asList(arguments));
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i) instanceof I18NKey) {
                String translation = from((I18NKey) args.get(i));
                args.remove(i);
                args.add(i, translation);
            }
        }
        String result = MessageFormat.format(pattern, args.toArray());
        return result;
    }


    protected abstract String retrievePattern(I18NKey<?> key, Locale locale);

    /**
     * convenience method to get Collator instance for the {@link CurrentLocale}
     *
     * @return
     */
    @Override
    public Collator collator() {
        return Collator.getInstance(currentLocale.getLocale());
    }
}
