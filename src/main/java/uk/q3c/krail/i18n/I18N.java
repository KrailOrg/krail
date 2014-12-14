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

import com.google.inject.BindingAnnotation;
import com.vaadin.data.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

/**
 * Annotation used for marking a Vaadin UI component as needing I18N translation. The parameters provide the keys for
 * I18N lookup. All parameters are optional, but the value parameter is relevant only for those components which
 * implement {@link Property}. Its value would be ignored otherwise.
 *
 * @author David Sowerby 9 Feb 2013
 * @see https://sites.google.com/site/q3cjava/internationalisation-i18n
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@BindingAnnotation
public @interface I18N {
    LabelKey caption() default LabelKey.nullKey;

    DescriptionKey description() default DescriptionKey.nullKey;

    /**
     * The locale for an annotated component is usually taken from {@link CurrentLocale}, but if this optional
     * parameter
     * is specified, it will be used instead. This allows specific components to be fixed to display content in a
     * language different to the rest of the application. The format of the string should be as the IETF BCP 47
     * language
     * tag string; see {@link Locale#toLanguageTag()}
     */

    String locale() default "";

}
