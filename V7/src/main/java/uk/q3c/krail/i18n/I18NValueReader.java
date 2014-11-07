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

import com.google.inject.Inject;

import java.lang.annotation.Annotation;

/**
 * Reads any annotation which conforms to the standard of an {@link I18N} annotation, that is, that it provides the
 * same
 * parameters as {@link I18N}
 *
 * @author David Sowerby
 * @date 2 May 2014
 * @see I18NFlexReader
 */
public class I18NValueReader extends I18NReaderBase implements I18NValueAnnotationReader<Annotation> {

    private final Translate translate;

    @Inject
    protected I18NValueReader(Translate translate, CurrentLocale currentLocale) {
        super(currentLocale);
        this.translate = translate;
    }

    /**
     * @see uk.q3c.krail.i18n.I18NAnnotationReader#caption()
     */
    @Override
    public String value() {
        I18NKey<?> captionKey = (I18NKey<?>) annotationParam("value");
        return translate.from(captionKey, locale());
    }

}
