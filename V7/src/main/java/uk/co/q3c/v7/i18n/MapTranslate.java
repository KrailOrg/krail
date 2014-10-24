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
package uk.co.q3c.v7.i18n;

import com.google.inject.Inject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Translates an I18NKey to a value held in a map, expanding its arguments if it has them.  Using the standard V7 method
 * for I18N, the keys will be defined as Enum, implementing I18NKey.  However, this Translate implementation should also
 * work for any other object used as a key, although it has not been tested.
 *
 * @author David Sowerby 24 October 2014 - all translation made in this class, removing dependency on key itself
 * @author David Sowerby 3 Aug 2013
 */
public class MapTranslate implements Translate {

    private final CurrentLocale currentLocale;

    @Inject
    protected MapTranslate(CurrentLocale currentLocale) {
        super();
        this.currentLocale = currentLocale;
    }

    /**
     * Looks up key pattern from its associated map. The map used is determined by the actual parameter value of key
     * (that is, a key of type I18N<Labels> will use the Labels class as the map source)
     * <p/>
     * The locale is assumed to be {@link CurrentLocale}.
     * <p/>
     * If the key is not present in the map, and key is an Enum, the enum.name() is returned. Before returning the
     * enum.name(), underscores are replaced
     * with spaces.
     * <p/>
     * If the key is not present in the map, and key is not an Enum, the key.toString() is returned
     * <p/>
     * If arguments are supplied, these are applied to the pattern.  If key is null, a String "key is null"
     * is returned.
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
     * Looks up key pattern from its associated map. The map used is determined by the actual parameter value of key
     * (that is, a key of type I18N<Labels> will use the Labels class as the map source)
     * <p/>
     * If the key is not present in the map, and key is an Enum, the enum.name() is returned. Before returning the
     * enum.name(), underscores are replaced
     * with spaces.
     * <p/>
     * If the key is not present in the map, and key is not an Enum, the key.toString() is returned
     * <p/>
     * If arguments are supplied, these are applied to the pattern.  If key is null, a String "key is null"
     * is returned.
     *
     */
    @Override
    public String from(I18NKey<?> key, Locale locale, Object... arguments) {
        if (key == null) {
            return "key is null";
        }

        Type[] genericInterfaces = key.getClass()
                                      .getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];

        Type actualType = parameterizedType.getActualTypeArguments()[0];
        Class bundleClazz = (Class) actualType;

        EnumResourceBundle bundle = (EnumResourceBundle) ResourceBundle.getBundle(bundleClazz.getName(), locale);


        String pattern = bundle.getValue(key);

        //If no pattern defined use the enum name
        if (pattern == null) {
            return ((Enum) key).name()
                               .replace("_", " ");
        }
        if ((arguments == null) || (arguments.length == 0)) {
            return pattern;
        }
        String result = MessageFormat.format(pattern, arguments);
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
