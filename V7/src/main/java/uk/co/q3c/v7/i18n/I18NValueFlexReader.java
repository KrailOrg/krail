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

import java.util.Locale;

import com.google.inject.Inject;
import com.mycila.inject.internal.guava.base.Strings;

/**
 * returns translated value parameter for an {@link I18NValueFlex} annotation
 *
 * @author David Sowerby
 * @date 3 May 2014
 */
public class I18NValueFlexReader extends I18NFlexReaderBase implements I18NValueAnnotationReader<I18NValueFlex> {
	private I18NValueFlex annotation;
	private final CurrentLocale currentLocale;

	@Inject
	protected I18NValueFlexReader(Translate translate, CurrentLocale currentLocale) {
		super(translate);
		this.currentLocale = currentLocale;
	}

	@Override
	public String value() {
		return decode(annotation.valueKeyClass(), annotation.valueKeyName(), locale());
	}

	@Override
	public Locale locale() {
		if (Strings.isNullOrEmpty(annotation.locale())) {
			return currentLocale.getLocale();
		}
		return Locale.forLanguageTag(annotation.locale());
	};

	@Override
	public I18NValueFlex getAnnotation() {
		return annotation;
	}

	@Override
	public void setAnnotation(I18NValueFlex annotation) {
		this.annotation = annotation;
	}
}
