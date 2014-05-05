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

import java.lang.annotation.Annotation;

import com.google.inject.Inject;

/**
 * Reads any annotation which conforms to the standard of an {@link I18N} annotation, that is, that it provides the same
 * parameters as {@link I18N}
 *
 * @see I18NFlexReader
 * @author David Sowerby
 * @date 2 May 2014
 */
public class I18NReader extends I18NReaderBase implements I18NAnnotationReader<Annotation> {
	private final Translate translate;

	@Inject
	protected I18NReader(Translate translate, CurrentLocale currentLocale) {
		super(currentLocale);
		this.translate = translate;

	}

	/**
	 *
	 * @see uk.co.q3c.v7.i18n.I18NAnnotationReader#caption()
	 */
	@Override
	public String caption() {
		I18NKey<?> captionKey = (I18NKey<?>) annotationParam("caption");
		return translate.from(captionKey, locale());
	}

	/**
	 *
	 * @see uk.co.q3c.v7.i18n.I18NAnnotationReader#description()
	 */
	@Override
	public String description() {
		I18NKey<?> descriptionKey = (I18NKey<?>) annotationParam("description");
		return translate.from(descriptionKey);
	}

}
