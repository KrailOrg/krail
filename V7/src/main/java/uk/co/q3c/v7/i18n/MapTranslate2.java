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
 * Translates an I18NKey to a value held in a map, expanding its arguments if it has them. You can also get the
 * value for the key (but cannot use arguments) by using {@link I18NKey#getValue(Locale)}. This class simply provides a
 * slightly neater syntax, a method for expanding a pattern with parameters. Some methods also have a simpler signature
 * by defaulting to {@link CurrentLocale}
 *
 * @author David Sowerby 3 Aug 2013
 */
public class MapTranslate2 {

    private final CurrentLocale currentLocale;

    @Inject
    protected MapTranslate2(CurrentLocale currentLocale) {
        super();
        this.currentLocale = currentLocale;
    }

    /**
     * Looks up key pattern from its associated map. The locale is assumed to be {@link CurrentLocale}. If the key is
     * not present in the map, the enum.name() is returned. Before returning the enum.name(), underscores are replaced
     * with spaces. If arguments are supplied, these are applied to the pattern.
     *
     * @param key
     * @param arguments
     *
     * @return
     */
    //    @Override
    public String from(I18NKey2<?> key, Object... arguments) {
        return from(key, currentLocale.getLocale(), arguments);
    }

    /**
     * Looks up key pattern from its associated, locale specific, map. If the key is not present in the map, the
     * enum.name() is returned. Before returning the enum.name(), underscores are replaced with spaces. If arguments
     * are
     * supplied, these are applied to the pattern.
     *
     * @param locale
     * @param key
     * @param arguments
     *
     * @return
     */
    //    @Override
    public String from(I18NKey2<?> key, Locale locale, Object... arguments) {
        if (key == null) {
            return "key is null";
        }

        Type[] genericInterfaces = key.getClass()
                                      .getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];

        Type actualType = parameterizedType.getActualTypeArguments()[0];
        Class bundleClazz = (Class) actualType;

        EnumResourceBundle2 bundle = (EnumResourceBundle2) ResourceBundle.getBundle(bundleClazz.getName(), locale);


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
    //    @Override
    public Collator collator() {
        return Collator.getInstance(currentLocale.getLocale());
    }

}
