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
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Translates an I18NKey to a value held in a map, expanding its arguments if it has them.  Using the standard V7
 * method
 * for I18N, the keys will be defined as Enum, implementing I18NKey.  However, this Translate implementation should
 * also
 * work for any other object used as a key, although it has not been tested.
 *
 * @author David Sowerby 24 October 2014 - all translation made in this class, removing dependency on key itself
 * @author David Sowerby 3 Aug 2013
 */
public class MapTranslate extends TranslateBase implements Translate {

    @Inject
    protected MapTranslate(CurrentLocale currentLocale) {
        super(currentLocale);

    }


    /**
     * This implementation uses a static map to define the patterns for translation.  The {@link MapResourceBundle}
     * used to locate the pattern determined by the actual parameter value of key (that is, a key of type I18N<Labels>
     * will use the Labels class as the map source)
     * <p/>
     */
    protected String retrievePattern(I18NKey<?> key, Locale locale) {
        Type[] genericInterfaces = key.getClass()
                                      .getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];

        Type actualType = parameterizedType.getActualTypeArguments()[0];
        Class bundleClazz = (Class) actualType;

        MapResourceBundle bundle = (MapResourceBundle) ResourceBundle.getBundle(bundleClazz.getName(), locale);


        String pattern = bundle.getValue(key);
        return pattern;
    }


}
