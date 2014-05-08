/*
 * Copyright (C) 2014 David Sowerby
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import uk.co.q3c.util.MessageFormat;

import com.google.common.base.Strings;
import com.google.inject.Inject;

public abstract class I18NReaderBase {

	protected Annotation annotation;
	protected final CurrentLocale currentLocale;

	@Inject
	protected I18NReaderBase(CurrentLocale currentLocale) {
		this.currentLocale = currentLocale;

	}

	protected Object annotationParam(String methodName) {
		checkNotNull(annotation);
		try {
			Method method = annotation.getClass().getDeclaredMethod(methodName);
			Object paramValue = method.invoke(annotation);
			return paramValue;
		} catch (NoSuchMethodException e) {
			String msg = MessageFormat.format("I18N annotation class {0} must define a '{1}()' method", methodName);
			throw new I18NException(msg);
		} catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
			throw new I18NException(e);
		}
	}

	/**
	 *
	 * @see uk.co.q3c.v7.i18n.I18NAnnotationReader#locale()
	 */
	public Locale locale() {
		String localeString = (String) annotationParam("locale");
		if (Strings.isNullOrEmpty(localeString)) {
			return currentLocale.getLocale();
		}
		Locale locale = Locale.forLanguageTag(localeString);
		return locale;
	}

	/**
	 *
	 * @see uk.co.q3c.v7.i18n.I18NAnnotationReader#setAnnotation(java.lang.annotation.Annotation)
	 */
	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	/**
	 *
	 * @see uk.co.q3c.v7.i18n.I18NAnnotationReader#getAnnotation()
	 */
	public Annotation getAnnotation() {
		return annotation;
	}
}
