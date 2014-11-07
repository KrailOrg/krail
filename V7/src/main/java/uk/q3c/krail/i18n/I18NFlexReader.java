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

import com.google.common.base.Strings;
import com.google.inject.Inject;

import java.util.Locale;

/**
 * returns translated parameter values for an {@link I18NFlex} annotation
 *
 * @author David Sowerby
 * @date 3 May 2014
 */
public class I18NFlexReader extends I18NFlexReaderBase implements I18NAnnotationReader<I18NFlex> {
    private final CurrentLocale currentLocale;
    private I18NFlex annotation;

    @Inject
    protected I18NFlexReader(Translate translate, CurrentLocale currentLocale) {
        super(translate);
        this.currentLocale = currentLocale;
    }

    @Override
    public String caption() {
        return decode(annotation.captionKeyClass(), annotation.captionKeyName(), locale());
    }

    @Override
    public Locale locale() {
        if (Strings.isNullOrEmpty(annotation.locale())) {
            return currentLocale.getLocale();
        }
        return Locale.forLanguageTag(annotation.locale());
    }

    ;

    @Override
    public String description() {
        return decode(annotation.descriptionKeyClass(), annotation.descriptionKeyName(), locale());
    }

    ;

    @Override
    public I18NFlex getAnnotation() {
        return annotation;
    }

    @Override
    public void setAnnotation(I18NFlex annotation) {
        this.annotation = annotation;
    }
}
