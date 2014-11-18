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
package uk.q3c.krail.i18n;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import uk.q3c.util.MessageFormat;

import java.text.Collator;
import java.util.*;

/**
 * Translates from an  {@link I18NKey} to a value from a {@link PatternSource}, expanding its arguments if it has them.
 * Using the standard Krail method for I18N, the keys will be defined as Enum, implementing I18NKey.  However, this
 * Translate implementation should also work for any other object used as a key, although it has not been tested.
 *
 * @author David Sowerby 24 October 2014 - all translation made in this class, removing dependency on key itself
 * @author David Sowerby 3 Aug 2013
 */
public class DefaultTranslate implements Translate {


    private final CurrentLocale currentLocale;
    private final Set<Locale> supportedLocales;
    private TreeMap<Integer, PatternSource> patternSources;

    /**
     * Creates a local TreeMap copy (to sort by key)
     *
     * @param patternSources
     * @param currentLocale
     */
    @Inject
    protected DefaultTranslate(@PatternSources Map<Integer, PatternSource> patternSources, CurrentLocale
            currentLocale, @SupportedLocales Set<Locale> supportedLocales) {
        super();
        this.supportedLocales = supportedLocales;
        this.patternSources = new TreeMap<>(patternSources);
        this.currentLocale = currentLocale;
    }


    /**
     * The same as {@link #from(I18NKey, Locale, Object...)}, but using {@link #currentLocale}
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
     * Iterates through {@link #patternSources} in ascending order (the order need not be sequential), and returns the
     * first pattern found for {@code key}.
     * <p/>
     * <p/>
     * If the key does not provide a pattern from any of the sources, and key is an Enum, the enum.name() is returned.
     * Before returning the enum.name(), underscores are replaced with spaces.
     * <p/>
     * If the key does not provide a pattern from any of the sources, and key is not an Enum, the key.toString() is
     * returned
     * <p/>
     * If arguments are supplied, these are applied to the pattern.  If key is null, a String "key is null"
     * is returned.  Any arguments which are also I18NKey types are also translated
     *
     * @param key
     *         the key to look up the I18N pattern
     * @param arguments
     *         the arguments used to expand the pattern, if required
     *
     * @throws UnsupportedLocaleException if locale is not in {@link #supportedLocales}
     *
     * @return the translated value as described above, or "key is null" if {@code key} is null
     */
    @Override
    public String from(I18NKey<?> key, Locale locale, Object... arguments) {
        if (!supportedLocales.contains(locale)) {
            throw new UnsupportedLocaleException(locale);
        }
        if (key == null) {
            return "key is null";
        }

        Optional<String> pattern = Optional.absent();
        for (Integer patternIndex : patternSources.keySet()) {
            pattern = patternSources.get(patternIndex)
                                    .retrievePattern(key, locale);
            if (pattern.isPresent()) {
                break;
            }
        }


        //If no pattern defined use the enum name or toString()
        if (!pattern.isPresent()) {
            if (key instanceof Enum) {
                pattern = Optional.of(((Enum) key).name()
                                                  .replace("_", " "));
            } else {
                // or toString()
                pattern = Optional.of(key.toString());
            }
        }
        //If no arguments, return the pattern as it is
        if ((arguments == null) || (arguments.length == 0)) {
            return pattern.get();
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
        String result = MessageFormat.format(pattern.get(), args.toArray());
        return result;
    }

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
